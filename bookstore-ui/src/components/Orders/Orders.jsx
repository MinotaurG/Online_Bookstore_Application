import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  Container, Typography, Box, Card, CardContent, Chip, 
  Divider, Alert, CircularProgress, Button
} from '@mui/material';
import { Receipt, ShoppingBag, CheckCircle } from '@mui/icons-material';
import { api } from '../../api';
import bookCoverService from '../../services/bookCoverService';
import { getGenreGradient } from '../../utils/imageUtils';

export default function Orders() {
  const nav = useNavigate();
  const [orders, setOrders] = useState([]);
  const [err, setErr] = useState('');
  const [needsAuth, setNeedsAuth] = useState(false);
  const [loading, setLoading] = useState(true);
  
  const load = async () => {
    try { 
      setLoading(true);
      const ordersData = await api.myOrders();
      
      // Sort by date (newest first)
      const sorted = (ordersData || []).sort((a, b) => {
        const dateA = new Date(a.createdAt || a.timestamp || 0);
        const dateB = new Date(b.createdAt || b.timestamp || 0);
        return dateB - dateA;
      });
      
      setOrders(sorted);
      setErr('');
      setNeedsAuth(false);
    } catch (e) { 
      if (e.message.includes('401') || e.message.includes('Unauthorized')) {
        setNeedsAuth(true);
        setErr('Please log in to view your orders');
      } else {
        setErr(e.message);
      }
    } finally {
      setLoading(false);
    }
  };
  
  useEffect(() => { load(); }, []);

  const formatDate = (dateString) => {
    if (!dateString) return 'Recently';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { 
      month: 'long', 
      day: 'numeric', 
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };
  
  if (needsAuth) {
    return (
      <Container maxWidth="lg" sx={{ py: 8, textAlign: 'center' }}>
        <Receipt sx={{ fontSize: 80, color: 'text.secondary', mb: 2 }} />
        <Typography variant="h4" gutterBottom>Login Required</Typography>
        <Typography color="text.secondary" paragraph>
          Please log in to view your order history
        </Typography>
        <Button variant="contained" onClick={() => nav('/login')} size="large">
          Go to Login
        </Button>
      </Container>
    );
  }

  if (loading) {
    return (
      <Container maxWidth="lg" sx={{ py: 8, textAlign: 'center' }}>
        <CircularProgress size={60} />
        <Typography sx={{ mt: 2 }} color="text.secondary">
          Loading orders...
        </Typography>
      </Container>
    );
  }
  
  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" gutterBottom fontWeight="bold">
          🛍️ Your Orders
        </Typography>
        <Typography variant="body1" color="text.secondary">
          {orders.length === 0 
            ? 'No orders yet' 
            : `${orders.length} order${orders.length > 1 ? 's' : ''} placed`}
        </Typography>
      </Box>

      {err && <Alert severity="error" sx={{ mb: 3 }}>{err}</Alert>}

      {orders.length === 0 ? (
        <Box sx={{ 
          p: 6, 
          textAlign: 'center', 
          border: '1px solid #e7e7e7',
          borderRadius: 2,
          backgroundColor: '#fafafa'
        }}>
          <ShoppingBag sx={{ fontSize: 80, color: 'text.secondary', mb: 2 }} />
          <Typography variant="h6" gutterBottom>
            No Orders Yet
          </Typography>
          <Typography color="text.secondary" paragraph>
            Start shopping to see your orders here
          </Typography>
          <Button variant="contained" href="/">
            Browse Catalog
          </Button>
        </Box>
      ) : (
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
          {orders.map((order) => {
            // Convert items object to array
            const itemsObj = order.items || {};
            const itemsArray = Object.values(itemsObj);
            const hasItems = itemsArray.length > 0;

            return (
              <Card 
                key={order.orderId} 
                sx={{ 
                  border: '1px solid #e7e7e7',
                  borderRadius: '8px',
                  boxShadow: 'none',
                  '&:hover': {
                    boxShadow: '0 4px 12px rgba(0,0,0,0.1)'
                  }
                }}
              >
                <CardContent sx={{ p: 3 }}>
                  {/* Order Header */}
                  <Box sx={{ 
                    display: 'flex', 
                    justifyContent: 'space-between', 
                    alignItems: 'flex-start',
                    flexWrap: 'wrap',
                    gap: 2,
                    mb: 2 
                  }}>
                    <Box>
                      <Typography variant="caption" color="text.secondary" display="block">
                        Order Placed
                      </Typography>
                      <Typography variant="body2" fontWeight="medium">
                        {formatDate(order.createdAt || order.timestamp)}
                      </Typography>
                    </Box>

                    <Box>
                      <Typography variant="caption" color="text.secondary" display="block">
                        Total Amount
                      </Typography>
                      <Typography variant="h6" color="primary" fontWeight="bold">
                        ₹{order.total}
                      </Typography>
                    </Box>

                    <Box>
                      <Typography variant="caption" color="text.secondary" display="block">
                        Order ID
                      </Typography>
                      <Typography 
                        variant="body2" 
                        sx={{ fontFamily: 'monospace', fontSize: '0.75rem' }}
                      >
                        {order.orderId}
                      </Typography>
                    </Box>

                    <Chip 
                      icon={<CheckCircle />}
                      label="Order Placed" 
                      color="success" 
                      size="small"
                      sx={{ alignSelf: 'center' }}
                    />
                  </Box>

                  <Divider sx={{ my: 2 }} />

                  {/* Order Items */}
                  {hasItems ? (
                    <Box>
                      <Typography variant="subtitle2" fontWeight="medium" sx={{ mb: 2 }}>
                        Items in this order:
                      </Typography>
                      
                      {itemsArray.map((item, index) => {
                        const coverUrl = bookCoverService.getBookCover(item);
                        const fallbackGradient = getGenreGradient('General');
                        
                        return (
                          <Box 
                            key={index}
                            sx={{ 
                              display: 'flex', 
                              gap: 2, 
                              mb: 2,
                              pb: 2,
                              borderBottom: index < itemsArray.length - 1 ? '1px solid #f0f0f0' : 'none'
                            }}
                          >
                            {/* Book Cover - Small */}
                            <Box sx={{
                              width: 60,
                              height: 80,
                              minWidth: 60,
                              backgroundColor: '#f5f5f5',
                              border: '1px solid #e7e7e7',
                              borderRadius: '4px',
                              display: 'flex',
                              alignItems: 'center',
                              justifyContent: 'center',
                              overflow: 'hidden',
                              padding: 1
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
                                <Box sx={{
                                  width: '100%',
                                  height: '100%',
                                  display: 'flex',
                                  alignItems: 'center',
                                  justifyContent: 'center',
                                  background: fallbackGradient,
                                  borderRadius: '2px'
                                }}>
                                  <Typography 
                                    variant="caption" 
                                    sx={{ 
                                      fontSize: '0.6rem', 
                                      textAlign: 'center',
                                      color: 'white',
                                      fontWeight: 700,
                                      textShadow: '0 1px 3px rgba(0,0,0,0.3)',
                                      overflow: 'hidden',
                                      display: '-webkit-box',
                                      WebkitLineClamp: 3,
                                      WebkitBoxOrient: 'vertical',
                                      px: 0.5
                                    }}
                                  >
                                    {item.title}
                                  </Typography>
                                </Box>
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
                                Quantity: {item.quantity || 1}
                              </Typography>
                            </Box>

                            {/* Price */}
                            <Box sx={{ textAlign: 'right' }}>
                              <Typography variant="body1" fontWeight="medium">
                                ₹{((item.unitPrice || 0) * (item.quantity || 1)).toFixed(2)}
                              </Typography>
                              {item.quantity > 1 && (
                                <Typography variant="caption" color="text.secondary">
                                  ₹{item.unitPrice || 0} each
                                </Typography>
                              )}
                            </Box>
                          </Box>
                        );
                      })}
                    </Box>
                  ) : (
                    <Box sx={{ 
                      textAlign: 'center', 
                      py: 2,
                      backgroundColor: '#f9f9f9',
                      borderRadius: '4px'
                    }}>
                      <Typography variant="body2" color="text.secondary">
                        Order details not available
                      </Typography>
                    </Box>
                  )}
                </CardContent>
              </Card>
            );
          })}
        </Box>
      )}
    </Container>
  );
}