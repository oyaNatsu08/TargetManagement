package com.example.TargetManagement.form;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UserForm {

    @NotEmpty
    @Length(min = 1, max = 50)
    private String loginId;

    @NotEmpty
    @Length(min = 1, max = 50)
    private String name;

    @NotEmpty
    @Length(min = 1, max = 50)
    private String password;

    @NotEmpty
    @Length(min = 1, max = 50)
    private String password2;
}
