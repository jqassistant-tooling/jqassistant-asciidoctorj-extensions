package org.jqassistant.contrib.asciidoctorj.includeprocessor;

import org.jqassistant.contrib.asciidoctorj.freemarker.TemplateLoader;
import org.jqassistant.contrib.asciidoctorj.includeprocessor.attributes.ProcessAttributes;
import org.jqassistant.contrib.asciidoctorj.reportrepo.ReportRepo;
import org.jqassistant.contrib.asciidoctorj.reportrepo.model.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConceptResult extends AbstractIncludeProcessor<Map<String, Object>> {

    public ConceptResult(ReportRepo repo, TemplateLoader templateLoader) {
        super(repo, templateLoader, "ConceptResult", "ConceptResult");
    }

    @Override
    Map<String, Object> fillDataStructure(ProcessAttributes attributes) {
        Map<String, Object> root = new HashMap<>();

        Result result = repo.findConceptResult(attributes);

        if(result == Result.EMPTY_RESULT) return null;

        List<String> keys = result.getColumnKeys();

        root.put("result_header", keys);

        List<List<String>> resultLines = new ArrayList<>();
        for(Map<String, String> row : result.getRows()) {
            List<String> rowContent = new ArrayList<>();
            for(String key : keys) {
                rowContent.add(row.get(key));
            }
            resultLines.add(rowContent);
        }

        root.put("result_lines", resultLines);

        return root;
    }
}
