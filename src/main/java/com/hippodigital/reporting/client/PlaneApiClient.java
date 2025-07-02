package com.hippodigital.reporting.client;

import com.hippodigital.reporting.dto.Assignee;
import com.hippodigital.reporting.dto.PlaneIssueResponse;
import com.hippodigital.reporting.dto.PlaneModuleResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class PlaneApiClient {

    private final RestTemplate restTemplate;
    @Value("${plane.hippo.bench.url}")
    private String PLANE_HIPPO_BENCH_URL_PREFIX;
    @Value("${plane.api.key}")
    private String API_KEY;

    public PlaneApiClient(RestTemplate restTemplate) {this.restTemplate = restTemplate;}

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", API_KEY);
        return headers;
    }

    public PlaneIssueResponse getIssues() {
        HttpEntity<Void> entity = new HttpEntity<>(buildHeaders());
        String issueUrl = PLANE_HIPPO_BENCH_URL_PREFIX + "/issues?expand=state,assignees";

        ResponseEntity<PlaneIssueResponse> response = restTemplate.exchange(
                issueUrl,
                HttpMethod.GET,
                entity,
                PlaneIssueResponse.class
        );

        return response.getBody();
    }

    public PlaneModuleResponse getModules() {
        HttpEntity<Void> entity = new HttpEntity<>(buildHeaders());
        String moduleUrl = PLANE_HIPPO_BENCH_URL_PREFIX + "/modules?fields=id,name";

        ResponseEntity<PlaneModuleResponse> response = restTemplate.exchange(
                moduleUrl,
                HttpMethod.GET,
                entity,
                PlaneModuleResponse.class
        );

        return response.getBody();
    }

    public PlaneIssueResponse getModuleIssues(String id) {
        HttpEntity<Void> entity = new HttpEntity<>(buildHeaders());
        String issuesForModuleUrl = PLANE_HIPPO_BENCH_URL_PREFIX + "/modules/" + id + "/module-issues?fields=id";

        ResponseEntity<PlaneIssueResponse> response = restTemplate.exchange(
                issuesForModuleUrl,
                HttpMethod.GET,
                entity,
                PlaneIssueResponse.class
        );

        return response.getBody();
    }

    public List<Assignee> getMembers() {
        HttpEntity<Void> entity = new HttpEntity<>(buildHeaders());
        String membersUrl = PLANE_HIPPO_BENCH_URL_PREFIX + "/members/";

        ResponseEntity<List<Assignee>> response = restTemplate.exchange(
                membersUrl,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        return response.getBody();
    }
}
