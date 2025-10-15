import { useState } from 'react';
import { Card, CardContent, CardActions, Typography, Button, Chip, Box } from '@mui/material';
import { ShoppingCart, Visibility } from '@mui/icons-material';
import bookCoverService from '../../services/bookCoverService';
import { getGenreGradient } from '../../utils/imageUtils';

export default function BookCard({ book, onAddToCart, onView, onDelete, user }) {
  const [imageError, setImageError] = useState(false);
  
  const stock = book.stockQuantity ?? 0;
  const isOutOfStock = stock <= 0;
  const isLowStock = stock < 5 && stock > 0;
  const shouldShowStock = user?.isAdmin || isLowStock;

  const coverUrl = bookCoverService.getBookCover(book);
  const fallbackGradient = getGenreGradient(book.genre);

  return (
    <Card sx={{
      height: '100%',
      width: '100%',
      display: 'flex',
      flexDirection: 'column',
      transition: 'transform 0.2s, box-shadow 0.2s',
      '&:hover': { transform: 'translateY(-4px)', boxShadow: 4 }
    }}>
      <Box sx={{
        height: 280,
        width: '100%',
        position: 'relative',
        overflow: 'hidden',
        background: imageError ? fallbackGradient : 'transparent'
      }}>
        {!imageError && coverUrl && (
          <Box
            component="img"
            src={coverUrl}
            alt={book.title}
            onError={() => setImageError(true)}
            sx={{ width: '100%', height: '100%', objectFit: 'cover', display: imageError ? 'none' : 'block' }}
          />
        )}

        {imageError && (
          <Box sx={{
            position: 'absolute', top: 0, left: 0, right: 0, bottom: 0,
            display: 'flex', alignItems: 'center', justifyContent: 'center', p: 2
          }}>
            <Typography variant="h6" sx={{
              color: 'white', textAlign: 'center', fontWeight: 700,
              textShadow: '0 2px 4px rgba(0,0,0,0.5)',
              overflow: 'hidden', textOverflow: 'ellipsis',
              display: '-webkit-box', WebkitLineClamp: 3, WebkitBoxOrient: 'vertical'
            }}>
              {book.title}
            </Typography>
          </Box>
        )}

        {shouldShowStock && isLowStock && (
          <Chip label={`Only ${stock} left!`} color="error" size="small"
            sx={{ position: 'absolute', top: 8, right: 8, fontWeight: 'bold', zIndex: 2 }} />
        )}
        {isOutOfStock && (
          <Chip label="Out of Stock" color="error" size="small"
            sx={{ position: 'absolute', top: 8, right: 8, fontWeight: 'bold', zIndex: 2 }} />
        )}
      </Box>

      <CardContent sx={{ flexGrow: 1, display: 'flex', flexDirection: 'column', p: 2 }}>
        <Typography variant="h6" sx={{
          minHeight: '3.5em', overflow: 'hidden', textOverflow: 'ellipsis',
          display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical',
          fontWeight: 600, mb: 1
        }}>
          {book.title}
        </Typography>

        <Typography variant="body2" color="text.secondary" sx={{
          minHeight: '2.5em', overflow: 'hidden', textOverflow: 'ellipsis',
          display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical', mb: 1
        }}>
          by {book.author || 'Unknown Author'}
        </Typography>

        <Box sx={{ my: 1 }}>
          <Chip label={book.genre || 'General'} size="small" variant="outlined" />
        </Box>

        {user?.isAdmin && (
          <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
            Stock: {stock} units
          </Typography>
        )}

        <Box sx={{ flexGrow: 1 }} />

        <Typography variant="h5" color="primary" sx={{ mt: 2 }} fontWeight="bold">
          ₹{book.price}
        </Typography>
      </CardContent>

      <CardActions sx={{ p: 2, pt: 0, flexDirection: 'column', gap: 1 }}>
        <Button variant="contained" fullWidth startIcon={<ShoppingCart />}
          onClick={() => onAddToCart(book.id, 1)} disabled={isOutOfStock}>
          {isOutOfStock ? 'Out of Stock' : 'Add to Cart'}
        </Button>

        <Button variant="outlined" fullWidth size="small" startIcon={<Visibility />}
          onClick={() => onView(book.id)}>
          Mark as Viewed
        </Button>

        {user?.isAdmin && onDelete && (
          <Button size="small" color="error" fullWidth onClick={() => onDelete(book)}>
            Delete Book
          </Button>
        )}
      </CardActions>
    </Card>
  );
}