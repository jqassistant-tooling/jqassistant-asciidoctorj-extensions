package org.jqassistant.contrib.asciidoctorj.reportrepo;

import com.buschmais.jqassistant.core.rule.api.filter.RuleFilter;
import lombok.Getter;
import org.jqassistant.contrib.asciidoctorj.reportrepo.model.Concept;
import org.jqassistant.contrib.asciidoctorj.reportrepo.model.Constraint;
import org.jqassistant.contrib.asciidoctorj.reportrepo.model.ExecutableRule;
import org.jqassistant.contrib.asciidoctorj.reportrepo.model.Group;

import java.util.*;

@Getter
public class ReportRepoImpl implements ReportRepo{

    boolean initialized = false;
    Map<String, Group> groups = new HashMap<>();
    Map<String, Concept> concepts = new HashMap<>();
    Map<String, Constraint> constraints = new HashMap<>();

    public ReportRepoImpl() {
    }

    @Override
    public void addGroup(Group group) {
        groups.put(group.getId(), group);
    }

    @Override
    public void addConcept(Concept concept) {
        concepts.put(concept.getId(), concept);
    }

    @Override
    public void addConstraint(Constraint constraint) {
        constraints.put(constraint.getId(), constraint);
    }

    @Override
    public List<Constraint> findConstraints() {
        return new ArrayList<>(constraints.values());
    }

    @Override
    public List<ExecutableRule> findConceptsAndConstraints(Map<String, Object> attributes) {
        RuleFilter ruleFilter = RuleFilter.getInstance();
        List<ExecutableRule> execRules = new ArrayList<>();

        String filter = (String) attributes.get("filter"); //TODO: is necessary to check whether its a String?
        if(filter != null) {
            Set<String> matchingConceptIds = ruleFilter.match(concepts.keySet(), filter);
            Set<String> matchingConstraintIds = ruleFilter.match(constraints.keySet(), filter);

            matchingConceptIds.forEach(s -> execRules.add(concepts.get(s)));
            matchingConstraintIds.forEach(s -> execRules.add(constraints.get(s)));
        }
        else {
            execRules.addAll(concepts.values());
            execRules.addAll(constraints.values());
        }

        return execRules;
    }
}
