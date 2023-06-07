package com.example.TargetManagement.entity;

import java.time.LocalDate;

public record TargetRecord2(Integer id, Integer user_id, String title, String createdDate, String sharedUrl,
                            Boolean achivementFlag, LocalDate startTerm, LocalDate endTerm, Boolean every, Boolean mon,
                            Boolean tues, Boolean wednes, Boolean thurs, Boolean fri, Boolean satur, Boolean sun) {
}
