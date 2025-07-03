package com.hippodigital.reporting.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Assignee {
    private String id;
    private String first_name;
    private String last_name;
    private String email;
    private String avatar;
    private String avatar_url;
    private String display_name;
}
