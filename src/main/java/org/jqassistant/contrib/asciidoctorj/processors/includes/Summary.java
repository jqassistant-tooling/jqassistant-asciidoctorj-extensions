package org.jqassistant.contrib.asciidoctorj.processors.includes;

import org.jqassistant.contrib.asciidoctorj.freemarker.TemplateRepo;
import org.jqassistant.contrib.asciidoctorj.processors.attributes.ProcessAttributes;
import org.jqassistant.contrib.asciidoctorj.reportrepo.ReportRepo;
import org.jqassistant.contrib.asciidoctorj.reportrepo.model.ExecutableRule;

import java.util.*;

public class Summary extends AbstractIncludeProcessor<Map<String, SortedSet<? extends ExecutableRule>>> {

    public Summary(ReportRepo repo, TemplateRepo templateRepo) {
        super(repo, templateRepo, "Summary", List.of("Summary"));
    }

    /**
     * fill the data structure needed to fill the template in the abstract class
     *
     * @return rootElement for structure
     */
    @Override
    Map<String, SortedSet<? extends ExecutableRule>> fillDataStructure(ProcessAttributes attributes) {
        Map<String, SortedSet<? extends ExecutableRule>> root = new HashMap<>();
        root.put("concepts", repo.findConcepts(attributes));
        root.put("constraints", repo.findConstraints(attributes));

        return root;
    }
}