package com.example.contactmanagement.repository;

import com.example.contactmanagement.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testExistsByEmail() {
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("testrepo@example.com");
        user.setPhoneNumber("123456");
        user.setPassword("pass");
        userRepository.save(user);

        boolean exists = userRepository.existsByEmail("testrepo@example.com");
        assertTrue(exists);
    }

    @Test
    public void testFindByEmail() {
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("testfind@example.com");
        user.setPhoneNumber("123456");
        user.setPassword("pass");
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("testfind@example.com");
        assertTrue(found.isPresent());
    }
}
