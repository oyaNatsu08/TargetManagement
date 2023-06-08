package com.example.TargetManagement.entity;

import java.time.LocalDate;
import java.util.List;

public record TargetRecord3(TargetRecord2 targetRecord, List<DetailRecord2> detailRecord) {
}
