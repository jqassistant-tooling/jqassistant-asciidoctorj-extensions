package org.jqassistant.contrib.asciidoctorj.processors.includes;

import freemarker.template.TemplateException;
import org.asciidoctor.ast.Document;
import org.asciidoctor.extension.IncludeProcessor;
import org.asciidoctor.extension.PreprocessorReader;
import org.jqassistant.contrib.asciidoctorj.freemarker.TemplateRepo;
import org.jqassistant.contrib.asciidoctorj.processors.attributes.ProcessAttributes;
import org.jqassistant.contrib.asciidoctorj.processors.attributes.ProcessAttributesFactory;
import org.jqassistant.contrib.asciidoctorj.reportrepo.ReportRepo;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class AbstractIncludeProcessor<T> extends IncludeProcessor {
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

        T root = fillDataStructure(attributes);

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
    private String fillTemplates(T root, ProcessAttributes attributes) {
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
                throw new RuntimeException("You're \"" + tName + "\"-template seems to have an error in it's calls to the data structure! Refer to manual. " , e); //TODO: link zu manual
            } catch (IOException e) {
                throw new RuntimeException("You're \"" + tName + "\"-template file can not be parsed to a freemarker template!" , e);
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
    abstract T fillDataStructure(ProcessAttributes attributes);
}
