import { useState } from 'react';
import { 
  Card, CardContent, Box, Typography, Button, Chip 
} from '@mui/material';
import { ShoppingCart, Visibility } from '@mui/icons-material';
import bookCoverService from '../../services/bookCoverService';
import { getGenreGradient } from '../../utils/imageUtils';

export default function BookListItem({ 
  book, 
  onAddToCart, 
  onView,
  user,
  subtitle
}) {
  const [imageError, setImageError] = useState(false);
  
  const stock = book.stockQuantity ?? 0;
  const isOutOfStock = stock <= 0;
  const isLowStock = stock < 5 && stock > 0;

  const coverUrl = bookCoverService.getBookCover(book);
  const fallbackGradient = getGenreGradient(book.genre);

  return (
    <Card sx={{
      display: 'flex',
      height: 200,
      border: '1px solid #e7e7e7',
      borderRadius: '8px',
      backgroundColor: '#fff',
      transition: 'all 0.15s ease-in-out',
      '&:hover': { 
        boxShadow: '0 4px 12px rgba(0,0,0,0.12)',
        borderColor: '#c7c7c7'
      }
    }}>
      {/* Book Cover - Amazon Style */}
      <Box sx={{
        width: 140,
        minWidth: 140,
        backgroundColor: '#ffffff',
        borderRight: '1px solid #e7e7e7',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        padding: 2,
        position: 'relative'
      }}>
        {!imageError && coverUrl ? (
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
              objectFit: 'contain'
            }}
          />
        ) : (
          <Box sx={{
            width: '100%',
            height: '100%',
            display: 'flex', 
            alignItems: 'center', 
            justifyContent: 'center', 
            background: fallbackGradient,
            borderRadius: '4px',
            padding: 1
          }}>
            <Typography variant="caption" sx={{
              color: 'white', 
              textAlign: 'center', 
              fontWeight: 700,
              textShadow: '0 2px 8px rgba(0,0,0,0.6)',
              fontSize: '0.7rem',
              overflow: 'hidden',
              display: '-webkit-box',
              WebkitLineClamp: 3,
              WebkitBoxOrient: 'vertical'
            }}>
              {book.title}
            </Typography>
          </Box>
        )}

        {/* Stock Badge */}
        {isLowStock && (
          <Chip 
            label={`${stock} left`}
            color="warning"
            size="small"
            sx={{ 
              position: 'absolute',
              bottom: 8,
              right: 8,
              fontSize: '0.7rem',
              height: 20
            }}
          />
        )}
      </Box>

      {/* Book Info */}
      <CardContent sx={{ 
        flex: 1,
        display: 'flex',
        flexDirection: 'column',
        p: 2,
        '&:last-child': { pb: 2 }
      }}>
        {/* Title */}
        <Typography 
          variant="h6" 
          component="h3"
          sx={{
            fontSize: '1rem',
            fontWeight: 500,
            color: '#0F1111',
            mb: 0.5,
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            display: '-webkit-box',
            WebkitLineClamp: 2,
            WebkitBoxOrient: 'vertical'
          }}
        >
          {book.title}
        </Typography>

        {/* Author */}
        <Typography 
          variant="body2" 
          sx={{
            color: '#565959',
            fontSize: '0.875rem',
            mb: 0.5
          }}
        >
          by {book.author || 'Unknown Author'}
        </Typography>

        {/* Subtitle (e.g., "Viewed 2 days ago") */}
        {subtitle && (
          <Typography 
            variant="caption" 
            sx={{
              color: '#565959',
              fontSize: '0.75rem',
              mb: 1
            }}
          >
            {subtitle}
          </Typography>
        )}

        {/* Genre */}
        <Box sx={{ mb: 'auto' }}>
          <Chip 
            label={book.genre || 'General'} 
            size="small" 
            variant="outlined"
            sx={{ 
              fontSize: '0.7rem',
              height: 24,
              borderColor: '#D5D9D9',
              color: '#565959'
            }}
          />
        </Box>

        {/* Price & Actions */}
        <Box sx={{ 
          display: 'flex', 
          alignItems: 'center', 
          justifyContent: 'space-between',
          mt: 2,
          flexWrap: 'wrap',
          gap: 1
        }}>
          <Typography 
            variant="h6" 
            sx={{ 
              fontSize: '1.25rem',
              fontWeight: 400,
              color: '#B12704'
            }}
          >
            ₹{book.price}
          </Typography>

          <Box sx={{ display: 'flex', gap: 1 }}>
            {onAddToCart && (
              <Button 
                variant="contained" 
                size="small"
                startIcon={<ShoppingCart />}
                onClick={() => onAddToCart(book.id, 1)}
                disabled={isOutOfStock}
                sx={{ 
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
            )}

            {onView && (
              <Button 
                variant="outlined" 
                size="small"
                startIcon={<Visibility />}
                onClick={() => onView(book.id)}
                sx={{ 
                  textTransform: 'none',
                  borderColor: '#D5D9D9',
                  color: '#0F1111',
                  '&:hover': {
                    backgroundColor: '#F7FAFA',
                    borderColor: '#D5D9D9'
                  }
                }}
              >
                View
              </Button>
            )}
          </Box>
        </Box>
      </CardContent>
    </Card>
  );
}