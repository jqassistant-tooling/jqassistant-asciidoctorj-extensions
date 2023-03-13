package org.jqassistant.contrib.asciidoctorj.reportrepo;

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
     * @param attributes contains at least conceptIdFilter for filtering and reportPath for lazy initialize
     * @return all to attributes fitting concepts; if conceptIdFilter from attributes is not set, return an empty List
     */
    SortedSet<Concept> findConcepts(ProcessAttributes attributes);

    /**
     * finds and returns all fitting constraints for an id filter
     *
     * @param attributes contains at least constraintIdFilter for filtering and reportPath for lazy initialize
     * @return all to attributes fitting concepts; if constraintIdFilter from attributes is not set, return an empty List
     */
    SortedSet<Constraint> findConstraints(ProcessAttributes attributes);
}
