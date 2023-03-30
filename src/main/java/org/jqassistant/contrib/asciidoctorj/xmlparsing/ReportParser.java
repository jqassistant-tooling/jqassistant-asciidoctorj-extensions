package org.jqassistant.contrib.asciidoctorj.xmlparsing;

import io.smallrye.common.constraint.NotNull;
import org.jqassistant.contrib.asciidoctorj.reportrepo.model.*;
import org.jqassistant.schema.report.v2.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportParser.class);

    private static final ReportParser INSTANCE = new ReportParser();

    public static ReportParser getInstance() {
        return INSTANCE;
    }

    /**
     * creates a ParsedReport instance from a report-xml
     *
     * @param fileDestination the place where the underlying xml is located
     * @return a ParsedReport created from xml file
     */
    public ParsedReport parseReportXml(@NotNull String fileDestination) {
        ReportReader reportReader = ReportReader.getInstance();

        LOGGER.info("Parsing xml-report from {}", fileDestination);
        JqassistantReport report = reportReader.read(new File(fileDestination));
        LOGGER.info("Successfully parsed xml-report from {}", fileDestination);

        return parseReport(report);
    }

    /**
     * creates a ParsedReport instance from a JqassistantReport instance
     *
     * @param report a JqassistantReport (received from a ReportReader)
     * @return a ParsedReport created from JqassistantReport instance
     */
    private ParsedReport parseReport(@NotNull JqassistantReport report) {
        List<ReferencableRuleType> nodes = report.getGroupOrConceptOrConstraint();
        ParsedReport parsedReport = new ParsedReport();

        for (ReferencableRuleType node : nodes) {
            parseNode(parsedReport, node);
        }

        return parsedReport;
    }

    /**
     * Adds the Rule that is generated from a node to a ParsedReport and returns it.
     *
     * @param parsedReport the ParsedReport that will be amended
     * @param node the node that will be parsed
     * @return the Rule that was generated from the node
     */
    private Rule parseNode(@NotNull ParsedReport parsedReport, @NotNull ReferencableRuleType node) {
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
     * Adds the given Group to the ParsedReport. Also parses sub-rules of the groupNode. These will be added to the ParsedReport and filled into the corresponding place in a Group that will be returned.
     *
     * @param parsedReport the ParsedReport that will be amended
     * @param groupNode the GroupNode that will be parsed
     * @return the Group that is generated from the node
     */
    private Group parseGroup(@NotNull ParsedReport parsedReport, @NotNull GroupType groupNode) {
        var groupBuilder = Group.builder().id(groupNode.getId()).duration(groupNode.getDuration());

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
     * Adds the given Concept to the ParsedReport. Also returns the parsed Concept
     *
     * @param conceptNode the Node from which the Concept will be generated
     * @return the Concept that is generated from the node
     */
    private Concept parseConcept(@NotNull ConceptType conceptNode) {
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
     * Adds the given Constraint to the ParsedReport. Also returns the parsed Constraint
     *
     * @param constraintNode the Node from which the Constraint will be generated
     * @return the Constraint that is generated from the node
     */
    private Constraint parseConstraint(@NotNull ConstraintType constraintNode) {
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
     *
     * @param resultNode the Node from which the Result will be generated
     * @return the Result that is generated from the node
     */
    private Result parseResult(@NotNull ResultType resultNode) {
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
     * give back the parsed Reports from reportsType (node)
     *
     * @param reportsNode the Node from which the Reports will be generated
     * @return the Reports that is generated from the node
     */
    private Reports parseReports(@NotNull ReportsType reportsNode) {
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
