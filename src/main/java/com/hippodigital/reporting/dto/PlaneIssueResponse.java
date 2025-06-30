package com.hippodigital.reporting.dto;

import java.util.List;

public class PlaneIssueResponse {
    private List<IssueDTO> results;

    public List<IssueDTO> getResults() {
        return results;
    }

    public void setResults(List<IssueDTO> results) {
        this.results = results;
    }
}
