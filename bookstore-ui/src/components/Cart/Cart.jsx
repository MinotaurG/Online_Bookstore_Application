import { useEffect } from 'react';
import { 
  Box, 
  Typography, 
  Paper, 
  IconButton,
  List,
  ListItem,
  Chip,
  Divider
} from '@mui/material';
import { Add, Remove, Delete } from '@mui/icons-material';
import { api } from '../../api';

export default function Cart({ cart, setCart, setPreview, stockMap, allBooks }) {
  
  useEffect(() => {
    const updatePreview = async () => {
      if (cart.length === 0) {
        setPreview(null);
        return;
      }
      try {
        const p = await api.previewCart(cart);
        setPreview(p);
      } catch (e) {
        console.error('Preview failed', e);
      }
    };
    updatePreview();
  }, [cart, setPreview]);

  const remove = (bookId) => {
    setCart(prev => prev.filter(i => i.bookId !== bookId));
  };

  const changeQty = (bookId, delta) => {
    setCart(prev => prev.map(item => {
      if (item.bookId !== bookId) return item;
      
      const stock = stockMap.get(bookId) ?? 0;
      const newQty = item.quantity + delta;
      const safeQty = Math.max(1, Math.min(newQty, stock));
      
      return { ...item, quantity: safeQty };
    }));
  };

  const getBookDetails = (bookId) => {
    if (!allBooks || allBooks.length === 0) return null;
    return allBooks.find(book => book.id === bookId);
  };

  if (cart.length === 0) {
    return (
      <Paper sx={{ p: 4, textAlign: 'center' }}>
        <Typography variant="h6" color="text.secondary" gutterBottom>
          🛒 Your cart is empty
        </Typography>
        <Typography variant="body2" color="text.secondary">
          Add some books to get started!
        </Typography>
      </Paper>
    );
  }

  return (
    <Paper sx={{ p: 3 }}>
      <Typography variant="h5" gutterBottom fontWeight="bold">
        Shopping Cart ({cart.length} {cart.length === 1 ? 'item' : 'items'})
      </Typography>
      
      <Divider sx={{ my: 2 }} />
      
      <List sx={{ mt: 2 }}>
        {cart.map((item) => {
          const stock = stockMap.get(item.bookId) ?? 0;
          const book = getBookDetails(item.bookId);
          const showStockWarning = stock < 5 && stock > 0;
          
          if (!book) {
            return (
              <ListItem
                key={item.bookId}
                sx={{
                  border: '1px solid',
                  borderColor: 'warning.main',
                  borderRadius: 2,
                  mb: 2,
                  p: 2
                }}
              >
                <Box sx={{ flexGrow: 1 }}>
                  <Typography color="error">
                    Book ID: {item.bookId} (Details not available)
                  </Typography>
                </Box>
                <IconButton 
                  color="error"
                  onClick={() => remove(item.bookId)}
                >
                  <Delete />
                </IconButton>
              </ListItem>
            );
          }
          
          return (
            <ListItem
              key={item.bookId}
              sx={{
                border: '1px solid',
                borderColor: 'divider',
                borderRadius: 2,
                mb: 2,
                p: 2,
                display: 'flex',
                flexDirection: 'column',
                gap: 2
              }}
            >
              {/* Book Info */}
              <Box sx={{ display: 'flex', justifyContent: 'space-between', width: '100%' }}>
                <Box sx={{ flexGrow: 1 }}>
                  <Typography variant="h6" fontWeight="medium">
                    {book.title}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    by {book.author || 'Unknown Author'}
                  </Typography>
                  <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5 }}>
                    ₹{book.price} each
                  </Typography>
                  
                  {showStockWarning && (
                    <Chip 
                      label={`Only ${stock} units left!`}
                      color="error"
                      size="small"
                      sx={{ mt: 1, fontWeight: 'bold' }}
                    />
                  )}
                </Box>

                <IconButton 
                  color="error"
                  onClick={() => remove(item.bookId)}
                  sx={{ ml: 2 }}
                >
                  <Delete />
                </IconButton>
              </Box>

              {/* Quantity Controls - NO MANUAL INPUT */}
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, flexWrap: 'wrap' }}>
                <Typography variant="body2">
                  Quantity:
                </Typography>
                
                <Box sx={{ 
                  display: 'flex', 
                  alignItems: 'center', 
                  gap: 1,
                  border: '1px solid',
                  borderColor: 'divider',
                  borderRadius: 1,
                  p: 0.5
                }}>
                  <IconButton
                    size="small"
                    onClick={() => changeQty(item.bookId, -1)}
                    disabled={item.quantity <= 1}
                  >
                    <Remove />
                  </IconButton>
                  
                  <Typography 
                    variant="h6" 
                    sx={{ 
                      minWidth: 40, 
                      textAlign: 'center',
                      fontWeight: 'bold',
                      px: 2
                    }}
                  >
                    {item.quantity}
                  </Typography>
                  
                  <IconButton
                    size="small"
                    onClick={() => changeQty(item.bookId, 1)}
                    disabled={item.quantity >= stock}
                  >
                    <Add />
                  </IconButton>
                </Box>
                
                <Typography variant="body2" color="text.secondary">
                  ({stock} available)
                </Typography>
              </Box>

              {/* Subtotal */}
              <Box sx={{ 
                display: 'flex', 
                justifyContent: 'space-between',
                pt: 1,
                borderTop: '1px solid',
                borderColor: 'divider'
              }}>
                <Typography variant="body2" color="text.secondary">
                  Subtotal:
                </Typography>
                <Typography variant="h6" color="primary" fontWeight="bold">
                  ₹{(book.price * item.quantity).toFixed(2)}
                </Typography>
              </Box>
            </ListItem>
          );
        })}
      </List>
    </Paper>
  );
}