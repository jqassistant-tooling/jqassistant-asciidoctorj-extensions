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

    public void initialize(@NotNull ProcessAttributes attributes) {
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

        return findExecutableRule(attributes.getConceptIdFilter(), concepts);
    }

    @Override
    public SortedSet<Constraint> findConstraints(ProcessAttributes attributes) {
        initialize(attributes);

        return findExecutableRule(attributes.getConstraintIdFilter(), constraints);
    }

    public <T extends ExecutableRule> SortedSet<T> findExecutableRule(String idFilter, Map<String, T> ruleMap) {

        SortedSet<T> rulesSet = new TreeSet<>(Comparator.comparing(Rule::getId));

        rulesSet.addAll((Collection<T>) filterRulesById(ruleMap, idFilter));

        if (idFilter == null) {
            LOGGER.debug("Giving back all rules due to empty ruleIdFilter");
        } else {
            LOGGER.debug("Giving back all rules matching {}", idFilter);
        }

        return rulesSet;
    }

    /**
     * filters all given rules by their id
     *
     * @param ruleMap
     *         a map for all given rules: 1. element = id; 2. element = rule
     * @param id
     *         the id(-wildcard) to match against
     * @return all matching rules
     */
    private Collection<? extends Rule> filterRulesById(@NotNull Map<String, ? extends Rule> ruleMap, String id) {
        if (id == null) {
            return ruleMap.values();
        }

        Set<String> matchingIds = RuleFilter.match(ruleMap.keySet(), id);

        List<Rule> matchingRules = new ArrayList<>();
        matchingIds.forEach(s -> matchingRules.add(ruleMap.get(s)));

        return matchingRules;
    }
}
