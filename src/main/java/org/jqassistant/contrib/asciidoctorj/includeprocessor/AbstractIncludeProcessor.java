package org.jqassistant.contrib.asciidoctorj.includeprocessor;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.asciidoctor.ast.Document;
import org.asciidoctor.extension.IncludeProcessor;
import org.asciidoctor.extension.PreprocessorReader;
import org.jqassistant.contrib.asciidoctorj.freemarker.TemplateLoader;
import org.jqassistant.contrib.asciidoctorj.reportrepo.ReportRepo;
import org.jqassistant.contrib.asciidoctorj.xmlparsing.ReportParser;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

public abstract class AbstractIncludeProcessor extends IncludeProcessor {

    ReportRepo repo;

    ReportParser parser;

    TemplateLoader templateLoader;
    Template template;

    String target;
    String templateName;


    public AbstractIncludeProcessor(ReportRepo reportRepository, ReportParser parser, TemplateLoader templateLoader, String target, String templateName) {
        this.repo = reportRepository;
        this.parser = parser;
        this.templateLoader = templateLoader;
        this.template = templateLoader.loadTemplate(templateName);
        this.target = target;
        this.templateName = templateName;
    }

    /**
     * Fills the template with the content of root.
     * If called for first time the to templateName corresponding Template will be loaded and stored for subsequent calls
     *
     * @param root the data structure the template is filled with
     * @return the from template and root produced String
     */
    protected String fillTemplate(Object root) {

        Writer writer = new StringWriter();

        try {
            template.process(root, writer);
        } catch (TemplateException | IOException e) {
            throw new RuntimeException(e);
        }

        return writer.toString();
    }

    @Override
    public boolean handles(String target) {
        return this.target.equals(target);
    }

    @Override
    public void process(Document document, PreprocessorReader reader, String target, Map<String, Object> attributes) {
        if (!repo.isInitialized())
            parser.parseReportXml(repo, attributes.get("report_path").toString()); //TODO: error handling for "report_path" not available

        if (attributes.containsKey("template_location")) { //TODO: Use different name?
            String templateLocation = (String) attributes.get("template_location");
            templateLoader.setTemplateLocation(templateLocation);
            try {
                template = templateLoader.loadTemplate(templateName);
            } catch (Exception e) {
                //TODO: log that at specified position template x is missing or that the directory does not exist
            }
        }

        Object root = fillDataStructure(attributes);

        reader.pushInclude(fillTemplate(root),
                target,
                "",
                1,
                attributes);
    }

    abstract Object fillDataStructure(Map<String, Object> attributes);
}
