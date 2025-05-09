package com.tokioschool.filmapp.specifications;

import com.tokioschool.filmapp.domain.Artist;
import com.tokioschool.filmapp.domain.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

public class ArtistSpecification {

    /** Nombre de los campos de la clase {@link User} utilizados para filtrar **/
    private static final String ARTIST_NAME_FIELD = "name";
    private static final String ARTIST_SURNAME_FIELD = "surname";
    private static final String ARTIST_TYPE_FIELD = "typeArtist";

    public static Specification<Artist> containsName(String name) {
        return filterContainsStringIgnoreCase(ARTIST_NAME_FIELD, name);
    }

    public static Specification<Artist> containsSurname(String surname) {
        return filterContainsStringIgnoreCase(ARTIST_SURNAME_FIELD, surname);
    }

    public static Specification<Artist> hasTypeArtist(String typeArtist) {
        return filterEqualString(ARTIST_TYPE_FIELD,typeArtist);
    }

    /**
     * Crea una especificación para filtrar usuarios por un atributo de tipo cadena con coincidencia exacta.
     *
     * @param name  El nombre del atributo por el cual filtrar.
     * @param value El valor por el cual filtrar.
     * @return Una especificación para filtrar usuarios por el atributo especificado.
     */
    private static Specification<Artist> filterEqualString(@NonNull final String name, String value) {
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
    private static Specification<Artist> filterContainsStringIgnoreCase(@NonNull final String nameField, String valueField) {
        final String maybeValue = StringUtils.stripToNull(valueField);
        return (root, query, cb) -> maybeValue == null ? null : cb.like(cb.lower(root.get(nameField)), String.format("%%%s%%", maybeValue.toLowerCase()));
    }
}
