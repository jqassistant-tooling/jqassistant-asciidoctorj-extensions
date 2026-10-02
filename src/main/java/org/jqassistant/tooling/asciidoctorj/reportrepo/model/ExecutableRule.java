package org.jqassistant.tooling.asciidoctorj.reportrepo.model;

public interface ExecutableRule extends Rule {
    Status getStatus();

    Severity getSeverity();

    Result getResult();

    Reports getReports();

    VerificationResult getVerificationResult();
}
