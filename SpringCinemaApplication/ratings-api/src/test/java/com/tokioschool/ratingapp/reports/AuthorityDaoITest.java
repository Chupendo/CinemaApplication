package com.tokioschool.ratingapp.reports;

import com.tokioschool.ratingapp.domains.Authority;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class AuthorityDaoITest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AuthorityDao authorityDao;

    @Test
    void findById_returnsAuthority_whenIdExists(){
        Authority authority = Authority.builder().name("READ").build();
        authority = entityManager.persistAndFlush(authority);

        Optional<Authority> maybeFoundAuthority = authorityDao.findById( authority.getId() );

        Assertions.assertThat( maybeFoundAuthority ).isPresent()
                .get()
                .returns(authority.getName(),Authority::getName);
    }

    @Test
    void findById_returnsEmpty_whenIdDoesNotExist() {
        Optional<Authority> maybeFoundAuthority = authorityDao.findById(999L);

        assertThat( maybeFoundAuthority ).isNotPresent();
    }

    @Test
    void save_savesAuthoritySuccessfully() {
        Authority authority = new Authority();
        authority.setName("WRITER");

        Authority savedAuthority = authorityDao.save(authority);

        assertThat(savedAuthority).isNotNull();
        assertThat(savedAuthority.getName()).isEqualTo("WRITER");
    }

    @Test
    void deleteById_deletesAuthoritySuccessfully() {
        Authority authority = new Authority();
        authority.setName("READ");
        Authority savedAuthority = entityManager.persistAndFlush(authority);

        authorityDao.deleteById(savedAuthority.getId());
        Optional<Authority> maybeFoundAuthority = authorityDao.findById(savedAuthority.getId());

        assertThat( maybeFoundAuthority ).isNotPresent();
    }

}
