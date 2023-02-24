package org.jqassistant.contrib.asciidoctorj.reportrepo.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Getter
public abstract class AbstractExecutableRule extends AbstractRule implements ExecutableRule {
    private String status;
    private String severity;

    private Result result;
}
