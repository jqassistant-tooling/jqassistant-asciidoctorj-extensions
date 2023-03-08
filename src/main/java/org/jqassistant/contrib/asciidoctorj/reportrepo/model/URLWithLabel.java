package org.jqassistant.contrib.asciidoctorj.reportrepo.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class URLWithLabel {
    String link;
    String label;
}
