package com.bookstore;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
public class User {
    private String username;
    private String email;
    private String password;
    private boolean isAdmin;

    // No-arg constructor required by DynamoDB
    public User() {}

    @JsonCreator
    public User(
            @JsonProperty("username") String username,
            @JsonProperty("email") String email,
            @JsonProperty("password") String password,
            @JsonProperty("isAdmin") boolean isAdmin) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    @DynamoDbPartitionKey
    @DynamoDbAttribute("username")
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    @DynamoDbAttribute("email")
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @DynamoDbAttribute("password")
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @DynamoDbAttribute("isAdmin")
    @JsonProperty("isAdmin")
    public boolean isAdmin() { return isAdmin; }
    public void setAdmin(boolean admin) { isAdmin = admin; }

    @Override
    public String toString() {
        return username + " (" + email + ")";
    }
}