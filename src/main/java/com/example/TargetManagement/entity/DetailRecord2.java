package com.example.TargetManagement.entity;

import java.time.LocalDate;
import java.util.List;

public record DetailRecord2(Integer id, Integer targetId, List<String> content) {
}
