package com.egs.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Objects;

public class User {

    public String id;

    private String email;

    private String firstName;

    private String lastName;

    private Date created;

    private Date updated;

    private Date deleted;

    public User() {
        super();
    }

    public User(final ResultSet resultSet) throws SQLException {
        this.id = resultSet.getString("id");
        this.email = resultSet.getString("email");

        this.firstName = resultSet.getString("first_name");
        this.lastName = resultSet.getString("last_name");

        this.created = resultSet.getTimestamp("created");
        this.updated = resultSet.getTimestamp("updated");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(email, user.email) &&
                Objects.equals(firstName, user.firstName) &&
                Objects.equals(lastName, user.lastName) &&
                Objects.equals(created, user.created) &&
                Objects.equals(updated, user.updated) &&
                Objects.equals(deleted, user.deleted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, firstName, lastName, created, updated, deleted);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", created=" + created +
                ", updated=" + updated +
                ", deleted=" + deleted +
                '}';
    }
}