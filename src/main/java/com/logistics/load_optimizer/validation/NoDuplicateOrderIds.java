package com.logistics.load_optimizer.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NoDuplicateOrderIdsValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface NoDuplicateOrderIds {
    String message() default "Duplicate order IDs are not allowed";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}