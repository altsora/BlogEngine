package main.services.impl;

import main.MainTest;
import main.model.entities.User;
import main.repositories.UserRepository;
import main.services.UserService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MainTest.class)
public class UserServiceImplTest {

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    //==================================================================================================================

    @Test
    public void findById() {
        long userId = 1;
        String name = "userName";
        given(userRepository.findById(ArgumentMatchers.any())).willReturn(
                Optional.of(new User() {{
                    setId(userId);
                    setName(name);
                }}));
        User user = userService.findById(2);
        System.err.println(user);
        assertNotNull(user);
        assertThat(userId, equalTo(user.getId()));
        assertThat(name, equalTo(user.getName()));
    }
}