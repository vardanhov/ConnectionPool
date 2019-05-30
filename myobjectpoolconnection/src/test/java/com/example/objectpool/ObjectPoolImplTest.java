package com.example.objectpool;

import com.egs.user.User;
import com.egs.user.exception.EmailAlreadyExistException;
import com.egs.user.exception.UserNotFoundException;
import com.egs.user.service.CreateUserRequest;
import com.egs.user.service.UpdateUserRequest;
import com.egs.user.service.UserService;
import com.egs.user.service.impl.UserServiceImpl;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ObjectPoolImplTest {

    private static final UserService userService = new UserServiceImpl();

    @BeforeClass
    public static void beforeClazz() {
        userService.deleteAll();
    }

    @AfterClass
    public static void afterClazz() {
        userService.deleteAll();
    }

    @Test
    public void testCreateWithExistingEmail() {
        final User user = createUser(randomUUID(), randomUUID(), randomUUID());
        try {
            createUser(user.getEmail(), randomUUID(), randomUUID());
            Assert.fail("Exception should be thrown.");
        } catch (final EmailAlreadyExistException ex) {
            Assert.assertEquals(user.getEmail(), ex.getEmail());
        }
    }

    @Test
    public void testCreate() {
        final String email = randomUUID();
        final String firstName = randomUUID();
        final String lastName = randomUUID();

        final User user = createUser(email, firstName, lastName);

        Assert.assertNotNull(user);

        Assert.assertNotNull(user.getId());
        Assert.assertNotNull(user.getCreated());

        Assert.assertEquals(email, user.getEmail());
        Assert.assertEquals(firstName, user.getFirstName());
        Assert.assertEquals(lastName, user.getLastName());
    }

    @Test
    public void testUpdateWithExistingEmail() {
        final User user1 = createUser(randomUUID(), randomUUID(), randomUUID());
        final User user2 = createUser(randomUUID(), randomUUID(), randomUUID());
        try {
            updateUser(user2.getId(), user1.getEmail(), randomUUID(), randomUUID());
            Assert.fail("Exception should be thrown.");
        } catch (final EmailAlreadyExistException ex) {
            Assert.assertEquals(user1.getEmail(), ex.getEmail());
        }
    }

    @Test
    public void testUpdate() {
        final User user = createUser(randomUUID(), randomUUID(), randomUUID());

        final String updatedEmail = randomUUID();
        final String updatedFirstName = randomUUID();
        final String updatedLastName = randomUUID();

        final User result = updateUser(user.getId(), updatedEmail, updatedFirstName, updatedLastName);

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getUpdated());

        Assert.assertEquals(user.getId(), result.getId());

        Assert.assertEquals(updatedEmail, result.getEmail());
        Assert.assertEquals(updatedFirstName, result.getFirstName());
        Assert.assertEquals(updatedLastName, result.getLastName());

        Assert.assertEquals(user.getCreated(), result.getCreated());
    }

    @Test
    public void testDeleteNotExistingUser() {
        final String userId = randomUUID();
        try {
            userService.getById(userId);
            Assert.fail("Exception should be thrown.");
        } catch (final UserNotFoundException ex) {
            Assert.assertEquals(userId, ex.getIdentifier());
        }
    }

    @Test
    public void testDelete() {
        final User user = createUser(randomUUID(), randomUUID(), randomUUID());
        userService.delete(user.getId());
        final User result = userService.findById(user.getId());
        Assert.assertNull(result);
    }

    @Test
    public void testGetAll() {
        createUser(randomUUID(), randomUUID(), randomUUID());
        final List<User> users = userService.getAll();
        Assert.assertNotNull(users);
    }

    @Test
    public void testFindByIdNotExistingUser() {
        final User user = userService.findById(randomUUID());
        Assert.assertNull(user);
    }

    @Test
    public void testFindById() {
        final User user = createUser(randomUUID(), randomUUID(), randomUUID());
        final User result = userService.findById(user.getId());
        Assert.assertEquals(user, result);
    }

    @Test
    public void testGetByIdNotExistingUser() {
        final String userId = randomUUID();
        try {
            userService.getById(userId);
            Assert.fail("Exception should be thrown.");
        } catch (final UserNotFoundException ex) {
            Assert.assertEquals(userId, ex.getIdentifier());
        }
    }

    @Test
    public void testGetById() {
        final User user = createUser(randomUUID(), randomUUID(), randomUUID());
        final User result = userService.getById(user.getId());
        Assert.assertEquals(user, result);
    }

    @Test
    public void testExistByEmail() {
        final String email = randomUUID();
        Assert.assertFalse(userService.existsByEmail(email));
        final User user = createUser(email, randomUUID(), randomUUID());
        Assert.assertTrue(userService.existsByEmail(user.getEmail()));
    }

    @Test
    public void findBy() {
        final User user1 = createUser(randomUUID(), randomUUID(), randomUUID());
        final User user2 = createUser(randomUUID(), randomUUID(), randomUUID());
        final User user3 = createUser(randomUUID(), randomUUID(), randomUUID());

        final List<String> ids = new ArrayList<>(Arrays.asList(user1.getId(), user2.getId(), user3.getId()));

        final List<User> result = userService.findBy(ids);
        Assert.assertTrue(result.containsAll(Arrays.asList(user1, user2, user3)));
    }

    @Test
    public void testDeleteByIds() {
        final User user1 = createUser(randomUUID(), randomUUID(), randomUUID());
        final User user2 = createUser(randomUUID(), randomUUID(), randomUUID());
        final User user3 = createUser(randomUUID(), randomUUID(), randomUUID());

        final List<String> ids = new ArrayList<>(Arrays.asList(user1.getId(), user2.getId(), user3.getId()));

        Assert.assertEquals(ids.size(), userService.deleteAll(ids));
        Assert.assertEquals(0, userService.findBy(ids).size());
    }

    private User createUser(final String email, final String firstName, final String lastName) {
        return userService.create(new CreateUserRequest(email, firstName, lastName));
    }

    private User updateUser(final String id, final String email, final String firstName, final String lastName) {
        return userService.update(new UpdateUserRequest(id, email, firstName, lastName));
    }

    private static String randomUUID() {
        return UUID.randomUUID().toString();
    }
}