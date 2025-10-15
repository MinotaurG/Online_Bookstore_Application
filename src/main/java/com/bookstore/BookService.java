package com.bookstore;


import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.*;
import software.amazon.awssdk.services.dynamodb.model.*;


import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


/**
 * BookService using DynamoDB Enhanced client (v2).
 * - Connects to local DynamoDB at http://localhost:8000
 * - Creates a table "Books" if missing, with a GSI "TitleIndex" on attribute "title"
 * - Provides CRUD and simple search utilities
 * - Includes idempotent save/update to avoid duplicate books (by title)
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
            dynamoDbClient.describeTable(DescribeTableRequest.builder().tableName(tableName).build());
        } catch (ResourceNotFoundException e) {
            CreateTableRequest createTableRequest = CreateTableRequest.builder()
                    .tableName(tableName)
                    .attributeDefinitions(
                            AttributeDefinition.builder().attributeName("id").attributeType(ScalarAttributeType.S).build(),
                            AttributeDefinition.builder().attributeName("title").attributeType(ScalarAttributeType.S).build(),
                            AttributeDefinition.builder().attributeName("asin").attributeType(ScalarAttributeType.S).build()  // NEW
                    )
                    .keySchema(KeySchemaElement.builder().attributeName("id").keyType(KeyType.HASH).build())
                    .provisionedThroughput(ProvisionedThroughput.builder()
                            .readCapacityUnits(5L).writeCapacityUnits(5L).build())
                    .globalSecondaryIndexes(
                            // Existing TitleIndex
                            GlobalSecondaryIndex.builder()
                                    .indexName("TitleIndex")
                                    .keySchema(KeySchemaElement.builder().attributeName("title").keyType(KeyType.HASH).build())
                                    .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                                    .provisionedThroughput(ProvisionedThroughput.builder()
                                            .readCapacityUnits(5L).writeCapacityUnits(5L).build())
                                    .build(),
                            // NEW: AsinIndex
                            GlobalSecondaryIndex.builder()
                                    .indexName("AsinIndex")
                                    .keySchema(KeySchemaElement.builder().attributeName("asin").keyType(KeyType.HASH).build())
                                    .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                                    .provisionedThroughput(ProvisionedThroughput.builder()
                                            .readCapacityUnits(5L).writeCapacityUnits(5L).build())
                                    .build()
                    )
                    .build();

            dynamoDbClient.createTable(createTableRequest);
            dynamoDbClient.waiter().waitUntilTableExists(
                    DescribeTableRequest.builder().tableName(tableName).build()
            );
        }
    }


    // -------------------------------
    // CRUD OPERATIONS
    // -------------------------------


    /** Plain insert (used for tests or to insert with a known id). */
    public void saveBook(Book book) {
        book.ensureAsin();
        bookTable.putItem(book);
    }


    /**
     * Inserts or updates a book (by title) to prevent duplicates.
     * If a book with the same title (case-insensitive) exists, update its price/stock.
     * Otherwise insert the incoming book (preserving its id).
     */
    public void saveOrUpdateBookByTitle(Book book) {
        book.ensureAsin();

        Optional<Book> existing = findOneByTitleIgnoreCase(book.getTitle());


        if (existing.isPresent()) {
            Book old = existing.get();
            // choose what to preserve: keep old id, update price/stock/genre/author as desired
            old.setPrice(book.getPrice());
            old.setStockQuantity(book.getStockQuantity());
            old.setAuthor(book.getAuthor());
            old.setGenre(book.getGenre());
            bookTable.updateItem(old);
            System.out.println("Updated existing book: " + old.getTitle() + " (id=" + old.getId() + ")");
        } else {
            bookTable.putItem(book);
            System.out.println("Inserted new book: " + book.getTitle() + " (id=" + book.getId() + ")");
        }
    }


    /** Get by ID. */
    public Book getBookById(String id) {
        return bookTable.getItem(r -> r.key(k -> k.partitionValue(id)));
    }


    /** Delete by ID. */
    public void deleteBook(String id) {
        bookTable.deleteItem(Key.builder().partitionValue(id).build());
    }

    /** Delete all books with the given title (exact match via GSI). Returns count deleted. */
    public int deleteByTitle(String title) {
        List<Book> books = findByTitle(title);
        for (Book book : books) {
            deleteBook(book.getId());
        }
        return books.size();
    }


    /** List all books (scan). */
    public List<Book> listAllBooks() {
        return StreamSupport.stream(bookTable.scan().spliterator(), false)
                .flatMap(page -> page.items().stream())
                .collect(Collectors.toList());
    }


    /** Query by exact title (via GSI). */
    public List<Book> findByTitle(String title) {
        DynamoDbIndex<Book> titleIndex = bookTable.index("TitleIndex");
        QueryConditional qc = QueryConditional.keyEqualTo(Key.builder().partitionValue(title).build());


        return StreamSupport.stream(titleIndex.query(r -> r.queryConditional(qc)).spliterator(), false)
                .flatMap(page -> page.items().stream())
                .collect(Collectors.toList());
    }


    /** Search by partial match (title or author). */
    public List<Book> searchByTitleOrAuthorContains(String text) {
        String lower = text == null ? "" : text.toLowerCase();
        return StreamSupport.stream(bookTable.scan().spliterator(), false)
                .flatMap(page -> page.items().stream())
                .filter(b -> (b.getTitle() != null && b.getTitle().toLowerCase().contains(lower)) ||
                        (b.getAuthor() != null && b.getAuthor().toLowerCase().contains(lower)))
                .collect(Collectors.toList());
    }


    // -------------------------------
    // UTILITIES
    // -------------------------------


    /** Find one book by title, ignoring case. */
    public Optional<Book> findOneByTitleIgnoreCase(String title) {
        if (title == null || title.isEmpty()) return Optional.empty();


        List<Book> all = listAllBooks();
        return all.stream()
                .filter(b -> b.getTitle() != null && b.getTitle().equalsIgnoreCase(title))
                .findFirst();
    }

    // -------------------------------
    // ASIN-RELATED OPERATIONS
    // -------------------------------

    /**
     * Get book by ASIN (using GSI)
     */
    public Book getBookByAsin(String asin) {
        if (asin == null || !AsinGenerator.isValid(asin)) {
            return null;
        }

        DynamoDbIndex<Book> asinIndex = bookTable.index("AsinIndex");
        QueryConditional qc = QueryConditional.keyEqualTo(Key.builder().partitionValue(asin).build());

        List<Book> results = StreamSupport.stream(
                        asinIndex.query(r -> r.queryConditional(qc)).spliterator(), false)
                .flatMap(page -> page.items().stream())
                .collect(Collectors.toList());

        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Migrate all existing books to have ASINs
     * Call this once to add ASINs to books that don't have them
     */
    public int migrateExistingBooksToAsin() {
        List<Book> allBooks = listAllBooks();
        int updated = 0;

        for (Book book : allBooks) {
            if (book.getAsin() == null || book.getAsin().isBlank()) {
                book.ensureAsin();
                bookTable.updateItem(book);
                updated++;
                System.out.println("Added ASIN to: " + book.getTitle() + " -> " + book.getAsin());
            }
        }

        System.out.println("Migration complete: " + updated + " books updated with ASINs");
        return updated;
    }

    /**
     * Delete by ASIN (easier to remember than UUID)
     */
    public boolean deleteByAsin(String asin) {
        Book book = getBookByAsin(asin);
        if (book != null) {
            deleteBook(book.getId());
            return true;
        }
        return false;
    }


    /** Close client resources. */
    public void close() {
        if (dynamoDbClient != null) {
            dynamoDbClient.close();
        }
    }
}