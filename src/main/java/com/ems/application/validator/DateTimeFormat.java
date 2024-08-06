package com.ems.application.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DateTimeFormatConstraintValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface DateTimeFormat {

    int index() default 0;

    String type() default "";

    String name() default "";

    String message() default "dateTime.notValid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
