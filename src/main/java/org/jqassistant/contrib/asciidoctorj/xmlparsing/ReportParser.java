package org.jqassistant.contrib.asciidoctorj.xmlparsing;

import org.jqassistant.contrib.asciidoctorj.reportrepo.model.*;
import org.jqassistant.schema.report.v2.*;

import java.io.File;
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
     * @param fileDestination the place where the underlying xml is located
     */
    public ParsedReport parseReportXml(String fileDestination) {
        ReportReader reportReader = ReportReader.getInstance();
        JqassistantReport report = reportReader.read(new File(fileDestination));

        return parseReport(report);
    }

    /**
     * fills the given ResultRepo with data JqassistantReport
     *
     * @param report the from ReportReader received report
     */
    private ParsedReport parseReport(JqassistantReport report) {
        List<ReferencableRuleType> nodes = report.getGroupOrConceptOrConstraint();
        ParsedReport parsedReport = new ParsedReport();

        for (ReferencableRuleType node : nodes) {
            parseNode(parsedReport, node);
        }

        return parsedReport;
    }

    /**
     * fills the given ResultRepo the content of a node
     */
    private Rule parseNode(ParsedReport parsedReport, ReferencableRuleType node) {
        Rule rule = null;

        if (node instanceof GroupType) {

            GroupType groupNode = (GroupType) node;

            Group group = parseGroup(parsedReport, groupNode);
            parsedReport.addGroup(group);

            rule = group;

        } else if (node instanceof ConceptType) {

            Concept concept = parseConcept((ConceptType) node);

            parsedReport.addConcept(concept);

            rule = concept;

        } else if (node instanceof ConstraintType) {

            Constraint constraint = parseConstraint((ConstraintType) node);

            parsedReport.addConstraint(constraint);

            rule = constraint;
        }

        return rule;
    }

    /**
     * give back the parsed Result from conceptType (node)
     */
    private Group parseGroup(ParsedReport parsedReport, GroupType groupNode) {
        Group.GroupBuilder groupBuilder = Group.builder().id(groupNode.getId()).duration(groupNode.getDuration());

        List<ReferencableRuleType> childNodes = groupNode.getGroupOrConceptOrConstraint();
        for (ReferencableRuleType childNode : childNodes) {
            Rule rule = parseNode(parsedReport, childNode);

            if (rule instanceof Group) groupBuilder.subGroup((Group) rule);
            else if (rule instanceof Concept) groupBuilder.nestedConcept((Concept) rule);
            else if (rule instanceof Constraint) groupBuilder.nestedConstraint((Constraint) rule);
        }

        return groupBuilder.build();
    }

    /**
     * give back the parsed Result from conceptType (node)
     */
    private Concept parseConcept(ConceptType conceptNode) {
        return Concept.builder()
                .status(conceptNode.getStatus().value())
                .severity(conceptNode.getSeverity().getValue())
                .id(conceptNode.getId())
                .description(conceptNode.getDescription())
                .duration(conceptNode.getDuration())
                .result(parseResult(conceptNode.getResult()))
                .reports(parseReports(conceptNode.getReports()))
                .build();
    }

    /**
     * give back the parsed Result from constraintType (node)
     */
    private Constraint parseConstraint(ConstraintType constraintNode) {
        return Constraint.builder()
                .status(constraintNode.getStatus().value())
                .severity(constraintNode.getSeverity().getValue())
                .id(constraintNode.getId())
                .description(constraintNode.getDescription())
                .duration(constraintNode.getDuration())
                .result(parseResult(constraintNode.getResult()))
                .reports(parseReports(constraintNode.getReports()))
                .build();
    }

    /**
     * give back the parsed Result from resultType (node)
     */
    private Result parseResult(ResultType resultNode) {
        if (resultNode == null) return Result.EMPTY_RESULT;

        Result.ResultBuilder builder = Result.builder();

        for (ColumnHeaderType column : resultNode.getColumns().getColumn()) {
            builder.columnKey(column.getValue());
        }

        for (RowType row : resultNode.getRows().getRow()) {
            Map<String, String> rowMap = new HashMap<>();
            for (ColumnType cell : row.getColumn()) {
                rowMap.put(cell.getName(), cell.getValue());
            }
            builder.row(rowMap);
        }

        return builder.build();
    }

    /**
     * give back the parsed Report from reportType (node)
     */
    private Reports parseReports(ReportsType reportsNode) {
        if (reportsNode == null) return Reports.EMPTY_REPORTS;

        Reports.ReportsBuilder reports = Reports.builder();

        for (AbstractReportType imageOrLink : reportsNode.getImageOrLink()) {
            if(imageOrLink instanceof ImageType) {
                reports.image(URLWithLabel.builder().label(imageOrLink.getLabel()).link(imageOrLink.getValue()).build());
            }
            else if(imageOrLink instanceof LinkType){
                reports.link(URLWithLabel.builder().label(imageOrLink.getLabel()).link(imageOrLink.getValue()).build());

            }
        }

        return reports.build();
    }
}
