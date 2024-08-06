package com.ems.application.validator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = FieldPatternConstraintValidator.class)
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface FieldPattern {

    int index() default 0;

    String name() default "";

    String regexp();

    String message() default "validation.formula.invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
