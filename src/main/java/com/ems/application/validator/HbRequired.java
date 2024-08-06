package com.ems.application.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = HbRequiredConstraintValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface HbRequired {

    /**
     *
     */
    int index() default 0;

    /**
     *
     */
    String type() default "";

    /**
     *
     */
    String name() default "";

    /**
     *
     */
    String message() default "Please enter %s";

    /**
     *
     */
    Class<?>[] groups() default {};

    /**
     *
     */
    Class<? extends Payload>[] payload() default {};
}
