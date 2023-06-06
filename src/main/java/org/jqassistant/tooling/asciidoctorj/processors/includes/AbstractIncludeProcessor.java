package org.jqassistant.tooling.asciidoctorj.processors.includes;

import freemarker.template.TemplateException;
import io.smallrye.common.constraint.NotNull;
import org.asciidoctor.ast.Document;
import org.asciidoctor.extension.IncludeProcessor;
import org.asciidoctor.extension.PreprocessorReader;
import org.jqassistant.tooling.asciidoctorj.freemarker.TemplateRepo;
import org.jqassistant.tooling.asciidoctorj.freemarker.templateroots.RuleRootParser;
import org.jqassistant.tooling.asciidoctorj.freemarker.templateroots.RulesRoot;
import org.jqassistant.tooling.asciidoctorj.processors.attributes.ProcessAttributes;
import org.jqassistant.tooling.asciidoctorj.processors.attributes.ProcessAttributesFactory;
import org.jqassistant.tooling.asciidoctorj.reportrepo.ReportRepo;
import org.jqassistant.tooling.asciidoctorj.reportrepo.model.Concept;
import org.jqassistant.tooling.asciidoctorj.reportrepo.model.Constraint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;


public abstract class AbstractIncludeProcessor extends IncludeProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractIncludeProcessor.class);

    private static final String PREFIX = "jQAssistant:";

    ReportRepo repo;

    List<String> templateNames;
    TemplateRepo templateRepo;

    String target;


    protected AbstractIncludeProcessor(@NotNull ReportRepo reportRepository, @NotNull TemplateRepo templateRepo, @NotNull String target, @NotNull List<String> templateNames) {
        this.repo = reportRepository;
        this.templateRepo = templateRepo;
        this.templateNames = templateNames;
        this.target = target;
    }

    @Override
    public boolean handles(String target) {
        return (PREFIX + this.target).equals(target);
    }

    @Override
    public void process(Document document, PreprocessorReader reader, String target, Map<String, Object> attributeMap) {
        LOGGER.info("Starting to process include for {}", target);

        ProcessAttributes attributes = ProcessAttributesFactory.createProcessAttributesInclude(document, attributeMap);

        RulesRoot root = fillDataStructure(attributes);

        reader.pushInclude(fillTemplates(root, attributes),
                target,
                "",
                1,
                attributeMap);

        LOGGER.info("Finished to process include for {}", target);
    }

    /**
     * Fills all templates in "templates" with the content of root.
     *
     * @param root the data structure the templates are filled with
     * @param attributes the ProcessAttribute instance. May optionally be filled with: templatesPath
     * @return the from template and root produced String
     */
    private String fillTemplates(@NotNull RulesRoot root, @NotNull ProcessAttributes attributes) {
        Writer writer = new StringWriter();

        List<String> tNames;

        if(root.getConcepts().size() == 0 && root.getConstraints().size() == 0) {
            tNames = List.of("NoResult");
            LOGGER.info("Filters for concepts {} and constraints {} returned no matching Rules!", attributes.getConceptIdFilter(), attributes.getConstraintIdFilter());
        }
        else {
            tNames = templateNames;
            LOGGER.info("Starting to fill templates {}", tNames);
        }

        for (String tName : tNames) {
            try {
                templateRepo.findTemplate(attributes, tName).process(root, writer);
            } catch (TemplateException e) {
                throw new IllegalArgumentException("Your \"" + tName + "\"-template seems to have an error in it's calls to the data structure! Refer to manual section \"Using the jqassistant-asciidoctor-extension\"." , e);
            } catch (IOException e) {
                throw new IllegalArgumentException("Your \"" + tName + "\"-template file can not be parsed to a freemarker template! Refer to manual section \"Using your own template\"." , e);
            }
        }

        return writer.toString();
    }

    /**
     * build the data structure needed to fill the template
     *
     * @param attributes the ProcessAttribute instance. Should at least contain: reportPath, outputDirectory! May optionally be filled with: conceptIdFilter, constraintIdFilter
     * @return rootElement for data-structure
     */
    RulesRoot fillDataStructure(@NotNull ProcessAttributes attributes) {
        RulesRoot.RulesRootBuilder rootBuilder = RulesRoot.builder();

        LOGGER.info("Starting to fill RulesRoot with for {} matching concepts and for {} matching constraints.", attributes.getConceptIdFilter(), attributes.getConstraintIdFilter());
        for (Concept concept :
                repo.findConcepts(attributes)) {
            rootBuilder.concept(RuleRootParser.createRuleRoot(concept, attributes.getOutputDirectory()));
        }

        for (Constraint constraint :
                repo.findConstraints(attributes)) {
            rootBuilder.constraint(RuleRootParser.createRuleRoot(constraint, attributes.getOutputDirectory()));
        }

        return rootBuilder.build();
    }
}
