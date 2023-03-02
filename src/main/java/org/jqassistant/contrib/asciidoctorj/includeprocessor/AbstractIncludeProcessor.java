package org.jqassistant.contrib.asciidoctorj.includeprocessor;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.asciidoctor.ast.Document;
import org.asciidoctor.extension.IncludeProcessor;
import org.asciidoctor.extension.PreprocessorReader;
import org.jqassistant.contrib.asciidoctorj.freemarker.TemplateLoader;
import org.jqassistant.contrib.asciidoctorj.includeprocessor.attributes.ProcessAttributes;
import org.jqassistant.contrib.asciidoctorj.reportrepo.ReportRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;


public abstract class AbstractIncludeProcessor<ROOT_STRUCT> extends IncludeProcessor {
    Logger logger = LoggerFactory.getLogger("include_processor");

    public static final String PREFIX = "jQA:";

    ReportRepo repo;

    TemplateLoader templateLoader;
    Template template;

    String target;
    String templateName;


    public AbstractIncludeProcessor(ReportRepo reportRepository, TemplateLoader templateLoader, String target, String templateName) {
        this.repo = reportRepository;
        this.templateLoader = templateLoader;
        this.template = templateLoader.loadTemplate(templateName);
        this.target = target;
        this.templateName = templateName;
    }

    @Override
    public boolean handles(String target) {
        return (PREFIX+this.target).equals(target);
    }

    @Override
    public void process(Document document, PreprocessorReader reader, String target, Map<String, Object> attributeMap) {
        //System.out.println(document.getAttributes().keySet().toString() + attributeMap.keySet());

        if(!(document.getAttributes().get("report-path") instanceof String)) {
            throw new IllegalStateException("You're report xml file location isn't set properly! Please set the destination of you're jqassistant-report.xml via the global document attributes for you're asciidoctor.");
        }
        if(document.getAttributes().get("templates-path") != null && !(document.getAttributes().get("templates-path") instanceof String)) {
            throw new IllegalStateException("You're templates folder location isn't a String! Please set the destination of you're template folder to a propper String via the global document attributes for you're asciidoctor.");
        }

        ProcessAttributes attributes = ProcessAttributes.builder()
                .idWildcard((String) attributeMap.get("id"))
                .reportPath((String) document.getAttributes().get("report-path"))
                .templatesPath((String) document.getAttributes().get("templates-path"))
                .build();

        ROOT_STRUCT root = fillDataStructure(attributes); //TODO: generic from subclass

        if(root == null) {  //TODO: über boolean mit geben? --> Rest könnte zum Processor passende Fehlermeldung sein
            reader.pushInclude(fillNoResultTemplate(),
                    target,
                    "",
                    1,
                    attributeMap);
            return;
        } //TODO: maybe use noresult template for each subclass

        if (attributes.getTemplatesPath() != null) {
            String templateLocation = attributes.getTemplatesPath();
            templateLoader.setTemplateLocation(templateLocation);
            try {
                template = templateLoader.loadTemplate(templateName);
            } catch (Exception e) {
                logger.warn("No template with name " + templateName + " found at " + attributes.getTemplatesPath() + "! Defaulting to standard template for " + templateName);
                //System.out.println("No template with name " + templateName + " found at " + attributeMap.get("templates-path") + "! Defaulting to standard template for " + templateName);
            }
        }

        reader.pushInclude(fillTemplate(root),
                target,
                "",
                1,
                attributeMap);
    }

    /**
     * Fills the template with the content of root.
     * If called for first time the to templateName corresponding Template will be loaded and stored for subsequent calls
     *
     * @param root the data structure the template is filled with
     * @return the from template and root produced String
     */
    private String fillTemplate(Object root) {

        Writer writer = new StringWriter();

        try {
            template.process(root, writer);
        } catch (TemplateException | IOException e) {
            throw new RuntimeException(e);
        }

        return writer.toString();
    }

    /**
     * Fills the template with the content of root.
     * If called for first time the to templateName corresponding Template will be loaded and stored for subsequent calls
     *
     * @return the from template and root produced String
     */
    private String fillNoResultTemplate() {

        Writer writer = new StringWriter();

        try {
            templateLoader.loadTemplate("NoResult").process(null, writer);
        } catch (TemplateException | IOException e) {
            throw new RuntimeException(e);
        }

        return writer.toString();
    }

    /**
     * build the data structure needed to fill the template
     *
     * @param attributes give the attributes parsed into the include call in adoc
     * @return rootElement for data-structure
     */
    abstract ROOT_STRUCT fillDataStructure(ProcessAttributes attributes);
}
