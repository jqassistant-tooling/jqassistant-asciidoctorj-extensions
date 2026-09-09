package org.jqassistant.tooling.asciidoctorj.reportrepo;

import java.io.File;
import java.util.*;

import com.buschmais.jqassistant.core.report.api.model.Result;

import io.smallrye.common.constraint.NotNull;
import lombok.Getter;
import org.jqassistant.tooling.asciidoctorj.processors.attributes.ProcessAttributes;
import org.jqassistant.tooling.asciidoctorj.reportrepo.model.*;
import org.jqassistant.tooling.asciidoctorj.xmlparsing.ParsedReport;
import org.jqassistant.tooling.asciidoctorj.xmlparsing.ReportParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.buschmais.jqassistant.core.rule.api.filter.RuleFilter.matches;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;

@Getter
public class ReportRepoImpl implements ReportRepo {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportRepoImpl.class);
    private static final Set<String> STATUSES = stream(Result.Status.values()).map(Enum::toString)
            .collect(toSet());
    private final ReportParser reportParser;
    private boolean initialized = false;
    private Map<String, Group> groups = new HashMap<>();
    private Map<String, Concept> concepts = new HashMap<>();
    private Map<String, Constraint> constraints = new HashMap<>();

    public ReportRepoImpl(@NotNull ReportParser reportParser) {
        this.reportParser = reportParser;
    }

    private void initialize(@NotNull ProcessAttributes attributes) {
        if (!isInitialized()) {
            LOGGER.debug("initializing reportRepo");
            String reportPath = attributes.getReportPath();
            File reportFile = new File(reportPath);

            if (reportFile.exists() && reportFile.isFile()) {
                ParsedReport report = reportParser.parseReportXml(attributes.getReportPath());
                this.groups = report.getGroups();
                this.concepts = report.getConcepts();
                this.constraints = report.getConstraints();

                initialized = true;
                LOGGER.debug("successfully initialized reportRepo");
            } else {
                LOGGER.warn("jQAssistant Report XML not found at: {}. Any rule includes will remain empty.", reportPath);
            }
        }
    }

    @Override
    public SortedSet<Concept> findConcepts(ProcessAttributes attributes) {
        initialize(attributes);

        return findExecutableRule(concepts, attributes.getConceptIdFilter(), attributes.getStatusFilter());
    }

    @Override
    public SortedSet<Constraint> findConstraints(ProcessAttributes attributes) {
        initialize(attributes);

        return findExecutableRule(constraints, attributes.getConstraintIdFilter(), attributes.getStatusFilter());
    }

    public <T extends ExecutableRule> SortedSet<T> findExecutableRule(Map<String, T> ruleMap, String idFilter, String statusFilter) {

        SortedSet<T> rulesSet = new TreeSet<>(Comparator.comparing(Rule::getId));

        Set<String> allowedStatus = parseStatusFilter(statusFilter);

        ruleMap.entrySet()
                .stream()
                .filter(entry -> idFilter == null || matches(entry.getKey(), idFilter))
                .map(Map.Entry::getValue)
                .filter(rule -> allowedStatus.isEmpty() || (rule.getStatus() != null && allowedStatus.contains(rule.getStatus()
                        .toUpperCase())))
                .forEach(rulesSet::add);

        return rulesSet;
    }

    private Set<String> parseStatusFilter(String statusFilter) {

        Set<String> allowedStatus = new HashSet<>();

        if (statusFilter == null || statusFilter.isEmpty()) {
            allowedStatus = Set.of("WARNING", "FAILURE");
            return allowedStatus;
        }

        allowedStatus = stream(statusFilter.split(",")).map(String::toUpperCase)
                .map(String::trim)
                .collect(toSet());

        for (String status : allowedStatus) {
            if (!STATUSES.contains(status)) {
                throw new IllegalStateException(String.format("Invalid status '%s' provided in status filter. Allowed values  are: %s", status, STATUSES));
            }
        }
        return allowedStatus;
    }
}
