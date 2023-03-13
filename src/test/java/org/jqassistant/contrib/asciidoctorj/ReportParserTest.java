package org.jqassistant.contrib.asciidoctorj;

import org.jqassistant.contrib.asciidoctorj.processors.attributes.ProcessAttributes;
import org.jqassistant.contrib.asciidoctorj.reportrepo.model.*;
import org.jqassistant.contrib.asciidoctorj.xmlparsing.ParsedReport;
import org.jqassistant.contrib.asciidoctorj.xmlparsing.ReportParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

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
        assert (report.getConcepts().keySet().size() == 2);
        assert (report.getConcepts().containsKey("test-concept"));
        assert (report.getConcepts().containsKey("test-concept-empty-result"));

        assert (report.getConstraints().keySet().size() == 1);
        assert (report.getConstraints().containsKey("test-constraint-empty-result"));
    }

    @Test
    void testConceptStructure() {
        Concept testConcept = report.getConcepts().get("test-concept");
        assert (testConcept.getDescription().equals("Test description"));
        assert (testConcept.getSeverity().equals("info"));
        assert (testConcept.getStatus().equals("success"));
        assert (testConcept.getDuration() == 140);

        Result result = testConcept.getResult();
        assert (result.getColumnKeys().size() == 1 && result.getColumnKeys().get(0).equals("Column 1"));
        assert (result.getRows().size() == 1 && result.getRows().get(0).equals(Map.of("Column 1", "test-cell")));

        Reports reports = testConcept.getReports();
        assert (reports.getImages().size() == 2);
        assert (reports.getImages().get(0).getLabel().equals("Ricki Boy"));
        assert (reports.getImages().get(1).getLabel().equals("other"));
        assert (reports.getImages().get(0).getLink().equals("test.jpeg"));
        assert (reports.getImages().get(1).getLink().equals("link/to/picture"));
        assert (reports.getLinks().size() == 1);
        assert (reports.getLinks().get(0).getLabel().equals("CSV"));
        assert (reports.getLinks().get(0).getLink().equals("https://youtu.be/dQw4w9WgXcQ"));
    }

    @Test
    void testGroupStructure() {
        assert (report.getGroups().size() == 2);
        assert (report.getGroups().get("test-group").getId().equals("test-group"));
        assert (report.getGroups().get("test-group 2").getId().equals("test-group 2"));
        assert (report.getGroups().get("test-group").getDuration() == 140);
        assert (report.getGroups().get("test-group").getSubGroups().size() == 0);
        assert (report.getGroups().get("test-group").getNestedConstraints().size() == 0);
        assert (report.getGroups().get("test-group").getNestedConcepts().size() == 1 && report.getGroups().get("test-group").getNestedConcepts().get(0).getId().equals("test-concept"));
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
        Constraint testConstraint = report.getConstraints().get("test-constraint-empty-result");
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
