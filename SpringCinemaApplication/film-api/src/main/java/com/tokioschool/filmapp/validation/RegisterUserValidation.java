package com.tokioschool.filmapp.validation;

import com.tokioschool.filmapp.dto.user.UserFormDto;
import com.tokioschool.filmapp.records.SearchUserRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.CustomValidatorBean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

/**
 * Clase de validación personalizada para el registro de usuarios.
 *
 * Esta clase extiende {@link CustomValidatorBean} y proporciona validaciones adicionales
 * para los datos de entrada relacionados con el registro de usuarios.
 *
 * Anotaciones:
 * - {@link Component}: Marca esta clase como un componente de Spring.
 * - {@link RequiredArgsConstructor}: Genera un constructor con los argumentos requeridos para los campos finales.
 * - {@link Slf4j}: Proporciona un logger para la clase.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RegisterUserValidation extends CustomValidatorBean {

    /**
     * Bean de validación local para realizar validaciones estándar.
     */
    @Qualifier("filmApiLocalValidatorFactoryBean")
    private final LocalValidatorFactoryBean localValidatorFactoryBean;

    /**
     * Determina si esta clase de validación es compatible con el tipo de clase proporcionado.
     *
     * @param clazz Clase objetivo para la validación.
     * @return true si la clase es compatible, de lo contrario false.
     */
    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return UserFormDto.class.isAssignableFrom(clazz) || SearchUserRecord.class.isAssignableFrom(clazz);
    }

    /**
     * Realiza la validación de un objeto objetivo.
     *
     * @param target Objeto a validar.
     * @param errors Objeto {@link Errors} para registrar errores de validación.
     */
    @Override
    public void validate(Object target, Errors errors) {
        log.debug("RegisterUserValidation start");
        localValidatorFactoryBean.validate(target, errors);

        if (!UserFormDto.class.isInstance(target)) {
            return;
        }

        final UserFormDto userFormDTO = (UserFormDto) target;

        if (userFormDTO == null ||
                userFormDTO.getRoles() == null ||
                userFormDTO.getRoles().isEmpty() ||
                !validationRolNewUser(userFormDTO.getRoles())) {
            errors.rejectValue(
                    "roles",
                    "roles.code", // mensaje i18n personalizado
                    "Roles of user don't allow"
            );
        }
    }

    /**
     * Valida que un usuario o un usuario anónimo no pueda crear un nuevo usuario con rol de administrador.
     *
     * @param roles Lista de roles del nuevo usuario.
     * @return true si los roles son permitidos, de lo contrario false.
     */
    private boolean validationRolNewUser(List<String> roles) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        boolean isAdmin = false;
        for (String authority : authorities) { // Verifica si el usuario actual tiene rol ADMIN
            if (authority.equalsIgnoreCase("ROLE_ADMIN")) {
                isAdmin = true;
                break;
            }
        }

        boolean typeUserAllow = true;
        if (!isAdmin) { // Valida que el rol no sea ADMIN
            for (String role : roles) {
                if (role.toUpperCase().contains("ADMIN")) {
                    typeUserAllow = false;
                    break;
                }
            }
        }

        return typeUserAllow;
    }
}