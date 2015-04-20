package at.pria.joust.webapp.validators;

import at.pria.joust.webapp.auth.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CurrentPasswordValidator implements ConstraintValidator<CurrentPassword, Object> {
    @Autowired
    private UserService users;

    @Override
    public void initialize(CurrentPassword constraintAnnotation) {}

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String password = (String) value;

        UserDetails user = users.getUserDetailsManager().loadUserByUsername(username);
        return users.getPasswordEncoder().matches(password, user.getPassword());
    }
}