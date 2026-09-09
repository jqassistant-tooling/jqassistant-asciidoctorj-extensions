package org.jqassistant.tooling.asciidoctorj.freemarker.templateroots;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import io.smallrye.common.constraint.NotNull;
import org.apache.commons.io.FileUtils;
import org.jqassistant.tooling.asciidoctorj.freemarker.templateroots.RuleRoot.RuleRootBuilder;
import org.jqassistant.tooling.asciidoctorj.reportrepo.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleRootParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(RuleRootParser.class);

    private RuleRootParser() {
    }

    /**
     * Parses an ExecutableRule instance to a RuleRoot instance and copies all attachments given in the rule reports to the outputDirectory. The returned RuleRoot instance is readable by freemarker templates.
     *
     * @param rule
     *         the rule to parse
     * @param outputDirectory
     *         the directory where the attachments from the rule reports are copied to
     * @return a by freemarker readable RuleRoot instance
     */
    public static RuleRoot createRuleRoot(@NotNull ExecutableRule rule, @NotNull File outputDirectory, File imagesDirectory) {
        RuleRootBuilder builder = RuleRoot.builder();

        builder.id(rule.getId());
        builder.description(rule.getDescription());
        builder.status(rule.getStatus()
                .toUpperCase());
        builder.severity(rule.getSeverity()
                .toUpperCase());

        Result result = rule.getResult();

        if (result != Result.EMPTY_RESULT) {
            List<String> resultKeys = rule.getResult()
                    .getColumnKeys();

            parseVisibleResults(result, builder, resultKeys);

            parseBaselineResults(result, builder, resultKeys);

            parseSuppressedResults(result, builder, resultKeys);
        }

        if (rule.getReports() != Reports.EMPTY_REPORTS && outputDirectory != null) {
            builder.hasReports(true);
            builder.reports(RuleRootParser.parseReports(rule.getReports(), outputDirectory, imagesDirectory));
        } else {
            builder.reports(Reports.EMPTY_REPORTS);
        }

        if (rule instanceof Concept) {
            LOGGER.debug("Successfully parsed Concept {}.", rule.getId());
        } else if (rule instanceof Constraint) {
            LOGGER.debug("Successfully parsed Constraint {}.", rule.getId());
        } else {
            LOGGER.debug("Successfully parsed Rule {}.", rule.getId());
            LOGGER.warn(
                    "If you are a user of this application please contact the developers of this plugin. If you are the developer, please check the implementation of this logger. The extension seems to be expanded, but the logger is not adapted!");
        }

        return (RuleRoot) builder.build();
    }

    /**
     * Parses all attachments from original reports to relative paths and copies the resources to the outputDirectory
     *
     * @param reports
     *         the reports that will be parsed
     * @param outputDirectory
     *         the location the attachments are copied to
     * @return the parse report with adapted links
     */
    private static Reports parseReports(@NotNull Reports reports, @NotNull File outputDirectory, File imagesDirectory) {
        Reports.ReportsBuilder repBuilder = Reports.builder();

        for (URLWithLabel image : reports.getImages()) {
            if (imagesDirectory != null) {
                repBuilder.image(URLWithLabel.builder()
                        .label(image.getLabel())
                        .link(copyAttachmentAndRelativizePath(image.getLink(), imagesDirectory))
                        .build());
            } else {
                repBuilder.image(URLWithLabel.builder()
                        .label(image.getLabel())
                        .link(copyAttachmentAndRelativizePath(image.getLink(), outputDirectory))
                        .build());
            }
        }
        for (URLWithLabel link : reports.getLinks()) {
            repBuilder.link(URLWithLabel.builder()
                    .label(link.getLabel())
                    .link(copyAttachmentAndRelativizePath(link.getLink(), outputDirectory))
                    .build());
        }

        return repBuilder.build();
    }

    private static String copyAttachmentAndRelativizePath(@NotNull String link, @NotNull File targetDirectory) {
        URI uri;
        try {
            uri = new URI(link);
        } catch (URISyntaxException e) {
            LOGGER.warn("Cannot create URI from {}", link);
            return link;
        }

        if (uri.getScheme() == null) {
            LOGGER.warn("URI '{}' has no scheme. JQA should not produce a URI without a scheme. Please contact developer!", uri);
            return link;
        }

        if (!uri.getScheme()
                .equals("file")) {
            return link;
        }

        File file;
        Path path = Paths.get(uri);
        try {
            file = new File(uri);
        } catch (Exception e) {
            LOGGER.warn("Cannot find File from uri {}", uri);
            return targetDirectory.getAbsoluteFile()
                    .toPath()
                    .relativize(path)
                    .toString();
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
            return targetDirectory.getAbsoluteFile()
                    .toPath()
                    .relativize(path)
                    .toString();
        }

        LOGGER.debug("Copied reports attachment file from {} to targetDirectory {}.", file.getAbsolutePath(), targetDirectory.getAbsolutePath());
        return path.getFileName()
                .toString();

    }

    private static void parseVisibleResults(Result result, RuleRootBuilder builder, List<String> resultKeys) {
        if (result.getRows() != null && !result.getRows()
                .isEmpty()) {
            builder.resultColumnKeys(resultKeys);
            for (Result.Row row : result.getRows()) {
                builder.resultRow(createRowRoot(row, resultKeys));
            }
            builder.hasResult(true);
        }
    }

    private static void parseBaselineResults(Result result, RuleRootBuilder builder, List<String> resultKeys) {
        if (result.getBaselineRows() != null && !result.getBaselineRows()
                .isEmpty()) {
            for (Result.Row baselineRow : result.getBaselineRows()) {
                builder.baselineRow(createRowRoot(baselineRow, resultKeys));
            }
            builder.hasBaselineResult(true);
        }
    }

    private static void parseSuppressedResults(Result result, RuleRootBuilder builder, List<String> resultKeys) {
        if (result.getSuppressedRows() != null && !result.getSuppressedRows()
                .isEmpty()) {
            for (Result.SuppressedRow suppressedRow : result.getSuppressedRows()) {
                RuleRoot.SuppressedRowRoot.SuppressedRowRootBuilder suppressedRowRootBuilder = RuleRoot.SuppressedRowRoot.builder()
                        .reason(suppressedRow.getReason())
                        .until(suppressedRow.getUntil());

                suppressedRowRootBuilder.columns(createRowRoot(suppressedRow, resultKeys).getColumns());

                builder.suppressedRow(suppressedRowRootBuilder.build());
            }
            builder.hasSuppressedResult(true);
        }
    }

    private static RuleRoot.RowRoot createRowRoot(Result.Row row, List<String> resultKeys) {
        RuleRoot.RowRoot.RowRootBuilder<?, ?> rowRootBuilder = RuleRoot.RowRoot.builder();
        for (String key : resultKeys) {
            String value = Optional.ofNullable(row.getColumns())
                    .map(cols -> cols.get(key))
                    .map(Result.Row.Column::getValue)
                    .orElseThrow(() -> new IllegalStateException("Expected column key" + key + " was not found in result row:" + row));
            rowRootBuilder.column(value);
        }
        return rowRootBuilder.build();
    }
}
