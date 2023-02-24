package org.jqassistant.contrib.asciidoctorj.reportrepo.model;

import lombok.Getter;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

import java.util.List;

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
