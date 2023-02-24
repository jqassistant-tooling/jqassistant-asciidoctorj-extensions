package org.jqassistant.contrib.asciidoctorj;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.extension.JavaExtensionRegistry;
import org.asciidoctor.jruby.extension.spi.ExtensionRegistry;
import org.jqassistant.contrib.asciidoctorj.freemarker.TemplateLoader;
import org.jqassistant.contrib.asciidoctorj.includeprocessor.ConceptsAndConstraints;
import org.jqassistant.contrib.asciidoctorj.includeprocessor.SummaryTable;
import org.jqassistant.contrib.asciidoctorj.reportrepo.ReportRepo;
import org.jqassistant.contrib.asciidoctorj.reportrepo.ReportRepoImpl;
import org.jqassistant.contrib.asciidoctorj.xmlparsing.ReportParser;

public class ExtensionsLoader implements ExtensionRegistry {
    @Override
    public void register(Asciidoctor asciidoctor) {

        ReportRepo reportRepository = new ReportRepoImpl();
        ReportParser parser = ReportParser.getInstance();
        TemplateLoader templateLoader = new TemplateLoader();

        JavaExtensionRegistry javaExtensionRegistry = asciidoctor.javaExtensionRegistry();

        javaExtensionRegistry.includeProcessor(new SummaryTable(reportRepository, parser, templateLoader));
        javaExtensionRegistry.includeProcessor(new ConceptsAndConstraints(reportRepository, parser, templateLoader));
    }
}
