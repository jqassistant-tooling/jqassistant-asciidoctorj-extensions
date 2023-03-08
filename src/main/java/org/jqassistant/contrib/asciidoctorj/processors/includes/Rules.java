package org.jqassistant.contrib.asciidoctorj.processors.includes;

import org.jqassistant.contrib.asciidoctorj.freemarker.TemplateRepo;
import org.jqassistant.contrib.asciidoctorj.freemarker.templateroots.RuleRoot;
import org.jqassistant.contrib.asciidoctorj.freemarker.templateroots.RulesRoot;
import org.jqassistant.contrib.asciidoctorj.processors.attributes.ProcessAttributes;
import org.jqassistant.contrib.asciidoctorj.reportrepo.ReportRepo;
import org.jqassistant.contrib.asciidoctorj.reportrepo.model.Concept;
import org.jqassistant.contrib.asciidoctorj.reportrepo.model.Constraint;

import java.util.*;

public class Rules extends AbstractIncludeProcessor<RulesRoot> {

    public Rules(ReportRepo repo, TemplateRepo templateRepo) {
        super(repo, templateRepo, "Rules", List.of("RulesConcept", "RulesConstraint"));
    }

    @Override
    RulesRoot fillDataStructure(ProcessAttributes attributes) {
        RulesRoot.RulesRootBuilder rootBuilder = RulesRoot.builder();

        for (Concept concept :
                repo.findConcepts(attributes)) {
            rootBuilder.concept(RuleRoot.ruleToRuleRoot(concept));
        }

        for (Constraint constraint :
                repo.findConstraints(attributes)) {
            rootBuilder.constraint(RuleRoot.ruleToRuleRoot(constraint));
        }

        return rootBuilder.build();
    }
}
