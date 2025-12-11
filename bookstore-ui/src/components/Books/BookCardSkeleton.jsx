import { Card, CardContent, CardActions, Box, Skeleton, useTheme } from '@mui/material';

/**
 * A placeholder card shown while books are loading
 * Now with dark mode support
 */
export default function BookCardSkeleton() {
  const theme = useTheme();
  const isDarkMode = theme.palette.mode === 'dark';

  return (
    <Card sx={{ 
      height: '100%', 
      display: 'flex', 
      flexDirection: 'column',
      backgroundColor: isDarkMode ? '#1e1e1e' : '#ffffff',
      border: `1px solid ${isDarkMode ? 'rgba(255,255,255,0.12)' : '#e7e7e7'}`,
    }}>
      {/* Book cover placeholder */}
      <Skeleton 
        variant="rectangular" 
        height={280} 
        animation="wave"
        sx={{
          backgroundColor: isDarkMode ? 'rgba(255,255,255,0.1)' : undefined,
        }}
      />
      
      <CardContent sx={{ flexGrow: 1 }}>
        {/* Title placeholder */}
        <Skeleton 
          variant="text" 
          sx={{ 
            fontSize: '1.5rem', 
            mb: 1,
            backgroundColor: isDarkMode ? 'rgba(255,255,255,0.1)' : undefined,
          }} 
          width="80%"
          animation="wave"
        />
        
        {/* Author placeholder */}
        <Skeleton 
          variant="text" 
          sx={{ 
            fontSize: '1rem', 
            mb: 2,
            backgroundColor: isDarkMode ? 'rgba(255,255,255,0.1)' : undefined,
          }} 
          width="60%"
          animation="wave"
        />
        
        {/* Genre chip placeholder */}
        <Box sx={{ display: 'flex', gap: 1, mb: 2 }}>
          <Skeleton 
            variant="rounded" 
            width={80} 
            height={24}
            animation="wave"
            sx={{
              backgroundColor: isDarkMode ? 'rgba(255,255,255,0.1)' : undefined,
            }}
          />
          <Skeleton 
            variant="text" 
            width={60}
            animation="wave"
            sx={{
              backgroundColor: isDarkMode ? 'rgba(255,255,255,0.1)' : undefined,
            }}
          />
        </Box>
        
        {/* Price placeholder */}
        <Skeleton 
          variant="text" 
          sx={{ 
            fontSize: '1.8rem',
            backgroundColor: isDarkMode ? 'rgba(255,255,255,0.1)' : undefined,
          }} 
          width={100}
          animation="wave"
        />
      </CardContent>
      
      <CardActions sx={{ p: 2, pt: 0 }}>
        {/* Button placeholder */}
        <Skeleton 
          variant="rounded" 
          width="100%" 
          height={40}
          animation="wave"
          sx={{
            backgroundColor: isDarkMode ? 'rgba(255,255,255,0.1)' : undefined,
          }}
        />
      </CardActions>
    </Card>
  );
}
