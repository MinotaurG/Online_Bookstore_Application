package com.bookstore;

import com.bookstore.config.DynamoUsersConfig.UserDdb;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Primary
public class DynamoDbUserRepository implements UserRepository {

    private final DynamoDbTable<UserDdb> table;

    public DynamoDbUserRepository(DynamoDbTable<UserDdb> usersTable) {
        this.table = usersTable;
    }

    private static User toUser(UserDdb d) {
        if (d == null) return null;
        // map into your existing immutable User (username, email, password, isAdmin)
        return new User(d.getUsername(), Optional.ofNullable(d.getEmail()).orElse(""),
                d.getPassword(), Optional.ofNullable(d.getIsAdmin()).orElse(false));
    }

    private static UserDdb toDdb(User u) {
        UserDdb d = new UserDdb();
        d.setUsername(u.getUsername());
        d.setEmail(u.getEmail());
        d.setPassword(u.getPassword()); // place hash here when you enable BCrypt
        d.setIsAdmin(u.isAdmin());
        return d;
    }

    @Override
    public User save(User user) {
        table.putItem(toDdb(user));
        return user;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        UserDdb d = table.getItem(Key.builder().partitionValue(username).build());
        return Optional.ofNullable(toUser(d));
    }

    @Override
    public boolean existsByUsername(String username) {
        return table.getItem(Key.builder().partitionValue(username).build()) != null;
    }

    @Override
    public List<User> findAll() {
        List<User> out = new ArrayList<>();
        PageIterable<UserDdb> pages = table.scan();
        for (Page<UserDdb> p : pages) {
            p.items().forEach(i -> out.add(toUser(i)));
        }
        return out;
    }
}
