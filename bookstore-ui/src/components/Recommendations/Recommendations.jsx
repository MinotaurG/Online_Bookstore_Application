import { useState, useEffect } from 'react';
import { 
  Container, Typography, Grid, Card, CardContent, CardActions, Button, Box, 
  Alert, Chip, CircularProgress
} from '@mui/material';
import { ShoppingCart, Recommend } from '@mui/icons-material';
import { api } from '../../api';
import { useNavigate } from 'react-router-dom';

export default function Recommendations() {
  const [recs, setRecs] = useState([]);
  const [err, setErr] = useState('');
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  
  useEffect(() => {
    (async () => {
      try {
        setLoading(true);
        
        let personalRecs = [];
        try {
          personalRecs = await api.recommendations();
        } catch (e) {
          console.log('No personalized recommendations');
        }
        
        if (personalRecs && personalRecs.length > 0) {
          setRecs(personalRecs);
          setErr('');
          setLoading(false);
          return;
        }
        
        const allBooks = await api.listBooks();
        const topBooks = allBooks
          .filter(book => (book.stockQuantity || 0) > 0)
          .sort((a, b) => {
            const stockDiff = (b.stockQuantity || 0) - (a.stockQuantity || 0);
            if (stockDiff !== 0) return stockDiff;
            return (a.price || 0) - (b.price || 0);
          })
          .slice(0, 5);
        
        setRecs(topBooks);
        setErr('');
      } catch (e) { 
        setErr(e.message);
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
          {recs.length > 0 ? 'Popular books available now' : 'No recommendations available'}
        </Typography>
      </Box>

      {err && <Alert severity="error" sx={{ mb: 3 }}>{err}</Alert>}
      
      {recs.length === 0 ? (
        <Box sx={{ p: 6, textAlign: 'center', border: '1px solid', borderColor: 'divider', borderRadius: 2 }}>
          <Recommend sx={{ fontSize: 80, color: 'text.secondary', mb: 2 }} />
          <Typography variant="h6" gutterBottom>
            No Recommendations Available
          </Typography>
          <Typography color="text.secondary" paragraph>
            Check back later for personalized suggestions
          </Typography>
          <Button variant="contained" href="/">
            Browse All Books
          </Button>
        </Box>
      ) : (
        <Grid container spacing={3}>
          {recs.map(book => {
            const stock = book.stockQuantity || 0;
            const isLowStock = stock < 5 && stock > 0;
            
            return (
              <Grid item xs={12} sm={6} md={4} key={book.id}>
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
                  <Box
                    sx={{
                      height: 200,
                      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      p: 2,
                      position: 'relative'
                    }}
                  >
                    {isLowStock && (
                      <Chip 
                        label={`Only ${stock} left!`} 
                        size="small" 
                        color="error"
                        sx={{ 
                          position: 'absolute', 
                          top: 12, 
                          right: 12,
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
                      {book.title}
                    </Typography>
                  </Box>
                  
                  <CardContent sx={{ flexGrow: 1 }}>
                    <Typography variant="subtitle1" gutterBottom fontWeight="medium">
                      {book.title}
                    </Typography>
                    <Typography variant="body2" color="text.secondary" gutterBottom>
                      by {book.author || 'Unknown Author'}
                    </Typography>
                    
                    <Chip 
                      label={book.genre || 'General'} 
                      size="small" 
                      variant="outlined"
                      sx={{ mt: 1 }}
                    />
                    
                    <Typography variant="h5" color="primary" fontWeight="bold" sx={{ mt: 2 }}>
                      ₹{book.price}
                    </Typography>
                  </CardContent>
                  
                  <CardActions sx={{ p: 2, pt: 0 }}>
                    <Button 
                      variant="contained" 
                      fullWidth
                      startIcon={<ShoppingCart />}
                      onClick={() => navigate('/')}
                    >
                      View in Catalog
                    </Button>
                  </CardActions>
                </Card>
              </Grid>
            );
          })}
        </Grid>
      )}
    </Container>
  );
}