package at.pria.joust.webapp.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD, METHOD})
@Retention(RUNTIME)
@Constraint(validatedBy = CurrentPasswordValidator.class)
@Documented
public @interface CurrentPassword {
    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * @return Whether the username must exist
     */
    boolean exists() default true;

    /**
     * Defines several <code>@CurrentPassword</code> annotations on the same element
     *
     * @see CurrentPassword
     */
    @Target({TYPE, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        CurrentPassword[] value();
    }
}