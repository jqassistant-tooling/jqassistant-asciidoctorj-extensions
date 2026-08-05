package org.jqassistant.tooling.asciidoctorj.freemarker.templateroots;

import io.smallrye.common.constraint.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import org.jqassistant.tooling.asciidoctorj.reportrepo.model.Reports;

import java.util.List;

import com.buschmais.jqassistant.core.rule.api.model.RuleException;
import com.buschmais.jqassistant.core.rule.api.model.Severity;

import static com.buschmais.jqassistant.core.rule.api.model.Severity.fromValue;

// This class acts as an interface to the Freemaker templates.

@Builder
@Getter
public class RuleRoot implements Comparable<RuleRoot>{

    private static String statSuccess = "SUCCESS";
    private static String statWarn = "WARNING";
    private static String statFail = "FAILURE";
    private static String statSkipped = "SKIPPED";

    private String id;
    private String description;
    private String status;
    private String severity;

    private boolean hasReports;
    private boolean hasResult;
    private boolean hasBaselineResult;
    private boolean hasSuppressedResult;

    private List<String> resultColumnKeys;
    @Singular
    private List<List<String>> resultRows;
    @Singular
    private List<List<String>> baselineRows;
    @Singular
    private List<List<String>> suppressedRows;

    private Reports reports;

    @Override
    public int compareTo(@NotNull RuleRoot other) {

        if(this.status.equals(other.status)) {

            if (!this.severity.equals(other.severity)) {
                try {
                    return Integer.compare(fromValue(this.severity).getLevel(), fromValue(other.severity).getLevel());
                } catch (RuleException e) {
                    throw new RuntimeException(e);
                }
            }

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

    //only to solve lombok builder-javadoc bug
    public static class RuleRootBuilder {};
}
