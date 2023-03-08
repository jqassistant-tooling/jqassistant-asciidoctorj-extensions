package org.jqassistant.contrib.asciidoctorj.processors.attributes;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProcessAttributes {
    private String conceptIdFilter, constraintIdFilter;

    private String reportPath, templatesPath;
}
