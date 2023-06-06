package com.example.TargetManagement.entity;

import java.util.List;

public record DetailRecord(Integer id, Integer targetId, List<String> content, List<Integer> startTerm, List<Integer> endTerm, List<String> week) {
}
