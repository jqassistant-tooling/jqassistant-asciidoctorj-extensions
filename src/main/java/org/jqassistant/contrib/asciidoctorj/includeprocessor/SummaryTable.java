package org.jqassistant.contrib.asciidoctorj.includeprocessor;

import org.jqassistant.contrib.asciidoctorj.freemarker.TemplateLoader;
import org.jqassistant.contrib.asciidoctorj.reportrepo.ReportRepo;
import org.jqassistant.contrib.asciidoctorj.xmlparsing.ReportParser;

import java.util.HashMap;
import java.util.Map;

public class SummaryTable extends AbstractIncludeProcessor {

    public SummaryTable(ReportRepo repo, ReportParser parser, TemplateLoader templateLoader) {
        super(repo, parser, templateLoader, "jQA:Summary", "SummaryTable");
    }

    /**
     * fill the data structure needed to fill the template in the abstract class
     *
     * @return rootElement for structure
     */
    @Override
    Object fillDataStructure(Map<String, Object> attributes) {
        Map<String, Object> root = new HashMap<>();
        root.put("concepts_or_constraints", repo.findConstraints());

        return root;
    }
}
