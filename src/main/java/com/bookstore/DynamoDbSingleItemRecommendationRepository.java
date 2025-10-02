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

    public void close() {
        client.close();
    }
}
