package com.tokioschool.filmapp.specifications;

import com.tokioschool.filmapp.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserSpecificationUTest {

    @Test
    void hasName_returnsSpecification_whenNameIsProvided() {
        Specification<User> spec = UserSpecification.hasName("John");
        assertThat(spec).isNotNull();
    }

    @Test
    void hasName_returnsNull_whenNameIsNull() {
        Specification<User> spec = UserSpecification.hasName(null);
        assertThat(spec.toPredicate(null, null, null)).isNull();
    }

    @Test
    void hasSurname_returnsSpecification_whenSurnameIsProvided() {
        Specification<User> spec = UserSpecification.hasSurname("Doe");
        assertThat(spec).isNotNull();
    }

    @Test
    void hasSurname_returnsNull_whenSurnameIsNull() {
        Specification<User> spec = UserSpecification.hasSurname(null);
        assertThat(spec.toPredicate(null, null, null)).isNull();
    }

    @Test
    void hasUsername_returnsSpecification_whenUsernameIsProvided() {
        Specification<User> spec = UserSpecification.hasUsername("johndoe");
        assertThat(spec).isNotNull();
    }

    @Test
    void hasUsername_returnsNull_whenUsernameIsNull() {
        Specification<User> spec = UserSpecification.hasUsername(null);
        assertThat(spec.toPredicate(null, null, null)).isNull();
    }

    @Test
    void containsEmail_returnsSpecification_whenEmailIsProvided() {
        Specification<User> spec = UserSpecification.containsEmail("john.doe@example.com");
        assertThat(spec).isNotNull();
    }

    @Test
    void containsEmail_returnsNull_whenEmailIsNull() {
        Specification<User> spec = UserSpecification.containsEmail(null);
        assertThat(spec.toPredicate(null, null, null)).isNull();
    }
}