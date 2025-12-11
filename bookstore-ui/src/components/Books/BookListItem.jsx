import { useState } from 'react';
import { 
  Card, CardContent, Box, Typography, Button, Chip,
  useTheme  // 👈 Import useTheme
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
  // ============================================
  // THEME DETECTION
  // ============================================
  const theme = useTheme();
  const isDarkMode = theme.palette.mode === 'dark';

  const [imageError, setImageError] = useState(false);
  
  const stock = book.stockQuantity ?? 0;
  const isOutOfStock = stock <= 0;
  const isLowStock = stock < 5 && stock > 0;

  const coverUrl = bookCoverService.getBookCover(book);
  const fallbackGradient = getGenreGradient(book.genre);

  // ============================================
  // THEME-AWARE COLORS
  // ============================================
  // LeetCode analogy: Lookup table for O(1) access
  const colors = {
    // Card
    cardBg: isDarkMode ? '#1e1e1e' : '#ffffff',
    border: isDarkMode ? 'rgba(255,255,255,0.12)' : '#e7e7e7',
    borderHover: isDarkMode ? 'rgba(255,255,255,0.24)' : '#c7c7c7',
    hoverShadow: isDarkMode 
      ? '0 4px 12px rgba(0,0,0,0.5)' 
      : '0 4px 12px rgba(0,0,0,0.12)',
    
    // Image section
    imageBg: isDarkMode ? '#2a2a2a' : '#ffffff',
    imageBorder: isDarkMode ? 'rgba(255,255,255,0.08)' : '#e7e7e7',
    
    // Text
    titleText: isDarkMode ? '#ffffff' : '#0F1111',
    secondaryText: isDarkMode ? '#b0b0b0' : '#565959',
    priceText: isDarkMode ? '#ff8c00' : '#B12704',
    
    // Chip
    chipBorder: isDarkMode ? 'rgba(255,255,255,0.3)' : '#D5D9D9',
    chipText: isDarkMode ? '#b0b0b0' : '#565959',
    
    // Buttons
    primaryBtnBg: isDarkMode ? '#ffa41c' : '#FFD814',
    primaryBtnHover: isDarkMode ? '#ffb84d' : '#F7CA00',
    primaryBtnBorder: isDarkMode ? '#ff9900' : '#FCD200',
    primaryBtnText: '#0F1111',
    
    secondaryBtnBorder: isDarkMode ? 'rgba(255,255,255,0.3)' : '#D5D9D9',
    secondaryBtnText: isDarkMode ? '#ffffff' : '#0F1111',
    secondaryBtnHover: isDarkMode ? 'rgba(255,255,255,0.08)' : '#F7FAFA',
    
    disabledBtnBg: isDarkMode ? 'rgba(255,255,255,0.12)' : '#F0F2F2',
    disabledBtnText: isDarkMode ? 'rgba(255,255,255,0.5)' : '#565959',
  };

  return (
    <Card sx={{
      display: 'flex',
      height: 200,
      border: `1px solid ${colors.border}`,
      borderRadius: '8px',
      backgroundColor: colors.cardBg,
      transition: 'all 0.15s ease-in-out',
      '&:hover': { 
        boxShadow: colors.hoverShadow,
        borderColor: colors.borderHover
      }
    }}>
      {/* Book Cover */}
      <Box sx={{
        width: 140,
        minWidth: 140,
        backgroundColor: colors.imageBg,
        borderRight: `1px solid ${colors.imageBorder}`,
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
        backgroundColor: colors.cardBg,
        '&:last-child': { pb: 2 }
      }}>
        {/* Title */}
        <Typography 
          variant="h6" 
          component="h3"
          sx={{
            fontSize: '1rem',
            fontWeight: 500,
            color: colors.titleText,
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
            color: colors.secondaryText,
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
              color: colors.secondaryText,
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
              borderColor: colors.chipBorder,
              color: colors.chipText
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
              color: colors.priceText
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
                  backgroundColor: colors.primaryBtnBg,
                  color: colors.primaryBtnText,
                  border: `1px solid ${colors.primaryBtnBorder}`,
                  boxShadow: 'none',
                  '&:hover': {
                    backgroundColor: colors.primaryBtnHover,
                    boxShadow: 'none'
                  },
                  '&:disabled': {
                    backgroundColor: colors.disabledBtnBg,
                    color: colors.disabledBtnText
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
                  borderColor: colors.secondaryBtnBorder,
                  color: colors.secondaryBtnText,
                  '&:hover': {
                    backgroundColor: colors.secondaryBtnHover,
                    borderColor: colors.secondaryBtnBorder
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
