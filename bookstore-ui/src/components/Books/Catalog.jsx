import { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { 
  Container, 
  Grid, 
  TextField, 
  Box,
  Typography,
  Button,
  InputAdornment,
  Alert,
  Pagination,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Stack,
  Paper
} from '@mui/material';
import { Search } from '@mui/icons-material';
import { api } from '../../api';
import BookCardSkeleton from './BookCardSkeleton';
import BookCard from './BookCard';

export default function Catalog({ addToCart, allBooks, user, loading }) {
  const [searchParams, setSearchParams] = useSearchParams();
  
  const [q, setQ] = useState(searchParams.get('q') || '');
  const [books, setBooks] = useState([]);
  const [err, setErr] = useState('');
  
  const [currentPage, setCurrentPage] = useState(parseInt(searchParams.get('page')) || 1);
  const [itemsPerPage, setItemsPerPage] = useState(parseInt(searchParams.get('perPage')) || 12);
  const [sortBy, setSortBy] = useState(searchParams.get('sort') || 'title');
  const [filterGenre, setFilterGenre] = useState(searchParams.get('genre') || 'all');

  const genres = ['all', ...new Set(allBooks.map(b => b.genre).filter(Boolean))];
  
  const load = async () => {
    try {
      const data = q ? await api.searchBooks(q) : allBooks;
      setBooks(data);
      setErr('');
    } catch (e) { 
      setErr(e.message);
    }
  };
  
  useEffect(() => { 
    if (!loading) {
      load();
    }
  }, [allBooks, loading]);
  
  const handleSearch = () => {
    setCurrentPage(1);
    load();
  };

  const filteredAndSortedBooks = books
    .filter(book => filterGenre === 'all' || book.genre === filterGenre)
    .sort((a, b) => {
      switch (sortBy) {
        case 'price-low':
          return (a.price || 0) - (b.price || 0);
        case 'price-high':
          return (b.price || 0) - (a.price || 0);
        case 'stock':
          return (b.stockQuantity || 0) - (a.stockQuantity || 0);
        case 'author':
          return (a.author || '').localeCompare(b.author || '');
        case 'title':
        default:
          return (a.title || '').localeCompare(b.title || '');
      }
    });

  const totalPages = Math.ceil(filteredAndSortedBooks.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  const currentBooks = filteredAndSortedBooks.slice(startIndex, endIndex);

  const updateURL = () => {
    const params = {};
    if (q) params.q = q;
    if (currentPage > 1) params.page = currentPage;
    if (itemsPerPage !== 12) params.perPage = itemsPerPage;
    if (sortBy !== 'title') params.sort = sortBy;
    if (filterGenre !== 'all') params.genre = filterGenre;
    setSearchParams(params);
  };

  useEffect(() => {
    updateURL();
  }, [currentPage, itemsPerPage, sortBy, filterGenre]);

  const handlePageChange = (event, value) => {
    setCurrentPage(value);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleItemsPerPageChange = (event) => {
    setItemsPerPage(event.target.value);
    setCurrentPage(1);
  };

  const handleDeleteBook = async (book) => {
    if (!confirm(`Delete "${book.title}"?`)) return;
    try {
      await api.deleteBookById(book.id);
      await load();
      if (loadBooks) loadBooks();
    } catch (e) {
      alert(e.message);
    }
  };
  
  return (
    <Container maxWidth="xl" sx={{ py: 4 }}>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h3" gutterBottom fontWeight="bold">
          📚 Book Catalog
        </Typography>
        <Typography variant="body1" color="text.secondary">
          {loading ? 'Loading books...' : `${filteredAndSortedBooks.length} books available`}
        </Typography>
      </Box>

      <Paper sx={{ p: 3, mb: 4 }}>
        <Grid container spacing={2} alignItems="center">
          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              placeholder="Search by title or author..."
              value={q}
              onChange={(e) => setQ(e.target.value)}
              onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <Search />
                  </InputAdornment>
                ),
              }}
            />
          </Grid>

          <Grid item xs={12} sm={6} md={2}>
            <Button fullWidth variant="contained" onClick={handleSearch}>
              Search
            </Button>
          </Grid>

          <Grid item xs={12} sm={6} md={2}>
            <FormControl fullWidth>
              <InputLabel>Genre</InputLabel>
              <Select
                value={filterGenre}
                label="Genre"
                onChange={(e) => {
                  setFilterGenre(e.target.value);
                  setCurrentPage(1);
                }}
              >
                {genres.map(genre => (
                  <MenuItem key={genre} value={genre}>
                    {genre === 'all' ? 'All Genres' : genre}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>

          <Grid item xs={12} sm={6} md={2}>
            <FormControl fullWidth>
              <InputLabel>Sort By</InputLabel>
              <Select
                value={sortBy}
                label="Sort By"
                onChange={(e) => setSortBy(e.target.value)}
              >
                <MenuItem value="title">Title (A-Z)</MenuItem>
                <MenuItem value="author">Author (A-Z)</MenuItem>
                <MenuItem value="price-low">Price: Low to High</MenuItem>
                <MenuItem value="price-high">Price: High to Low</MenuItem>
                <MenuItem value="stock">Stock Available</MenuItem>
              </Select>
            </FormControl>
          </Grid>
        </Grid>
      </Paper>

      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="body2" color="text.secondary">
          Showing {startIndex + 1}-{Math.min(endIndex, filteredAndSortedBooks.length)} of {filteredAndSortedBooks.length} books
        </Typography>
        
        <FormControl size="small" sx={{ minWidth: 120 }}>
          <InputLabel>Per Page</InputLabel>
          <Select
            value={itemsPerPage}
            label="Per Page"
            onChange={handleItemsPerPageChange}
          >
            <MenuItem value={12}>12</MenuItem>
            <MenuItem value={24}>24</MenuItem>
            <MenuItem value={48}>48</MenuItem>
            <MenuItem value={96}>96</MenuItem>
          </Select>
        </FormControl>
      </Box>

      {err && <Alert severity="error" sx={{ mb: 3 }}>{err}</Alert>}

      <Grid container spacing={3}>
        {loading ? (
          Array.from({ length: itemsPerPage }).map((_, index) => (
            <Grid item xs={12} sm={6} md={4} lg={3} key={index}>
              <BookCardSkeleton />
            </Grid>
          ))
        ) : currentBooks.length === 0 ? (
          <Grid item xs={12}>
            <Alert severity="info">No books found</Alert>
          </Grid>
        ) : (
          currentBooks.map(book => (
            <Grid item xs={12} sm={6} md={4} lg={3} key={book.id}>
              <BookCard
                book={book}
                onAddToCart={addToCart}
                onView={(id) => api.viewBook(id)}
                onDelete={handleDeleteBook}
                user={user}
              />
            </Grid>
          ))
        )}
      </Grid>

      {totalPages > 1 && !loading && (
        <Box sx={{ display: 'flex', justifyContent: 'center', mt: 6 }}>
          <Stack spacing={2} alignItems="center">
            <Pagination
              count={totalPages}
              page={currentPage}
              onChange={handlePageChange}
              color="primary"
              size="large"
              showFirstButton
              showLastButton
            />
            <Typography variant="body2" color="text.secondary">
              Page {currentPage} of {totalPages}
            </Typography>
          </Stack>
        </Box>
      )}
    </Container>
  );
}