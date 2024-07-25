package org.jqassistant.tooling.asciidoctorj.reportrepo.model;

public interface ExecutableRule extends Rule {
    String getStatus();
    String getSeverity();

    Result getResult();
    Reports getReports();
}
