package main.service.impl;

import main.MainTest;
import main.model.entity.User;
import main.repository.UserRepository;
import main.service.UserService;
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

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void add() {
    }

    @Test
    public void findByEmailAndPassword() {
    }

    @Test
    public void update() {
    }

    @Test
    public void emailExists() {
    }

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

    @Test
    public void nameIsInvalid() {
    }

    @Test
    public void passwordIsInvalid() {
    }

    @Test
    public void emailIsInvalid() {
    }

    @Test
    public void findByCode() {
    }
}