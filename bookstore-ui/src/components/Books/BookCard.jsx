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
      maxWidth: '100%',
      minWidth: 0,
      display: 'flex',
      flexDirection: 'column',
      border: '1px solid #e7e7e7',
      borderRadius: '8px',
      backgroundColor: '#fff',
      transition: 'all 0.15s ease-in-out',
      '&:hover': { 
        transform: 'translateY(-2px)',
        boxShadow: '0 4px 12px rgba(0,0,0,0.12)',
        borderColor: '#c7c7c7'
      }
    }}>
      {/* Image Section - Amazon Style */}
      <Box sx={{
        height: 380,
        width: '100%',
        position: 'relative',
        overflow: 'hidden',
        backgroundColor: '#ffffff',
        borderBottom: '1px solid #e7e7e7',
        flexShrink: 0,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        padding: 3
      }}>
        {!imageError && coverUrl && (
          <Box
            component="img"
            src={coverUrl}
            alt={book.title}
            onError={() => setImageError(true)}
            sx={{ 
              maxWidth: '100%',
              maxHeight: '100%',
              width: 'auto',
              height: 'auto',
              objectFit: 'contain',
              display: imageError ? 'none' : 'block'
            }}
          />
        )}

        {imageError && (
          <Box sx={{
            width: '100%',
            height: '100%',
            display: 'flex', 
            alignItems: 'center', 
            justifyContent: 'center', 
            background: fallbackGradient,
            p: 3
          }}>
            <Typography variant="h6" sx={{
              color: 'white', 
              textAlign: 'center', 
              fontWeight: 700,
              textShadow: '0 2px 8px rgba(0,0,0,0.6)',
              overflow: 'hidden', 
              textOverflow: 'ellipsis',
              display: '-webkit-box', 
              WebkitLineClamp: 4, 
              WebkitBoxOrient: 'vertical',
              lineHeight: 1.4
            }}>
              {book.title}
            </Typography>
          </Box>
        )}

        {/* Stock Badge */}
        {shouldShowStock && isLowStock && (
          <Chip 
            label={`Only ${stock} left!`} 
            color="error" 
            size="small"
            sx={{ 
              position: 'absolute', 
              top: 12, 
              right: 12, 
              fontWeight: 'bold', 
              zIndex: 2,
              boxShadow: 2
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
              top: 12, 
              right: 12, 
              fontWeight: 'bold', 
              zIndex: 2,
              boxShadow: 2
            }} 
          />
        )}
      </Box>

      {/* Content Section */}
      <CardContent sx={{ 
        flexGrow: 1, 
        display: 'flex', 
        flexDirection: 'column', 
        p: 2.5,
        minHeight: 200
      }}>
        {/* Title - Fixed 2 lines */}
        <Typography 
          variant="h6" 
          component="h3"
          sx={{
            height: '3.2em',
            lineHeight: '1.6em',
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            display: '-webkit-box',
            WebkitLineClamp: 2,
            WebkitBoxOrient: 'vertical',
            fontWeight: 500,
            mb: 0.5,
            fontSize: '1rem',
            color: '#0F1111'
          }}
        >
          {book.title}
        </Typography>

        {/* Author - Fixed 2 lines */}
        <Typography 
          variant="body2" 
          sx={{
            height: '2.8em',
            lineHeight: '1.4em',
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            display: '-webkit-box',
            WebkitLineClamp: 2,
            WebkitBoxOrient: 'vertical',
            mb: 1.5,
            color: '#565959',
            fontSize: '0.875rem'
          }}
        >
          by {book.author || 'Unknown Author'}
        </Typography>

        {/* Genre Chip */}
        <Box sx={{ mb: 1.5, height: 28, display: 'flex', alignItems: 'center' }}>
          <Chip 
            label={book.genre || 'General'} 
            size="small" 
            variant="outlined"
            sx={{ 
              fontSize: '0.75rem',
              borderColor: '#D5D9D9',
              color: '#565959'
            }}
          />
        </Box>

        {/* Admin Info */}
        {user?.isAdmin && (
          <Box sx={{ mb: 1 }}>
            <Typography variant="caption" sx={{ color: '#565959' }} display="block">
              Stock: {stock} units
            </Typography>
            {book.asin && (
              <Typography 
                variant="caption" 
                sx={{ 
                  fontFamily: 'monospace', 
                  fontSize: '0.7rem',
                  color: '#565959'
                }}
              >
                ASIN: {book.asin}
              </Typography>
            )}
          </Box>
        )}

        {/* Spacer */}
        <Box sx={{ flexGrow: 1 }} />

        {/* Price - Amazon Style */}
        <Box sx={{ mt: 'auto', pt: 1 }}>
          <Typography 
            variant="h5" 
            sx={{ 
              fontSize: '1.5rem',
              fontWeight: 400,
              color: '#B12704'
            }}
          >
            ₹{book.price}
          </Typography>
        </Box>
      </CardContent>

      {/* Actions */}
      <CardActions sx={{ 
        p: 2, 
        pt: 0, 
        flexDirection: 'column', 
        gap: 1,
        minHeight: user?.isAdmin ? 140 : 100
      }}>
        <Button 
          variant="contained" 
          fullWidth 
          startIcon={<ShoppingCart />}
          onClick={() => onAddToCart(book.id, 1)} 
          disabled={isOutOfStock}
          sx={{ 
            height: 40,
            fontWeight: 400,
            textTransform: 'none',
            backgroundColor: '#FFD814',
            color: '#0F1111',
            border: '1px solid #FCD200',
            boxShadow: 'none',
            '&:hover': {
              backgroundColor: '#F7CA00',
              boxShadow: 'none'
            },
            '&:disabled': {
              backgroundColor: '#F0F2F2',
              color: '#565959'
            }
          }}
        >
          {isOutOfStock ? 'Out of Stock' : 'Add to Cart'}
        </Button>

        <Button 
          variant="outlined" 
          fullWidth 
          size="small" 
          startIcon={<Visibility />}
          onClick={() => onView(book.id)}
          sx={{ 
            height: 36,
            textTransform: 'none',
            borderColor: '#D5D9D9',
            color: '#0F1111',
            '&:hover': {
              backgroundColor: '#F7FAFA',
              borderColor: '#D5D9D9'
            }
          }}
        >
          Mark as Viewed
        </Button>

        {user?.isAdmin && onDelete && (
          <Button 
            size="small" 
            color="error" 
            fullWidth 
            onClick={() => onDelete(book)}
            sx={{ 
              height: 36,
              textTransform: 'none'
            }}
          >
            Delete Book
          </Button>
        )}
      </CardActions>
    </Card>
  );
}