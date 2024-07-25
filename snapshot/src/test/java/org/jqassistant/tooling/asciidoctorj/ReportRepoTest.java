package org.jqassistant.tooling.asciidoctorj;

import org.jqassistant.tooling.asciidoctorj.processors.attributes.ProcessAttributes;
import org.jqassistant.tooling.asciidoctorj.reportrepo.ReportRepo;
import org.jqassistant.tooling.asciidoctorj.reportrepo.ReportRepoImpl;
import org.jqassistant.tooling.asciidoctorj.reportrepo.model.Concept;
import org.jqassistant.tooling.asciidoctorj.reportrepo.model.Constraint;
import org.jqassistant.tooling.asciidoctorj.xmlparsing.ParsedReport;
import org.jqassistant.tooling.asciidoctorj.xmlparsing.ReportParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ReportRepoTest {
    private static ReportRepo testRepo;

    private static Concept tce1, tce2;
    private static Constraint tca1;

    @BeforeAll
    static void init() {
        ReportParser parser = mock(ReportParser.class);
        ParsedReport parsedReport = new ParsedReport();

        tce1 = Concept.builder().id("TestConceptId").build();
        tce2 = Concept.builder().id("TestConceptId2").build();
        tca1 = Constraint.builder().id("TestConstraintId").build();

        parsedReport.addConcept(tce1);
        parsedReport.addConcept(tce2);
        parsedReport.addConstraint(tca1);
        when(parser.parseReportXml("mock-report")).thenReturn(parsedReport);

        testRepo = new ReportRepoImpl(parser);
    }

    @Test
    void testFindConceptsByIdWildcard() {
        ProcessAttributes attributes1 = ProcessAttributes.builder().reportPath("mock-report").conceptIdFilter("Test*").build();

        assertThat(testRepo.findConcepts(attributes1).toArray()).hasSize(2);
        assertThat(testRepo.findConcepts(attributes1).toArray()).containsAll(List.of(tce1, tce2));
    }

    @Test
    void testFindConceptsById() {
        ProcessAttributes attributes2 = ProcessAttributes.builder().reportPath("mock-report").conceptIdFilter("TestConceptId2").constraintIdFilter("").build();

        assertThat (testRepo.findConcepts(attributes2).toArray()).hasSize(1);
        assertThat (testRepo.findConcepts(attributes2).toArray()).contains(tce2);
        assertThat (testRepo.findConstraints(attributes2).toArray()).isEmpty();
    }

    @Test
    void testFindConstraintsById() {
        ProcessAttributes attributes3 = ProcessAttributes.builder().reportPath("mock-report").constraintIdFilter("TestConstraintId").build();

        assertThat (testRepo.findConstraints(attributes3).toArray()).hasSize(1);
        assertThat (testRepo.findConstraints(attributes3).toArray()).contains(tca1);
        assertThat (testRepo.findConcepts(attributes3).toArray()).hasSize(2);
    }
}
