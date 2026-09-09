package org.jqassistant.tooling.asciidoctorj.reportrepo.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

@Builder(toBuilder = true)
@Getter
public class Result {

    /**
     * empty result used for case, that there's no result for constraint or concept
     */
    public static final Result EMPTY_RESULT = Result.builder()
            .build();

    @Singular
    List<String> columnKeys;
    @Singular
    List<Row> rows;
    @Singular
    List<Row> baselineRows;
    @Singular
    List<SuppressedRow> suppressedRows;

    @SuperBuilder
    @Getter
    public static class Row {

        @Singular("columns")
        private Map<String, Column> columns;

        @Builder
        @Getter
        @EqualsAndHashCode //for testing purposes
        public static class Column {
            private String value;
        }
    }


    @SuperBuilder
    @Getter
    public static class SuppressedRow extends Row{
        private Optional<String> reason;
        private Optional<LocalDate> until;
    }

}
