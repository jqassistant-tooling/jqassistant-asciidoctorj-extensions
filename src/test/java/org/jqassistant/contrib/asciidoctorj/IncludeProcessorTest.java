package org.jqassistant.contrib.asciidoctorj;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.Options;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class IncludeProcessorTest {
    static Asciidoctor asciidoctor;
    static Options opt;

    @BeforeAll
    static void init() {
        asciidoctor = Asciidoctor.Factory.create();
        opt = Options.builder().attributes(Attributes.builder().attribute("jqassistant-templates-path", "testtemplates").attribute("jqassistant-report-path", "src/test/resources/testing-xml/test-report.xml").build()).build();
    }

    @Test
    void testRulesInclude() throws IOException {
        //System.out.println(asciidoctor.convert("include::jQAssistant:Rules[concept = \"test-concept-e*\", constraint = \"*\"]", opt));
        //System.out.println(asciidoctor.convert("include::jQAssistant:Rules[concept = \"test-concept\"]", opt));
        assert(Files.readString(Paths.get("src/test/resources/testing-xml-convert-results/IncludeProcessorTest-expected-res1.html")).equals(asciidoctor.convert("include::jQAssistant:Rules[concept = \"test-concept-e*\", constraint = \"*\"]", opt)));
        assert(Files.readString(Paths.get("src/test/resources/testing-xml-convert-results/IncludeProcessorTest-expected-res2.html")).equals(asciidoctor.convert("include::jQAssistant:Rules[concept = \"test-concept\"]", opt)));
    }

    @Test
    void testSummaryInclude() throws IOException {
        //System.out.println(asciidoctor.convert("include::jQAssistant:Summary[concept = \"test-concept\", constraint = \"*\"]" , opt));
        assert(Files.readString(Paths.get("src/test/resources/testing-xml-convert-results/IncludeProcessorTest-expected-res3.html")).equals(asciidoctor.convert("include::jQAssistant:Summary[concept = \"test-concept\", constraint = \"*\"]" , opt)));
    }
}
