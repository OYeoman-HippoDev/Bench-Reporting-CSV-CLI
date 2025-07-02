package com.hippodigital.reporting.service;

import com.hippodigital.reporting.client.PlaneApiClient;
import com.hippodigital.reporting.dto.Assignee;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Service
public class PlaneMemberCsvService {

    private final PlaneApiClient planeApiClient;

    public PlaneMemberCsvService(PlaneApiClient planeApiClient) {
        this.planeApiClient = planeApiClient;
    }

    public ByteArrayInputStream fetchMembersToCsv() throws IOException {
        List<Assignee> assignees = planeApiClient.getMembers();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        CSVFormat format = CSVFormat.Builder.create()
                .setHeader("Person", "Email")
                .build();
        CSVPrinter printer = new CSVPrinter(new PrintWriter(out), format);

        for (Assignee assignee : assignees) {
            printer.printRecord(
                    assigneeNameResolver(assignee),
                    assignee.getEmail()
            );
        }

        printer.flush();
        return new ByteArrayInputStream(out.toByteArray());
    }

    private String assigneeNameResolver(Assignee assignee) {
        return assignee.getFirst_name() + " " + assignee.getLast_name();
    }
}
