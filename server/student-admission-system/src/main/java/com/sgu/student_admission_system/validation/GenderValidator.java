package com.sgu.student_admission_system.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class GenderValidator implements ConstraintValidator<ValidGender, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;

        return value.equalsIgnoreCase("NAM")
                || value.equalsIgnoreCase("NU");
    }
}
