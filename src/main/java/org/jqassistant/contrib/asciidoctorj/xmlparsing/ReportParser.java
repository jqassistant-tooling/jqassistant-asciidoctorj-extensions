package org.jqassistant.contrib.asciidoctorj.xmlparsing;

import org.jqassistant.contrib.asciidoctorj.reportrepo.ReportRepo;
import org.jqassistant.contrib.asciidoctorj.reportrepo.model.*;
import org.jqassistant.schema.report.v1.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportParser {

    private static final ReportParser INSTANCE = new ReportParser();

    public static ReportParser getInstance() {
        return INSTANCE;
    }

    /**
     * fills the given ResultRepo with data from xml
     *
     * @param repo            the repo that will be filled
     * @param fileDestination the place where the underlying xml is located
     */
    public void parseReportXml(ReportRepo repo, String fileDestination) {
        ReportReader reportReader = ReportReader.getInstance();
        JqassistantReport report = reportReader.read(new File(fileDestination));

        parseReport(repo, report);
    }

    private void parseReport(ReportRepo repo, JqassistantReport report) {
        List<ReferencableRuleType> nodes = report.getGroupOrConceptOrConstraint();

        for (ReferencableRuleType node : nodes) {
            parseNode(repo, node);
        }
    }

    private Rule parseNode(ReportRepo repo, ReferencableRuleType node) {
        Rule rule = null;

        if (node instanceof GroupType) {

            GroupType groupNode = (GroupType) node;

            Group group = parseGroup(repo, groupNode);
            repo.addGroup(group);

            rule = group;

        } else if (node instanceof ConceptType) {

            Concept concept = parseConcept((ConceptType) node);

            repo.addConcept(concept);

            rule = concept;

        } else if (node instanceof ConstraintType) {

            Constraint constraint = parseConstraint((ConstraintType) node);

            repo.addConstraint(constraint);

            rule = constraint;
        }

        return rule;
    }

    private Group parseGroup(ReportRepo repo, GroupType groupNode) {
        Group.GroupBuilder groupBuilder = Group.builder().id(groupNode.getId()).duration(groupNode.getDuration());

        List<ReferencableRuleType> childNodes = groupNode.getGroupOrConceptOrConstraint();
        for (ReferencableRuleType childNode : childNodes) {
            Rule rule = parseNode(repo, childNode);

            if (rule instanceof Group) groupBuilder.subGroup((Group) rule);
            else if (rule instanceof Concept) groupBuilder.nestedConcept((Concept) rule);
            else if (rule instanceof Constraint) groupBuilder.nestedConstraint((Constraint) rule);
        }

        return groupBuilder.build();
    }

    private Concept parseConcept(ConceptType conceptNode) {
        return Concept.builder()
                .status(conceptNode.getStatus().value())
                .severity(conceptNode.getSeverity().getValue())
                .id(conceptNode.getId())
                .description(conceptNode.getDescription())
                .duration(conceptNode.getDuration())
                .result(parseResult(conceptNode.getResult()))
                .build();
    }

    private Constraint parseConstraint(ConstraintType constraintNode) {
        return Constraint.builder()
                .status(constraintNode.getStatus().value())
                .severity(constraintNode.getSeverity().getValue())
                .id(constraintNode.getId())
                .description(constraintNode.getDescription())
                .duration(constraintNode.getDuration())
                .result(parseResult(constraintNode.getResult()))
                .build();
    }

    private Result parseResult(ResultType resultNode) {

        if (resultNode == null) return Result.EMPTY_RESULT;

        List<String> columnKeys = new ArrayList<>();
        List<Map<String, String>> rows = new ArrayList<>();

        for (ColumnHeaderType column : resultNode.getColumns().getColumn()) {
            columnKeys.add(column.getValue());
        }

        for (RowType row : resultNode.getRows().getRow()) {
            Map<String, String> rowMap = new HashMap<>();
            for (ColumnType cell : row.getColumn()) {
                rowMap.put(cell.getName(), cell.getValue());
            }
            rows.add(rowMap);
        }

        return Result.builder().columnKeys(columnKeys).rows(rows).build();
    }
}
