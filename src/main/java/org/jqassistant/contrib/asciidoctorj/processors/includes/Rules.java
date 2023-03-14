package org.jqassistant.contrib.asciidoctorj.processors.includes;

import org.jqassistant.contrib.asciidoctorj.freemarker.TemplateRepo;
import org.jqassistant.contrib.asciidoctorj.reportrepo.ReportRepo;

import java.util.*;

public class Rules extends AbstractIncludeProcessor {

    public Rules(ReportRepo repo, TemplateRepo templateRepo) {
        super(repo, templateRepo, "Rules", List.of("RulesConstraint", "RulesConcept"));
    }
}
