package org.jqassistant.contrib.asciidoctorj.reportrepo;

import org.jqassistant.contrib.asciidoctorj.reportrepo.model.Concept;
import org.jqassistant.contrib.asciidoctorj.reportrepo.model.Constraint;
import org.jqassistant.contrib.asciidoctorj.reportrepo.model.ExecutableRule;
import org.jqassistant.contrib.asciidoctorj.reportrepo.model.Group;

import java.util.List;
import java.util.Map;

public interface ReportRepo {

    boolean isInitialized();

    void addGroup(Group group);

    void addConcept(Concept concept);

    void addConstraint(Constraint constraint);

    List<Constraint> findConstraints();

    List<ExecutableRule> findConceptsAndConstraints(Map<String, Object> attributes);
}
