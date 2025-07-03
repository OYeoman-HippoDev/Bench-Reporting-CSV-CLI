package com.hippodigital.reporting.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hippodigital.reporting.client.PlaneApiClient;
import com.hippodigital.reporting.dto.PlaneIssueResponse;
import com.hippodigital.reporting.dto.PlaneModuleResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlaneIssueCsvServiceTest {

    @Mock
    private PlaneApiClient client;

    @InjectMocks
    private PlaneIssueCsvService issueCsvService;

    @Test
    void fetchAndConvertToCsvTest() throws IOException {

        LocalDate startDate = LocalDate.of(2025, 6, 20);
        LocalDate endDate = LocalDate.of(2025, 6, 27);

        InputStream issueResponse = getClass().getClassLoader().getResourceAsStream("issue-response.json");
        ObjectMapper issueObjectMapper = new ObjectMapper();
        PlaneIssueResponse mockIssueResponse = issueObjectMapper.readValue(issueResponse, PlaneIssueResponse.class);
        when(client.getIssues()).thenReturn(mockIssueResponse);

        InputStream moduleResponse = getClass().getClassLoader().getResourceAsStream("module-response.json");
        ObjectMapper moduleObjectMapper = new ObjectMapper();
        PlaneModuleResponse mockModuleResponse = moduleObjectMapper.readValue(moduleResponse, PlaneModuleResponse.class);
        when(client.getModules()).thenReturn(mockModuleResponse);

        InputStream moduleIssueResponse = getClass().getClassLoader().getResourceAsStream("module-issue-response.json");
        ObjectMapper moduleIssueObjectMapper = new ObjectMapper();
        PlaneIssueResponse mockModuleIssueResponse = moduleIssueObjectMapper.readValue(moduleIssueResponse, PlaneIssueResponse.class);

        InputStream moduleNoIssueResponse = getClass().getClassLoader().getResourceAsStream("module-no-issue-response.json");
        ObjectMapper moduleNoIssueObjectMapper = new ObjectMapper();
        PlaneIssueResponse mockModuleNoIssueResponse = moduleNoIssueObjectMapper.readValue(moduleNoIssueResponse, PlaneIssueResponse.class);

        when(client.getModuleIssues(anyString())).thenAnswer(
                moduleId -> {
                    String id = moduleId.getArgument(0);
                    if (id.equals("4")) {
                        return mockModuleIssueResponse;
                    } else {
                        return mockModuleNoIssueResponse;
                    }
                }
        );

        var result = issueCsvService.fetchAndConvertToCsv(startDate, endDate);
        byte[] actualBytes = result.readAllBytes();
        String actual = new String(actualBytes, StandardCharsets.UTF_8);

        assertNotNull(actual);
        assertTrue(actual.contains("Done in date range, 2 assignees issue"));
        assertTrue(actual.contains("In progress issue"));
        assertFalse(actual.contains("Done outside date range issue"));
        assertFalse(actual.contains("Todo issue"));
        assertFalse(actual.contains("Backlog issue"));
        assertFalse(actual.contains("Cancelled issue"));

        Pattern twoAssigneeIssuepattern = Pattern.compile("Done in date range, 2 assignees issue", Pattern.CASE_INSENSITIVE);
        Matcher twoAssigneeIssueMatcher = twoAssigneeIssuepattern.matcher(actual);
        int issueNameCount = 0;
        while (twoAssigneeIssueMatcher.find()) issueNameCount++;

        assertEquals(2, issueNameCount, "Expected issue name to appear twice, one for each assignee");

        Pattern firstAssigneePattern = Pattern.compile("first-name-1 last-name-1", Pattern.CASE_INSENSITIVE);
        Matcher firstAssigneeMatcher = firstAssigneePattern.matcher(actual);
        int AssigneeOneCount = 0;
        while (firstAssigneeMatcher.find()) AssigneeOneCount++;

        assertEquals(1, AssigneeOneCount, "Expected first-name-1 last-name-1 to appear once");

        Pattern SecondAssigneePattern = Pattern.compile("first-name-2 last-name-2", Pattern.CASE_INSENSITIVE);
        Matcher SecondAssigneeMatcher = SecondAssigneePattern.matcher(actual);
        int AssigneeTwoCount = 0;
        while (SecondAssigneeMatcher.find()) AssigneeTwoCount++;

        assertEquals(1, AssigneeTwoCount, "Expected first-name-2 last-name-2 to appear once");
    }
}
