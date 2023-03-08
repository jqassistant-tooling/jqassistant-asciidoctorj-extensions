package org.jqassistant.contrib.asciidoctorj.freemarker.templateroots;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;

@Builder
@Getter
public class RulesRoot {
    @Singular
    List<RuleRoot> concepts;
    @Singular
    List<RuleRoot> constraints;
}
