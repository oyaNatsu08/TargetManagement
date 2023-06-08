package com.example.TargetManagement.entity;

import java.time.LocalDate;
import java.util.List;

public record TargetRecord3(List<TargetRecord2> targetsRecord, List<List<DetailRecord2>> detailsRecord) {
}
