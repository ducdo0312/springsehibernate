package com.example.springsehibernate.Validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class    FileSizeValidator implements ConstraintValidator<MaxFileSize, byte[]> {

    private long maxFileSize;

    @Override
    public void initialize(MaxFileSize constraintAnnotation) {
        this.maxFileSize = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(byte[] file, ConstraintValidatorContext context) {
        if (file == null) {
            return true; // Null files are considered valid. Use @NotNull for null checks.
        }
        return file.length <= maxFileSize;
    }
}
