package com.example.springsehibernate.Validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FileSizeValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MaxFileSize {
    String message() default "File phải ít nhỏ hơn 10MB";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    long value() default 10_485_760; // 10MB in bytes
}