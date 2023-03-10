package org.jqassistant.contrib.asciidoctorj;

import org.jqassistant.contrib.asciidoctorj.processors.attributes.ProcessAttributes;
import org.jqassistant.contrib.asciidoctorj.reportrepo.ReportRepo;
import org.jqassistant.contrib.asciidoctorj.reportrepo.ReportRepoImpl;
import org.jqassistant.contrib.asciidoctorj.reportrepo.model.Concept;
import org.jqassistant.contrib.asciidoctorj.reportrepo.model.Constraint;
import org.jqassistant.contrib.asciidoctorj.xmlparsing.ParsedReport;
import org.jqassistant.contrib.asciidoctorj.xmlparsing.ReportParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

public class ReportRepoTest {
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
    void findConceptsByIdWildcard() {
        ProcessAttributes attributes1 = ProcessAttributes.builder().reportPath("mock-report").conceptIdFilter("Test*").build();

        assert(testRepo.findConcepts(attributes1).size() == 2);
        assert(testRepo.findConcepts(attributes1).containsAll(List.of(tce1, tce2)));
    }

    @Test
    void findConceptsById() {
        ProcessAttributes attributes2 = ProcessAttributes.builder().reportPath("mock-report").conceptIdFilter("TestConceptId2").constraintIdFilter("").build();

        assert(testRepo.findConcepts(attributes2).size() == 1);
        assert(testRepo.findConcepts(attributes2).contains(tce2));
        assert(testRepo.findConstraints(attributes2).size() == 0);
    }

    @Test
    void findConstraintsById() {
        ProcessAttributes attributes3 = ProcessAttributes.builder().reportPath("mock-report").constraintIdFilter("TestConstraintId").build();

        assert(testRepo.findConstraints(attributes3).size() == 1);
        assert(testRepo.findConstraints(attributes3).contains(tca1));
        assert(testRepo.findConcepts(attributes3).size() == 2);
    }
}
