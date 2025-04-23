package com.tokioschool.filmapp.validators.anotations;

import com.tokioschool.filmapp.validators.anotations.impl.PasswordBisImpl;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Anotación personalizada para validar contraseñas.
 *
 * Esta anotación se utiliza para aplicar una validación personalizada a nivel de clase
 * que verifica la validez de las contraseñas según una lógica específica definida
 * en la implementación de la validación.
 *
 * Es compatible con la validación de clases y se utiliza para garantizar que las contraseñas
 * cumplan con los requisitos establecidos.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Documented
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordBisImpl.class)
public @interface PasswordBis {

    /**
     * Mensaje de error predeterminado que se mostrará si la validación falla.
     *
     * @return El mensaje de error.
     */
    String message() default "{form.error.user.password.bis}";

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