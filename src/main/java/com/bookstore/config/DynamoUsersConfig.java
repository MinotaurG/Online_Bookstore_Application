package com.bookstore.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.*;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.net.URI;

@Configuration
public class DynamoUsersConfig {

    private static final Logger log = LoggerFactory.getLogger(DynamoUsersConfig.class);

    @Bean
    public DynamoDbClient dynamoDbClient(
            @Value("${dynamodb.endpoint:}") String endpoint,
            @Value("${dynamodb.region:ap-south-1}") String region,
            @Value("${dynamodb.accessKey:dummy}") String accessKey,
            @Value("${dynamodb.secretKey:dummy}") String secretKey) {

        log.info("Initializing DynamoDB client for region: {}", region);

        DynamoDbClientBuilder builder = DynamoDbClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)));

        // Override endpoint if provided (for local DynamoDB)
        if (endpoint != null && !endpoint.isBlank()) {
            log.info("Using custom DynamoDB endpoint: {}", endpoint);
            builder.endpointOverride(URI.create(endpoint));
        } else {
            log.info("Using AWS DynamoDB service endpoint");
        }

        return builder.build();
    }

    @Bean
    public DynamoDbEnhancedClient enhancedClient(DynamoDbClient client) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(client)
                .build();
    }

    @Bean
    public DynamoDbTable<UserDdb> usersTable(
            DynamoDbClient client,  // Add this parameter
            DynamoDbEnhancedClient enhanced,
            @Value("${dynamodb.table.users:Users}") String tableName) {

        log.info("Initializing Users table: {}", tableName);

        DynamoDbTable<UserDdb> table = enhanced.table(tableName, TableSchema.fromBean(UserDdb.class));

        // Create table if it doesn't exist (safe for local dev)
        createTableIfNotExists(client, tableName);  // Pass client directly

        return table;
    }

    private void createTableIfNotExists(DynamoDbClient client, String tableName) {
        try {
            DescribeTableResponse response = client.describeTable(
                    DescribeTableRequest.builder().tableName(tableName).build());
            log.info("Table '{}' already exists with status: {}",
                    tableName, response.table().tableStatus());
        } catch (ResourceNotFoundException e) {
            log.info("Table '{}' not found. Creating...", tableName);

            CreateTableResponse createResponse = client.createTable(CreateTableRequest.builder()
                    .tableName(tableName)
                    .attributeDefinitions(
                            AttributeDefinition.builder()
                                    .attributeName("username")
                                    .attributeType(ScalarAttributeType.S)
                                    .build())
                    .keySchema(
                            KeySchemaElement.builder()
                                    .attributeName("username")
                                    .keyType(KeyType.HASH)
                                    .build())
                    .billingMode(BillingMode.PAY_PER_REQUEST)
                    .build());

            log.info("Table '{}' creation initiated. Status: {}",
                    tableName, createResponse.tableDescription().tableStatus());

            // Wait for table to become active
            waitForTableActive(client, tableName);
        }
    }

    private void waitForTableActive(DynamoDbClient client, String tableName) {
        try {
            log.info("Waiting for table '{}' to become active...", tableName);
            client.waiter().waitUntilTableExists(
                    DescribeTableRequest.builder().tableName(tableName).build());
            log.info("Table '{}' is now active", tableName);
        } catch (Exception e) {
            log.warn("Error waiting for table '{}': {}", tableName, e.getMessage());
        }
    }

    // DynamoDB bean mapped to User fields
    @DynamoDbBean
    public static class UserDdb {
        private String username;
        private String email;
        private String password; // store BCrypt hash
        private Boolean isAdmin;

        @DynamoDbPartitionKey
        @DynamoDbAttribute("username")
        public String getUsername() {
            return username;
        }
        public void setUsername(String username) {
            this.username = username;
        }

        @DynamoDbAttribute("email")
        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
        }

        @DynamoDbAttribute("password")
        public String getPassword() {
            return password;
        }
        public void setPassword(String password) {
            this.password = password;
        }

        @DynamoDbAttribute("isAdmin")
        public Boolean getIsAdmin() {
            return isAdmin;
        }
        public void setIsAdmin(Boolean admin) {
            isAdmin = admin;
        }
    }
}