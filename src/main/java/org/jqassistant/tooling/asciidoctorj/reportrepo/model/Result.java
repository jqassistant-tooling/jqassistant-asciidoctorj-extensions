package org.jqassistant.tooling.asciidoctorj.reportrepo.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Builder(toBuilder = true)
@Getter
public class Result {

    /**
     * empty result used for case, that there's no result for constraint or concept
     */
    public static final Result EMPTY_RESULT = Result.builder().build();

    @Singular
    List<String> columnKeys;
    @Singular
    List<Row> rows;
    @Singular
    List<Row> baselineRows;
    @Singular
    List<SuppressedRow> suppressedRows;

    @Builder
    @Getter
    public static class SuppressedRow {

        private Row row;
        //metadata
        private Optional<String> reason;
        private Optional<LocalDate> until;
    }
}
