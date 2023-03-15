package org.jqassistant.contrib.asciidoctorj;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.Options;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IncludeProcessorTest {
    private static Asciidoctor asciidoctor;
    private static Options opt;

    @BeforeAll
    static void init() {
        asciidoctor = Asciidoctor.Factory.create();
        opt = Options.builder().attributes(
                Attributes.builder()
                        .attribute("jqassistant-templates-path", "testtemplates")
                        .attribute("jqassistant-report-path", "src/test/resources/testing-xml/test-report.xml")
                        .build())
                .build();
        //opt.setToDir(IncludeProcessorTest.class.getResource(IncludeProcessorTest.class.getSimpleName() + ".class").getPath().replace(IncludeProcessorTest.class.getSimpleName() + ".class", ""));
    }

    @Test
    void testRulesInclude() {
        //System.out.println(asciidoctor.convert("include::jQAssistant:Rules[concept = \"test-concept-e*\", constraint = \"*\"]", opt));
        //System.out.println(asciidoctor.convert("include::jQAssistant:Rules[concept = \"test-concept\"]", opt));

        String result = asciidoctor.convert("include::jQAssistant:Rules[concept = \"test-concept-e*\", constraint = \"*\"]", opt);
        result = assertIsPartOfAndShorten(result, "test-constraint");
        result = assertIsPartOfAndShorten(result, "Test description 2");
        result = assertIsPartOfAndShorten(result, "Status: <span class=\"red\">FAILURE</span>, Severity: MAJOR");
        result = assertIsPartOfAndShorten(result, "Column 1");
        result = assertIsPartOfAndShorten(result, "Column 2");
        result = assertIsPartOfAndShorten(result, "test-cell 11");
        result = assertIsPartOfAndShorten(result, "test-cell 12");
        result = assertIsPartOfAndShorten(result, "test-cell 21");
        result = assertIsPartOfAndShorten(result, "test-cell 22");
        result = assertIsPartOfAndShorten(result, "test-concept-empty-result");
        result = assertIsPartOfAndShorten(result, "Test description");
        assertIsPartOfAndShorten(result, "Status: <span class=\"green\">SUCCESS</span>, Severity: INFO");

        result = asciidoctor.convert("include::jQAssistant:Rules[concept = \"test-concept\"]", opt);

        result = assertIsPartOfAndShorten(result, "test-constraint");
        result = assertIsPartOfAndShorten(result, "test-concept");
        result = assertIsPartOfAndShorten(result, "Test description");
        result = assertIsPartOfAndShorten(result, "Status: <span class=\"green\">SUCCESS</span>, Severity: INFO");
        result = assertIsPartOfAndShorten(result, "<a href=\"https://youtu.be/dQw4w9WgXcQ\">CSV</a>");
        result = assertIsPartOfAndShorten(result, "<img src=\"test.jpeg\" alt=\"Ricki Boy\">");
        result = assertIsPartOfAndShorten(result, "Ricki Boy");
        result = assertIsPartOfAndShorten(result, "<img src=\"asciidoctorj/picture\" alt=\"other\">");
        assertIsPartOfAndShorten(result, "other");
    }

    @Test
    void testSummaryInclude() {
        //System.out.println(asciidoctor.convert("include::jQAssistant:Summary[concept = \"test-concept\", constraint = \"*\"]" , opt));

        String result = asciidoctor.convert("include::jQAssistant:Summary[concept = \"test-concept\", constraint = \"*\"]" , opt);

        result = assertIsPartOfAndShorten(result, "table");
        result = assertIsPartOfAndShorten(result, "Id");
        result = assertIsPartOfAndShorten(result, "Description");
        result = assertIsPartOfAndShorten(result, "Severity");
        result = assertIsPartOfAndShorten(result, "Status");
        result = assertIsPartOfAndShorten(result, "test-constraint");
        result = assertIsPartOfAndShorten(result, "Test description 2");
        result = assertIsPartOfAndShorten(result, "MAJOR");
        result = assertIsPartOfAndShorten(result, "<span class=\"red\">FAILURE</span>");
        result = assertIsPartOfAndShorten(result, "test-concept");
        result = assertIsPartOfAndShorten(result, "Test description");
        result = assertIsPartOfAndShorten(result, "INFO");
        result = assertIsPartOfAndShorten(result, "<span class=\"green\">SUCCESS</span>");
        assertIsPartOfAndShorten(result, "/table");
    }

    private String assertIsPartOfAndShorten(String text, String sequence) {
        assertThat(text).contains(sequence);
        return text.substring(text.indexOf(sequence)).substring(sequence.length());
    }
}
