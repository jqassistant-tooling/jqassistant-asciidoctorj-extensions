package org.jqassistant.tooling.asciidoctorj;

import org.assertj.core.api.Assertions;
import org.jqassistant.tooling.asciidoctorj.processors.attributes.ProcessAttributes;
import org.jqassistant.tooling.asciidoctorj.xmlparsing.ParsedReport;
import org.jqassistant.tooling.asciidoctorj.xmlparsing.ReportParser;
import org.jqassistant.tooling.asciidoctorj.reportrepo.model.Concept;
import org.jqassistant.tooling.asciidoctorj.reportrepo.model.Constraint;
import org.jqassistant.tooling.asciidoctorj.reportrepo.model.Reports;
import org.jqassistant.tooling.asciidoctorj.reportrepo.model.Result;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ReportParserTest {
    private static ParsedReport report;

    @BeforeAll
    static void init() {
        ReportParser parser = ReportParser.getInstance();
        ProcessAttributes attributes = ProcessAttributes.builder()
                .reportPath("src/test/resources/testing-xml/test-report.xml")
                .build();
        report = parser.parseReportXml(attributes.getReportPath());
    }

    @Test
    void testRepoStructure() {
        assertThat(report.getConcepts().keySet()).hasSize(2);
        Assertions.assertThat(report.getConcepts()).containsKey("test-concept");
        Assertions.assertThat(report.getConcepts()).containsKey("test-concept-empty-result");

        assertThat(report.getConstraints().keySet()).hasSize(1);
        Assertions.assertThat(report.getConstraints()).containsKey("test-constraint");
    }

    @Test
    void testConceptStructure() {
        Concept testConcept = report.getConcepts().get("test-concept");
        assertThat(testConcept.getDescription()).isEqualTo("Test description");
        assertThat(testConcept.getSeverity()).isEqualTo("info");
        assertThat(testConcept.getStatus()).isEqualTo("success");
        assertThat(testConcept.getDuration()).isEqualTo(140);

        Result result = testConcept.getResult();
        assertThat(result.getColumnKeys()).hasSize(1);
        assertThat(result.getColumnKeys().get(0)).isEqualTo("Column 1");
        assertThat(result.getRows()).hasSize(1);
        assertThat(result.getRows().get(0)).isEqualTo(Map.of("Column 1", "test-cell"));

        Reports reports = testConcept.getReports();
        assertThat (reports.getImages()).hasSize(2);
        assertThat (reports.getImages().get(0).getLabel()).isEqualTo("Ricki Boy");
        assertThat (reports.getImages().get(1).getLabel()).isEqualTo("other");
        assertThat (reports.getImages().get(0).getLink()).isEqualTo("test.jpeg");
        assertThat (reports.getImages().get(1).getLink()).isEqualTo("asciidoctorj/picture");

        assertThat(reports.getLinks()).hasSize(1);
        assertThat (reports.getLinks().get(0).getLabel()).isEqualTo("CSV");
        assertThat (reports.getLinks().get(0).getLink()).isEqualTo("https://youtu.be/dQw4w9WgXcQ");
    }

    @Test
    void testGroupStructure() {
        Assertions.assertThat(report.getGroups()).hasSize(2);
        Assertions.assertThat(report.getGroups().get("test-group").getId()).isEqualTo("test-group");
        Assertions.assertThat(report.getGroups().get("test-group 2").getId()).isEqualTo("test-group 2");
        Assertions.assertThat(report.getGroups().get("test-group").getDuration()).isEqualTo(140);
        Assertions.assertThat(report.getGroups().get("test-group").getSubGroups()).isEmpty();
        Assertions.assertThat(report.getGroups().get("test-group").getNestedConstraints()).isEmpty();
        Assertions.assertThat(report.getGroups().get("test-group").getNestedConcepts()).hasSize(1);
        Assertions.assertThat(report.getGroups().get("test-group").getNestedConcepts().get(0).getId()).isEqualTo("test-concept");
    }

    @Test
    void parseEmptyResultAndReportConcept() {
        Concept testConcept = report.getConcepts().get("test-concept-empty-result");
        assertThat(testConcept.getDescription()).isEqualTo("Test description");
        assertThat(testConcept.getSeverity()).isEqualTo("info");
        assertThat(testConcept.getStatus()).isEqualTo("success");
        assertThat(testConcept.getDuration()).isEqualTo(140);

        assertThat(testConcept.getResult()).isEqualTo(Result.EMPTY_RESULT);
        assertThat(testConcept.getReports()).isEqualTo(Reports.EMPTY_REPORTS);
    }

    @Test
    void parseResultConstraint() {
        Constraint testConstraint = report.getConstraints().get("test-constraint");
        assertThat(testConstraint.getDescription()).isEqualTo("Test description 2");
        assertThat (testConstraint.getSeverity()).isEqualTo("major");
        assertThat (testConstraint.getStatus()).isEqualTo("failure");
        assertThat (testConstraint.getDuration()).isEqualTo(221);

        assertThat (testConstraint.getReports()).isEqualTo(Reports.EMPTY_REPORTS);

        Result result = testConstraint.getResult();
        assertThat (result.getColumnKeys()).hasSize(2);
        assertThat (result.getColumnKeys().get(0)).isEqualTo("Column 1");
        assertThat (result.getColumnKeys().get(1)).isEqualTo("Column 2");

        assertThat (result.getRows()).hasSize(2);
        assertThat (result.getRows().get(0)).isEqualTo(Map.of("Column 1", "test-cell 11", "Column 2", "test-cell 12"));
        assertThat (result.getRows().get(1)).isEqualTo(Map.of("Column 1", "test-cell 21", "Column 2", "test-cell 22"));
    }
}
