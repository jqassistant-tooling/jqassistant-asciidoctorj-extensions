package org.jqassistant.contrib.asciidoctorj.processors.includes;

import org.jqassistant.contrib.asciidoctorj.freemarker.TemplateRepo;
import org.jqassistant.contrib.asciidoctorj.reportrepo.ReportRepo;

import java.util.*;

public class Summary extends AbstractIncludeProcessor{

    public Summary(ReportRepo repo, TemplateRepo templateRepo) {
        super(repo, templateRepo, "Summary", List.of("Summary"));
    }
}