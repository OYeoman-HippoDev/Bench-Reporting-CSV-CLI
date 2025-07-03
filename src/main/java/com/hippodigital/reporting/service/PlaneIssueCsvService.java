package com.hippodigital.reporting.service;

import com.hippodigital.reporting.client.PlaneApiClient;
import com.hippodigital.reporting.dto.Assignee;
import com.hippodigital.reporting.dto.IssueDTO;
import com.hippodigital.reporting.dto.PlaneIssueResponse;
import com.hippodigital.reporting.dto.PlaneModuleResponse;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
public class PlaneIssueCsvService {

    private final PlaneApiClient planeApiClient;

    public PlaneIssueCsvService(PlaneApiClient planeApiClient) {
        this.planeApiClient = planeApiClient;
    }

    public ByteArrayInputStream fetchAndConvertToCsv(LocalDate startDate, LocalDate endDate) throws IOException {
        PlaneIssueResponse response = planeApiClient.getIssues();
        List<IssueDTO> issues = Objects.requireNonNull(response).getResults();
        List<IssueDTO> filteredIssues = getFilteredIssues(issues, startDate, endDate);
        List<IssueDTO> assigneeSeperatedIssues = getAssigneeSeperatedIssues(filteredIssues);
        List<IssueDTO> sortedIssues = getSortedIssues(assigneeSeperatedIssues);
        List<IssueDTO> removedDuplicateNameIssues = duplicateNameRemover(sortedIssues);
        Map<String, String> issueModuleNameMap = getIssueModuleNameMap();

        return csvFormatBuilder(removedDuplicateNameIssues, issueModuleNameMap, startDate);
    }

    private List<IssueDTO> getFilteredIssues(List<IssueDTO> issues, LocalDate startDate, LocalDate endDate) {
        Set<String> allowedStates = Set.of("in progress", "done");

        return new java.util.ArrayList<>(issues.stream()
            .filter(issue -> {
                if (issue.getState() == null) return false;
                String stateName = issue.getState().getName();
                if (stateName == null || !allowedStates.contains(stateName.toLowerCase())) return false;

                if (stateName.toLowerCase().contains("done") && issue.getCompleted_at() != null) {
                    try {
                        OffsetDateTime completedAt = OffsetDateTime.parse(issue.getCompleted_at());
                        LocalDate completedDate = completedAt.toLocalDate();
                        return (startDate == null || !completedDate.isBefore(startDate)) &&
                                (endDate == null || !completedDate.isAfter(endDate));
                    } catch (Exception e) {
                        return false;
                    }
                }
                return true;
            }
            )
            .toList()
        );
    }

    private List<IssueDTO> getAssigneeSeperatedIssues(List<IssueDTO> filteredIssues) {
        var seperatedIssues = new ArrayList<>(filteredIssues);
        filteredIssues.forEach(
            issueDTO -> {
                if(issueDTO.getAssignees().size() > 1) {
                    issueDTO.getAssignees().forEach(
                        assignee -> seperatedIssues.add(new IssueDTO(issueDTO.getId(), issueDTO.getName(), issueDTO.getState(), List.of(assignee)))
                    );
                    seperatedIssues.remove(issueDTO);
                }
            }
        );
        return seperatedIssues;
    }

    private List<IssueDTO> getSortedIssues(List<IssueDTO> issues) {
        issues.sort(Comparator.nullsLast(
            Comparator.comparing(
                issue -> {
                    if (issue.getAssignees() != null && !issue.getAssignees().isEmpty() && issue.getAssignees().get(0) != null) {
                        return issue.getAssignees().get(0).getDisplay_name();
                    }
                    return null;
                },
                Comparator.nullsLast(String::compareTo)
            )
        ));
        return issues;
    }

    private List<IssueDTO> duplicateNameRemover(List<IssueDTO> issues) {
        var namesList = new ArrayList<>();
        issues.forEach(
            issueDTO -> {
                if(issueDTO.getAssignees().isEmpty()) return;
                var assignee = issueDTO.getAssignees().get(0);
                if (namesList.contains(assignee.getDisplay_name()) ) {
                    assignee.setDisplay_name("duplicate");
                } else {
                    namesList.add(assignee.getDisplay_name());
                }
            }
        );

        return issues;
    }

    private Map<String, String> getIssueModuleNameMap() {
        PlaneModuleResponse allModules = planeApiClient.getModules();
        Map<String, String> issueModuleNameMap = new HashMap<>();

        allModules.getResults().forEach(
                module -> {
                    PlaneIssueResponse moduleIssues = planeApiClient.getModuleIssues(module.getId());
                    Objects.requireNonNull(moduleIssues).getResults().forEach(
                            issue -> issueModuleNameMap.put(issue.getId(), module.getName())
                    );
                }
        );

        return issueModuleNameMap;
    }

    private ByteArrayInputStream csvFormatBuilder(List<IssueDTO> sortedIssues, Map<String, String> issueModuleNameMap, LocalDate startDate) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        CSVFormat format = CSVFormat.Builder.create()
                .setHeader("Person", "Week Commencing", "Activity", "Detail", "Days Expected to spend on this activity this week", "Achieved", "Links to outputs (where relevant)")
                .build();
        CSVPrinter printer = new CSVPrinter(new PrintWriter(out), format);

        for (IssueDTO issue : sortedIssues) {
            printer.printRecord(
                    assigneeNamesResolver(issue.getAssignees()),
                    startDate,
                    issueModuleNameMap.get(issue.getId()),
                    issue.getName(),
                    null,
                    issue.getState().getName().equalsIgnoreCase("done") ? "Done" : "On going",
                    null
            );
        }

        printer.flush();
        return new ByteArrayInputStream(out.toByteArray());
    }

    private String assigneeNamesResolver(List<Assignee> assignees) {
        if(assignees.isEmpty()) return "No member(s) assigned";

        Assignee assignee = assignees.get(0);
        if(assignee.getDisplay_name().equals("duplicate")) return "";

        return assignee.getFirst_name()+" "+assignee.getLast_name();
    }
}
