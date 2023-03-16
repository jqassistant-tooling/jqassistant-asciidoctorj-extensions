package org.jqassistant.contrib.asciidoctorj;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.extension.JavaExtensionRegistry;
import org.asciidoctor.jruby.extension.spi.ExtensionRegistry;
import org.jqassistant.contrib.asciidoctorj.freemarker.TemplateRepo;
import org.jqassistant.contrib.asciidoctorj.freemarker.TemplateRepoImpl;
import org.jqassistant.contrib.asciidoctorj.processors.includes.Rules;
import org.jqassistant.contrib.asciidoctorj.processors.includes.Summary;
import org.jqassistant.contrib.asciidoctorj.processors.pre.IconEnabler;
import org.jqassistant.contrib.asciidoctorj.reportrepo.ReportRepo;
import org.jqassistant.contrib.asciidoctorj.reportrepo.ReportRepoImpl;
import org.jqassistant.contrib.asciidoctorj.xmlparsing.ReportParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtensionsLoader implements ExtensionRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtensionsLoader.class);

    @Override
    public void register(Asciidoctor asciidoctor) {
        LOGGER.info("creating empty report and template repos");
        ReportRepo reportRepository = new ReportRepoImpl(ReportParser.getInstance());
        TemplateRepo templateRepo = new TemplateRepoImpl();

        JavaExtensionRegistry javaExtensionRegistry = asciidoctor.javaExtensionRegistry();

        LOGGER.info("creating and registering Processors");
        javaExtensionRegistry.preprocessor(new IconEnabler(templateRepo));
        LOGGER.info("registered {}", IconEnabler.class);
        javaExtensionRegistry.includeProcessor(new Summary(reportRepository, templateRepo));
        LOGGER.info("registered {}", Summary.class);
        javaExtensionRegistry.includeProcessor(new Rules(reportRepository, templateRepo));
        LOGGER.info("registered {}", Rules.class);
    }
}
