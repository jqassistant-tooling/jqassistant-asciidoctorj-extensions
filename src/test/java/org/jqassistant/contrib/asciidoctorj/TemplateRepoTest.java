package org.jqassistant.contrib.asciidoctorj;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.jqassistant.contrib.asciidoctorj.freemarker.TemplateRepo;
import org.jqassistant.contrib.asciidoctorj.freemarker.TemplateRepoImpl;
import org.jqassistant.contrib.asciidoctorj.processors.attributes.ProcessAttributes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.TimeZone;

class TemplateRepoTest {
    private static TemplateRepo repo;
    private static Configuration cfg;

    private static ProcessAttributes attributes;

    @BeforeAll
    static void init() {
        cfg = setupFreemarker();
    }

    @Test
    void testDefaultLoading() throws IOException {
        repo = new TemplateRepoImpl();

        attributes = ProcessAttributes.builder().build();
        setLoadingDestination("src/main/resources/defaulttemplates");

        Template templateLoaded = repo.findTemplate(attributes, "RulesConcept");
        Template templateExpected = cfg.getTemplate("RulesConcept");
        assert(templateExpected.toString().equals(templateLoaded.toString()));
    }

    @Test
    void testCustomLoadingAndFallback() throws IOException {
        repo = new TemplateRepoImpl();

        //test custom loading
        attributes = ProcessAttributes.builder().templatesPath("testing-custom-templates").build();
        setLoadingDestination("src/test/resources/testing-custom-templates");

        Template templateLoaded = repo.findTemplate(attributes, "IconEnabler");
        Template templateExpected = cfg.getTemplate("IconEnabler");
        assert(templateExpected.toString().equals(templateLoaded.toString()));

        //test fallback if custom does not exist
        setLoadingDestination("src/main/resources/defaulttemplates");

        templateLoaded = repo.findTemplate(attributes, "Summary");
        templateExpected = cfg.getTemplate("Summary");
        assert(templateExpected.toString().equals(templateLoaded.toString()));
    }

    private static Configuration setupFreemarker() {
        /*Setup Freemarker Configuration*/
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);
        cfg.setSQLDateAndTimeTimeZone(TimeZone.getDefault());

        return cfg;
    }

    private static void setLoadingDestination(String path) {
        try {
            cfg.setDirectoryForTemplateLoading(new File(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
