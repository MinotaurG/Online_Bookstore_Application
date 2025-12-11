import { useState, useEffect } from 'react';
import { 
  Container, Typography, Box, Button, CircularProgress, Alert, Stack,
  useTheme  // 👈 Import useTheme
} from '@mui/material';
import { Recommend } from '@mui/icons-material';
import { api } from '../../api';
import BookListItem from '../Books/BookListItem';

export default function Recommendations({ addToCart, user }) {
  // ============================================
  // THEME DETECTION
  // ============================================
  const theme = useTheme();
  const isDarkMode = theme.palette.mode === 'dark';

  const [recs, setRecs] = useState([]);
  const [err, setErr] = useState('');
  const [loading, setLoading] = useState(true);
  const [isPersonalized, setIsPersonalized] = useState(false);

  // ============================================
  // THEME-AWARE COLORS
  // ============================================
  const colors = {
    emptyStateBg: isDarkMode ? '#1e1e1e' : '#fafafa',
    emptyStateBorder: isDarkMode ? 'rgba(255,255,255,0.12)' : '#e7e7e7',
  };
  
  useEffect(() => {
    (async () => {
      try {
        setLoading(true);
        
        // Try to get personalized recommendations first
        let personalRecs = [];
        try {
          personalRecs = await api.recommendations();
          if (personalRecs && personalRecs.length > 0) {
            setRecs(personalRecs);
            setIsPersonalized(true);
            setErr('');
            setLoading(false);
            return;
          }
        } catch (e) {
          console.log('No personalized recommendations available');
        }
        
        // Fallback to popular books
        const allBooks = await api.listBooks();
        const topBooks = allBooks
          .filter(book => (book.stockQuantity || 0) > 0)
          .sort((a, b) => {
            // Sort by stock (higher = more popular) then by price
            const stockDiff = (b.stockQuantity || 0) - (a.stockQuantity || 0);
            if (stockDiff !== 0) return stockDiff;
            return (a.price || 0) - (b.price || 0);
          })
          .slice(0, 20); // Show top 20
        
        setRecs(topBooks);
        setIsPersonalized(false);
        setErr('');
      } catch (e) { 
        setErr(e.message);
        console.error('Failed to load recommendations:', e);
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  if (loading) {
    return (
      <Container maxWidth="lg" sx={{ py: 8, textAlign: 'center' }}>
        <CircularProgress size={60} />
        <Typography sx={{ mt: 2 }} color="text.secondary">
          Loading recommendations...
        </Typography>
      </Container>
    );
  }
  
  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" gutterBottom fontWeight="bold">
          ✨ Recommended for You
        </Typography>
        <Typography variant="body1" color="text.secondary">
          {isPersonalized 
            ? `${recs.length} personalized recommendation${recs.length > 1 ? 's' : ''} based on your browsing`
            : `${recs.length} popular book${recs.length > 1 ? 's' : ''} available now`}
        </Typography>
      </Box>

      {err && <Alert severity="error" sx={{ mb: 3 }}>{err}</Alert>}
      
      {recs.length === 0 ? (
        <Box sx={{ 
          p: 6, 
          textAlign: 'center', 
          border: `1px solid ${colors.emptyStateBorder}`,
          borderRadius: 2,
          backgroundColor: colors.emptyStateBg
        }}>
          <Recommend sx={{ fontSize: 80, color: 'text.secondary', mb: 2 }} />
          <Typography variant="h6" gutterBottom>
            No Recommendations Available
          </Typography>
          <Typography color="text.secondary" paragraph>
            Start browsing books to get personalized recommendations
          </Typography>
          <Button variant="contained" href="/">
            Browse All Books
          </Button>
        </Box>
      ) : (
        <Stack spacing={2}>
          {recs.map(book => (
            <BookListItem
              key={book.id}
              book={book}
              onAddToCart={addToCart}
              onView={(id) => api.viewBook(id)}
              user={user}
              subtitle={isPersonalized ? 'Recommended for you' : 'Popular choice'}
            />
          ))}
        </Stack>
      )}
    </Container>
  );
}
