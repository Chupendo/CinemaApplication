package com.tokioschool.filmapp.validators.anotations;

import com.tokioschool.filmapp.validators.anotations.impl.EnumListStringValidImpl;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.springframework.lang.NonNull;

import java.lang.annotation.*;

/**
 * Anotación personalizada para validar que un valor pertenece a una lista de valores de un enum.
 *
 * Esta anotación se utiliza para restringir los valores de un campo o método a los valores definidos
 * en un tipo enumerado específico. Es compatible con la validación de campos, métodos y otras anotaciones.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Documented
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumListStringValidImpl.class)
public @interface EnumListValid {

    /**
     * Mensaje de error predeterminado que se mostrará si la validación falla.
     *
     * @return El mensaje de error.
     */
    String message() default "Invalid enum value";

    /**
     * Clase del enum cuyos valores se validarán.
     *
     * @return La clase del enum objetivo.
     */
    @NonNull Class<? extends Enum<?>> target();

    /**
     * Indica si el valor es obligatorio.
     *
     * @return true si el valor es obligatorio, false en caso contrario.
     */
    boolean required() default false;

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
}