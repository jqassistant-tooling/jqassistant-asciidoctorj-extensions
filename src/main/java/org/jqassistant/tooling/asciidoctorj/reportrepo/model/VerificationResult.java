package org.jqassistant.tooling.asciidoctorj.reportrepo.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VerificationResult {
    private final boolean success;
    private final int rowCount;
    private final int hiddenRowCount;
}
