import { Card, CardContent, Box, Typography, Chip, Divider } from '@mui/material';
import { CheckCircle } from '@mui/icons-material';
import bookCoverService from '../../services/bookCoverService';

export default function OrderCard({ order }) {
  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { 
      month: 'long', 
      day: 'numeric', 
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const items = order.items || [];
  const total = items.reduce((sum, item) => sum + (item.price * item.quantity), 0);

  return (
    <Card sx={{ 
      mb: 3, 
      border: '1px solid #e7e7e7',
      borderRadius: '8px'
    }}>
      <CardContent sx={{ p: 3 }}>
        {/* Order Header */}
        <Box sx={{ 
          display: 'flex', 
          justifyContent: 'space-between', 
          alignItems: 'flex-start',
          mb: 2 
        }}>
          <Box>
            <Typography variant="caption" color="text.secondary" display="block">
              Order Placed
            </Typography>
            <Typography variant="body2" fontWeight="medium">
              {formatDate(order.timestamp || order.createdAt)}
            </Typography>
          </Box>

          <Box sx={{ textAlign: 'right' }}>
            <Typography variant="caption" color="text.secondary" display="block">
              Total
            </Typography>
            <Typography variant="h6" color="primary" fontWeight="bold">
              ₹{total.toFixed(2)}
            </Typography>
          </Box>

          <Box sx={{ textAlign: 'right' }}>
            <Typography variant="caption" color="text.secondary" display="block">
              Order ID
            </Typography>
            <Typography 
              variant="body2" 
              sx={{ fontFamily: 'monospace', fontSize: '0.75rem' }}
            >
              {order.id || order.orderId}
            </Typography>
          </Box>
        </Box>

        {/* Status */}
        <Chip 
          icon={<CheckCircle />}
          label="Order Placed" 
          color="success" 
          size="small"
          sx={{ mb: 2 }}
        />

        <Divider sx={{ my: 2 }} />

        {/* Order Items */}
        <Box>
          {items.map((item, index) => {
            const coverUrl = bookCoverService.getBookCover(item);
            
            return (
              <Box 
                key={index}
                sx={{ 
                  display: 'flex', 
                  gap: 2, 
                  mb: 2,
                  pb: 2,
                  borderBottom: index < items.length - 1 ? '1px solid #f0f0f0' : 'none'
                }}
              >
                {/* Book Cover */}
                <Box sx={{
                  width: 60,
                  height: 80,
                  minWidth: 60,
                  backgroundColor: '#f5f5f5',
                  borderRadius: '4px',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  overflow: 'hidden'
                }}>
                  {coverUrl ? (
                    <img 
                      src={coverUrl} 
                      alt={item.title}
                      style={{
                        maxWidth: '100%',
                        maxHeight: '100%',
                        objectFit: 'contain'
                      }}
                    />
                  ) : (
                    <Typography variant="caption" sx={{ fontSize: '0.6rem', textAlign: 'center', p: 0.5 }}>
                      {item.title}
                    </Typography>
                  )}
                </Box>

                {/* Book Details */}
                <Box sx={{ flex: 1 }}>
                  <Typography variant="body1" fontWeight="medium">
                    {item.title}
                  </Typography>
                  <Typography variant="body2" color="text.secondary" sx={{ mb: 0.5 }}>
                    by {item.author || 'Unknown Author'}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Quantity: {item.quantity}
                  </Typography>
                </Box>

                {/* Price */}
                <Box sx={{ textAlign: 'right' }}>
                  <Typography variant="body1" fontWeight="medium">
                    ₹{(item.price * item.quantity).toFixed(2)}
                  </Typography>
                  <Typography variant="caption" color="text.secondary">
                    ₹{item.price} each
                  </Typography>
                </Box>
              </Box>
            );
          })}
        </Box>
      </CardContent>
    </Card>
  );
}