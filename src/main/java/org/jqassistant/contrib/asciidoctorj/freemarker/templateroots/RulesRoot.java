package org.jqassistant.contrib.asciidoctorj.freemarker.templateroots;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;

@Builder
@Getter
public class RulesRoot {
    @Singular
    List<ResultRoot> conceptResults;
    @Singular
    List<ResultRoot> constraintResults;
}
