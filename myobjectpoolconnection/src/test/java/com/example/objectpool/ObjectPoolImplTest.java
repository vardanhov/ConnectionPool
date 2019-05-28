package com.example.objectpool;


import com.egs.user.User;
import com.egs.user.service.CreateUserRequest;
import com.egs.user.service.UpdateUserRequest;
import com.egs.user.service.UserService;
import com.egs.user.service.impl.UserServiceImpl;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;


public class ObjectPoolImplTest {

    private final UserService userService;

    public ObjectPoolImplTest() {
        this.userService = new UserServiceImpl();
    }

    @Test
    public void testCreate() {
        final String email = UUID.randomUUID().toString();
        final String firstName = UUID.randomUUID().toString();
        final String lastName = UUID.randomUUID().toString();

        final User user = createUser(email, firstName, lastName);

        Assert.assertNotNull(user);

        Assert.assertNotNull(user.getId());
        Assert.assertNotNull(user.getCreated());

        Assert.assertEquals(email, user.getEmail());
        Assert.assertEquals(firstName, user.getFirstName());
        Assert.assertEquals(lastName, user.getLastName());
    }

    @Test
    public void testUpdate() {
        final String email = UUID.randomUUID().toString();
        final String firstName = UUID.randomUUID().toString();
        final String lastName = UUID.randomUUID().toString();

        final User user = createUser(email, firstName, lastName);

        final String updatedEmail = UUID.randomUUID().toString();
        final String updatedFirstName = UUID.randomUUID().toString();
        final String updatedLastName = UUID.randomUUID().toString();

        final User result = updateUser(user.getId(), updatedEmail, updatedFirstName, updatedLastName);

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getUpdated());

        Assert.assertEquals(user.getId(), result.getId());

        Assert.assertEquals(updatedEmail, result.getEmail());
        Assert.assertEquals(updatedFirstName, result.getFirstName());
        Assert.assertEquals(updatedLastName, result.getLastName());

        Assert.assertEquals(user.getCreated(), result.getCreated());
    }

    private User createUser(final String email, final String firstName, final String lastName) {
        return userService.create(new CreateUserRequest(email, firstName, lastName));
    }

    private User updateUser(final String id, final String email, final String firstName, final String lastName) {
        return userService.update(new UpdateUserRequest(id, email, firstName, lastName));
    }
}