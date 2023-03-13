package org.jqassistant.contrib.asciidoctorj.processors.pre;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.asciidoctor.ast.Document;
import org.asciidoctor.extension.Preprocessor;
import org.asciidoctor.extension.PreprocessorReader;
import org.jqassistant.contrib.asciidoctorj.freemarker.TemplateRepo;
import org.jqassistant.contrib.asciidoctorj.processors.attributes.ProcessAttributes;
import org.jqassistant.contrib.asciidoctorj.processors.attributes.ProcessAttributesFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

public class IconEnabler extends Preprocessor {
    String templateName = "IconEnabler";
    TemplateRepo templateRepo;

    public IconEnabler(TemplateRepo templateRepo) {
        this.templateRepo = templateRepo;
    }

    @Override
    public void process(Document document, PreprocessorReader reader) {
        ProcessAttributes attributes = ProcessAttributesFactory.createProcessAttributesPre(document);

        Writer writer = new StringWriter();
        Template template = templateRepo.findTemplate(attributes, templateName);

        try {
            template.process(null, writer);
        } catch (TemplateException e) {
            throw new RuntimeException("You're \"" + templateName + "\"-template seems to have an error in it's calls to the data structure! Refer to manual. " , e); //TODO: link zu manual
        } catch (IOException e) {
            throw new RuntimeException("You're \"" + templateName + "\"-template file can not be parsed to a freemarker template!" , e);
        }

        List<String> lines = reader.readLines();
        lines.add(0, writer.toString());
        reader.restoreLines(lines);
    }
}
