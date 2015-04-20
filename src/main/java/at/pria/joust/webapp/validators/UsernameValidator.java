package at.pria.joust.webapp.validators;

import at.pria.joust.webapp.auth.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UsernameValidator implements ConstraintValidator<Username, Object> {
    private boolean exists;

    @Autowired
    private UserService users;

    @Override
    public void initialize(Username constraintAnnotation) {
        exists = constraintAnnotation.exists();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        String username = (String) value;
        return users.getUserDetailsManager().userExists(username) == exists;
    }
}