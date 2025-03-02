package com.tokioschool.ratingapp.reports;

import com.tokioschool.ratingapp.domains.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UserDaoITest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserDao userDao;

    @Test
    void findById_returnsUser_whenIdExists() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("mailtestuser@test.com");
        user.setPassword("testpwd");
        user.setPasswordBis("testpwd");

        User savedUser = entityManager.persistAndFlush(user);

        Optional<User> maybeFoundUser = userDao.findById(savedUser.getId());

        assertThat( maybeFoundUser ).isPresent();
        assertThat( maybeFoundUser.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    void findById_returnsEmpty_whenIdDoesNotExist() {
        Optional<User> maybeFoundUser = userDao.findById("nonexistentuser");

        assertThat( maybeFoundUser ).isNotPresent();
    }

    @Test
    void save_savesUserSuccessfully() {
        User user = new User();
        user.setUsername("newuser");

        User savedUser = userDao.save(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("newuser");
    }

    @Test
    void deleteById_deletesUserSuccessfully() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("mailtestuser@test.com");
        user.setPassword("testpwd");
        user.setPasswordBis("testpwd");

        User savedUser = entityManager.persistAndFlush(user);

        userDao.deleteById(savedUser.getUsername());
        Optional<User> maybeFoundUser = userDao.findById(savedUser.getUsername());

        assertThat( maybeFoundUser ).isNotPresent();
    }

    @Test
    void findUsersByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase_returnsUsers_whenUsernameMatches() {
        User user1 = new User();
        user1.setUsername("testuser1");
        user1.setEmail("mailtestuser1@test.com");
        user1.setPassword("testpwd");
        user1.setPasswordBis("testpwd");

        User user2 = new User();
        user2.setUsername("testuser2");
        user2.setEmail("mailtestuser2@test.com");
        user2.setPassword("testpwd");
        user2.setPasswordBis("testpwd");

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        List<User> foundUsers = userDao.findUsersByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase("testuser", "nonexistentemail@test.com");

        assertThat(foundUsers).hasSize(2);
        assertThat(foundUsers).extracting(User::getUsername).containsExactlyInAnyOrder("testuser1", "testuser2");
    }

    @Test
    void findUsersByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase_returnsUsers_whenEmailMatches() {
        User user1 = new User();
        user1.setUsername("testuser1");
        user1.setEmail("mailtestuser1@test.com");
        user1.setPassword("testpwd");
        user1.setPasswordBis("testpwd");

        User user2 = new User();
        user2.setUsername("testuser2");
        user2.setEmail("mailtestuser2@test.com");
        user2.setPassword("testpwd");
        user2.setPasswordBis("testpwd");

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        List<User> foundUsers = userDao.findUsersByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase("nonexistentuser", "mailtestuser");

        assertThat(foundUsers).hasSize(2);
        assertThat(foundUsers).extracting(User::getEmail).containsExactlyInAnyOrder("mailtestuser1@test.com", "mailtestuser2@test.com");
    }

    @Test
    void findUsersByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase_returnsEmpty_whenNoMatch() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("mailtestuser@test.com");
        user.setPassword("testpwd");
        user.setPasswordBis("testpwd");

        entityManager.persistAndFlush(user);

        List<User> foundUsers = userDao.findUsersByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase("nonexistentuser", "nonexistentemail@test.com");

        assertThat(foundUsers).isEmpty();
    }

    @Test
    void findUsersByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase_returnsUsers_whenUsernameMatchesIgnoreCase() {
        User user1 = new User();
        user1.setUsername("TestUser1");
        user1.setEmail("mailtestuser1@test.com");
        user1.setPassword("testpwd");
        user1.setPasswordBis("testpwd");

        User user2 = new User();
        user2.setUsername("TestUser2");
        user2.setEmail("mailtestuser2@test.com");
        user2.setPassword("testpwd");
        user2.setPasswordBis("testpwd");

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        List<User> foundUsers = userDao.findUsersByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase("testuser", "nonexistentemail@test.com");

        assertThat(foundUsers).hasSize(2);
        assertThat(foundUsers).extracting(User::getUsername).containsExactlyInAnyOrder("TestUser1", "TestUser2");
    }

    @Test
    void findUsersByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase_returnsUsers_whenEmailMatchesIgnoreCase() {
        User user1 = new User();
        user1.setUsername("testuser1");
        user1.setEmail("MailTestUser1@test.com");
        user1.setPassword("testpwd");
        user1.setPasswordBis("testpwd");

        User user2 = new User();
        user2.setUsername("testuser2");
        user2.setEmail("MailTestUser2@test.com");
        user2.setPassword("testpwd");
        user2.setPasswordBis("testpwd");

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        List<User> foundUsers = userDao.findUsersByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase("nonexistentuser", "mailtestuser");

        assertThat(foundUsers).hasSize(2);
        assertThat(foundUsers).extracting(User::getEmail).containsExactlyInAnyOrder("MailTestUser1@test.com", "MailTestUser2@test.com");
    }

    @Test
    void findUserByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase_returnsUser_whenUsernameMatches() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("mailtestuser@test.com");
        user.setPassword("testpwd");
        user.setPasswordBis("testpwd");

        entityManager.persistAndFlush(user);

        Optional<User> maybeFoundUser = userDao.findUserByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase("testuser", "nonexistentemail@test.com");

        assertThat(maybeFoundUser).isPresent();
        assertThat(maybeFoundUser.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    void findUserByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase_returnsUser_whenEmailMatches() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("mailtestuser@test.com");
        user.setPassword("testpwd");
        user.setPasswordBis("testpwd");

        entityManager.persistAndFlush(user);

        Optional<User> maybeFoundUser = userDao.findUserByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase("nonexistentuser", "mailtestuser@test.com");

        assertThat(maybeFoundUser).isPresent();
        assertThat(maybeFoundUser.get().getEmail()).isEqualTo("mailtestuser@test.com");
    }

    @Test
    void findUserByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase_returnsEmpty_whenNoMatch() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("mailtestuser@test.com");
        user.setPassword("testpwd");
        user.setPasswordBis("testpwd");

        entityManager.persistAndFlush(user);

        Optional<User> maybeFoundUser = userDao.findUserByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase("nonexistentuser", "nonexistentemail@test.com");

        assertThat(maybeFoundUser).isNotPresent();
    }

    @Test
    void findUserByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase_returnsUser_whenUsernameMatchesIgnoreCase() {
        User user = new User();
        user.setUsername("TestUser");
        user.setEmail("mailtestuser@test.com");
        user.setPassword("testpwd");
        user.setPasswordBis("testpwd");

        entityManager.persistAndFlush(user);

        Optional<User> maybeFoundUser = userDao.findUserByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase("testuser", "nonexistentemail@test.com");

        assertThat(maybeFoundUser).isPresent();
        assertThat(maybeFoundUser.get().getUsername()).isEqualTo("TestUser");
    }

    @Test
    void findUserByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase_returnsUser_whenEmailMatchesIgnoreCase() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("MailTestUser@test.com");
        user.setPassword("testpwd");
        user.setPasswordBis("testpwd");

        entityManager.persistAndFlush(user);

        Optional<User> maybeFoundUser = userDao.findUserByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase("nonexistentuser", "mailtestuser@test.com");

        assertThat(maybeFoundUser).isPresent();
        assertThat(maybeFoundUser.get().getEmail()).isEqualTo("MailTestUser@test.com");
    }
}
