package org.jqassistant.contrib.asciidoctorj.reportrepo;

import org.jqassistant.contrib.asciidoctorj.includeprocessor.attributes.ProcessAttributes;
import org.jqassistant.contrib.asciidoctorj.reportrepo.model.*;

import java.util.List;

public interface ReportRepo {

    boolean isInitialized();

    List<Constraint> findConstraints(ProcessAttributes attributes);

    List<ExecutableRule> findConceptsAndConstraints(ProcessAttributes attributes);

    Result findConceptResult(ProcessAttributes attributes);
}
