package com.lifeapp.controller;

import com.lifeapp.common.ApiResponse;
import com.lifeapp.model.CheckInRecord;
import com.lifeapp.service.CheckInService;
import com.lifeapp.vo.CheckInBoardItem;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/check-in")
public class CheckInController {

    private final CheckInService checkInService;

    public CheckInController(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

    @GetMapping("/today")
    public ApiResponse<CheckInRecord> today() {
        return ApiResponse.ok(checkInService.today());
    }

    @PostMapping
    public ApiResponse<CheckInRecord> checkIn() {
        return ApiResponse.ok(checkInService.checkIn());
    }

    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> statistics() {
        return ApiResponse.ok(checkInService.statistics());
    }

    @GetMapping("/calendar")
    public ApiResponse<List<CheckInRecord>> calendar(@RequestParam(required = false) Integer year,
                                                     @RequestParam(required = false) Integer month) {
        LocalDate now = LocalDate.now();
        int targetYear = year == null ? now.getYear() : year;
        int targetMonth = month == null ? now.getMonthValue() : month;
        return ApiResponse.ok(checkInService.calendar(targetYear, targetMonth));
    }

    @GetMapping("/public-board")
    public ApiResponse<List<CheckInBoardItem>> publicBoard() {
        return ApiResponse.ok(checkInService.publicBoard());
    }
}
