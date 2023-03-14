package org.jqassistant.contrib.asciidoctorj.processors.attributes;

import org.asciidoctor.ast.Document;

import java.util.Map;

public class ProcessAttributesFactory {
    private static final String REPORT_PATH = "jqassistant-report-path";
    private static final String TEMPLATES_PATH = "jqassistant-templates-path";

    private ProcessAttributesFactory() {}

    /**
     * creates a ProcessAttributes instance from Asciidoctor Document and additional attributes for IncludeProcessor
     * @return returns generated ProcessAttributes
     * @throws IllegalStateException if jqassistant-report-path not found or not a valid String or jqassistatn-templaes-path not a valid String
     */
    public static ProcessAttributes createProcessAttributesInclude(Document document, Map<String, Object> attributeMap) {
        checkReportPathValidity(document);
        checkTemplatesPathValidity(document);

        return ProcessAttributes.builder()
                .conceptIdFilter((String) attributeMap.get("concept"))
                .constraintIdFilter((String) attributeMap.get("constraint"))
                .reportPath((String) document.getAttributes().get(REPORT_PATH))
                .templatesPath((String) document.getAttributes().get(TEMPLATES_PATH))
                .build();
    }

    /**
     * creates a ProcessAttributes instance from Asciidoctor Document for PreProcessor
     * @return returns generated ProcessAttributes
     * @throws IllegalStateException if jqassistant-templaes-path not a valid String
     */
    public static ProcessAttributes createProcessAttributesPre(Document document) {
        checkTemplatesPathValidity(document);

        return ProcessAttributes.builder()
                .templatesPath((String) document.getAttributes().get(TEMPLATES_PATH))
                .build();
    }

    private static void checkReportPathValidity(Document document) throws IllegalStateException{
        if(!(document.getAttributes().get(REPORT_PATH) instanceof String)) {
            throw new IllegalStateException("You're report xml file location isn't set properly! Please set the destination of you're jqassistant-report.xml via the global document attributes for you're asciidoctor.");
        }
    }

    private static void checkTemplatesPathValidity(Document document) {
        if(document.getAttributes().get(TEMPLATES_PATH) != null && !(document.getAttributes().get(TEMPLATES_PATH) instanceof String)) {
            throw new IllegalStateException("You're templates folder location isn't a String! Please set the destination of you're template folder to a String via the global document attributes for you're asciidoctor. Or delete the attribute to use default templates");
        }
    }
}
