package com.bookstore;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.*;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * BookService using DynamoDB Enhanced client (v2).
 * - Connects to local DynamoDB at http://localhost:8000
 * - Creates a table "Books" if missing, with a GSI "TitleIndex" on attribute "title"
 * - Provides CRUD and simple search utilities
 */
public class BookService {

    private final DynamoDbClient dynamoDbClient;
    private final DynamoDbEnhancedClient enhancedClient;
    private final DynamoDbTable<Book> bookTable;
    private final String tableName = "Books";

    public BookService() {
        // Connect to local DynamoDB; use dummy credentials for local testing
        this.dynamoDbClient = DynamoDbClient.builder()
                .endpointOverride(URI.create("http://localhost:8000"))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("dummy", "dummy")))
                .region(Region.US_EAST_1)
                .build();

        this.enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();

        this.bookTable = enhancedClient.table(tableName, TableSchema.fromBean(Book.class));
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        try {
            // If describeTable succeeds, table exists
            dynamoDbClient.describeTable(DescribeTableRequest.builder().tableName(tableName).build());
        } catch (ResourceNotFoundException e) {
            // Build CreateTableRequest with a simple GSI on "title"
            CreateTableRequest createTableRequest = CreateTableRequest.builder()
                    .tableName(tableName)
                    .attributeDefinitions(
                            AttributeDefinition.builder().attributeName("id").attributeType(ScalarAttributeType.S).build(),
                            AttributeDefinition.builder().attributeName("title").attributeType(ScalarAttributeType.S).build()
                    )
                    .keySchema(KeySchemaElement.builder().attributeName("id").keyType(KeyType.HASH).build())
                    .provisionedThroughput(ProvisionedThroughput.builder()
                            .readCapacityUnits(5L).writeCapacityUnits(5L).build())
                    .globalSecondaryIndexes(GlobalSecondaryIndex.builder()
                            .indexName("TitleIndex")
                            .keySchema(KeySchemaElement.builder().attributeName("title").keyType(KeyType.HASH).build())
                            .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                            .provisionedThroughput(ProvisionedThroughput.builder()
                                    .readCapacityUnits(5L).writeCapacityUnits(5L).build())
                            .build())
                    .build();

            dynamoDbClient.createTable(createTableRequest);

            // Wait for table to exist
            dynamoDbClient.waiter().waitUntilTableExists(DescribeTableRequest.builder().tableName(tableName).build());
        }
    }

    // Put / Save a book
    public void saveBook(Book book) {
        bookTable.putItem(book);
    }

    // Get by id
    public Book getBookById(String id) {
        return bookTable.getItem(r -> r.key(k -> k.partitionValue(id)));
    }

    // Delete by id
    public void deleteBook(String id) {
        bookTable.deleteItem(Key.builder().partitionValue(id).build());
    }

    // List all books (scan) — properly flatten pages -> items
    public List<Book> listAllBooks() {
        return StreamSupport.stream(bookTable.scan().spliterator(), false)
                .flatMap(page -> page.items().stream())
                .collect(Collectors.toList());
    }

    // Query by exact title using the TitleIndex GSI (flatten pages -> items)
    public List<Book> findByTitle(String title) {
        DynamoDbIndex<Book> titleIndex = bookTable.index("TitleIndex");
        QueryConditional qc = QueryConditional.keyEqualTo(Key.builder().partitionValue(title).build());

        return StreamSupport.stream(titleIndex.query(r -> r.queryConditional(qc)).spliterator(), false)
                .flatMap(page -> page.items().stream())
                .collect(Collectors.toList());
    }

    // Simple contains-search (scan + in-memory filter) — useful for demo/small datasets
    public List<Book> searchByTitleOrAuthorContains(String text) {
        String lower = text == null ? "" : text.toLowerCase();
        return StreamSupport.stream(bookTable.scan().spliterator(), false)
                .flatMap(page -> page.items().stream())
                .filter(b -> (b.getTitle() != null && b.getTitle().toLowerCase().contains(lower)) ||
                        (b.getAuthor() != null && b.getAuthor().toLowerCase().contains(lower)))
                .collect(Collectors.toList());
    }

    // Close client
    public void close() {
        if (dynamoDbClient != null) {
            dynamoDbClient.close();
        }
    }
}
