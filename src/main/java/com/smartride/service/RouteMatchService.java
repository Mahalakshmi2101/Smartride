package com.smartride.service;

import com.smartride.dto.RouteMatchResponse;
import java.util.List;

public interface RouteMatchService {
    List<RouteMatchResponse> findDirectMatches(String source, String destination);
    List<RouteMatchResponse> findAllMatches(String source, String destination);
}