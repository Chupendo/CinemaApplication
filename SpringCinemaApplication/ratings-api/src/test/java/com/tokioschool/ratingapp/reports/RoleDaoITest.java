package com.tokioschool.ratingapp.reports;

import com.tokioschool.ratingapp.domains.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class RoleDaoITest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RoleDao roleDao;

    @Test
    void findById_returnsRole_whenIdExists() {
        Role role = new Role();
        role.setName("USER");
        Role savedRole = entityManager.persistAndFlush(role);

        Optional<Role> maybeFoundRole = roleDao.findById(savedRole.getId());

        assertThat(maybeFoundRole).isPresent();
        assertThat(maybeFoundRole.get().getName()).isEqualTo("USER");
    }

    @Test
    void findById_returnsEmpty_whenIdDoesNotExist() {
        Optional<Role> maybeFoundRole = roleDao.findById(999L);

        assertThat( maybeFoundRole ).isNotPresent();
    }

    @Test
    void save_savesRoleSuccessfully() {
        Role role = new Role();
        role.setName("ADMIN");

        Role savedRole = roleDao.save(role);

        assertThat(savedRole).isNotNull();
        assertThat(savedRole.getName()).isEqualTo("ADMIN");
    }

    @Test
    void deleteById_deletesRoleSuccessfully() {
        Role role = new Role();
        role.setName("USER");
        Role savedRole = entityManager.persistAndFlush(role);

        roleDao.deleteById(savedRole.getId());
        Optional<Role> maybeFoundRole = roleDao.findById(savedRole.getId());

        assertThat(maybeFoundRole).isNotPresent();
    }
}