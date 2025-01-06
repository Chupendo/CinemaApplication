package com.tokioschool.filmapp.validators.anotations;

import com.tokioschool.filmapp.validators.anotations.impl.PasswordBisImpl;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordBisImpl.class)
public @interface PasswordBis {

    String message() default "Invalid password";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
