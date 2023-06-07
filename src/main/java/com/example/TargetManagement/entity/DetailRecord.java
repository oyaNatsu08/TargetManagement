package com.example.TargetManagement.entity;

import java.time.LocalDate;
import java.util.List;

public record DetailRecord(Integer id, Integer targetId, String content, LocalDate startTerm, LocalDate endTerm, String week) {
}
