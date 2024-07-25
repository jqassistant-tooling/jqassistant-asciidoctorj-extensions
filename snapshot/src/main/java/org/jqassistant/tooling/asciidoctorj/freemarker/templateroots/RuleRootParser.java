package org.jqassistant.tooling.asciidoctorj.freemarker.templateroots;

import io.smallrye.common.constraint.NotNull;
import org.apache.commons.io.FileUtils;
import org.jqassistant.tooling.asciidoctorj.reportrepo.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RuleRootParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(RuleRootParser.class);

    private RuleRootParser() {}

    /**
     * Parses an ExecutableRule instance to a RuleRoot instance and copies all attachments given in the rule reports to the outputDirectory. The returned RuleRoot instance is readable by freemarker templates.
     * @param rule the rule to parse
     * @param outputDirectory the directory where the attachments from the rule reports are copied to
     * @return a by freemarker readable RuleRoot instance
     */
    public static RuleRoot createRuleRoot(@NotNull ExecutableRule rule, @NotNull File outputDirectory, File imagesDirectory) {
        RuleRoot.RuleRootBuilder builder = RuleRoot.builder();

        builder.id(rule.getId());
        builder.description(rule.getDescription());
        builder.status(rule.getStatus().toUpperCase());
        builder.severity(rule.getSeverity().toUpperCase());

        Result result = rule.getResult();

        if(result != Result.EMPTY_RESULT) {
            List<String> resultKeys = rule.getResult().getColumnKeys();

            builder.resultColumnKeys(resultKeys);
            for (Map<String, String> row : rule.getResult().getRows()) {
                List<String> rowContent = new ArrayList<>();
                for (String key : resultKeys) {
                    rowContent.add(row.get(key));
                }
                builder.resultRow(rowContent);
            }
            builder.hasResult(true);
        }

        if(rule.getReports() != Reports.EMPTY_REPORTS && outputDirectory != null){
            builder.hasReports(true);
            builder.reports(RuleRootParser.parseReports(rule.getReports(), outputDirectory, imagesDirectory));
        }
        else {
            builder.reports(Reports.EMPTY_REPORTS);
        }

        if(rule instanceof Concept) {
            LOGGER.debug("Successfully parsed Concept {}.", rule.getId());
        }
        else if(rule instanceof Constraint) {
            LOGGER.debug("Successfully parsed Constraint {}.", rule.getId());
        }
        else {
            LOGGER.debug("Successfully parsed Rule {}.", rule.getId());
            LOGGER.warn("If you are a user of this application please contact the developers of this plugin. If you are the developer, please check the implementation of this logger. The extension seems to be expanded, but the logger is not adapted!");
        }

        return builder.build();
    }

    /**
     * Parses all attachments from original reports to relative paths and copies the resources to the outputDirectory
     * @param reports the reports that will be parsed
     * @param outputDirectory the location the attachments are copied to
     * @return the parse report with adapted links
     */
    private static Reports parseReports(@NotNull Reports reports, @NotNull File outputDirectory, File imagesDirectory) {
        Reports.ReportsBuilder repBuilder = Reports.builder();

        for(URLWithLabel image : reports.getImages()) {
            if(imagesDirectory != null) {
                repBuilder.image(URLWithLabel.builder().label(image.getLabel()).link(copyAttachmentAndRelativizePath(image.getLink(), imagesDirectory)).build());
            }
            else {
                repBuilder.image(URLWithLabel.builder().label(image.getLabel()).link(copyAttachmentAndRelativizePath(image.getLink(), outputDirectory)).build());
            }
        }
        for(URLWithLabel link : reports.getLinks()) {
            repBuilder.link(URLWithLabel.builder().label(link.getLabel()).link(copyAttachmentAndRelativizePath(link.getLink(), outputDirectory)).build());
        }

        return repBuilder.build();
    }

    private static String copyAttachmentAndRelativizePath(@NotNull String link, @NotNull File targetDirectory){
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
            return targetDirectory.getAbsoluteFile().toPath().relativize(path).toString();
        }
/*
        File attachmentDirectory = targetDirectory.toPath().resolve("attachments").toFile();
        try {
            FileUtils.forceMkdir(attachmentDirectory);
        } catch (IOException e1) {
            LOGGER.warn("Cannot create attachment directory {} in output directory ({}). \n Putting attachments directly into output directory!", attachmentDirectory, targetDirectory);
            try {;
                FileUtils.copyToDirectory(file, targetDirectory);
            } catch (IOException | IllegalArgumentException e2) {
                LOGGER.warn("Cannot copy file {} to {}. Maybe the targetDirectory is assigned incorrectly", uri, targetDirectory);
                return targetDirectory.getAbsoluteFile().toPath().relativize(path).toString();
            }
        }

        Path newPath;
        try {;
            FileUtils.copyToDirectory(file, attachmentDirectory);
            newPath = Paths.get(attachmentDirectory.getAbsolutePath()).resolve(path.getFileName());
        } catch (IOException | IllegalArgumentException e) {
            throw new IllegalStateException(e);
        }

        return targetDirectory.getAbsoluteFile().toPath().relativize(newPath).toString();
*/
        try {
            FileUtils.copyToDirectory(file, targetDirectory);
        } catch (IOException | IllegalArgumentException e2) {
            LOGGER.warn("Cannot copy file {} to {}. Maybe the targetDirectory is assigned incorrectly.", uri, targetDirectory);
            return targetDirectory.getAbsoluteFile().toPath().relativize(path).toString();
        }

        LOGGER.debug("Copied reports attachment file from {} to targetDirectory {}.", file.getAbsolutePath(), targetDirectory.getAbsolutePath());
        return path.getFileName().toString();

    }
}
