package com.hippodigital.reporting.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class PlaneModuleResponse {
    private String grouped_by;
    private String sub_grouped_by;
    private int total_count;
    private String next_cursor;
    private String prev_cursor;
    private boolean next_page_results;
    private boolean prev_page_results;
    private int count;
    private int total_pages;
    private int total_results;
    private String extra_stats;
    private List<ModuleDTO> results;
}
