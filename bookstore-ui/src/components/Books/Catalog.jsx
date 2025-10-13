import { useState, useEffect } from 'react'; 
import { Container, Grid, TextField, Box, Typography, Button, InputAdornment, Alert, Card, CardContent, CardActions, Chip } from '@mui/material'; 
import { Search, ShoppingCart } from '@mui/icons-material'; 
import { api } from '../../api'; 
import BookCardSkeleton from './BookCardSkeleton';

export default function Catalog({ addToCart, allBooks, user, loading }) {
const [q, setQ] = useState('');
const [books, setBooks] = useState([]);
const [err, setErr] = useState('');

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
load();
};

// Helper function to determine if stock should be shown
const shouldShowStock = (book, isAdmin) => {
const stock = book.stockQuantity ?? 0;
return isAdmin || stock < 5;
};

return (
<Container maxWidth="xl" sx={{ py: 4 }}>
{/* Header */}
<Box sx={{ mb: 4 }}>
<Typography variant="h3" gutterBottom fontWeight="bold">
📚 Books Catalog
</Typography>
<Typography variant="body1" color="text.secondary">
{loading ? 'Loading books...' : `${books.length} books available`}
</Typography>
</Box>

  {/* Search Bar */}
  <Box sx={{ mb: 4, display: 'flex', gap: 2 }}>
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
    <Button 
      variant="contained" 
      onClick={handleSearch}
      sx={{ minWidth: 120 }}
    >
      Search
    </Button>
  </Box>

  {/* Error message */}
  {err && <Alert severity="error" sx={{ mb: 3 }}>{err}</Alert>}

  {/* Books Grid */}
  <Grid container spacing={3}>
    {loading ? (
      // Show skeleton loading cards
      Array.from({ length: 8 }).map((_, index) => (
        <Grid item xs={12} sm={6} md={4} lg={3} key={index}>
          <BookCardSkeleton />
        </Grid>
      ))
    ) : books.length === 0 ? (
      // No books found
      <Grid item xs={12}>
        <Alert severity="info">No books found</Alert>
      </Grid>
    ) : (
      // Show actual books
      books.map(b => {
        const stock = b.stockQuantity ?? 0;
        const isOutOfStock = stock <= 0;
        const isLowStock = stock < 5 && stock > 0;
        const showStock = shouldShowStock(b, user?.isAdmin);
        
        return (
          <Grid item xs={12} sm={6} md={4} lg={3} key={b.id}>
            <Card sx={{ 
              height: '100%', 
              display: 'flex', 
              flexDirection: 'column',
              transition: 'transform 0.2s, box-shadow 0.2s',
              '&:hover': {
                transform: 'translateY(-4px)',
                boxShadow: 4,
              }
            }}>
              {/* Book Cover */}
              <Box
                sx={{
                  height: 200,
                  background: `linear-gradient(135deg, #667eea 0%, #764ba2 100%)`,
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  p: 2,
                  position: 'relative'
                }}
              >
                {/* Stock badge - only show if needed */}
                {showStock && isLowStock && (
                  <Chip
                    label={`Only ${stock} left!`}
                    color="error"
                    size="small"
                    sx={{ 
                      position: 'absolute', 
                      top: 8, 
                      right: 8,
                      fontWeight: 'bold'
                    }}
                  />
                )}
                {isOutOfStock && (
                  <Chip
                    label="Out of Stock"
                    color="error"
                    size="small"
                    sx={{ 
                      position: 'absolute', 
                      top: 8, 
                      right: 8,
                      fontWeight: 'bold'
                    }}
                  />
                )}
                
                <Typography 
                  variant="h6" 
                  sx={{ 
                    color: 'white', 
                    textAlign: 'center',
                    fontWeight: 700,
                    textShadow: '0 2px 4px rgba(0,0,0,0.3)'
                  }}
                >
                  {b.title}
                </Typography>
              </Box>

              <CardContent sx={{ flexGrow: 1 }}>
                <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                  by {b.author}
                </Typography>
                
                <Box sx={{ my: 1 }}>
                  <Chip label={b.genre || 'General'} size="small" variant="outlined" />
                </Box>
                
                {/* Show stock for admin always */}
                {user?.isAdmin && (
                  <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                    Stock: {stock} units
                  </Typography>
                )}
                
                <Typography variant="h5" color="primary" sx={{ mt: 2 }} fontWeight="bold">
                  ₹{b.price}
                </Typography>
              </CardContent>

              <CardActions sx={{ p: 2, pt: 0, flexDirection: 'column', gap: 1 }}>
                <Button
                  variant="contained"
                  fullWidth
                  startIcon={<ShoppingCart />}
                  onClick={() => addToCart(b.id, 1)}
                  disabled={isOutOfStock}
                >
                  {isOutOfStock ? 'Out of Stock' : 'Add to Cart'}
                </Button>
                
                <Button
                  variant="outlined"
                  fullWidth
                  size="small"
                  onClick={() => api.viewBook(b.id)}
                >
                  Mark as Viewed
                </Button>
                
                {user?.isAdmin && (
                  <Button
                    size="small"
                    color="error"
                    fullWidth
                    onClick={async () => {
                      if (!confirm(`Delete "${b.title}"?`)) return;
                      try {
                        await api.deleteBookById(b.id);
                        window.location.reload();
                      } catch (e) { 
                        alert(e.message);
                      }
                    }}
                  >
                    Delete Book
                  </Button>
                )}
              </CardActions>
            </Card>
          </Grid>
        );
      })
    )}
  </Grid>
</Container>

);
}