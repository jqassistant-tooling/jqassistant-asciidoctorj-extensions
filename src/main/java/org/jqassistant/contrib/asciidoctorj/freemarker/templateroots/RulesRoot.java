package org.jqassistant.contrib.asciidoctorj.freemarker.templateroots;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.SortedSet;

@Builder
@Getter
public class RulesRoot {
    @Singular
    SortedSet<RuleRoot> concepts;
    @Singular
    SortedSet<RuleRoot> constraints;
}
