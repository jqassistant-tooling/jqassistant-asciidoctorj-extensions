package org.jqassistant.contrib.asciidoctorj.processors.pre;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.asciidoctor.ast.Document;
import org.asciidoctor.extension.Preprocessor;
import org.asciidoctor.extension.PreprocessorReader;
import org.jqassistant.contrib.asciidoctorj.freemarker.TemplateRepo;
import org.jqassistant.contrib.asciidoctorj.processors.attributes.ProcessAttributes;

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
        if(document.getAttributes().get("templates-path") != null && !(document.getAttributes().get("templates-path") instanceof String)) {
            throw new IllegalStateException("You're templates folder location isn't a String! Please set the destination of you're template folder to a propper String via the global document attributes for you're asciidoctor.");
        }
        ProcessAttributes attributes = ProcessAttributes.builder()
                .templatesPath((String) document.getAttributes().get("templates-path"))
                .build();

        Writer writer = new StringWriter();
        Template template = templateRepo.findTemplate(attributes, templateName);

        try {
            template.process(null, writer);
        } catch (TemplateException | IOException e) {
            throw new RuntimeException(e); //TODO: sinvolle Fehlermeldung
        }

        List<String> lines = reader.readLines();
        lines.add(0, writer.toString());
        reader.restoreLines(lines);
    }
}
