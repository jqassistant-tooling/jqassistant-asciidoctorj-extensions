package org.jqassistant.tooling.asciidoctorj;

import org.apache.commons.io.FileUtils;
import org.jqassistant.tooling.asciidoctorj.freemarker.templateroots.RuleRoot;
import org.jqassistant.tooling.asciidoctorj.freemarker.templateroots.RuleRootParser;
import org.jqassistant.tooling.asciidoctorj.freemarker.templateroots.RulesRoot;
import org.jqassistant.tooling.asciidoctorj.processors.attributes.ProcessAttributes;
import org.jqassistant.tooling.asciidoctorj.reportrepo.model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class RulesRootTest {

    private static ProcessAttributes attributes;

    private static Constraint tca2;

    private static RulesRoot rulesRoot;

    @BeforeAll
    static void init() throws URISyntaxException {
        attributes = ProcessAttributes.builder()
                .outputDirectory(Paths.get(RulesRootTest.class.getResource("/testattachments/it_CSVReport.csv").toURI()).getParent().getParent().resolve("testoutputdirectory").toFile())
                .imagesDirectory(Paths.get(RulesRootTest.class.getResource("/testattachments/it_CSVReport.csv").toURI()).getParent().getParent().resolve("testoutputdirectory").resolve("images").toFile())
                .build();

        Result res = Result.builder().columnKeys(List.of("Col1", "Col2"))
                .row(Map.of("Col1", "Cell11", "Col2", "Cell12"))
                .row(Map.of("Col1", "Cell21", "Col2", "Cell22"))
                .build();
        Reports reps = Reports.builder().link(URLWithLabel.builder().label("test link").link("https://youtu.be").build())
                .image(URLWithLabel.builder().label("test image").link(RulesRootTest.class.getResource("/testattachments/it_ToBeContextMapReport.svg").toString()).build())
                .build();
        Reports reps2 = Reports.builder().link(URLWithLabel.builder().label("test csv").link(RulesRootTest.class.getResource("/testattachments/it_CSVReport.csv").toString()).build())
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
        Concept tce4 = Concept.builder().id("TestConceptId 4").description("Test Description 5")
                .status("success").severity("info").duration(69)
                .result(Result.EMPTY_RESULT).reports(reps2).build();
        Constraint tca1 = Constraint.builder().id("TestConstraintId").description("Test Description 3")
                .status("warning").severity("minor").duration(42)
                .result(res).reports(Reports.EMPTY_REPORTS).build();
        tca2 = Constraint.builder().id("TestConstraintId 2").description("Test Description 4")
                .status("failure").severity("major").duration(444)
                .result(res).reports(reps).build();

        rulesRoot = RulesRoot.builder()
                .concept(RuleRootParser.createRuleRoot(tce1, new File(""),  new File("")))
                .concept(RuleRootParser.createRuleRoot(tce2, new File(""),  new File("")))
                .concept(RuleRootParser.createRuleRoot(tce3, new File(""),  new File("")))
                .concept(RuleRootParser.createRuleRoot(tce4, attributes.getOutputDirectory(),  attributes.getImagesDirectory()))
                .constraint(RuleRootParser.createRuleRoot(tca2, attributes.getOutputDirectory(),  attributes.getImagesDirectory()))
                .constraint(RuleRootParser.createRuleRoot(tca1, new File(""),  new File("")))
                .build();
    }

    @Test
    void testSortingInRulesRoot() {
        assertThat(rulesRoot.getConcepts().toArray()).hasSize(4);
        assertThat(rulesRoot.getConcepts().first().getId()).isEqualTo("TestConceptId 2");
        assertThat(rulesRoot.getConcepts().last().getId()).isEqualTo("TestConceptId 4");

        assertThat(rulesRoot.getConstraints().toArray()).hasSize(2);
        assertThat(rulesRoot.getConstraints().first().getId()).isEqualTo("TestConstraintId 2");
        assertThat(rulesRoot.getConstraints().last().getId()).isEqualTo("TestConstraintId");
    }

    @Test
    void testRuleToRuleRoot() {
        RuleRoot root = rulesRoot.getConstraints().first();

        assertThat (root.getId()).isEqualTo(tca2.getId());
        assertThat (root.getDescription()).isEqualTo(tca2.getDescription());
        assertThat (root.getStatus()).isEqualTo(tca2.getStatus().toUpperCase());
        assertThat (root.getSeverity()).isEqualTo(tca2.getSeverity().toUpperCase());

        assertThat (root.isHasResult()).isTrue();
        assertThat (root.isHasReports()).isTrue();

        assertThat (root.getResultColumnKeys()).isEqualTo(tca2.getResult().getColumnKeys());
        assertThat (root.getResultRows().get(0)).isEqualTo(List.of("Cell11", "Cell12"));
        assertThat (root.getResultRows().get(1)).isEqualTo(List.of("Cell21", "Cell22"));
    }

    @Test
    void testImageAndLinkPaths() {
        RuleRoot root = rulesRoot.getConstraints().first();

        URLWithLabel image = root.getReports().getImages().get(0);
        assertThat (image.getLabel()).isEqualTo("test image");
        assertThat (image.getLink()).isEqualTo("it_ToBeContextMapReport.svg");

        URLWithLabel link = root.getReports().getLinks().get(0);
        assertThat (link.getLabel()).isEqualTo("test link");
        assertThat (link.getLink()).isEqualTo("https://youtu.be");

        root = rulesRoot.getConcepts().last();

        link = root.getReports().getLinks().get(0);
        assertThat (link.getLabel()).isEqualTo("test csv");
        assertThat (link.getLink()).isEqualTo("it_CSVReport.csv");
    }

    @Test
    void testHasResultAndReports() {
        assertThat (rulesRoot.getConstraints().last().isHasResult()).isTrue();
        assertThat (rulesRoot.getConstraints().last().isHasReports()).isFalse();

        assertThat (rulesRoot.getConcepts().last().isHasResult()).isFalse();
        assertThat (rulesRoot.getConcepts().last().isHasReports()).isTrue();

        assertThat (rulesRoot.getConcepts().first().isHasResult()).isFalse();
        assertThat (rulesRoot.getConcepts().first().isHasReports()).isFalse();
    }


    @Test
    void testCopyingOfResources() throws IOException {
        Reports reps = Reports.builder().link(URLWithLabel.builder().label("test csv").link(RulesRootTest.class.getResource("/testattachments/it_CSVReport.csv").toString()).build())
                .image(URLWithLabel.builder().label("test image").link(RulesRootTest.class.getResource("/testattachments/it_ToBeContextMapReport.svg").toString()).build())
                .build();
        Concept tce4 = Concept.builder().id("TestConceptId 4").description("Test Description 5")
                .status("success").severity("major").duration(69)
                .result(Result.EMPTY_RESULT).reports(reps).build();

        FileUtils.forceMkdir(attributes.getOutputDirectory());

        RuleRootParser.createRuleRoot(tce4, attributes.getOutputDirectory(),  attributes.getImagesDirectory());

        assertThat(Paths.get(attributes.getOutputDirectory().toURI()).resolve("it_CSVReport.csv").toFile()).isFile();
        assertThat(Paths.get(attributes.getImagesDirectory().toURI()).resolve("it_ToBeContextMapReport.svg").toFile()).isFile();
    }
}
