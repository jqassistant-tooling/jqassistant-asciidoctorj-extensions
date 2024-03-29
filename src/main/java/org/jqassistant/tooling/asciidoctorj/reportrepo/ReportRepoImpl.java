package org.jqassistant.tooling.asciidoctorj.reportrepo;

import com.buschmais.jqassistant.core.rule.api.filter.RuleFilter;
import io.smallrye.common.constraint.NotNull;
import lombok.Getter;
import org.jqassistant.tooling.asciidoctorj.processors.attributes.ProcessAttributes;
import org.jqassistant.tooling.asciidoctorj.reportrepo.model.Concept;
import org.jqassistant.tooling.asciidoctorj.reportrepo.model.Constraint;
import org.jqassistant.tooling.asciidoctorj.reportrepo.model.Group;
import org.jqassistant.tooling.asciidoctorj.reportrepo.model.Rule;
import org.jqassistant.tooling.asciidoctorj.xmlparsing.ParsedReport;
import org.jqassistant.tooling.asciidoctorj.xmlparsing.ReportParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Getter
public class ReportRepoImpl implements ReportRepo {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportRepoImpl.class);

    private boolean initialized = false;

    private final ReportParser reportParser;

    private Map<String, Group> groups = new HashMap<>();
    private Map<String, Concept> concepts = new HashMap<>();
    private Map<String, Constraint> constraints = new HashMap<>();

    public ReportRepoImpl(@NotNull ReportParser reportParser) {
        this.reportParser = reportParser;
    }

    private void initialize(@NotNull ProcessAttributes attributes) {
        if (!isInitialized()) {
            LOGGER.debug("initializing reportRepo");
            ParsedReport report = reportParser.parseReportXml(attributes.getReportPath());
            this.groups = report.getGroups();
            this.concepts = report.getConcepts();
            this.constraints = report.getConstraints();

            initialized = true;
            LOGGER.debug("successfully initialized reportRepo");
        }
    }

    @Override
    public SortedSet<Concept> findConcepts(@NotNull ProcessAttributes attributes) {
        initialize(attributes);

        SortedSet<Concept> conceptSSet = new TreeSet<>(Comparator.comparing(Rule::getId));

        if (attributes.getConceptIdFilter() == null) {
            conceptSSet.addAll(concepts.values());
            LOGGER.debug("Giving back all concepts due to empty conceptIdFilter");
            return conceptSSet;
        }

        String id = attributes.getConceptIdFilter();

        conceptSSet.addAll((Collection<Concept>) filterRulesById(concepts, id));

        LOGGER.debug("Giving back all concepts matching {}", attributes.getConceptIdFilter());

        return conceptSSet;
    }

    @Override
    public SortedSet<Constraint> findConstraints(@NotNull ProcessAttributes attributes) {
        initialize(attributes);

        SortedSet<Constraint> constraintSSet = new TreeSet<>(Comparator.comparing(Rule::getId));

        if (attributes.getConstraintIdFilter() == null) {
            constraintSSet.addAll(constraints.values());
            LOGGER.debug("Giving back all constraints due to empty constraintIdFilter");
            return constraintSSet;
        }

        String id = attributes.getConstraintIdFilter();

        constraintSSet.addAll((Collection<Constraint>) filterRulesById(constraints, id));

        LOGGER.debug("Giving back all constraints matching {}", attributes.getConstraintIdFilter());

        return constraintSSet;
    }

    /**
     * filters all given rules by their id
     *
     * @param ruleMap a map for all given rules: 1. element = id; 2. element = rule
     * @param id      the id(-wildcard) to match against
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
