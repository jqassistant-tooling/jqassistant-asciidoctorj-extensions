package org.jqassistant.tooling.asciidoctorj.reportrepo.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Getter
public abstract class AbstractExecutableRule extends AbstractRule implements ExecutableRule {
    private Status status;
    private Severity severity;

    private Result result;
    private Reports reports;
    private VerificationResult verificationResult;
}
