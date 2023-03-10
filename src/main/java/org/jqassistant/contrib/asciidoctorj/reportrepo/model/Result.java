package org.jqassistant.contrib.asciidoctorj.reportrepo.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;
import java.util.Map;

@Builder(toBuilder = true)
@Getter
public class Result {

    /**
     * empty result used for case, that there's no result for constraint or concept
     */
    public static final Result EMPTY_RESULT = Result.builder().build();

    @Singular
    List<String> columnKeys; //TODO: possible two columns with same name?
    @Singular
    List<Map<String, String>> rows;
}
