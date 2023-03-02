package org.jqassistant.contrib.asciidoctorj.xmlparsing;

import lombok.Getter;
import org.jqassistant.contrib.asciidoctorj.reportrepo.model.Concept;
import org.jqassistant.contrib.asciidoctorj.reportrepo.model.Constraint;
import org.jqassistant.contrib.asciidoctorj.reportrepo.model.Group;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ParsedReport {

    private final Map<String, Group> groups = new HashMap<>();
    private final Map<String, Concept> concepts = new HashMap<>();
    private final Map<String, Constraint> constraints = new HashMap<>();

    protected void addGroup(Group group) {
        groups.put(group.getId(), group);
    }

    protected void addConcept(Concept concept) {
        concepts.put(concept.getId(), concept);
    }

    protected void addConstraint(Constraint constraint) {
        constraints.put(constraint.getId(), constraint);
    }

}
