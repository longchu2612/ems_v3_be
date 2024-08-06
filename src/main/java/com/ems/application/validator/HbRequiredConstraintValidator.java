package com.ems.application.validator;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class HbRequiredConstraintValidator implements ConstraintValidator<HbRequired, Object> {

    private HbRequired validation;

    @Override
    public void initialize(HbRequired validation) {
        this.validation = validation;
    }

    private String getMessage() {
        return String.format(validation.message(), validation.name());
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext cxt) {
        boolean valid = true;
        if (value == null) {
            valid = false;
        } else if (value instanceof String) {
            if (!StringUtils.hasText((String) value)) {
                valid = false;
            }
        } else if (value instanceof List) {
            if (CollectionUtils.isEmpty((List<?>) value)) {
                valid = false;
            }
        } else if (value instanceof String[]) {
            String[] array = (String[]) value;
            if (array.length == 0) {
                valid = false;
            }
        }

        if (!valid) {
            cxt.disableDefaultConstraintViolation();
            cxt.buildConstraintViolationWithTemplate(this.getMessage()).addConstraintViolation();
            return false;
        }
        return true;
    }
}
