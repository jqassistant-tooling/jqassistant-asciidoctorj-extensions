package org.jqassistant.contrib.asciidoctorj.freemarker;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.jqassistant.contrib.asciidoctorj.processors.attributes.ProcessAttributes;

import java.util.TimeZone;

public class TemplateRepoImpl implements TemplateRepo {

    Configuration cfg;
    TemplateLoader customLoader;
    TemplateLoader defaultLoader;

    public TemplateRepoImpl() {
        defaultLoader = new ClassTemplateLoader(getClass().getClassLoader(), "defaulttemplates");
        cfg = setupFreemarker();
    }

    @Override
    public Template findTemplate(ProcessAttributes attributes, String templateName) {
        if(!cfg.isTemplateLoaderExplicitlySet()) {
            if(attributes.getTemplatesPath() != null) {
                customLoader = new ClassTemplateLoader(getClass().getClassLoader(), attributes.getTemplatesPath());
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
            throw new IllegalStateException("No valid Template with name \"" + templateName + "\" found neither in custom template location nor in default template location");
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
