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

public class ExtensionsLoader implements ExtensionRegistry {
    @Override
    public void register(Asciidoctor asciidoctor) {

        ReportRepo reportRepository = new ReportRepoImpl(ReportParser.getInstance());
        TemplateRepo templateRepo = new TemplateRepoImpl();

        JavaExtensionRegistry javaExtensionRegistry = asciidoctor.javaExtensionRegistry();

        javaExtensionRegistry.preprocessor(new IconEnabler(templateRepo));
        javaExtensionRegistry.includeProcessor(new Summary(reportRepository, templateRepo));
        javaExtensionRegistry.includeProcessor(new Rules(reportRepository, templateRepo));
    }
}
