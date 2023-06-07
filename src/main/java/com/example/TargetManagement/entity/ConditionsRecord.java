package com.example.TargetManagement.entity;

import java.time.LocalDate;

public record ConditionsRecord(Integer id, LocalDate startTerm, LocalDate endTerm, Boolean every, Boolean mon,
                               Boolean tues, Boolean wednes, Boolean thurs, Boolean fri, Boolean satur, Boolean sun) {
}
