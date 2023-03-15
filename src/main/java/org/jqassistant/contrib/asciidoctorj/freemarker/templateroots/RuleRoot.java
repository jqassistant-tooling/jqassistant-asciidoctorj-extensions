package org.jqassistant.contrib.asciidoctorj.freemarker.templateroots;

import io.smallrye.common.constraint.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import org.jqassistant.contrib.asciidoctorj.reportrepo.model.ExecutableRule;
import org.jqassistant.contrib.asciidoctorj.reportrepo.model.Reports;
import org.jqassistant.contrib.asciidoctorj.reportrepo.model.Result;
import org.jqassistant.contrib.asciidoctorj.reportrepo.model.URLWithLabel;
import org.jqassistant.contrib.asciidoctorj.xmlparsing.ReportParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Builder
@Getter
public class RuleRoot implements Comparable<RuleRoot>{
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportParser.class);

    private static String statSuccess = "SUCCESS";
    private static String statWarn = "WARNING";
    private static String statFail = "FAILURE";
    private static String statSkipped = "SKIPPED";

    private String id;
    private String description;
    private String status;
    private String severity;

    private boolean hasReports;
    private boolean hasResult;

    private List<String> resultColumnKeys;
    @Singular
    private List<List<String>> resultRows;

    private Reports reports;

    public static RuleRoot createRuleRoot(ExecutableRule rule, File outputDirectory) {
        RuleRootBuilder builder = builder();

        builder.id(rule.getId());
        builder.description(rule.getDescription());
        builder.status(rule.getStatus().toUpperCase());
        builder.severity(rule.getSeverity().toUpperCase());

        Result result = rule.getResult();
        if(result == null) throw new NullPointerException("Results of rules should never be null! Instead they should be Result.EMPTY_RESULT.");

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
            builder.hasResult = true;
        }

        if(rule.getReports() == null) throw new NullPointerException("Reports of rules should never be null! Instead they should be Reports.EMPTY_RESULT.");
        if(rule.getReports() != Reports.EMPTY_REPORTS) builder.hasReports = true;

        Reports reports = rule.getReports();
        Reports.ReportsBuilder repBuilder = Reports.builder();

        for(URLWithLabel image : reports.getImages()) {
            repBuilder.image(URLWithLabel.builder().label(image.getLabel()).link(relativizeLink(image.getLink(), outputDirectory)).build());
        }
        for(URLWithLabel link : reports.getLinks()) {
            repBuilder.image(URLWithLabel.builder().label(link.getLabel()).link(relativizeLink(link.getLink(), outputDirectory)).build());
        }

        builder.reports(repBuilder.build());

        return builder.build();
    }

    @Override
    public int compareTo(@NotNull RuleRoot other) {
        if(this.getStatus().equals(other.getStatus())) {
            return this.id.compareTo(other.id);
        }
        else if(this.getStatus().equals(statFail)) return -1;
        else if(other.getStatus().equals(statFail)) return 1;
        else if(this.getStatus().equals(statWarn)) return -1;
        else if(other.getStatus().equals(statWarn)) return 1;
        else if(this.getStatus().equals(statSuccess)) return -1;
        else if(other.getStatus().equals(statSuccess)) return 1;

        throw new IllegalStateException("Rule Root should be comparable; statuses were: " + this.status + " " + other.status);
    }

    private static String relativizeLink(String absolute, File outputDirectory) {
        absolute = absolute.replace("file:", "");
        Path path;
        try {
            path = Paths.get(absolute);
            Path relativePath = outputDirectory.getAbsoluteFile().toPath().relativize(path);
            return relativePath.toString().replace('\\', '/');
        } catch (Exception e) {
            LOGGER.warn("Cannot match '" + absolute + " and " + outputDirectory.getAbsoluteFile().toString() + "' for creating relative path.", e);
            return absolute;
        }
    }
}
