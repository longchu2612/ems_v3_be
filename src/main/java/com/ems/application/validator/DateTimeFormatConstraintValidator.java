package com.ems.application.validator;

import org.springframework.util.ObjectUtils;

import com.ems.config.MsgTranslator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.OffsetDateTime;

public class DateTimeFormatConstraintValidator
        implements ConstraintValidator<DateTimeFormat, Object> {

    private DateTimeFormat validation;

    @Override
    public void initialize(DateTimeFormat validation) {
        this.validation = validation;
    }

    private String getMessage() {
        return MsgTranslator.toLocale(validation.message());
    }

    @Override
    public boolean isValid(Object datetime, ConstraintValidatorContext cxt) {
        boolean valid = true;
        if (ObjectUtils.isEmpty(datetime)) {
            return true;
        }
        try {
            OffsetDateTime.parse((String) datetime);
        } catch (Exception ex) {
            valid = false;
        }
        if (!valid) {
            cxt.disableDefaultConstraintViolation();
            cxt.buildConstraintViolationWithTemplate(getMessage()).addConstraintViolation();
            return false;
        }
        return true;
    }
}
