package org.jqassistant.tooling.asciidoctorj.processors.includes;

import java.util.List;

import io.smallrye.common.constraint.NotNull;
import org.jqassistant.tooling.asciidoctorj.freemarker.TemplateRepo;
import org.jqassistant.tooling.asciidoctorj.reportrepo.ReportRepo;

public class Summary extends AbstractIncludeProcessor {

    public Summary(@NotNull ReportRepo repo, @NotNull TemplateRepo templateRepo) {
        super(repo, templateRepo, "Summary", List.of("Summary"));
    }
}