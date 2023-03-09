package org.jqassistant.contrib.asciidoctorj.freemarker.templateroots;

import io.smallrye.common.constraint.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import org.jqassistant.contrib.asciidoctorj.reportrepo.model.ExecutableRule;
import org.jqassistant.contrib.asciidoctorj.reportrepo.model.Reports;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Builder
@Getter
public class RuleRoot implements Comparable<RuleRoot>{

    private static String statSuccess = "SUCCESS", statWarn = "WARNING", statFail = "FAILURE", statSkipped = "SKIPPED";

    private String id;
    private String description;
    private String status, severity;

    private boolean hasReports, hasResult;

    @Singular
    private List<String> resultColumnKeys;
    @Singular
    private List<List<String>> resultRows;

    private Reports reports;

    public static RuleRoot ruleToRuleRoot(ExecutableRule rule) {
        RuleRootBuilder builder = builder();

        builder.id(rule.getId());
        builder.description(rule.getDescription());
        builder.status(rule.getStatus().toUpperCase());
        builder.severity(rule.getSeverity().toUpperCase());

        List<String> resultKeys = rule.getResult().getColumnKeys();

        if(resultKeys != null) {
            builder.resultColumnKeys(resultKeys);

            for (Map<String, String> row : rule.getResult().getRows()) {
                List<String> rowContent = new ArrayList<>();
                for (String key : resultKeys) {
                    rowContent.add(row.get(key));
                }
                builder.resultRow(rowContent);
            }
            builder.hasResult = true;
        }

        if(rule.getReports() != Reports.EMPTY_REPORTS) builder.hasReports = true;
        builder.reports(rule.getReports());

        return builder.build();
    }

    @Override
    public int compareTo(@NotNull RuleRoot other) {
        if(this.getStatus().equals(other.getStatus())) {
            return this.id.compareTo(other.id);
        }
        else if(this.getStatus().equals(statFail)) return -1;
        else if(other.getStatus().equals(statFail)) return 1;
        else if(this.getStatus().equals(statWarn)) return -1;
        else if(other.getStatus().equals(statWarn)) return 1;
        else if(this.getStatus().equals(statSuccess)) return -1;
        else if(other.getStatus().equals(statSuccess)) return 1;

        throw new IllegalStateException("Rule Root should be comparable; statuses were: " + this.status + " " + other.status);
    }
}
