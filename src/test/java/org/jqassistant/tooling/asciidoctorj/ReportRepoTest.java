package org.jqassistant.tooling.asciidoctorj;

import org.jqassistant.tooling.asciidoctorj.processors.attributes.ProcessAttributes;
import org.jqassistant.tooling.asciidoctorj.reportrepo.ReportRepo;
import org.jqassistant.tooling.asciidoctorj.reportrepo.ReportRepoImpl;
import org.jqassistant.tooling.asciidoctorj.reportrepo.model.Concept;
import org.jqassistant.tooling.asciidoctorj.reportrepo.model.Constraint;
import org.jqassistant.tooling.asciidoctorj.reportrepo.model.Group;
import org.jqassistant.tooling.asciidoctorj.xmlparsing.ParsedReport;
import org.jqassistant.tooling.asciidoctorj.xmlparsing.ReportParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

class ReportRepoTest {
    private static ReportRepoImpl testRepo;

    private static Concept tce1, tce2;
    private static Constraint tca1;
    private static final String someExistingFile = "src/test/resources/jqassistant-report.xml";
    private static final String nonExistingFile = "non-existent-report.xml";

    @BeforeAll
    static void init() {
        tce1 = Concept.builder().id("TestConceptId").build();
        tce2 = Concept.builder().id("TestConceptId2").build();
        tca1 = Constraint.builder().id("TestConstraintId").build();
    }

    @BeforeEach
    void setUp() {
        ReportParser parser = mock(ReportParser.class);
        ParsedReport parsedReport = new ParsedReport();

        parsedReport.addConcept(tce1);
        parsedReport.addConcept(tce2);
        parsedReport.addConstraint(tca1);
        when(parser.parseReportXml(someExistingFile)).thenReturn(parsedReport);

        testRepo = new ReportRepoImpl(parser);
    }

    @Test
    void testFindConceptsByIdWildcard() {
        ProcessAttributes attributes = ProcessAttributes.builder().reportPath(someExistingFile).conceptIdFilter("Test*").build();

        assertThat(testRepo.findConcepts(attributes).toArray()).hasSize(2);
        assertThat(testRepo.findConcepts(attributes).toArray()).containsAll(List.of(tce1, tce2));
    }

    @Test
    void testFindConceptsById() {
        ProcessAttributes attributes = ProcessAttributes.builder().reportPath(someExistingFile).conceptIdFilter("TestConceptId2").constraintIdFilter("").build();

        assertThat (testRepo.findConcepts(attributes).toArray()).hasSize(1);
        assertThat (testRepo.findConcepts(attributes).toArray()).contains(tce2);
        assertThat (testRepo.findConstraints(attributes).toArray()).isEmpty();
    }

    @Test
    void testFindConstraintsById() {
        ProcessAttributes attributes = ProcessAttributes.builder().reportPath(someExistingFile).constraintIdFilter("TestConstraintId").build();

        assertThat (testRepo.findConstraints(attributes).toArray()).hasSize(1);
        assertThat (testRepo.findConstraints(attributes).toArray()).contains(tca1);
        assertThat (testRepo.findConcepts(attributes).toArray()).hasSize(2);
    }
    @Test
    void testMissingReportXML() {
        ProcessAttributes attributes = ProcessAttributes.builder().reportPath(nonExistingFile).build();

        assertThat(testRepo.findConcepts(attributes).toArray()).isEmpty();
        assertThat(testRepo.findConstraints(attributes)).isEmpty();
        assertThat(testRepo.getGroups()).isEmpty();

    }
}
