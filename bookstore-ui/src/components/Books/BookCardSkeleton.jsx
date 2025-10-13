import { Card, CardContent, CardActions, Box, Skeleton } from '@mui/material';

/**
 * A placeholder card shown while books are loading
 * This creates a "skeleton" animation effect
 */
export default function BookCardSkeleton() {
  return (
    <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      {/* Book cover placeholder - gray rectangle with pulse animation */}
      <Skeleton 
        variant="rectangular" 
        height={280} 
        animation="wave"
      />
      
      <CardContent sx={{ flexGrow: 1 }}>
        {/* Title placeholder */}
        <Skeleton 
          variant="text" 
          sx={{ fontSize: '1.5rem', mb: 1 }} 
          width="80%"
          animation="wave"
        />
        
        {/* Author placeholder */}
        <Skeleton 
          variant="text" 
          sx={{ fontSize: '1rem', mb: 2 }} 
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
          />
          <Skeleton 
            variant="text" 
            width={60}
            animation="wave"
          />
        </Box>
        
        {/* Price placeholder */}
        <Skeleton 
          variant="text" 
          sx={{ fontSize: '1.8rem' }} 
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
        />
      </CardActions>
    </Card>
  );
}