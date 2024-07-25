package org.jqassistant.tooling.asciidoctorj.processors.includes;

import io.smallrye.common.constraint.NotNull;
import org.jqassistant.tooling.asciidoctorj.freemarker.TemplateRepo;
import org.jqassistant.tooling.asciidoctorj.reportrepo.ReportRepo;

import java.util.*;

public class Rules extends AbstractIncludeProcessor {

    public Rules(@NotNull ReportRepo repo, @NotNull TemplateRepo templateRepo) {
        super(repo, templateRepo, "Rules", List.of("RulesConstraint", "RulesConcept"));
    }
}
