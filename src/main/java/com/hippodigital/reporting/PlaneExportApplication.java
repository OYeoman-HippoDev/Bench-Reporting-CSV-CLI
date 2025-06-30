package com.hippodigital.reporting;

import com.hippodigital.reporting.service.PlaneIssueCsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileOutputStream;
import java.time.LocalDate;

@SpringBootApplication
public class PlaneExportApplication implements CommandLineRunner {

    @Autowired
    private PlaneIssueCsvService csvService;

    public static void main(String[] args) {
        SpringApplication.run(PlaneExportApplication.class, args);
    }

    @Override
    public void run(String... args) {
        LocalDate startDate = null;
        LocalDate endDate = null;

        for (String arg : args) {
            if (arg.startsWith("--startDate=")) {
                startDate = LocalDate.parse(arg.substring("--startDate=".length()));
            } else if (arg.startsWith("--endDate=")) {
                endDate = LocalDate.parse(arg.substring("--endDate=".length()));
            }
        }

        try {
            var csvStream = csvService.fetchAndConvertToCsv(startDate, endDate);
            var fileName = startDate != null && endDate != null
                    ? "bench_plane_issues_"+startDate+"_to_"+endDate+".csv"
                    : "bench_plane_issues";
            try (FileOutputStream fos = new FileOutputStream(fileName)) {
                csvStream.transferTo(fos);
            }
            System.out.println("CSV exported to: " + fileName);
        } catch (Exception e) {
            System.err.println("Failed to export CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
