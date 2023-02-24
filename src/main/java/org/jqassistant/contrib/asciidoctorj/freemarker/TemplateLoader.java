package org.jqassistant.contrib.asciidoctorj.freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.File;
import java.io.IOException;
import java.util.TimeZone;

public class TemplateLoader {

    private final Configuration cfg;

    public TemplateLoader() {
        cfg = setupFreemarker();
    }

    public Template loadTemplate(String templateName) {

        Template template;

        try {
            template = cfg.getTemplate(templateName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return template;
    }

    public Configuration setupFreemarker() {
        /*Setup Freemarker Configuration*/
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
        try {
            cfg.setDirectoryForTemplateLoading(new File("src/main/resources/defaulttemplates")); //TODO: change path relative to resource fodler
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);
        cfg.setSQLDateAndTimeTimeZone(TimeZone.getDefault());

        return cfg;
    }

    public void setTemplateLocation(String templateLocation) {
        try {
            cfg.setDirectoryForTemplateLoading(new File(templateLocation)); //TODO: change path relative to resource fodler
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
