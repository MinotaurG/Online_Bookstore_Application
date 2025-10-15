import { useState, useEffect } from 'react';
import { Container, Typography, Grid, Card, CardContent, Box, Button, CircularProgress } from '@mui/material';
import { History as HistoryIcon, ShoppingCart } from '@mui/icons-material';
import { api } from '../../api';

export default function History() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  
  useEffect(() => {
    (async () => {
      try {
        setLoading(true);
        const history = await api.history();
        setItems(history || []);
      } catch (e) { 
        setItems([]);
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  if (loading) {
    return (
      <Container maxWidth="lg" sx={{ py: 8, textAlign: 'center' }}>
        <CircularProgress size={60} />
        <Typography sx={{ mt: 2 }} color="text.secondary">Loading history...</Typography>
      </Container>
    );
  }
  
  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" gutterBottom fontWeight="bold">📖 Recently Viewed</Typography>
        <Typography variant="body1" color="text.secondary">
          {items.length > 0 ? `${items.length} book(s) in your history` : 'No books viewed yet'}
        </Typography>
      </Box>
      
      {items.length === 0 ? (
        <Box sx={{ p: 6, textAlign: 'center', border: '1px solid', borderColor: 'divider', borderRadius: 2 }}>
          <HistoryIcon sx={{ fontSize: 80, color: 'text.secondary', mb: 2 }} />
          <Typography variant="h6" gutterBottom>No Browsing History</Typography>
          <Typography color="text.secondary" paragraph>
            Start browsing to see your history here
          </Typography>
          <Button variant="contained" href="/">Browse Catalog</Button>
        </Box>
      ) : (
        <Grid container spacing={3}>
          {items.map(book => (
            <Grid item xs={12} sm={6} md={4} lg={3} key={book.id}>
              <Card sx={{ height: '100%', '&:hover': { boxShadow: 4 } }}>
                <Box sx={{ height: 150, background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', display: 'flex', alignItems: 'center', justifyContent: 'center', p: 2 }}>
                  <Typography variant="h6" sx={{ color: 'white', textAlign: 'center', fontWeight: 700 }}>
                    {book.title}
                  </Typography>
                </Box>
                <CardContent>
                  <Typography variant="subtitle1" gutterBottom fontWeight="medium">{book.title}</Typography>
                  <Typography variant="body2" color="text.secondary">by {book.author}</Typography>
                  {book.price && <Typography variant="h6" color="primary" sx={{ mt: 2 }}>₹{book.price}</Typography>}
                  <Button fullWidth variant="outlined" startIcon={<ShoppingCart />} href="/" sx={{ mt: 2 }}>
                    View in Catalog
                  </Button>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}
    </Container>
  );
}