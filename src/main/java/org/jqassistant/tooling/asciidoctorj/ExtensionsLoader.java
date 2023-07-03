package org.jqassistant.tooling.asciidoctorj;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.extension.JavaExtensionRegistry;
import org.asciidoctor.jruby.extension.spi.ExtensionRegistry;
import org.jqassistant.tooling.asciidoctorj.freemarker.TemplateRepo;
import org.jqassistant.tooling.asciidoctorj.freemarker.TemplateRepoImpl;
import org.jqassistant.tooling.asciidoctorj.processors.includes.Rules;
import org.jqassistant.tooling.asciidoctorj.processors.includes.Summary;
import org.jqassistant.tooling.asciidoctorj.processors.pre.IconEnabler;
import org.jqassistant.tooling.asciidoctorj.reportrepo.ReportRepo;
import org.jqassistant.tooling.asciidoctorj.reportrepo.ReportRepoImpl;
import org.jqassistant.tooling.asciidoctorj.xmlparsing.ReportParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtensionsLoader implements ExtensionRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtensionsLoader.class);

    @Override
    public void register(Asciidoctor asciidoctor) {
        LOGGER.debug("creating empty report and template repos");
        ReportRepo reportRepository = new ReportRepoImpl(ReportParser.getInstance());
        TemplateRepo templateRepo = new TemplateRepoImpl();

        JavaExtensionRegistry javaExtensionRegistry = asciidoctor.javaExtensionRegistry();

        LOGGER.debug("creating and registering Processors");
        javaExtensionRegistry.preprocessor(new IconEnabler(templateRepo));
        String registeredMessage = "registered {}";
        LOGGER.debug(registeredMessage, IconEnabler.class);
        javaExtensionRegistry.includeProcessor(new Summary(reportRepository, templateRepo));
        LOGGER.debug(registeredMessage, Summary.class);
        javaExtensionRegistry.includeProcessor(new Rules(reportRepository, templateRepo));
        LOGGER.debug(registeredMessage, Rules.class);
    }
}
