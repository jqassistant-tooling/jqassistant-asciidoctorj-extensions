package org.jqassistant.contrib.asciidoctorj.reportrepo.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;

@Builder
@Getter
public class Reports {
    public static final Reports EMPTY_REPORTS = Reports.builder().build();

    @Singular
    List<URLWithLabel> links;
    @Singular
    List<URLWithLabel> images;
}
