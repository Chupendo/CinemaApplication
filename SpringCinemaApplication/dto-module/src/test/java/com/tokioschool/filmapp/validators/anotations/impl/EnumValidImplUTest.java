package com.tokioschool.filmapp.validators.anotations.impl;

import com.tokioschool.filmapp.enums.RoleEnum;
import com.tokioschool.filmapp.validators.anotations.EnumValid;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.Annotation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
class EnumValidImplUTest {

    private EnumValidImpl validator;

    @BeforeEach
    void init(){
        validator = new EnumValidImpl();
        EnumValid annotation = new EnumValid() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return EnumValid.class;
            }

            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }

            @Override
            public Class<? extends Payload>[] payload() {
                return new Class[0];
            }

            @Override
            public String message() {
                return "Invalid value";
            }

            @Override
            public Class<? extends Enum<?>> target() {
                return RoleEnum.class;
            }

            @Override
            public boolean required() {
                return true;
            }
        };
        validator.initialize(annotation);
    }

    @Test
    void givenRoleNameValid_whenValidated_thenReturnTrue() {
        ConstraintValidatorContext context = Mockito.mock(ConstraintValidatorContext.class);
        assertTrue(validator.isValid("USER", context));
    }

    @Test
    void givenRoleNameInvalid_whenValidated_thenReturnFalse() {
        ConstraintValidatorContext context = Mockito.mock(ConstraintValidatorContext.class);
        assertFalse(validator.isValid("YELLOW", context));
    }

    @Test
    void givenRoleNameNull_whenValidatedRequired_thenReturnFalse() {
        ConstraintValidatorContext context = Mockito.mock(ConstraintValidatorContext.class);
        assertFalse(validator.isValid(null, context));
    }
    @Test
    void givenRoleNameNull_whenValidatedDontRequired_thenReturnTrue() {
        ConstraintValidatorContext context = Mockito.mock(ConstraintValidatorContext.class);
        validator.initialize(new EnumValid() {
            @Override
            public Class<? extends Enum<?>> target() {
                return RoleEnum.class;
            }

            @Override
            public boolean required() {
                return false;
            }

            @Override
            public String message() {
                return "Invalid value";
            }

            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }

            @Override
            public Class<? extends Payload>[] payload() {
                return new Class[0];
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return EnumValid.class;
            }
        });
        assertTrue(validator.isValid(null, context));
    }
}