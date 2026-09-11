package org.jqassistant.tooling.asciidoctorj.freemarker.templateroots;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.buschmais.jqassistant.core.rule.api.model.RuleException;

import io.smallrye.common.constraint.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import org.jqassistant.tooling.asciidoctorj.reportrepo.model.Reports;
import org.jqassistant.tooling.asciidoctorj.reportrepo.model.VerificationResult;

import static com.buschmais.jqassistant.core.rule.api.model.Severity.fromValue;

// This class acts as an interface to the Freemaker templates.

@Getter
@Builder
public class RuleRoot implements Comparable<RuleRoot> {

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
    private List<RowRoot> resultRows;
    @Singular
    private List<RowRoot> baselineRows;
    @Singular
    private List<SuppressedRowRoot> suppressedRows;

    private Reports reports;
    private VerificationResult verificationResult;

    //-------------------Subclasses----------------------

    @SuperBuilder
    @Getter
    public static class RowRoot {
        @Singular
        private List<String> columns;
    }

    @SuperBuilder
    @Getter
    public static class SuppressedRowRoot extends RowRoot {
        private Optional<String> reason;
        private Optional<LocalDate> until;
    }

    //---------------------------------------------------

    @Override
    public int compareTo(@NotNull RuleRoot other) {
        //Sorting for Status
        if (!this.status.equals(other.status)) {
            if (this.status.equals(statFail))
                return -1;
            if (other.status.equals(statFail))
                return 1;
            if (this.status.equals(statWarn))
                return -1;
            if (other.status.equals(statWarn))
                return 1;
            if (this.status.equals(statSuccess))
                return -1;
            if (other.status.equals(statSuccess))
                return 1;

            throw new IllegalStateException("Rule Root should be comparable; statuses were: " + this.status + " " + other.status);
        }
        //Sorting for Severity
        if (!this.severity.equals(other.severity)) {
            try {
                return Integer.compare(fromValue(this.severity).getLevel(), fromValue(other.severity).getLevel());
            } catch (RuleException e) {
                throw new RuntimeException(e);
            }
        }
        //Sorting for Violations
        boolean thisFailed = this.verificationResult != null && !this.verificationResult.isSuccess();
        boolean otherFailed = other.verificationResult != null && !other.verificationResult.isSuccess();

        if (thisFailed != otherFailed) {
            return thisFailed ? -1 : 1;
        }
        //alphabetical sorting
        return this.id.compareTo(other.id);
    }

    //only to solve lombok builder-javadoc bug (source for solution: https://stackoverflow.com/questions/51947791/javadoc-cannot-find-symbol-error-when-using-lomboks-builder-annotation)
    public static class RuleRootBuilder {
    }
}
