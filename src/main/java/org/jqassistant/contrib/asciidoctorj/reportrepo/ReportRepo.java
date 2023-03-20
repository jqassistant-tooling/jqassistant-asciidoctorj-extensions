package org.jqassistant.contrib.asciidoctorj.reportrepo;

import io.smallrye.common.constraint.NotNull;
import org.jqassistant.contrib.asciidoctorj.processors.attributes.ProcessAttributes;
import org.jqassistant.contrib.asciidoctorj.reportrepo.model.*;

import java.util.SortedSet;

/**
 * interface for finding concepts and constraints from parsed report xml
 */
public interface ReportRepo {
    /**
     * finds and returns all fitting concepts for an id filter
     *
     * @param attributes Should at least contain: reportPath! May optionally be filled with: conceptIdFilter
     * @return all to attributes fitting concepts; if conceptIdFilter from attributes is null, return all concepts; if conceptIdFilter from attributes is "", return empty Set
     */
    SortedSet<Concept> findConcepts(@NotNull ProcessAttributes attributes);

    /**
     * finds and returns all fitting constraints for an id filter
     *
     * @param attributes Should at least contain: reportPath! May optionally be filled with: constraintIdFilter
     * @return all to attributes fitting constraints; if constraintIdFilter from attributes is null, return all constraints; if constraintIdFilter from attributes is "", return empty Set
     */
    SortedSet<Constraint> findConstraints(@NotNull ProcessAttributes attributes);
}
