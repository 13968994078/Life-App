package com.lifeapp.service;

import com.lifeapp.model.CheckInRecord;

import com.lifeapp.vo.CheckInBoardItem;

import java.util.List;
import java.util.Map;

public interface CheckInService {

    CheckInRecord today();

    CheckInRecord checkIn();

    Map<String, Object> statistics();

    List<CheckInRecord> calendar(int year, int month);

    List<CheckInBoardItem> publicBoard();
}
