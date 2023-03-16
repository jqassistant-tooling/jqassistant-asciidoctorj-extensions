package org.jqassistant.contrib.asciidoctorj;

import org.jqassistant.contrib.asciidoctorj.processors.attributes.ProcessAttributes;
import org.jqassistant.contrib.asciidoctorj.reportrepo.model.*;
import org.jqassistant.contrib.asciidoctorj.xmlparsing.ParsedReport;
import org.jqassistant.contrib.asciidoctorj.xmlparsing.ReportParser;
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
        assertThat(report.getConcepts().keySet().size()).isEqualTo(2);
        assertThat(report.getConcepts()).containsKey("test-concept");
        assertThat(report.getConcepts()).containsKey("test-concept-empty-result");

        assertThat(report.getConstraints().keySet().size()).isEqualTo(1);
        assertThat(report.getConstraints()).containsKey("test-constraint");
    }

    @Test
    void testConceptStructure() {
        Concept testConcept = report.getConcepts().get("test-concept");
        assertThat(testConcept.getDescription()).isEqualTo("Test description");
        assertThat(testConcept.getSeverity()).isEqualTo("info");
        assertThat(testConcept.getStatus()).isEqualTo("success");
        assertThat(testConcept.getDuration()).isEqualTo(140);

        Result result = testConcept.getResult();
        assertThat(result.getColumnKeys().size()).isEqualTo(1);
        assertThat(result.getColumnKeys().get(0)).isEqualTo("Column 1");
        assertThat(result.getRows().size()).isEqualTo(1);
        assertThat(result.getRows().get(0)).isEqualTo(Map.of("Column 1", "test-cell"));

        Reports reports = testConcept.getReports();
        assertThat (reports.getImages().size()).isEqualTo(2);
        assertThat (reports.getImages().get(0).getLabel()).isEqualTo("Ricki Boy");
        assertThat (reports.getImages().get(1).getLabel()).isEqualTo("other");
        assertThat (reports.getImages().get(0).getLink()).isEqualTo("test.jpeg");
        assertThat (reports.getImages().get(1).getLink()).isEqualTo("asciidoctorj/picture");

        assertThat(reports.getLinks().size()).isEqualTo(1);
        assertThat (reports.getLinks().get(0).getLabel()).isEqualTo("CSV");
        assertThat (reports.getLinks().get(0).getLink()).isEqualTo("https://youtu.be/dQw4w9WgXcQ");
    }

    @Test
    void testGroupStructure() {
        assertThat(report.getGroups().size()).isEqualTo(2);
        assertThat(report.getGroups().get("test-group").getId()).isEqualTo("test-group");
        assertThat(report.getGroups().get("test-group 2").getId()).isEqualTo("test-group 2");
        assertThat(report.getGroups().get("test-group").getDuration()).isEqualTo(140);
        assertThat(report.getGroups().get("test-group").getSubGroups().size()).isEqualTo(0);
        assertThat(report.getGroups().get("test-group").getNestedConstraints().size()).isEqualTo(0);
        assertThat(report.getGroups().get("test-group").getNestedConcepts().size()).isEqualTo(1);
        assertThat(report.getGroups().get("test-group").getNestedConcepts().get(0).getId()).isEqualTo("test-concept");
    }

    @Test
    void parseEmptyResultAndReportConcept() {
        Concept testConcept = report.getConcepts().get("test-concept-empty-result");
        assert (testConcept.getDescription().equals("Test description"));
        assert (testConcept.getSeverity().equals("info"));
        assert (testConcept.getStatus().equals("success"));
        assert (testConcept.getDuration() == 140);

        assert (testConcept.getResult() == Result.EMPTY_RESULT);
        assert (testConcept.getReports() == Reports.EMPTY_REPORTS);
    }

    @Test
    void parseResultConstraint() {
        Constraint testConstraint = report.getConstraints().get("test-constraint");
        assert (testConstraint.getDescription().equals("Test description 2"));
        assert (testConstraint.getSeverity().equals("major"));
        assert (testConstraint.getStatus().equals("failure"));
        assert (testConstraint.getDuration() == 221);

        assert (testConstraint.getReports() == Reports.EMPTY_REPORTS);

        Result result = testConstraint.getResult();
        assert (result.getColumnKeys().size() == 2);
        assert (result.getColumnKeys().get(0).equals("Column 1"));
        assert (result.getColumnKeys().get(1).equals("Column 2"));

        assert (result.getRows().size() == 2);
        assert (result.getRows().get(0).equals(Map.of("Column 1", "test-cell 11", "Column 2", "test-cell 12")));
        assert (result.getRows().get(1).equals(Map.of("Column 1", "test-cell 21", "Column 2", "test-cell 22")));
    }
}
