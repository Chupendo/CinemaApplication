package com.tokioschool.filmapp.validation;

import com.tokioschool.filmapp.dto.user.UserFormDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.CustomValidatorBean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RegisterUserValidation extends CustomValidatorBean {

    // load other validations
    private final LocalValidatorFactoryBean localValidatorFactoryBean;

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return UserFormDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        log.debug("RegisterUserValidation start");
        localValidatorFactoryBean.validate(target, errors);

        final UserFormDTO userFormDTO = (UserFormDTO) target;

        if(userFormDTO==null ||
                userFormDTO.getRoles()==null ||
                userFormDTO.getRoles().isEmpty() ||
                !validationRolNewUser(userFormDTO.getRoles())
        ){
            errors.rejectValue(
                    "roles",
                    "roles.code", // message i18n custom
                    "Roles of user don't allow"
            );
        }
    }


    /**
     * Validate that user or an anonymous user don't create a new user type admin
     *
     * @param roles collection roles of new user
     * @return true if the type roles is allowed, otherwise false
     */
    private boolean validationRolNewUser(List<String> roles) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        boolean isAdmin = false;
        for (String authority : authorities){// verify if own quest is type ADMIN
            if (authority.equalsIgnoreCase("ROLE_ADMIN")) {
                isAdmin = true;
                break;
            }
        }

        boolean typeUserAllow = true;
        if( !isAdmin ){ // valid that rol isn't ADMIN
            for(String role : roles ){
                if( role.toUpperCase().contains( "ADMIN" ) ){
                    typeUserAllow = false;
                    break;
                }
            }
        }

        return typeUserAllow;
    }
}
