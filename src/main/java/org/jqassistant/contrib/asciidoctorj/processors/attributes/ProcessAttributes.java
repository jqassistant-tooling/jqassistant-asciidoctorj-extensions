package org.jqassistant.contrib.asciidoctorj.processors.attributes;

import lombok.Builder;
import lombok.Getter;

import java.io.File;

@Builder
@Getter
public class ProcessAttributes {
    private String conceptIdFilter;
    private String constraintIdFilter;

    private String reportPath;
    private String templatesPath;
    private File outputDirectory;
}
