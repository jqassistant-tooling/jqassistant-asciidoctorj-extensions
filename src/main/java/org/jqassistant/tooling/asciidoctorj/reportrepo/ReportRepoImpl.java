package org.jqassistant.tooling.asciidoctorj.reportrepo;

import java.io.File;
import java.util.*;

import com.buschmais.jqassistant.core.rule.api.filter.RuleFilter;
import io.smallrye.common.constraint.NotNull;
import lombok.Getter;
import org.jqassistant.tooling.asciidoctorj.processors.attributes.ProcessAttributes;
import org.jqassistant.tooling.asciidoctorj.reportrepo.model.*;
import org.jqassistant.tooling.asciidoctorj.xmlparsing.ParsedReport;
import org.jqassistant.tooling.asciidoctorj.xmlparsing.ReportParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.buschmais.jqassistant.core.rule.api.filter.RuleFilter.matches;

@Getter
public class ReportRepoImpl implements ReportRepo {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportRepoImpl.class);
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

        return findExecutableRule(concepts, attributes.getConceptIdFilter());
    }

    @Override
    public SortedSet<Constraint> findConstraints(ProcessAttributes attributes) {
        initialize(attributes);

        return findExecutableRule(constraints, attributes.getConstraintIdFilter());
    }

    public <T extends ExecutableRule> SortedSet<T> findExecutableRule(Map<String, T> ruleMap, String idFilter) {

        SortedSet<T> rulesSet = new TreeSet<>(Comparator.comparing(Rule::getId));

        ruleMap.entrySet()
                .stream()
                .filter(entry -> idFilter == null || matches(entry.getKey(), idFilter))
                .forEach(entry -> rulesSet.add(entry.getValue()));

        return rulesSet;
    }
}
