package org.jqassistant.tooling.asciidoctorj.reportrepo.model;

import java.util.Map;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;

@Builder
@Getter
public class Row {

    @Singular("columns")
    private Map<String, Column> columns;

    @Builder
    @Getter
    @EqualsAndHashCode //for testing purposes
    public static class Column {
        private String value;
    }
}
