package org.jqassistant.contrib.asciidoctorj.freemarker;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.jqassistant.contrib.asciidoctorj.processors.attributes.ProcessAttributes;

import java.io.File;
import java.io.IOException;
import java.util.TimeZone;

public class TemplateRepoImpl implements TemplateRepo {

    Configuration cfg;
    TemplateLoader customLoader;
    TemplateLoader defaultLoader;

    public TemplateRepoImpl() {
        try {
            defaultLoader = new FileTemplateLoader(new File("src/main/resources/defaulttemplates"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        cfg = setupFreemarker();
    }

    @Override
    public Template findTemplate(ProcessAttributes attributes, String templateName) {
        if(!cfg.isTemplateLoaderExplicitlySet()) {
            if(attributes.getTemplatesPath() != null) {
                customLoader = new ClassTemplateLoader(getClass(), attributes.getTemplatesPath());
                MultiTemplateLoader mtl = new MultiTemplateLoader(new TemplateLoader[] {customLoader, defaultLoader});
                cfg.setTemplateLoader(mtl);
            }
            else {
                cfg.setTemplateLoader(defaultLoader);
            }
        }

        try {
            return cfg.getTemplate(templateName);
        } catch (Exception e) {
            throw new IllegalStateException("Template \"" + templateName + "\" couldn't neither be found in custom Template location nor in default Template location");
        }
    }

    private Configuration setupFreemarker() {
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
}
