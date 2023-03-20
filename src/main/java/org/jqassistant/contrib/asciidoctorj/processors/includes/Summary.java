package org.jqassistant.contrib.asciidoctorj.processors.includes;

import io.smallrye.common.constraint.NotNull;
import org.jqassistant.contrib.asciidoctorj.freemarker.TemplateRepo;
import org.jqassistant.contrib.asciidoctorj.reportrepo.ReportRepo;

import java.util.*;

public class Summary extends AbstractIncludeProcessor{

    public Summary(@NotNull ReportRepo repo, @NotNull TemplateRepo templateRepo) {
        super(repo, templateRepo, "Summary", List.of("Summary"));
    }
}