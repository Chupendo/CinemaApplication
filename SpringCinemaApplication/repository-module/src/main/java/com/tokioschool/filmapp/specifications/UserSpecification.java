package com.tokioschool.filmapp.specifications;

import com.tokioschool.filmapp.domain.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

/**
 * Clase que proporciona especificaciones para filtrar entidades {@link User}.
 *
 * Esta clase utiliza el API de Criteria de JPA para construir consultas dinámicas
 * basadas en los atributos de la entidad User.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public class UserSpecification {

    /** Nombre de los campos de la clase {@link User} utilizados para filtrar **/
    private static final String USER_NAME_FIELD = "name";
    private static final String USER_SURNAME_FIELD = "surname";
    private static final String USER_USERNAME_FIELD = "username";
    private static final String USER_EMAIL_FIELD = "email";

    /**
     * Crea una especificación para filtrar usuarios por nombre.
     *
     * @param name El nombre por el cual filtrar.
     * @return Una especificación para filtrar usuarios por nombre.
     */
    public static Specification<User> hasName(String name) {
        return filterEqualString(USER_NAME_FIELD, name);
    }

    /**
     * Crea una especificación para filtrar usuarios por apellido.
     *
     * @param surname El apellido por el cual filtrar.
     * @return Una especificación para filtrar usuarios por apellido.
     */
    public static Specification<User> hasSurname(String surname) {
        return filterEqualString(USER_SURNAME_FIELD, surname);
    }

    /**
     * Crea una especificación para filtrar usuarios por nombre de usuario.
     *
     * @param username El nombre de usuario por el cual filtrar.
     * @return Una especificación para filtrar usuarios por nombre de usuario.
     */
    public static Specification<User> hasUsername(String username) {
        return filterEqualString(USER_USERNAME_FIELD, username);
    }

    /**
     * Crea una especificación para filtrar usuarios por correo electrónico, ignorando mayúsculas y minúsculas.
     *
     * @param email El correo electrónico por el cual filtrar.
     * @return Una especificación para filtrar usuarios por correo electrónico, ignorando mayúsculas y minúsculas.
     */
    public static Specification<User> containsEmail(String email) {
        return filterContainsStringIgnoreCase(USER_EMAIL_FIELD, email);
    }

    /**
     * Crea una especificación para filtrar usuarios por un atributo de tipo cadena con coincidencia exacta.
     *
     * @param name  El nombre del atributo por el cual filtrar.
     * @param value El valor por el cual filtrar.
     * @return Una especificación para filtrar usuarios por el atributo especificado.
     */
    private static Specification<User> filterEqualString(@NonNull final String name, String value) {
        final String maybeValue = StringUtils.stripToNull(value);
        return (root, query, cb) -> maybeValue == null ? null : cb.equal(root.get(name), maybeValue);
    }

    /**
     * Crea una especificación para filtrar usuarios por un atributo de tipo cadena que contenga un valor, ignorando mayúsculas y minúsculas.
     *
     * @param nameField  El nombre del atributo por el cual filtrar.
     * @param valueField El valor por el cual filtrar.
     * @return Una especificación para filtrar usuarios por el atributo especificado, ignorando mayúsculas y minúsculas.
     */
    private static Specification<User> filterContainsStringIgnoreCase(@NonNull final String nameField, String valueField) {
        final String maybeValue = StringUtils.stripToNull(valueField);
        return (root, query, cb) -> maybeValue == null ? null : cb.like(cb.lower(root.get(nameField)), String.format("%%%s%%", maybeValue.toLowerCase()));
    }
}