package org.jqassistant.tooling.asciidoctorj.reportrepo.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Getter
public abstract class AbstractRule implements Rule {
    private String id;
    private String description;
    private int duration;
}
