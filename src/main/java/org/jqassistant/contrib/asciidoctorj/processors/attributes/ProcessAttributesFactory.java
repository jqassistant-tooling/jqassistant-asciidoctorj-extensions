package org.jqassistant.contrib.asciidoctorj.processors.attributes;

import org.asciidoctor.ast.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

public class ProcessAttributesFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessAttributesFactory.class);

    private static final String REPORT_PATH = "jqassistant-report-path";
    private static final String TEMPLATES_PATH = "jqassistant-templates-path";
    private static final String OUTPUT_DIR = "to_dir";

    private ProcessAttributesFactory() {}

    /**
     * creates a ProcessAttributes instance from Asciidoctor Document and additional attributes for IncludeProcessor
     * @return returns generated ProcessAttributes
     * @throws IllegalStateException if jqassistant-report-path not found or not a valid String or jqassistatn-templaes-path not a valid String
     */
    public static ProcessAttributes createProcessAttributesInclude(Document document, Map<String, Object> attributeMap) {
        ProcessAttributes.ProcessAttributesBuilder builder = ProcessAttributes.builder();

        fillReportPathValidity(document, builder);
        fillTemplatesPathValidity(document, builder);
        fillOutputPathValidity(document, builder);

        //System.out.println(document.getAttributes());
        return builder
                .conceptIdFilter((String) attributeMap.get("concept"))
                .constraintIdFilter((String) attributeMap.get("constraint"))
                .build();
    }

    /**
     * creates a ProcessAttributes instance from Asciidoctor Document for PreProcessor
     * @return returns generated ProcessAttributes
     * @throws IllegalStateException if jqassistant-templaes-path not a valid String
     */
    public static ProcessAttributes createProcessAttributesPre(Document document) {
        ProcessAttributes.ProcessAttributesBuilder builder = ProcessAttributes.builder();

        fillTemplatesPathValidity(document, builder);

        return builder.build();
    }

    private static void fillReportPathValidity(Document document, ProcessAttributes.ProcessAttributesBuilder builder) {
        if(!(document.getAttributes().get(REPORT_PATH) instanceof String)) {
            throw new IllegalStateException("You're report xml file location isn't set properly! Please set the destination of you're jqassistant-report.xml via the global document attributes for you're asciidoctor.");
        }
        else {
            builder.reportPath((String) document.getAttributes().get(REPORT_PATH));
        }
    }

    private static void fillTemplatesPathValidity(Document document, ProcessAttributes.ProcessAttributesBuilder builder) {
        if(document.getAttributes().get(TEMPLATES_PATH) != null && !(document.getAttributes().get(TEMPLATES_PATH) instanceof String)) {
            throw new IllegalStateException("You're templates folder location isn't a String! Please set the destination of you're template folder to a String via the global document attributes for you're asciidoctor. Or delete the attribute to use default templates. For questions, refer to the README.md");
        }
        else {
            builder.templatesPath((String) document.getAttributes().get(TEMPLATES_PATH));
        }
    }

    private static void fillOutputPathValidity(Document document, ProcessAttributes.ProcessAttributesBuilder builder) {
        //System.out.println(((Map) document.getOptions().get("attributes")).get("to_dir"));
        //System.out.println(document.getAttributes());
        //System.out.println(document.getOptions());
        if(!(document.getOptions().get(OUTPUT_DIR) instanceof String)) {
            LOGGER.warn("You're output directory isn't a String! This should only occur during testing.");
            builder.outputDirectory(new File(""));
        }
        else {
            builder.outputDirectory(new File((String) document.getOptions().get(OUTPUT_DIR)));
        }
    }
}
