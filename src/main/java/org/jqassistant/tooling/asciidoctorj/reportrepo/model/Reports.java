package org.jqassistant.tooling.asciidoctorj.reportrepo.model;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

@Builder
@Getter
public class Reports {
    public static final Reports EMPTY_REPORTS = Reports.builder()
            .build();

    @Singular
    List<URLWithLabel> links;
    @Singular
    List<URLWithLabel> images;
}
