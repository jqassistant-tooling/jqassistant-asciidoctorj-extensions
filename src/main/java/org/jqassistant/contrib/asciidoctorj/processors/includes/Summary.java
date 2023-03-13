package org.jqassistant.contrib.asciidoctorj.processors.includes;

import org.jqassistant.contrib.asciidoctorj.freemarker.TemplateRepo;
import org.jqassistant.contrib.asciidoctorj.freemarker.templateroots.RuleRoot;
import org.jqassistant.contrib.asciidoctorj.freemarker.templateroots.RulesRoot;
import org.jqassistant.contrib.asciidoctorj.processors.attributes.ProcessAttributes;
import org.jqassistant.contrib.asciidoctorj.reportrepo.ReportRepo;
import org.jqassistant.contrib.asciidoctorj.reportrepo.model.Concept;
import org.jqassistant.contrib.asciidoctorj.reportrepo.model.Constraint;

import java.util.*;

public class Summary extends AbstractIncludeProcessor<RulesRoot> { //TODO: fix space in id breaking html hook

    public Summary(ReportRepo repo, TemplateRepo templateRepo) {
        super(repo, templateRepo, "Summary", List.of("Summary"));
    }

    /**
     * fill the data structure needed to fill the template in the abstract class
     *
     * @return rootElement for structure
     */
    @Override
    RulesRoot fillDataStructure(ProcessAttributes attributes) {
        RulesRoot.RulesRootBuilder rootBuilder = RulesRoot.builder();

        for (Concept concept :
                repo.findConcepts(attributes)) {
            rootBuilder.concept(RuleRoot.createRuleRoot(concept));
        }

        for (Constraint constraint :
                repo.findConstraints(attributes)) {
            rootBuilder.constraint(RuleRoot.createRuleRoot(constraint));
        }

        return rootBuilder.build();
    }
}