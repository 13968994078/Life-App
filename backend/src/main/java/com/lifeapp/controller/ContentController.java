package com.lifeapp.controller;

import com.lifeapp.common.ApiResponse;
import com.lifeapp.service.ContentService;
import com.lifeapp.vo.HomeContentResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/content")
public class ContentController {

    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    @GetMapping("/home")
    public ApiResponse<HomeContentResponse> home() {
        return ApiResponse.ok(contentService.getHomeContent());
    }
}
