package com.example.TargetManagement.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
public class TargetForm {

    @NotEmpty
    @Length(min = 1, max = 255)
    private String title;

}
