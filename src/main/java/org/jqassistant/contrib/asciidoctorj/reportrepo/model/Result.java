package org.jqassistant.contrib.asciidoctorj.reportrepo.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Builder
@Getter
public class Result {

    /**
     * empty result used for case, that there's no result for constraint or concept
     */
    public static final Result EMPTY_RESULT = Result.builder().build();

    List<String> columnKeys;

    List<Map<String, String>> rows;

}
