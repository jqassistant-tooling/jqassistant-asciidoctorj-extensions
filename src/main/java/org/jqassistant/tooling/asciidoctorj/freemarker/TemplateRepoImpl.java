package org.jqassistant.tooling.asciidoctorj.freemarker;

import java.io.File;
import java.io.IOException;
import java.util.TimeZone;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import io.smallrye.common.constraint.NotNull;
import org.jqassistant.tooling.asciidoctorj.processors.attributes.ProcessAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateRepoImpl implements TemplateRepo {
    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateRepoImpl.class);

    Configuration cfg;
    TemplateLoader defaultLoader;

    public TemplateRepoImpl() {
        defaultLoader = new ClassTemplateLoader(getClass().getClassLoader(), "defaulttemplates");
        cfg = setupFreemarker();
    }

    @Override
    public Template findTemplate(@NotNull ProcessAttributes attributes, @NotNull String templateName) {
        if(!cfg.isTemplateLoaderExplicitlySet()) {
            String templatesPath = attributes.getTemplatesPath();
            if (templatesPath != null) {
                TemplateLoader fileTemplateLoader;
                try {
                    fileTemplateLoader = new FileTemplateLoader(new File(templatesPath), false);
                } catch (IOException e) {
                    throw new IllegalArgumentException("Cannot locate templates path '" + templatesPath + "'.", e);
                }
                MultiTemplateLoader mtl = new MultiTemplateLoader(new TemplateLoader[]{fileTemplateLoader, defaultLoader});
                cfg.setTemplateLoader(mtl);
                LOGGER.info("Template loading location set to {}. If template is not defined in this location, the extension will default to the respective default template.", templatesPath);
            }
            else {
                cfg.setTemplateLoader(defaultLoader);
                LOGGER.info("Template loading is set default templates. If you want to define your own templates, check the README.adoc for this extension");
            }
        }

        Template template;
        try {
            template = cfg.getTemplate(templateName);
        } catch (Exception e) {
            throw new IllegalStateException("No valid Template with name \"" + templateName + "\" found neither in custom template location nor in default template location");
        }
        LOGGER.debug("Template {} loaded", templateName);
        return template;
    }

    private Configuration setupFreemarker() {
        /*Setup Freemarker Configuration*/
        Configuration temporaryCfg = new Configuration(Configuration.VERSION_2_3_32);
        temporaryCfg.setDefaultEncoding("UTF-8");
        temporaryCfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        temporaryCfg.setLogTemplateExceptions(false);
        temporaryCfg.setWrapUncheckedExceptions(true);
        temporaryCfg.setFallbackOnNullLoopVariable(false);
        temporaryCfg.setSQLDateAndTimeTimeZone(TimeZone.getDefault());

        return temporaryCfg;
    }
}
