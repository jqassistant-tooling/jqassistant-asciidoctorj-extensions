package org.jqassistant.tooling.asciidoctorj.freemarker.templateroots;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import io.smallrye.common.constraint.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import org.jqassistant.tooling.asciidoctorj.reportrepo.model.Reports;
import org.jqassistant.tooling.asciidoctorj.reportrepo.model.Severity;
import org.jqassistant.tooling.asciidoctorj.reportrepo.model.Status;
import org.jqassistant.tooling.asciidoctorj.reportrepo.model.VerificationResult;

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
    private Status status;
    private Severity severity;

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
        // Sorting by Status Level increasing
        int statusComparison;
        statusComparison = Integer.compare(this.status.getLevel(), other.status.getLevel());
        if (statusComparison != 0) {
            return statusComparison;
        }

        // Sorting by Severity Level increasing
        int severityComparison = Integer.compare(this.severity.getLevel(), other.severity.getLevel());
        if (severityComparison != 0) {
            return severityComparison;
        }

        // Sorting by Violation - priority true
        boolean thisFailed = this.verificationResult != null && !this.verificationResult.isSuccess();
        boolean otherFailed = other.verificationResult != null && !other.verificationResult.isSuccess();
        if (thisFailed != otherFailed) {
            return thisFailed ? -1 : 1;
        }

        // Alphabetical sorting regarding ID
        return this.id.compareTo(other.id);
    }

    //only to solve lombok builder-javadoc bug (source for solution: https://stackoverflow.com/questions/51947791/javadoc-cannot-find-symbol-error-when-using-lomboks-builder-annotation)
    public static class RuleRootBuilder {
    }
}
