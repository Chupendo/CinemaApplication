package com.tokioschool.filmapp.validators.anotations;

import com.tokioschool.filmapp.enums.TYPE_ARTIS_DTO;
import com.tokioschool.filmapp.validators.anotations.impl.TypeArtistValidImpl;
import com.tokioschool.filmapp.validators.anotations.impl.TypeArtistsValidImpl;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.springframework.lang.NonNull;

import java.lang.annotation.*;

/**
 * Anotación personalizada para validar que un valor pertenece a un tipo específico de artista.
 *
 * Esta anotación se utiliza para restringir los valores de un campo o método a los valores definidos
 * en el enum TYPE_ARTIS_DTO. Es compatible con la validación de campos, métodos y otras anotaciones.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Documented
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TypeArtistsValidImpl.class)
public @interface TypeArtistsValid {

    /**
     * Grupos de validación a los que pertenece esta restricción.
     *
     * @return Los grupos de validación.
     */
    Class<?>[] groups() default {};

    /**
     * Carga útil adicional para los clientes de validación.
     *
     * @return Las clases de carga útil.
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * Mensaje de error predeterminado que se mostrará si la validación falla.
     *
     * @return El mensaje de error.
     */
    String message() default "Invalid enum value";

    /**
     * Enum objetivo cuyos valores se validarán.
     *
     * @return El enum TYPE_ARTIS_DTO objetivo.
     */
    @NonNull TYPE_ARTIS_DTO target();
}