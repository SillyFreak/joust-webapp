package at.pria.joust.webapp.validators;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Source: https://stackoverflow.com/a/2155576/371191
 */
public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String firstField, secondField;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        firstField = constraintAnnotation.first();
        secondField = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        context
                .buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode(secondField)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();

        BeanWrapper bean = new BeanWrapperImpl(value);
        Object first = bean.getPropertyValue(firstField);
        Object second = bean.getPropertyValue(secondField);

        return first == second || first != null && first.equals(second);
    }
}