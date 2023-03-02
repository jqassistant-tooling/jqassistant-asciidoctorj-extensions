package org.jqassistant.contrib.asciidoctorj.includeprocessor.attributes;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProcessAttributes {
    private String idWildcard;

    private String reportPath, templatesPath;
}
