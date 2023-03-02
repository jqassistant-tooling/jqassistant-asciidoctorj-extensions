package org.jqassistant.contrib.asciidoctorj.includeprocessor;

import org.jqassistant.contrib.asciidoctorj.freemarker.TemplateLoader;
import org.jqassistant.contrib.asciidoctorj.includeprocessor.attributes.ProcessAttributes;
import org.jqassistant.contrib.asciidoctorj.reportrepo.ReportRepo;
import org.jqassistant.contrib.asciidoctorj.reportrepo.model.ExecutableRule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConceptsAndConstraints extends AbstractIncludeProcessor<Map<String, List<ExecutableRule>>> {

    public ConceptsAndConstraints(ReportRepo repo, TemplateLoader templateLoader) {
        super(repo, templateLoader, "ConceptsAndConstraints", "ConceptsAndConstraints");
    }

    /**
     * fill the data structure needed to fill the template in the abstract class
     *
     * @return rootElement for structure
     */
    @Override
    Map<String, List<ExecutableRule>> fillDataStructure(ProcessAttributes attributes) {
        Map<String, List<ExecutableRule>> root = new HashMap<>();
        root.put("concepts_or_constraints", repo.findConceptsAndConstraints(attributes));

        return root;
    }
}
