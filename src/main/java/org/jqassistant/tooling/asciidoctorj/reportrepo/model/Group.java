package org.jqassistant.tooling.asciidoctorj.reportrepo.model;

import java.util.List;

import lombok.Getter;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Getter
public class Group extends AbstractRule {

    @Singular
    List<Group> subGroups;
    @Singular
    List<Concept> nestedConcepts;
    @Singular
    List<Constraint> nestedConstraints;

}
