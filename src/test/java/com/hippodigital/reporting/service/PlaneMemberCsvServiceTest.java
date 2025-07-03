package com.hippodigital.reporting.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hippodigital.reporting.client.PlaneApiClient;
import com.hippodigital.reporting.dto.Assignee;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlaneMemberCsvServiceTest {

    @Mock
    private PlaneApiClient client;

    @InjectMocks
    private PlaneMemberCsvService memberCsvService;

    @Test
    void fetchAndConvertToCsvTest() throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        List<Assignee> mockUsers = mapper.readValue(
                getClass().getClassLoader().getResource("member-response.json"),
                new TypeReference<>() {}
        );
        when(client.getMembers()).thenReturn(mockUsers);

        var result = memberCsvService.fetchMembersToCsv();
        byte[] actualBytes = result.readAllBytes();
        String actual = new String(actualBytes, StandardCharsets.UTF_8);

        assertNotNull(actual);
        assertTrue(actual.contains("first-name-1 last-name-1"));
        assertTrue(actual.contains("first-name-2 last-name-2"));
        assertTrue(actual.contains("email.1@hippodigital.co.uk"));
        assertTrue(actual.contains("email.2@hippodigital.co.uk"));
    }
}
