package com.ems.application.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = FieldRequiredConstraintValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldRequired {

    int index() default 0;

    String type() default "";

    String name() default "";

    String message() default "validation.required";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
