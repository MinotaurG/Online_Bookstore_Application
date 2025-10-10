package com.bookstore;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.net.URI;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DynamoDbSingleItemRecommendationRepository implements RecommendationRepository {

    private static final String TABLE_NAME = "Recommendations";

    private final DynamoDbClient client;
    private final DynamoDbEnhancedClient enhancedClient;
    private final DynamoDbTable<UserRecommendations> table;

    public DynamoDbSingleItemRecommendationRepository() {
        this.client = DynamoDbClient.builder()
                .endpointOverride(URI.create("http://localhost:8000")) // DynamoDB Local
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("dummy", "dummy")))
                .build();

        this.enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(client)
                .build();

        this.table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(UserRecommendations.class));
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        try {
            client.describeTable(DescribeTableRequest.builder().tableName(TABLE_NAME).build());
        } catch (ResourceNotFoundException e) {
            client.createTable(CreateTableRequest.builder()
                    .tableName(TABLE_NAME)
                    .keySchema(KeySchemaElement.builder().attributeName("userId").keyType(KeyType.HASH).build())
                    .attributeDefinitions(AttributeDefinition.builder().attributeName("userId").attributeType(ScalarAttributeType.S).build())
                    .provisionedThroughput(ProvisionedThroughput.builder().readCapacityUnits(5L).writeCapacityUnits(5L).build())
                    .build());
        }
    }

    @Override
    public void saveRecommendations(String userId, List<Recommendation> recs) {
        UserRecommendations ur = new UserRecommendations();
        ur.setUserId(userId);
        ur.setRecommendations(recs);
        table.putItem(ur);
    }

    @Override
    public List<Recommendation> getTopRecommendations(String userId, int limit) {
        UserRecommendations ur = table.getItem(Key.builder().partitionValue(userId).build());
        if (ur == null || ur.getRecommendations() == null) return Collections.emptyList();
        return ur.getRecommendations().stream()
                .sorted(Comparator.comparingInt(Recommendation::getRank))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteRecommendations(String userId) {
        table.deleteItem(Key.builder().partitionValue(userId).build());
    }


    @Override
    public List<Book> findRecommendationsForUser(String userId, int limit) {
        if (userId == null || limit <= 0) return Collections.emptyList();

        UserRecommendations ur = table.getItem(Key.builder().partitionValue(userId).build());
        if (ur == null || ur.getRecommendations() == null) return Collections.emptyList();

        return ur.getRecommendations().stream()
                .sorted(Comparator.comparingInt(Recommendation::getRank))
                .limit(limit)
                .map(this::bookFromRecommendation)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Try to obtain a Book from a Recommendation. First try common getter getBook()/getItem(),
     * otherwise try to build a Book from available fields (bookId/title/author).
     */
    private Book bookFromRecommendation(Recommendation r) {
        if (r == null) return null;

        // 1) try getBook()
        try {
            java.lang.reflect.Method m = r.getClass().getMethod("getBook");
            Object res = m.invoke(r);
            if (res instanceof Book) return (Book) res;
        } catch (NoSuchMethodException ignored) {
        } catch (Exception ignored) {
        }

        // 2) try getItem()
        try {
            java.lang.reflect.Method m = r.getClass().getMethod("getItem");
            Object res = m.invoke(r);
            if (res instanceof Book) return (Book) res;
        } catch (NoSuchMethodException ignored) {
        } catch (Exception ignored) {
        }

        // 3) fallback: try to read common primitive fields to build Book
        String id = tryInvokeStringGetter(r, "getBookId", "getItemId", "getId");
        String title = tryInvokeStringGetter(r, "getTitle", "getName");
        String author = tryInvokeStringGetter(r, "getAuthor", "getWriter");

        if (id == null && title == null && author == null) {
            // nothing useful found
            return null;
        }

        // build a minimal Book instance (assumes Book has no-arg constructor + setters)
        try {
            Book b = new Book();
            if (id != null) {
                try {
                    java.lang.reflect.Method setId = Book.class.getMethod("setId", String.class);
                    setId.invoke(b, id);
                } catch (NoSuchMethodException ns) {
                    // maybe field is named differently; ignore if setter absent
                }
            }
            if (title != null) {
                try {
                    java.lang.reflect.Method setTitle = Book.class.getMethod("setTitle", String.class);
                    setTitle.invoke(b, title);
                } catch (NoSuchMethodException ns) {
                }
            }
            if (author != null) {
                try {
                    java.lang.reflect.Method setAuthor = Book.class.getMethod("setAuthor", String.class);
                    setAuthor.invoke(b, author);
                } catch (NoSuchMethodException ns) {
                }
            }
            return b;
        } catch (Exception e) {
            return null;
        }
    }

    private String tryInvokeStringGetter(Recommendation r, String... methodNames) {
        for (String methodName : methodNames) {
            try {
                java.lang.reflect.Method m = r.getClass().getMethod(methodName);
                Object res = m.invoke(r);
                if (res instanceof String) return (String) res;
                if (res != null) return res.toString();
            } catch (NoSuchMethodException ignored) {
            } catch (Exception ignored) {
            }
        }
        // try fields as last resort
        for (String fieldName : new String[]{"bookId", "itemId", "id", "title", "name", "author", "writer"}) {
            try {
                java.lang.reflect.Field f = r.getClass().getDeclaredField(fieldName);
                f.setAccessible(true);
                Object res = f.get(r);
                if (res instanceof String) return (String) res;
                if (res != null) return res.toString();
            } catch (NoSuchFieldException ignored) {
            } catch (Exception ignored) {
            }
        }
        return null;
    }


    public void close() {
        client.close();
    }
}
