package org.jqassistant.contrib.asciidoctorj.includeprocessor;

import org.jqassistant.contrib.asciidoctorj.freemarker.TemplateLoader;
import org.jqassistant.contrib.asciidoctorj.includeprocessor.attributes.ProcessAttributes;
import org.jqassistant.contrib.asciidoctorj.reportrepo.ReportRepo;

import java.util.HashMap;
import java.util.Map;

public class SummaryTable extends AbstractIncludeProcessor<Object> {

    public SummaryTable(ReportRepo repo, TemplateLoader templateLoader) {
        super(repo, templateLoader, "Summary", "SummaryTable");
    }

    /**
     * fill the data structure needed to fill the template in the abstract class
     *
     * @return rootElement for structure
     */
    @Override
    Object fillDataStructure(ProcessAttributes attributes) {
        Map<String, Object> root = new HashMap<>();
        root.put("concepts_or_constraints", repo.findConstraints(attributes));

        return root;
    }
}
