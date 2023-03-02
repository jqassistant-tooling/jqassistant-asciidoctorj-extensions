package org.jqassistant.contrib.asciidoctorj.reportrepo;

import com.buschmais.jqassistant.core.rule.api.filter.RuleFilter;
import lombok.Getter;
import org.jqassistant.contrib.asciidoctorj.includeprocessor.attributes.ProcessAttributes;
import org.jqassistant.contrib.asciidoctorj.reportrepo.model.*;
import org.jqassistant.contrib.asciidoctorj.xmlparsing.ParsedReport;
import org.jqassistant.contrib.asciidoctorj.xmlparsing.ReportParser;

import java.util.*;

@Getter
public class ReportRepoImpl implements ReportRepo{

    private boolean initialized = false;

    private static final RuleFilter RULE_FILTER = RuleFilter.getInstance();

    private final ReportParser reportParser;

    private Map<String, Group> groups = new HashMap<>();
    private Map<String, Concept> concepts = new HashMap<>();
    private Map<String, Constraint> constraints = new HashMap<>();

    public ReportRepoImpl(ReportParser reportParser) {
        this.reportParser = reportParser;
    }

    private void initialize(ProcessAttributes attributes) {
        if (!isInitialized()) {
            ParsedReport report = reportParser.parseReportXml(attributes.getReportPath());
            this.groups = report.getGroups();
            this.concepts = report.getConcepts();
            this.constraints = report.getConstraints();

            initialized = true;
        }
    }

    @Override
    public List<Constraint> findConstraints(ProcessAttributes attributes) {
        initialize(attributes);

        return new ArrayList<>(constraints.values());
    }

    @Override
    public List<ExecutableRule> findConceptsAndConstraints(ProcessAttributes attributes) {
        initialize(attributes);

        List<ExecutableRule> execRules = new ArrayList<>();
        execRules.addAll((Collection<? extends ExecutableRule>) filterRulesById(concepts, attributes));
        execRules.addAll((Collection<? extends ExecutableRule>) filterRulesById(constraints, attributes));

/*      if(filter != null) {
            Set<String> matchingConceptIds = ruleFilter.match(concepts.keySet(), filter);
            Set<String> matchingConstraintIds = ruleFilter.match(constraints.keySet(), filter);

            matchingConceptIds.forEach(s -> execRules.add(concepts.get(s)));
            matchingConstraintIds.forEach(s -> execRules.add(constraints.get(s)));
        }
        else {
            execRules.addAll(concepts.values());
            execRules.addAll(constraints.values());
        }
*/
        return execRules;
    }

    @Override
    public Result findConceptResult(ProcessAttributes attributes) { //TODO: mehrere Results möglich und filter von attributen trennen
        initialize(attributes);

        List<Concept> concepts = (List<Concept>) filterRulesById(this.concepts, attributes);

        if(concepts.size() == 1) {
            return concepts.get(0).getResult();
        }

        return Result.EMPTY_RESULT;
    }

    private Collection<? extends Rule> filterRulesById(Map<String, ? extends Rule> ruleMap, ProcessAttributes attributes) {
        String id = attributes.getIdWildcard();
        if(id == null) return ruleMap.values();

        Set<String> matchingIds = RULE_FILTER.match(ruleMap.keySet(), id);

        List<Rule> matchingRules = new ArrayList<>();
        matchingIds.forEach(s -> matchingRules.add(ruleMap.get(s)));

        return matchingRules;
    }
}
