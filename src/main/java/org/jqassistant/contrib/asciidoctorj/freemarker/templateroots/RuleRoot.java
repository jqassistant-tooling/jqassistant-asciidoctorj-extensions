package org.jqassistant.contrib.asciidoctorj.freemarker.templateroots;

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
public class RuleRoot {

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
}
