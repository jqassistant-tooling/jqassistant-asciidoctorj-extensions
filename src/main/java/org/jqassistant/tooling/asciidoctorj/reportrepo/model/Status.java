package org.jqassistant.tooling.asciidoctorj.reportrepo.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Status {
    private String value;
    private int level;
}
