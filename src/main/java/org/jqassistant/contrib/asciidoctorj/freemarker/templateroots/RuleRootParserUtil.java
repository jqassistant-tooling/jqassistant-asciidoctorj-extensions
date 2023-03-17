package org.jqassistant.contrib.asciidoctorj.freemarker.templateroots;

import org.apache.commons.io.FileUtils;
import org.jqassistant.contrib.asciidoctorj.reportrepo.model.Reports;
import org.jqassistant.contrib.asciidoctorj.reportrepo.model.URLWithLabel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RuleRootParserUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(RuleRootParserUtil.class);

    private RuleRootParserUtil() {}

    /**
     * Parses all attachments from original reports to relative paths and copies the resources to the outputDirectory
     * @param reports the reports that will be parsed
     * @param outputDirectory the location the attachments are copied to
     * @return the parse report with adapted links
     */
    protected static Reports parseReports(Reports reports, File outputDirectory) {
        Reports.ReportsBuilder repBuilder = Reports.builder();

        for(URLWithLabel image : reports.getImages()) {
            repBuilder.image(URLWithLabel.builder().label(image.getLabel()).link(copyAttachmentAndRelativizePath(image.getLink(), outputDirectory)).build());
        }
        for(URLWithLabel link : reports.getLinks()) {
            repBuilder.link(URLWithLabel.builder().label(link.getLabel()).link(copyAttachmentAndRelativizePath(link.getLink(), outputDirectory)).build());
        }

        return repBuilder.build();
    }

    private static String copyAttachmentAndRelativizePath(String link, File outputDirectory){
        URI uri;
        try {
            uri = new URI(link);
        } catch (URISyntaxException e) {
            LOGGER.warn("Cannot create URI from {}", link);
            return link;
        }

        if(uri.getScheme() == null) {
            LOGGER.warn("URI '{}' has no scheme. JQA should not produce a URI without a scheme. Please contact developer!", uri);
            return link;
        }

        if(!uri.getScheme().equals("file")) {
            return link;
        }

        File file;
        Path path = Paths.get(uri);
        try {
            file = new File(uri);
        } catch (Exception e) {
            LOGGER.warn("Cannot find File from uri {}", uri);
            return outputDirectory.getAbsoluteFile().toPath().relativize(path).toString();
        }

        try {
            FileUtils.copyToDirectory(file, outputDirectory);
        } catch (IOException | IllegalArgumentException e) {
            LOGGER.warn("Cannot copy file {} to {}. Maybe the outputDirectory is assigned incorrectly", uri, outputDirectory);
            return outputDirectory.getAbsoluteFile().toPath().relativize(path).toString();
        }

        return file.getName();
    }
}
