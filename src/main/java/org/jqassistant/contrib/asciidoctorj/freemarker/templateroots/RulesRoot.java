package org.jqassistant.contrib.asciidoctorj.freemarker.templateroots;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.SortedSet;
import java.util.TreeSet;

@Builder
@Getter
public class RulesRoot {
    @Singular
    SortedSet<RuleRoot> concepts;
    @Singular
    SortedSet<RuleRoot> constraints;

    /**
     * not currently functional; describes a concept for custom sorting of RuleRoots
     */
    public void sortingStrategy() {
        concepts = new TreeSet<>(RuleRoot::compareTo);
        constraints = new TreeSet<>(RuleRoot::compareTo);
    }
}
