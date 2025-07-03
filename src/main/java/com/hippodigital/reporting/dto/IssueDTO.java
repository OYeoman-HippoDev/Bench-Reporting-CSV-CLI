package com.hippodigital.reporting.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class IssueDTO {
    private String id;
    private String type_id;
    private String created_at;
    private String updated_at;
    private String deleted_at;
    private String point;
    private String name;
    private String description_html;
    private String description_binary;
    private String priority;
    private String start_date;
    private String target_date;
    private int sequence_id;
    private Double sort_order;
    private String completed_at;
    private String archived_at;
    private String is_draft;
    private String external_source;
    private String external_id;
    private String created_by;
    private String updated_by;
    private String project;
    private String workspace;
    private String parent;
    private State state;
    private int estimate_point;
    private String type;
    private List<Assignee> assignees;
    private List<String> labels;

    public IssueDTO(String id, String name, State state, List<Assignee> assignees) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.assignees = assignees;
    }
}
