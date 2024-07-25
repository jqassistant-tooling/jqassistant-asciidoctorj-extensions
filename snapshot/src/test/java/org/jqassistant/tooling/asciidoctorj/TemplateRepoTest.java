package org.jqassistant.tooling.asciidoctorj;

import java.io.File;
import java.io.IOException;
import java.util.TimeZone;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.jqassistant.tooling.asciidoctorj.freemarker.TemplateRepo;
import org.jqassistant.tooling.asciidoctorj.freemarker.TemplateRepoImpl;
import org.jqassistant.tooling.asciidoctorj.processors.attributes.ProcessAttributes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class TemplateRepoTest {

    private static Configuration cfg;

    @BeforeAll
    static void init() throws IOException {
        cfg = setupFreemarker();
    }

    @Test
    void defaultLoading() throws IOException {
        TemplateRepo repo = new TemplateRepoImpl();

        Template template = repo.findTemplate(ProcessAttributes.builder().build(), "RulesConcept");
        Template templateExpected = cfg.getTemplate("RulesConcept");
        assertThat(templateExpected).hasToString(template.toString());
    }

    @Test
    void customLoading() throws IOException {
        TemplateRepo repo = new TemplateRepoImpl();

        //test custom loading
        ProcessAttributes attributes = ProcessAttributes.builder().templatesPath("src/test/resources/testing-custom-templates").build();
        Template template = repo.findTemplate(attributes, "IconEnabler");

        assertThat(template).isNotNull();
        Template templateExpected = cfg.getTemplate("IconEnabler");
        assertThat(templateExpected).doesNotHaveToString(template.toString());
    }

    @Test
    void customLoadingAndFallback() throws IOException {
        TemplateRepo repo = new TemplateRepoImpl();

        Template templateLoaded = repo.findTemplate(ProcessAttributes.builder().build(), "Summary");

        Template templateExpected = cfg.getTemplate("Summary");
        assertThat(templateExpected).hasToString(templateLoaded.toString());
    }

    /**
     * Creates a configuration to load the default templates, used for assertions.
     */
    private static Configuration setupFreemarker() throws IOException {
        /*Setup Freemarker Configuration*/
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);
        cfg.setSQLDateAndTimeTimeZone(TimeZone.getDefault());
        cfg.setDirectoryForTemplateLoading(new File("src/main/resources/defaulttemplates"));
        return cfg;
    }
}
