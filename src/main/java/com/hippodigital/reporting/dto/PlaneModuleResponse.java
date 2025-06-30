package com.hippodigital.reporting.dto;

import java.util.List;

public class PlaneModuleResponse {
    private List<ModuleDTO> results;

    public List<ModuleDTO> getResults() {
        return results;
    }

    public void setResults(List<ModuleDTO> results) {
        this.results = results;
    }
}
