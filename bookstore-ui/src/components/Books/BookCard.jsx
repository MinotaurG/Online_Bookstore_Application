import { useState } from 'react';
import { 
  Card, 
  CardContent, 
  CardActions, 
  Typography, 
  Button, 
  Chip, 
  Box,
  useTheme  // 👈 Import useTheme hook
} from '@mui/material';
import { ShoppingCart, Visibility } from '@mui/icons-material';
import bookCoverService from '../../services/bookCoverService';
import { getGenreGradient } from '../../utils/imageUtils';

export default function BookCard({ book, onAddToCart, onView, onDelete, user }) {
  // ============================================
  // THEME DETECTION
  // ============================================
  // useTheme() gives us access to the current theme
  // LeetCode analogy: Like checking a global config flag
  const theme = useTheme();
  const isDarkMode = theme.palette.mode === 'dark';

  // ============================================
  // STATE
  // ============================================
  const [imageError, setImageError] = useState(false);

  // ============================================
  // DERIVED VALUES
  // ============================================
  // LeetCode analogy: Pre-computing values before the main logic
  // Like how you might compute prefix sums before queries
  const stock = book.stockQuantity ?? 0;
  const isOutOfStock = stock <= 0;
  const isLowStock = stock < 5 && stock > 0;
  const shouldShowStock = user?.isAdmin || isLowStock;

  const coverUrl = bookCoverService.getBookCover(book);
  const fallbackGradient = getGenreGradient(book.genre);

  // ============================================
  // THEME-AWARE COLORS
  // ============================================
  // Instead of hardcoding, we compute colors based on mode
  // LeetCode analogy: Like a lookup table / hashmap
  // colors[isDarkMode] => appropriate color set
  const colors = {
    // Card background
    cardBg: isDarkMode ? '#1e1e1e' : '#ffffff',
    
    // Borders
    border: isDarkMode ? 'rgba(255,255,255,0.12)' : '#e7e7e7',
    borderHover: isDarkMode ? 'rgba(255,255,255,0.24)' : '#c7c7c7',
    
    // Text colors
    titleText: isDarkMode ? '#ffffff' : '#0F1111',
    secondaryText: isDarkMode ? '#b0b0b0' : '#565959',
    
    // Price color (Amazon orange-red, slightly lighter in dark mode)
    priceText: isDarkMode ? '#ff8c00' : '#B12704',
    
    // Image container background
    imageBg: isDarkMode ? '#2a2a2a' : '#ffffff',
    imageBorder: isDarkMode ? 'rgba(255,255,255,0.08)' : '#e7e7e7',
    
    // Chip (genre tag)
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
    
    // Shadow
    hoverShadow: isDarkMode 
      ? '0 4px 12px rgba(0,0,0,0.5)' 
      : '0 4px 12px rgba(0,0,0,0.12)',
  };

  return (
    <Card sx={{
      height: '100%',
      width: '100%',
      maxWidth: '100%',
      minWidth: 0,
      display: 'flex',
      flexDirection: 'column',
      // ✅ Theme-aware styles
      border: `1px solid ${colors.border}`,
      borderRadius: '8px',
      backgroundColor: colors.cardBg,
      transition: 'all 0.15s ease-in-out',
      '&:hover': {
        transform: 'translateY(-2px)',
        boxShadow: colors.hoverShadow,
        borderColor: colors.borderHover
      }
    }}>
      {/* ============================================ */}
      {/* IMAGE SECTION */}
      {/* ============================================ */}
      <Box sx={{
        height: 380,
        width: '100%',
        position: 'relative',
        overflow: 'hidden',
        backgroundColor: colors.imageBg,
        borderBottom: `1px solid ${colors.imageBorder}`,
        flexShrink: 0,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        padding: 3
      }}>
        {/* Book Cover Image */}
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

        {/* Fallback: Genre Gradient with Title */}
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

        {/* Stock Badges */}
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

      {/* ============================================ */}
      {/* CONTENT SECTION */}
      {/* ============================================ */}
      <CardContent sx={{
        flexGrow: 1,
        display: 'flex',
        flexDirection: 'column',
        p: 2.5,
        minHeight: 200,
        backgroundColor: colors.cardBg  // ✅ Theme-aware
      }}>
        {/* Title */}
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
            color: colors.titleText  // ✅ Theme-aware
          }}
        >
          {book.title}
        </Typography>

        {/* Author */}
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
            color: colors.secondaryText,  // ✅ Theme-aware
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
              borderColor: colors.chipBorder,  // ✅ Theme-aware
              color: colors.chipText  // ✅ Theme-aware
            }}
          />
        </Box>

        {/* Admin Info */}
        {user?.isAdmin && (
          <Box sx={{ mb: 1 }}>
            <Typography 
              variant="caption" 
              sx={{ color: colors.secondaryText }}  // ✅ Theme-aware
              display="block"
            >
              Stock: {stock} units
            </Typography>
            {book.asin && (
              <Typography
                variant="caption"
                sx={{
                  fontFamily: 'monospace',
                  fontSize: '0.7rem',
                  color: colors.secondaryText  // ✅ Theme-aware
                }}
              >
                ASIN: {book.asin}
              </Typography>
            )}
          </Box>
        )}

        {/* Spacer */}
        <Box sx={{ flexGrow: 1 }} />

        {/* Price */}
        <Box sx={{ mt: 'auto', pt: 1 }}>
          <Typography
            variant="h5"
            sx={{
              fontSize: '1.5rem',
              fontWeight: 400,
              color: colors.priceText  // ✅ Theme-aware
            }}
          >
            ₹{book.price}
          </Typography>
        </Box>
      </CardContent>

      {/* ============================================ */}
      {/* ACTIONS SECTION */}
      {/* ============================================ */}
      <CardActions sx={{
        p: 2,
        pt: 0,
        flexDirection: 'column',
        gap: 1,
        minHeight: user?.isAdmin ? 140 : 100,
        backgroundColor: colors.cardBg  // ✅ Theme-aware
      }}>
        {/* Add to Cart Button */}
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
            backgroundColor: colors.primaryBtnBg,  // ✅ Theme-aware
            color: colors.primaryBtnText,
            border: `1px solid ${colors.primaryBtnBorder}`,
            boxShadow: 'none',
            '&:hover': {
              backgroundColor: colors.primaryBtnHover,  // ✅ Theme-aware
              boxShadow: 'none'
            },
            '&:disabled': {
              backgroundColor: colors.disabledBtnBg,  // ✅ Theme-aware
              color: colors.disabledBtnText
            }
          }}
        >
          {isOutOfStock ? 'Out of Stock' : 'Add to Cart'}
        </Button>

        {/* Mark as Viewed Button */}
        <Button
          variant="outlined"
          fullWidth
          size="small"
          startIcon={<Visibility />}
          onClick={() => onView(book.id)}
          sx={{
            height: 36,
            textTransform: 'none',
            borderColor: colors.secondaryBtnBorder,  // ✅ Theme-aware
            color: colors.secondaryBtnText,  // ✅ Theme-aware
            '&:hover': {
              backgroundColor: colors.secondaryBtnHover,  // ✅ Theme-aware
              borderColor: colors.secondaryBtnBorder
            }
          }}
        >
          Mark as Viewed
        </Button>

        {/* Delete Button (Admin only) */}
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
