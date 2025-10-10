package com.bookstore;

import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for BookService.
 * Requires DynamoDB Local running on http://localhost:8000
 *
 * To run DynamoDB Local:
 * docker run -p 8000:8000 amazon/dynamodb-local
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookServiceTest {

    private static BookService bookService;
    private static final String TEST_ID_PREFIX = "test-";

    @BeforeAll
    static void setUpClass() {
        bookService = new BookService();
        // Clean up any existing test data
        cleanupTestData();
    }

    @AfterAll
    static void tearDownClass() {
        cleanupTestData();
        if (bookService != null) {
            bookService.close();
        }
    }

    @BeforeEach
    void setUp() {
        cleanupTestData();
    }

    private static void cleanupTestData() {
        try {
            List<Book> all = bookService.listAllBooks();
            for (Book book : all) {
                if (book.getId().startsWith(TEST_ID_PREFIX)) {
                    bookService.deleteBook(book.getId());
                }
            }
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    void testSaveBook() {
        Book book = new Book(TEST_ID_PREFIX + "1", "Test Book", "Test Author",
                "Fiction", new BigDecimal("29.99"), 10);

        bookService.saveBook(book);

        Book retrieved = bookService.getBookById(TEST_ID_PREFIX + "1");
        assertNotNull(retrieved);
        assertEquals("Test Book", retrieved.getTitle());
        assertEquals("Test Author", retrieved.getAuthor());
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    void testGetBookById_notFound() {
        Book retrieved = bookService.getBookById("nonexistent-id");
        assertNull(retrieved);
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    void testSaveOrUpdateBookByTitle_insertsNewBook() {
        Book book = new Book(TEST_ID_PREFIX + "2", "Unique Title", "Author",
                "Genre", new BigDecimal("19.99"), 5);

        bookService.saveOrUpdateBookByTitle(book);

        Optional<Book> found = bookService.findOneByTitleIgnoreCase("Unique Title");
        assertTrue(found.isPresent());
        assertEquals(new BigDecimal("19.99"), found.get().getPrice());
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    void testSaveOrUpdateBookByTitle_updatesDuplicate() {
        Book original = new Book(TEST_ID_PREFIX + "3", "Duplicate Title", "Author1",
                "Genre", new BigDecimal("10.00"), 5);
        bookService.saveOrUpdateBookByTitle(original);

        Book duplicate = new Book(TEST_ID_PREFIX + "4", "Duplicate Title", "Author2",
                "Genre", new BigDecimal("20.00"), 10);
        bookService.saveOrUpdateBookByTitle(duplicate);

        List<Book> found = bookService.searchByTitleOrAuthorContains("Duplicate Title");
        assertEquals(1, found.size(), "Should only have one book with this title");
        assertEquals(new BigDecimal("20.00"), found.get(0).getPrice());
    }

    @Test
    @org.junit.jupiter.api.Order(5)
    void testDeleteBook() {
        Book book = new Book(TEST_ID_PREFIX + "5", "To Delete", "Author",
                "Genre", BigDecimal.TEN, 1);
        bookService.saveBook(book);

        bookService.deleteBook(TEST_ID_PREFIX + "5");

        Book retrieved = bookService.getBookById(TEST_ID_PREFIX + "5");
        assertNull(retrieved);
    }

    @Test
    @org.junit.jupiter.api.Order(6)
    void testDeleteByTitle() {
        Book book1 = new Book(TEST_ID_PREFIX + "6a", "Same Title", "Author1",
                "Genre", BigDecimal.TEN, 1);
        Book book2 = new Book(TEST_ID_PREFIX + "6b", "Same Title", "Author2",
                "Genre", BigDecimal.TEN, 1);
        bookService.saveBook(book1);
        bookService.saveBook(book2);

        int deleted = bookService.deleteByTitle("Same Title");

        assertEquals(2, deleted);
        assertNull(bookService.getBookById(TEST_ID_PREFIX + "6a"));
        assertNull(bookService.getBookById(TEST_ID_PREFIX + "6b"));
    }

    @Test
    @org.junit.jupiter.api.Order(7)
    void testListAllBooks() {
        Book book1 = new Book(TEST_ID_PREFIX + "7a", "Book A", "Author",
                "Genre", BigDecimal.TEN, 1);
        Book book2 = new Book(TEST_ID_PREFIX + "7b", "Book B", "Author",
                "Genre", BigDecimal.TEN, 1);
        bookService.saveBook(book1);
        bookService.saveBook(book2);

        List<Book> all = bookService.listAllBooks();

        assertTrue(all.size() >= 2);
        assertTrue(all.stream().anyMatch(b -> b.getId().equals(TEST_ID_PREFIX + "7a")));
        assertTrue(all.stream().anyMatch(b -> b.getId().equals(TEST_ID_PREFIX + "7b")));
    }

    @Test
    @org.junit.jupiter.api.Order(8)
    void testFindByTitle() {
        Book book = new Book(TEST_ID_PREFIX + "8", "Exact Title Match", "Author",
                "Genre", BigDecimal.TEN, 1);
        bookService.saveBook(book);

        List<Book> found = bookService.findByTitle("Exact Title Match");

        assertFalse(found.isEmpty());
        assertEquals("Exact Title Match", found.get(0).getTitle());
    }

    @Test
    @org.junit.jupiter.api.Order(9)
    void testSearchByTitleOrAuthorContains_searchByTitle() {
        Book book = new Book(TEST_ID_PREFIX + "9", "Searchable Book", "John Doe",
                "Genre", BigDecimal.TEN, 1);
        bookService.saveBook(book);

        List<Book> found = bookService.searchByTitleOrAuthorContains("Searchable");

        assertFalse(found.isEmpty());
        assertTrue(found.stream().anyMatch(b -> b.getTitle().contains("Searchable")));
    }

    @Test
    @org.junit.jupiter.api.Order(10)
    void testSearchByTitleOrAuthorContains_searchByAuthor() {
        Book book = new Book(TEST_ID_PREFIX + "10", "Some Book", "Unique Author Name",
                "Genre", BigDecimal.TEN, 1);
        bookService.saveBook(book);

        List<Book> found = bookService.searchByTitleOrAuthorContains("Unique Author");

        assertFalse(found.isEmpty());
        assertTrue(found.stream().anyMatch(b -> b.getAuthor().contains("Unique Author")));
    }

    @Test
    @org.junit.jupiter.api.Order(11)
    void testSearchByTitleOrAuthorContains_caseInsensitive() {
        Book book = new Book(TEST_ID_PREFIX + "11", "CaseSensitive", "Author",
                "Genre", BigDecimal.TEN, 1);
        bookService.saveBook(book);

        List<Book> found = bookService.searchByTitleOrAuthorContains("casesensitive");

        assertFalse(found.isEmpty());
    }

    @Test
    @org.junit.jupiter.api.Order(12)
    void testFindOneByTitleIgnoreCase_found() {
        Book book = new Book(TEST_ID_PREFIX + "12", "Find Me", "Author",
                "Genre", BigDecimal.TEN, 1);
        bookService.saveBook(book);

        Optional<Book> found = bookService.findOneByTitleIgnoreCase("find me");

        assertTrue(found.isPresent());
        assertEquals("Find Me", found.get().getTitle());
    }

    @Test
    @org.junit.jupiter.api.Order(13)
    void testFindOneByTitleIgnoreCase_notFound() {
        Optional<Book> found = bookService.findOneByTitleIgnoreCase("Nonexistent Title");

        assertFalse(found.isPresent());
    }

    @Test
    @org.junit.jupiter.api.Order(14)
    void testFindOneByTitleIgnoreCase_handlesNull() {
        Optional<Book> found = bookService.findOneByTitleIgnoreCase(null);

        assertFalse(found.isPresent());
    }

    @Test
    @org.junit.jupiter.api.Order(15)
    void testFindOneByTitleIgnoreCase_handlesEmpty() {
        Optional<Book> found = bookService.findOneByTitleIgnoreCase("");

        assertFalse(found.isPresent());
    }
}