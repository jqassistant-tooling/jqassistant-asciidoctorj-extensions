package org.jqassistant.contrib.asciidoctorj.processors.includes;

import freemarker.template.TemplateException;
import org.asciidoctor.ast.Document;
import org.asciidoctor.extension.IncludeProcessor;
import org.asciidoctor.extension.PreprocessorReader;
import org.jqassistant.contrib.asciidoctorj.freemarker.TemplateRepo;
import org.jqassistant.contrib.asciidoctorj.processors.attributes.ProcessAttributes;
import org.jqassistant.contrib.asciidoctorj.reportrepo.ReportRepo;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;


public abstract class AbstractIncludeProcessor<ROOT_STRUCT> extends IncludeProcessor {
    public static final String PREFIX = "jQAssistant:";

    ReportRepo repo;

    List<String> templateNames;
    TemplateRepo templateRepo;

    String target;


    public AbstractIncludeProcessor(ReportRepo reportRepository, TemplateRepo templateRepo, String target, List<String> templateNames) {
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
        //System.out.println(document.getAttributes().keySet().toString() + attributeMap.keySet());

        if(!(document.getAttributes().get("report-path") instanceof String)) {
            throw new IllegalStateException("You're report xml file location isn't set properly! Please set the destination of you're jqassistant-report.xml via the global document attributes for you're asciidoctor.");
        }
        if(document.getAttributes().get("templates-path") != null && !(document.getAttributes().get("templates-path") instanceof String)) {
            throw new IllegalStateException("You're templates folder location isn't a String! Please set the destination of you're template folder to a propper String via the global document attributes for you're asciidoctor.");
        }

        ProcessAttributes attributes = ProcessAttributes.builder()
                .conceptIdFilter((String) attributeMap.get("concept"))
                .constraintIdFilter((String) attributeMap.get("constraint"))
                .reportPath((String) document.getAttributes().get("report-path"))
                .templatesPath((String) document.getAttributes().get("templates-path"))
                .build();

        //System.out.println(attributes.getTemplatesPath());

        ROOT_STRUCT root = fillDataStructure(attributes); //TODO: generic from subclass

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
    private String fillTemplates(ROOT_STRUCT root, ProcessAttributes attributes) {
        Writer writer = new StringWriter();

        for (String tName : templateNames) {
            try {
                if(root == null) writer.append(fillNoResultTemplate(attributes));
                else templateRepo.findTemplate(attributes, tName).process(root, writer);
            } catch (TemplateException | IOException e) {
                throw new RuntimeException(e); //TODO: sinvolle Fehlermeldung
            }
        }

        return writer.toString();
    }

    /**
     * Fills the template with the content of root.
     * If called for first time the to templateName corresponding Template will be loaded and stored for subsequent calls
     * possible to overwrite in subclass to make specific no Result
     *
     * @return the from template and root produced String
     */
    private String fillNoResultTemplate(ProcessAttributes attributes) {
        Writer writer = new StringWriter();

        try {
            templateRepo.findTemplate(attributes, "NoResult").process(null, writer);
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
