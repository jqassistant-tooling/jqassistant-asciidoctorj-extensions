package org.jqassistant.tooling.asciidoctorj;

import org.jqassistant.tooling.asciidoctorj.processors.attributes.ProcessAttributes;
import org.jqassistant.tooling.asciidoctorj.reportrepo.ReportRepoImpl;
import org.jqassistant.tooling.asciidoctorj.reportrepo.model.Concept;
import org.jqassistant.tooling.asciidoctorj.reportrepo.model.Constraint;
import org.jqassistant.tooling.asciidoctorj.xmlparsing.ParsedReport;
import org.jqassistant.tooling.asciidoctorj.xmlparsing.ReportParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

class ReportRepoTest {
    private static ReportRepoImpl testRepo;

    private static Concept tce1, tce2;
    private static Constraint tca1, tca2;
    private static final String someExistingFile = "src/test/resources/jqassistant-report.xml";
    private static final String nonExistingFile = "non-existent-report.xml";

    @BeforeAll
    static void init() {
        tce1 = Concept.builder().id("TestConceptId1").status("failure").build();
        tce2 = Concept.builder().id("TestConceptId2").status("WARNING").build();
        tca1 = Constraint.builder().id("TestConstraintId1").status("Failure").build();
        tca2 = Constraint.builder().id("TestConstraintId2").status("SUCCESS").build();
    }

    @BeforeEach
    void setUp() {
        ReportParser parser = mock(ReportParser.class);
        ParsedReport parsedReport = new ParsedReport();

        parsedReport.addConcept(tce1);
        parsedReport.addConcept(tce2);
        parsedReport.addConstraint(tca1);
        parsedReport.addConstraint(tca2);
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
        ProcessAttributes attributes = ProcessAttributes.builder().reportPath(someExistingFile).constraintIdFilter("TestConstraintId1").build();

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

    //testing statusFilter-Behaviour in the following tests. Note: Default setting [WARNING,FAILURE] has been tested through the previous tests
    @Test
    void testValidStatusFilter() {
        ProcessAttributes attributes = ProcessAttributes.builder()
                .reportPath(someExistingFile)
                .statusFilter("success, SKIPPED")
                .build();

        assertThat(testRepo.findConstraints(attributes)).hasSize(1).contains(tca2);
    }

    @Test
    void testInvalidStatusFilter() {
        ProcessAttributes attributes = ProcessAttributes.builder()
                .reportPath(someExistingFile)
                .statusFilter("INVALID_STATUS")
                .build();

        assertThatThrownBy(() -> testRepo.findConstraints(attributes))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Invalid status 'INVALID_STATUS' provided in status filter. Allowed values  are: [SUCCESS, SKIPPED, FAILURE, WARNING]");
    }

    @Test
    void testEmptyStatusFilter() {
        ProcessAttributes attributes = ProcessAttributes.builder()
                .reportPath(someExistingFile)
                .statusFilter("")
                .build();

        assertThat(testRepo.findConcepts(attributes).toArray()).hasSize(2);
        assertThat(testRepo.findConstraints(attributes).toArray()).hasSize(1).contains(tca1);
    }
}
