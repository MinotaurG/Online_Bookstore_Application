import { useState, useEffect } from 'react';
import { 
  Container, Typography, Box, Button, CircularProgress, Stack, Alert,
  useTheme  // 👈 Import useTheme
} from '@mui/material';
import { History as HistoryIcon } from '@mui/icons-material';
import { api } from '../../api';
import BookListItem from '../Books/BookListItem';

export default function History({ addToCart, user }) {
  // ============================================
  // THEME DETECTION
  // ============================================
  const theme = useTheme();
  const isDarkMode = theme.palette.mode === 'dark';

  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);

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
        const history = await api.history();
        setItems(history || []);
      } catch (e) { 
        console.error('Failed to load history:', e);
        setItems([]);
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  const formatDate = (dateString) => {
    if (!dateString) return 'Recently';
    const date = new Date(dateString);
    const now = new Date();
    const diffTime = Math.abs(now - date);
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    
    if (diffDays === 0) return 'Today';
    if (diffDays === 1) return 'Yesterday';
    if (diffDays < 7) return `${diffDays} days ago`;
    if (diffDays < 30) return `${Math.floor(diffDays / 7)} weeks ago`;
    return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
  };

  if (loading) {
    return (
      <Container maxWidth="lg" sx={{ py: 8, textAlign: 'center' }}>
        <CircularProgress size={60} />
        <Typography sx={{ mt: 2 }} color="text.secondary">
          Loading history...
        </Typography>
      </Container>
    );
  }
  
  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" gutterBottom fontWeight="bold">
          📖 Recently Viewed
        </Typography>
        <Typography variant="body1" color="text.secondary">
          {items.length > 0 
            ? `${items.length} book${items.length > 1 ? 's' : ''} in your browsing history` 
            : 'No books viewed yet'}
        </Typography>
      </Box>
      
      {items.length === 0 ? (
        <Box sx={{ 
          p: 6, 
          textAlign: 'center', 
          border: `1px solid ${colors.emptyStateBorder}`,
          borderRadius: 2,
          backgroundColor: colors.emptyStateBg
        }}>
          <HistoryIcon sx={{ fontSize: 80, color: 'text.secondary', mb: 2 }} />
          <Typography variant="h6" gutterBottom>
            No Browsing History
          </Typography>
          <Typography color="text.secondary" paragraph>
            Books you view will appear here for easy access later
          </Typography>
          <Button variant="contained" href="/">
            Browse Catalog
          </Button>
        </Box>
      ) : (
        <Stack spacing={2}>
          {items.map(book => (
            <BookListItem
              key={book.id}
              book={book}
              onAddToCart={addToCart}
              onView={(id) => api.viewBook(id)}
              user={user}
              subtitle={`Viewed ${formatDate(book.viewedAt)}`}
            />
          ))}
        </Stack>
      )}
    </Container>
  );
}
