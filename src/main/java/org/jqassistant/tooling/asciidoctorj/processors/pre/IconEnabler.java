package org.jqassistant.tooling.asciidoctorj.processors.pre;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.smallrye.common.constraint.NotNull;
import org.asciidoctor.ast.Document;
import org.asciidoctor.extension.Preprocessor;
import org.asciidoctor.extension.PreprocessorReader;
import org.asciidoctor.extension.Reader;
import org.jqassistant.tooling.asciidoctorj.freemarker.TemplateRepo;
import org.jqassistant.tooling.asciidoctorj.processors.attributes.ProcessAttributes;
import org.jqassistant.tooling.asciidoctorj.processors.attributes.ProcessAttributesFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

public class IconEnabler extends Preprocessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(IconEnabler.class);
    String templateName = "IconEnabler";
    TemplateRepo templateRepo;

    public IconEnabler(@NotNull TemplateRepo templateRepo) {
        this.templateRepo = templateRepo;
    }

    @Override
    public Reader process(Document document, PreprocessorReader reader) {
        ProcessAttributes attributes = ProcessAttributesFactory.createProcessAttributesPre(document);

        Writer writer = new StringWriter();
        Template template = templateRepo.findTemplate(attributes, templateName);

        try {
            template.process(null, writer);
        } catch (TemplateException e) {
            throw new IllegalArgumentException("Your \"" + templateName + "\"-template seems to have an error in it's calls to the data structure! Refer to manual section \"Using the jqassistant-asciidoctor-extension\"." , e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Your \"" + templateName + "\"-template file can not be parsed to a freemarker template! Refer to manual section \"Using your own template\"." , e);
        }

        List<String> lines = reader.readLines();
        String[] addedLines = writer.toString().split("\n");
        lines.addAll(0, List.of(addedLines));
        reader.restoreLines(lines);

        LOGGER.debug("Added icon configuration to top of the adoc");
        return reader;
    }
}
