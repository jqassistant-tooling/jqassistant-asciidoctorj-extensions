package org.jqassistant.contrib.asciidoctorj.processors.attributes;

import org.asciidoctor.ast.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Map;

public class ProcessAttributesFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessAttributesFactory.class);

    private static final String REPORT_PATH = "jqassistant-report-path";
    private static final String TEMPLATES_PATH = "jqassistant-templates-path";
    private static final List<String> OUTPUT_DIRS = List.of("to_dir", "outdir");

    private ProcessAttributesFactory() {}

    /**
     * creates a ProcessAttributes instance from Asciidoctor Document and additional attributes for IncludeProcessor
     * @param document the document passed into IncludeProcessor
     * @param attributeMap the attributes passed into IncludeProcessor
     * @return returns generated ProcessAttributes
     * @throws IllegalStateException if jqassistant-report-path not found or not a valid String or jqassistatn-templaes-path not a valid String
     */
    public static ProcessAttributes createProcessAttributesInclude(Document document, Map<String, Object> attributeMap) {
        ProcessAttributes.ProcessAttributesBuilder builder = ProcessAttributes.builder();

        fillReportPath(document, builder);
        fillTemplatesPath(document, builder);
        fillOutputPath(document, builder);

        return builder
                .conceptIdFilter((String) attributeMap.get("concept"))
                .constraintIdFilter((String) attributeMap.get("constraint"))
                .build();
    }

    /**
     * creates a ProcessAttributes instance from Asciidoctor Document for PreProcessor
     * @param document the document passed into Preprocessor
     * @return returns generated ProcessAttributes
     * @throws IllegalStateException if jqassistant-templaes-path not a valid String
     */
    public static ProcessAttributes createProcessAttributesPre(Document document) {
        ProcessAttributes.ProcessAttributesBuilder builder = ProcessAttributes.builder();

        fillTemplatesPath(document, builder);

        return builder.build();
    }

    private static void fillReportPath(Document document, ProcessAttributes.ProcessAttributesBuilder builder) {
        if(!(document.getAttributes().get(REPORT_PATH) instanceof String)) {
            throw new IllegalStateException("You're report xml file location isn't set properly! Please set the destination of you're jqassistant-report.xml via the global document attributes for you're asciidoctor.");
        }
        else {
            builder.reportPath((String) document.getAttributes().get(REPORT_PATH));
        }
    }

    private static void fillTemplatesPath(Document document, ProcessAttributes.ProcessAttributesBuilder builder) {
        if(document.getAttributes().get(TEMPLATES_PATH) != null && !(document.getAttributes().get(TEMPLATES_PATH) instanceof String)) {
            throw new IllegalStateException("You're templates folder location isn't a String! Please set the destination of you're template folder to a String via the global document attributes for you're asciidoctor. Or delete the attribute to use default templates. For questions, refer to the README.md");
        }
        else {
            builder.templatesPath((String) document.getAttributes().get(TEMPLATES_PATH));
        }
    }

    private static void fillOutputPath(Document document, ProcessAttributes.ProcessAttributesBuilder builder) {
        String optionsAttributesKey = "attributes";
        String outDirectory = "";

        for(String location : OUTPUT_DIRS) {
            if(document.getOptions().get(location) instanceof String) {
                outDirectory = (String) document.getOptions().get(location);
                break;
            }
            if (document.getOptions().get(optionsAttributesKey) instanceof Map && ((Map) document.getOptions().get(optionsAttributesKey)).get(location) instanceof String) {
                outDirectory = (String) ((Map) document.getOptions().get(optionsAttributesKey)).get(location);
                break;
            }
        }
        LOGGER.debug(outDirectory);
        if(!outDirectory.isEmpty()) {
            builder.outputDirectory(new File(outDirectory));
        }
        else {
            LOGGER.warn("Output directory neither found in document options nor in document option attributes! This should only occur during testing. Checked for \n {} \n in \n {} \n and \n {}", OUTPUT_DIRS, document.getOptions(), document.getOptions().get(optionsAttributesKey));
            builder.outputDirectory(new File(""));
        }
    }
}
