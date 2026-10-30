package org.jqassistant.tooling.asciidoctorj;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.Options;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IncludeProcessorTest {
    private static Asciidoctor asciidoctor;
    private static Options opt;

    @BeforeAll
    static void init() {
        opt = Options.builder()
                .attributes(Attributes.builder()
                        .attribute("jqassistant-report-path", "src/test/resources/testing-xml/test-report.xml")
                        .build())
                .build();
    }

    @BeforeEach
    void setUp() {
        asciidoctor = Asciidoctor.Factory.create();
    }

    @AfterEach
    void tearDown() {
        if (asciidoctor != null) {
            asciidoctor.shutdown();
        }
    }

    @Test
    void testTemplatesRules() {
        String rules = asciidoctor.convert("include::jQAssistant:Rules[concepts = \"*\", constraints = \"*\", status = \"SUCCESS, SKIPPED, FAILURE, WARNING\"]",
                opt);

        rules = assertIsPartOfAndShorten(rules, "FAILURE");
        rules = assertIsPartOfAndShorten(rules, "Test description 2");

        rules = assertIsPartOfAndShorten(rules, "Column 1");
        rules = assertIsPartOfAndShorten(rules, "Column 2");
        rules = assertIsPartOfAndShorten(rules, "test-column 11");
        rules = assertIsPartOfAndShorten(rules, "test-column 12");
        rules = assertIsPartOfAndShorten(rules, "test-column 21");
        rules = assertIsPartOfAndShorten(rules, "test-column 22");

        //verifying display of Hidden Findings
        rules = assertIsPartOfAndShorten(rules, "Baseline (1)");
        rules = assertIsPartOfAndShorten(rules, "table");
        rules = assertIsPartOfAndShorten(rules, "Column 1");
        rules = assertIsPartOfAndShorten(rules, "Column 2");
        rules = assertIsPartOfAndShorten(rules, "test-column 51 (base)");
        rules = assertIsPartOfAndShorten(rules, "test-column 52 (base)");
        rules = assertIsPartOfAndShorten(rules, "table");

        rules = assertIsPartOfAndShorten(rules, "Suppressions (2)");
        rules = assertIsPartOfAndShorten(rules, "table");
        rules = assertIsPartOfAndShorten(rules, "Column 1");
        rules = assertIsPartOfAndShorten(rules, "Column 2");
        //verifying correct display of Suppression Metadata
        rules = assertIsPartOfAndShorten(rules, "Reason");
        rules = assertIsPartOfAndShorten(rules, "Until");
        rules = assertIsPartOfAndShorten(rules, "test-column 31 (supp)");
        rules = assertIsPartOfAndShorten(rules, "test-column 32 (supp)");
        rules = assertIsPartOfAndShorten(rules, "Example suppression");
        rules = assertIsPartOfAndShorten(rules, "2050-12-31");
        rules = assertIsPartOfAndShorten(rules, "test-column 41 (suppW/oM)");
        rules = assertIsPartOfAndShorten(rules, "test-column 42 (suppW/oM)");
        rules = assertIsPartOfAndShorten(rules, "table");

        rules = assertIsPartOfAndShorten(rules, "test-constraint | MAJOR");

        rules = assertIsPartOfAndShorten(rules, "test-concept-empty-result");
        rules = assertIsPartOfAndShorten(rules, "SUCCESS");
        rules = assertIsPartOfAndShorten(rules, "Test description");
        assertIsPartOfAndShorten(rules, "test-concept-empty-result | INFO");
    }

    @Test
    void testTemplateSummary() {
        String summary = asciidoctor.convert(
                "include::jQAssistant:Summary[concepts = \"*\", constraints = \"*\", status = \"SUCCESS, SKIPPED, FAILURE, WARNING\"]", opt);
        //table with visible findings
        summary = assertIsPartOfAndShorten(summary, "table");
        summary = assertIsPartOfAndShorten(summary, "Status");
        summary = assertIsPartOfAndShorten(summary, "Severity");
        summary = assertIsPartOfAndShorten(summary, "Count");
        summary = assertIsPartOfAndShorten(summary, "Rule");

        summary = assertIsPartOfAndShorten(summary, "🌧");
        summary = assertIsPartOfAndShorten(summary, "✘");
        summary = assertIsPartOfAndShorten(summary, "⚑");
        summary = assertIsPartOfAndShorten(summary, "MAJOR");
        summary = assertIsPartOfAndShorten(summary, "2");
        summary = assertIsPartOfAndShorten(summary, "(3)");
        summary = assertIsPartOfAndShorten(summary, "test-constraint");

        summary = assertIsPartOfAndShorten(summary, "☀");
        summary = assertIsPartOfAndShorten(summary, "✔");
        summary = assertIsPartOfAndShorten(summary, "INFO");
        summary = assertIsPartOfAndShorten(summary, "1");
        summary = assertIsPartOfAndShorten(summary, "test-concept");

        summary = assertIsPartOfAndShorten(summary, "☀");
        summary = assertIsPartOfAndShorten(summary, "✔");
        summary = assertIsPartOfAndShorten(summary, "INFO");
        summary = assertIsPartOfAndShorten(summary, "test-concept-empty-result");
        summary = assertIsPartOfAndShorten(summary, "table");

        summary = assertIsPartOfAndShorten(summary, "Legend");
        summary = assertIsPartOfAndShorten(summary, "🌧 Violations Found | ☀ No Violations Found");
        summary = assertIsPartOfAndShorten(summary, "✘ Failure | \uD83D\uDF85 Warning | ✔ Success");
        summary = assertIsPartOfAndShorten(summary, "(Result evaluation according to warn-on-severity/fail-on-severity thresholds)");
        summary = assertIsPartOfAndShorten(summary, "⚑ Hidden Rows");

        assertIsPartOfAndShorten(summary, "(Baseline or Suppression)");
    }

    private String assertIsPartOfAndShorten(String text, String sequence) {
        assertThat(text).contains(sequence);
        return text.substring(text.indexOf(sequence))
                .substring(sequence.length());
    }
}
