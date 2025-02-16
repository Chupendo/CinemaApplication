package com.tokioschool.filmapp.validators.anotations;

import com.tokioschool.filmapp.enums.TYPE_ARTIS_DTO;
import com.tokioschool.filmapp.validators.anotations.impl.TypeArtistValidImpl;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.springframework.lang.NonNull;

import java.lang.annotation.*;

@Documented
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TypeArtistValidImpl.class)
public @interface TypeArtistValid {

    // required params
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String message() default "Invalid enum value";

    @NonNull TYPE_ARTIS_DTO target();
}
