package com.tokioschool.filmapp.validators.anotations;

import com.tokioschool.filmapp.validators.anotations.impl.EnumValidImpl;
import com.tokioschool.filmapp.validators.anotations.impl.PasswordBisImpl;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.springframework.lang.NonNull;

import java.lang.annotation.*;

@Documented
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumValidImpl.class)
public @interface EnumValid {

    // required params
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    // business drools
    String message() default "Invalid enum value";
    @NonNull Class<? extends Enum<?>> target(); // enumerations to validate
    boolean required() default false;
}
