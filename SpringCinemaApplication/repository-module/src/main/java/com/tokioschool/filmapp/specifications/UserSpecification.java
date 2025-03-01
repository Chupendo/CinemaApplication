package com.tokioschool.filmapp.specifications;

import com.tokioschool.filmapp.domain.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

public class UserSpecification {

    /** Name Fields of class {@link User} used for filter **/
    private static final String USER_NAME_FIELD = "name";
    private static final String USER_SURNAME_FIELD = "surname";
    private static final String USER_USERNAME_FIELD = "username";
    private static final String USER_EMAIL_FIELD = "email";

    /**
     * Creates a specification to filter users by name.
     *
     * @param name the name to filter by
     * @return a specification for filtering users by name
     */
    public static Specification<User> hasName(String name) {
        return filterEqualString(USER_NAME_FIELD, name);
    }

    /**
     * Creates a specification to filter users by surname.
     *
     * @param surname the surname to filter by
     * @return a specification for filtering users by surname
     */
    public static Specification<User> hasSurname(String surname) {
        return filterEqualString(USER_SURNAME_FIELD, surname);
    }

    /**
     * Creates a specification to filter users by username.
     *
     * @param username the username to filter by
     * @return a specification for filtering users by username
     */
    public static Specification<User> hasUsername(String username) {
        return filterEqualString(USER_USERNAME_FIELD, username);
    }

    /**
     * Creates a specification to filter users by email, ignoring case.
     *
     * @param email the email to filter by
     * @return a specification for filtering users by email, ignoring case
     */
    public static Specification<User> containsEmail(String email) {
        return filterContainsStringIgnoreCase(USER_EMAIL_FIELD, email);
    }

    /**
     * Creates a specification to filter users by a string attribute with an exact match.
     *
     * @param name  the attribute name to filter by
     * @param value the value to filter by
     * @return a specification for filtering users by the specified attribute
     */
    private static Specification<User> filterEqualString(@NonNull final String name, String value) {
        final String maybeValue = StringUtils.stripToNull(value);
        return (root, query, cb) -> maybeValue == null ? null : cb.equal(root.get(name), maybeValue);
    }

    /**
     * Creates a specification to filter users by a string attribute containing a value, ignoring case.
     *
     * @param nameField  the attribute name to filter by
     * @param valueField the value to filter by
     * @return a specification for filtering users by the specified attribute, ignoring case
     */
    private static Specification<User> filterContainsStringIgnoreCase(@NonNull final String nameField, String valueField) {
        final String maybeValue = StringUtils.stripToNull(valueField);
        return (root, query, cb) -> maybeValue == null ? null : cb.like(cb.lower(root.get(nameField)), String.format("%%%s%%", maybeValue.toLowerCase()));
    }
}