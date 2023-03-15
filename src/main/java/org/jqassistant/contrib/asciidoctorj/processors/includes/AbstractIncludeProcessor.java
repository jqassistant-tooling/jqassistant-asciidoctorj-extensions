package org.jqassistant.contrib.asciidoctorj.processors.includes;

import freemarker.template.TemplateException;
import org.asciidoctor.ast.Document;
import org.asciidoctor.extension.IncludeProcessor;
import org.asciidoctor.extension.PreprocessorReader;
import org.jqassistant.contrib.asciidoctorj.freemarker.TemplateRepo;
import org.jqassistant.contrib.asciidoctorj.freemarker.templateroots.RuleRoot;
import org.jqassistant.contrib.asciidoctorj.freemarker.templateroots.RulesRoot;
import org.jqassistant.contrib.asciidoctorj.processors.attributes.ProcessAttributes;
import org.jqassistant.contrib.asciidoctorj.processors.attributes.ProcessAttributesFactory;
import org.jqassistant.contrib.asciidoctorj.reportrepo.ReportRepo;
import org.jqassistant.contrib.asciidoctorj.reportrepo.model.Concept;
import org.jqassistant.contrib.asciidoctorj.reportrepo.model.Constraint;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;


public abstract class AbstractIncludeProcessor extends IncludeProcessor {
    private static final String PREFIX = "jQAssistant:";

    ReportRepo repo;

    List<String> templateNames;
    TemplateRepo templateRepo;

    String target;


    protected AbstractIncludeProcessor(ReportRepo reportRepository, TemplateRepo templateRepo, String target, List<String> templateNames) {
        this.repo = reportRepository;
        this.templateRepo = templateRepo;
        this.templateNames = templateNames;
        this.target = target;
    }

    @Override
    public boolean handles(String target) {
        return (PREFIX+this.target).equals(target);
    }

    @Override
    public void process(Document document, PreprocessorReader reader, String target, Map<String, Object> attributeMap) {
        ProcessAttributes attributes = ProcessAttributesFactory.createProcessAttributesInclude(document, attributeMap);

        RulesRoot root = fillDataStructure(attributes);

        reader.pushInclude(fillTemplates(root, attributes),
                target,
                "",
                1,
                attributeMap);
    }

    /**
     * Fills all templates in "templates" with the content of root.
     *
     * @param root the data structure the templates are filled with
     * @return the from template and root produced String
     */
    private String fillTemplates(RulesRoot root, ProcessAttributes attributes) {
        Writer writer = new StringWriter();

        List<String> tNames;

        if(root == null) {
            tNames = List.of("NoResult");
        }
        else {
            tNames = templateNames;
        }

        for (String tName : tNames) {
            try {
                templateRepo.findTemplate(attributes, tName).process(root, writer);
            } catch (TemplateException e) {
                throw new IllegalArgumentException("You're \"" + tName + "\"-template seems to have an error in it's calls to the data structure! Refer to manual section \"Using the jqassistant-asciidoctor-extension\"." , e);
            } catch (IOException e) {
                throw new IllegalArgumentException("You're \"" + tName + "\"-template file can not be parsed to a freemarker template! Refer to manual section \"Using your own template\"." , e);
            }
        }

        return writer.toString();
    }

    /**
     * build the data structure needed to fill the template
     *
     * @param attributes give the attributes parsed into the include call in adoc
     * @return rootElement for data-structure
     */
    RulesRoot fillDataStructure(ProcessAttributes attributes) {
        RulesRoot.RulesRootBuilder rootBuilder = RulesRoot.builder();

        for (Concept concept :
                repo.findConcepts(attributes)) {
            rootBuilder.concept(RuleRoot.createRuleRoot(concept, attributes.getOutputDirectory()));
        }

        for (Constraint constraint :
                repo.findConstraints(attributes)) {
            rootBuilder.constraint(RuleRoot.createRuleRoot(constraint, attributes.getOutputDirectory()));
        }

        return rootBuilder.build();
    }
}
