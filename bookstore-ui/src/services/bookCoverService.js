const OPEN_LIBRARY_BASE = 'https://covers.openlibrary.org/b';
const PLACEHOLDER_BASE = 'https://placehold.co';

class BookCoverService {
  getCoverByISBN(isbn, size = 'L') {
    if (!isbn) return null;
    const cleanIsbn = isbn.replace(/[-\s]/g, '');
    return `${OPEN_LIBRARY_BASE}/isbn/${cleanIsbn}-${size}.jpg`;
  }

  getPlaceholder(text, genre = '') {
    const colors = {
      'Programming': '667eea/764ba2',
      'Fiction': 'f093fb/f5576c',
      'Mystery': '4facfe/00f2fe',
      'Science Fiction': '30cfd0/330867',
      'Fantasy': 'a8edea/fed6e3',
      'default': 'a8c0ff/3f2b96'
    };
    
    const colorScheme = colors[genre] || colors.default;
    const encodedText = encodeURIComponent(text.substring(0, 50));
    return `${PLACEHOLDER_BASE}/400x600/${colorScheme}/fff?text=${encodedText}`;
  }

  getBookCover(book) {
    if (book.coverImage && book.coverImage.startsWith('http')) {
      return book.coverImage;
    }
    
    if (book.isbn) {
      return this.getCoverByISBN(book.isbn);
    }
    
    return this.getPlaceholder(book.title || 'Book', book.genre);
  }
}

export default new BookCoverService();