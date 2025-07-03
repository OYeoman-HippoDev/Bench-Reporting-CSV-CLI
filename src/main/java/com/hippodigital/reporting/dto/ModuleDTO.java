package com.hippodigital.reporting.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class ModuleDTO {
    private String id;
    private String name;
    private List<Assignee> members;
}
