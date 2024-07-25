package org.jqassistant.tooling.asciidoctorj.processors.attributes;

import java.io.File;

public class ProcessAttributes {
    private final String conceptIdFilter;
    private final String constraintIdFilter;

    private final String reportPath;
    private final String templatesPath;
    private final File outputDirectory;
    private final File imagesDirectory;

    private ProcessAttributes(String conceptIdFilter, String constraintIdFilter, String reportPath, String templatesPath, File outputDirectory, File imagesDirectory) {
        this.conceptIdFilter = conceptIdFilter;
        this.constraintIdFilter = constraintIdFilter;
        this.reportPath = reportPath;
        this.templatesPath = templatesPath;
        this.outputDirectory = outputDirectory;
        this.imagesDirectory = imagesDirectory;
    }

    public static ProcessAttributesBuilder builder() {
        return new ProcessAttributesBuilder();
    }

    public String getConceptIdFilter() {
        return this.conceptIdFilter;
    }

    public String getConstraintIdFilter() {
        return this.constraintIdFilter;
    }

    public String getReportPath() {
        return this.reportPath;
    }

    public String getTemplatesPath() {
        return this.templatesPath;
    }

    public File getOutputDirectory() {
        return this.outputDirectory;
    }

    public File getImagesDirectory() {
        return this.imagesDirectory;
    }

    public static class ProcessAttributesBuilder {
        private String conceptIdFilter;
        private String constraintIdFilter;
        private String reportPath;
        private String templatesPath;
        private File outputDirectory;
        private File imagesDirectory;

        ProcessAttributesBuilder() {
        }

        public ProcessAttributesBuilder conceptIdFilter(String conceptIdFilter) {
            this.conceptIdFilter = conceptIdFilter;
            return this;
        }

        public ProcessAttributesBuilder constraintIdFilter(String constraintIdFilter) {
            this.constraintIdFilter = constraintIdFilter;
            return this;
        }

        public ProcessAttributesBuilder reportPath(String reportPath) {
            this.reportPath = reportPath;
            return this;
        }

        public ProcessAttributesBuilder templatesPath(String templatesPath) {
            this.templatesPath = templatesPath;
            return this;
        }

        public ProcessAttributesBuilder outputDirectory(File outputDirectory) {
            this.outputDirectory = outputDirectory;
            return this;
        }

        public ProcessAttributesBuilder imagesDirectory(File imagesDirectory) {
            this.imagesDirectory = imagesDirectory;
            return this;
        }

        public ProcessAttributes build() {
            return new ProcessAttributes(this.conceptIdFilter, this.constraintIdFilter, this.reportPath, this.templatesPath, this.outputDirectory, this.imagesDirectory);
        }

        public String toString() {
            return "ProcessAttributes.ProcessAttributesBuilder(conceptIdFilter=" + this.conceptIdFilter + ", constraintIdFilter=" + this.constraintIdFilter + ", reportPath=" + this.reportPath + ", templatesPath=" + this.templatesPath + ", outputDirectory=" + this.outputDirectory + ", imagesDirectory=" + this.imagesDirectory + ")";
        }
    }
}
