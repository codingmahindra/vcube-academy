package com.vcube.academy.controller;

import com.vcube.academy.dto.search.GlobalSearchResultDto;
import com.vcube.academy.service.GlobalSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class GlobalSearchController {

    private final GlobalSearchService searchService;

    @GetMapping
    public ResponseEntity<List<GlobalSearchResultDto>> search(
            @RequestParam(name = "q", defaultValue = "") String query) {
        List<GlobalSearchResultDto> results = searchService.search(query);
        return ResponseEntity.ok(results);
    }
}
