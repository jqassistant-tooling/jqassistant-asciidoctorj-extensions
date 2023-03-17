package org.jqassistant.contrib.asciidoctorj;

import org.jqassistant.contrib.asciidoctorj.freemarker.templateroots.RuleRoot;
import org.jqassistant.contrib.asciidoctorj.freemarker.templateroots.RulesRoot;
import org.jqassistant.contrib.asciidoctorj.reportrepo.model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

class RulesRootTest {

    private static Constraint tca2;

    private static RulesRoot rulesRoot;

    @BeforeAll
    static void init() throws URISyntaxException {
        Result res = Result.builder().columnKeys(List.of("Col1", "Col2"))
                .row(Map.of("Col1", "Cell11", "Col2", "Cell12"))
                .row(Map.of("Col1", "Cell21", "Col2", "Cell22"))
                .build();
        Reports reps = Reports.builder().link(URLWithLabel.builder().label("test link").link("https://youtu.be").build())
                .image(URLWithLabel.builder().label("test image").link("file:" + new File(RulesRootTest.class.getResource(RulesRootTest.class.getSimpleName() + ".class").toURI()).getAbsolutePath()).build())
                .build();

        Concept tce1 = Concept.builder().id("TestConceptId").description("Test Description")
                .status("success").severity("info").duration(512)
                .result(Result.EMPTY_RESULT).reports(reps).build();
        Concept tce2 = Concept.builder().id("TestConceptId 2.1").description("Test Description 2")
                .status("failure").severity("info").duration(1451)
                .result(Result.EMPTY_RESULT).reports(Reports.EMPTY_REPORTS).build();
        Concept tce3 = Concept.builder().id("TestConceptId 2").description("Test Description 2")
                .status("failure").severity("info").duration(1451)
                .result(Result.EMPTY_RESULT).reports(Reports.EMPTY_REPORTS).build();
        Constraint tca1 = Constraint.builder().id("TestConstraintId").description("Test Description 3")
                .status("warning").severity("minor").duration(24)
                .result(res).reports(Reports.EMPTY_REPORTS).build();
        tca2 = Constraint.builder().id("TestConstraintId 2").description("Test Description 4")
                .status("failure").severity("major").duration(444)
                .result(res).reports(reps).build();

        rulesRoot = RulesRoot.builder()
                .concept(RuleRoot.createRuleRoot(tce1, new File("")))
                .concept(RuleRoot.createRuleRoot(tce2, new File("")))
                .concept(RuleRoot.createRuleRoot(tce3, new File("")))
                .constraint(RuleRoot.createRuleRoot(tca2, new File(IncludeProcessorTest.class.getResource(IncludeProcessorTest.class.getSimpleName() + ".class").toURI())))
                .constraint(RuleRoot.createRuleRoot(tca1, new File("")))
                .build();
    }

    @Test
    void testSortingInRulesRoot() {
        assert (rulesRoot.getConcepts().size() == 3);
        assert (rulesRoot.getConcepts().first().getId().equals("TestConceptId 2"));
        assert (rulesRoot.getConcepts().last().getId().equals("TestConceptId"));

        assert (rulesRoot.getConstraints().size() == 2);
        assert (rulesRoot.getConstraints().first().getId().equals("TestConstraintId 2"));
        assert (rulesRoot.getConstraints().last().getId().equals("TestConstraintId"));
    }

    @Test
    void testRuleToRuleRoot() {
        RuleRoot root = rulesRoot.getConstraints().first();

        assert (root.getId().equals(tca2.getId()));
        assert (root.getDescription().equals(tca2.getDescription()));
        assert (root.getStatus().equals(tca2.getStatus().toUpperCase()));
        assert (root.getSeverity().equals(tca2.getSeverity().toUpperCase()));

        assert (root.isHasResult());
        assert (root.isHasReports());

        assert (root.getResultColumnKeys().equals(tca2.getResult().getColumnKeys()));
        assert (root.getResultRows().get(0).equals(List.of("Cell11", "Cell12")));
        assert (root.getResultRows().get(1).equals(List.of("Cell21", "Cell22")));
    }

    @Test
    void testImageAndLinkPaths() throws URISyntaxException {
        RuleRoot root = rulesRoot.getConstraints().first();

        URLWithLabel image = root.getReports().getImages().get(0);
        assert (image.getLabel().equals("test image"));
        assert (image.getLink().contains("RulesRootTest.class"));
        //System.out.println(new File(IncludeProcessorTest.class.getResource(IncludeProcessorTest.class.getSimpleName() + ".class").toURI()));
        //System.out.println(new File(RulesRootTest.class.getResource(RulesRootTest.class.getSimpleName() + ".class").toURI()).getAbsolutePath());
        //assert (!image.getLink().contains("asciidoctorj"));
        assert (!image.getLink().contains(new File(RulesRootTest.class.getResource(RulesRootTest.class.getSimpleName() + ".class").toURI()).getParentFile().getName()));

        URLWithLabel link = root.getReports().getLinks().get(0);
        assert (link.getLabel().equals("test link"));
        assert (link.getLink().equals("https://youtu.be"));

        root = rulesRoot.getConcepts().last();

        image = root.getReports().getImages().get(0);
        assert (image.getLabel().equals("test image"));
        //System.out.println(image.getLink() + " " + new File(RulesRootTest.class.getResource(RulesRootTest.class.getSimpleName() + ".class").getPath()).toString().replace(new File("").getAbsolutePath(), ""));
        assert (image.getLink().contains(RulesRootTest.class.getSimpleName() + ".class"));
        assert (image.getLink().contains(new File(RulesRootTest.class.getResource(RulesRootTest.class.getSimpleName() + ".class").toURI()).getParentFile().getName()));

        link = root.getReports().getLinks().get(0);
        assert (link.getLabel().equals("test link"));
        assert (link.getLink().equals("https://youtu.be"));
    }

    @Test
    void testHasResultAndReports() {
        assert (rulesRoot.getConstraints().last().isHasResult());
        assert (!rulesRoot.getConstraints().last().isHasReports());

        assert (!rulesRoot.getConcepts().last().isHasResult());
        assert (rulesRoot.getConcepts().last().isHasReports());

        assert (!rulesRoot.getConcepts().first().isHasResult());
        assert (!rulesRoot.getConcepts().first().isHasReports());
    }

/*    private boolean areRuleRootsEqual(RuleRoot r1, RuleRoot r2) {
        if (!r1.getId().equals(r2.getId())) return false;
        if (!r1.getDescription().equals(r2.getDescription())) return false;
        if (!r1.getStatus().equals(r2.getStatus())) return false;
        if (!r1.getSeverity().equals(r2.getSeverity())) return false;

        if (r1.isHasResult() != r2.isHasResult()) return false;
        else if (r1.isHasResult()) {
            if (!r1.getResultColumnKeys().equals(r2.getResultColumnKeys())) return false;
            if (!r1.getResultRows().equals(r2.getResultRows())) return false;
        }

        if (r1.isHasReports() != r2.isHasReports()) return false;
        else if (r1.isHasReports()) {
            if (r1.getReports() != r2.getReports()) return false;
        }

        return true;
    } */
}
