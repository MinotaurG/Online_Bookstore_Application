package com.bookstore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CheckoutServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private BookService bookService;

    private CheckoutService checkoutService;
    private Cart cart;
    private Book book1;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        checkoutService = new CheckoutService(orderRepository, bookService);
        cart = new Cart();
        book1 = new Book("1", "Book1", "Author1", "Genre1", new BigDecimal("25.00"), 10);
    }

    @Test
    void testCheckout_createsAndSavesOrder() {
        cart.addBook(book1, 2);
        when(bookService.getBookById("1")).thenReturn(book1);

        Order order = checkoutService.checkout("user1", cart);

        assertNotNull(order);
        assertEquals("user1", order.getUsername());
        assertEquals(new BigDecimal("50.00"), order.getTotal());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testCheckout_throwsExceptionForEmptyCart() {
        assertThrows(IllegalArgumentException.class, () -> {
            checkoutService.checkout("user1", cart);
        });
    }

    @Test
    void testCheckout_throwsExceptionForNullCart() {
        assertThrows(IllegalArgumentException.class, () -> {
            checkoutService.checkout("user1", null);
        });
    }

    @Test
    void testCheckout_throwsExceptionWhenBookNotFound() {
        cart.addBook(book1, 1);
        when(bookService.getBookById("1")).thenReturn(null);

        assertThrows(IllegalStateException.class, () -> {
            checkoutService.checkout("user1", cart);
        });
    }

    @Test
    void testCheckout_throwsExceptionForInsufficientStock() {
        cart.addBook(book1, 15); // more than available stock
        when(bookService.getBookById("1")).thenReturn(book1);

        assertThrows(IllegalStateException.class, () -> {
            checkoutService.checkout("user1", cart);
        });
    }

    @Test
    void testCheckout_decrementsStock() {
        cart.addBook(book1, 3);
        when(bookService.getBookById("1")).thenReturn(book1);

        checkoutService.checkout("user1", cart);

        verify(bookService, times(1)).saveOrUpdateBookByTitle(argThat(book ->
                book.getId().equals("1") && book.getStockQuantity() == 7
        ));
    }

    @Test
    void testCheckout_clearsCartAfterSuccess() {
        cart.addBook(book1, 2);
        when(bookService.getBookById("1")).thenReturn(book1);

        checkoutService.checkout("user1", cart);

        assertTrue(cart.isEmpty());
    }

    @Test
    void testCheckout_worksWithoutBookService() {
        CheckoutService serviceWithoutBookService = new CheckoutService(orderRepository, null);
        cart.addBook(book1, 2);

        Order order = serviceWithoutBookService.checkout("user1", cart);

        assertNotNull(order);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testCheckout_allowsNullUsername() {
        cart.addBook(book1, 1);
        when(bookService.getBookById("1")).thenReturn(book1);

        Order order = checkoutService.checkout(null, cart);

        assertNotNull(order);
        assertNull(order.getUsername());
    }

    @Test
    void testCheckout_preventsNegativeStock() {
        Book bookWithLowStock = new Book("2", "Book2", "Author2", "Genre", new BigDecimal("10"), 2);
        cart.addBook(bookWithLowStock, 3); // requesting more than available
        when(bookService.getBookById("2")).thenReturn(bookWithLowStock);

        assertThrows(IllegalStateException.class, () -> {
            checkoutService.checkout("user1", cart);
        });
    }

    @Test
    void testConstructor_throwsExceptionForNullRepository() {
        assertThrows(NullPointerException.class, () -> {
            new CheckoutService(null, bookService);
        });
    }
}