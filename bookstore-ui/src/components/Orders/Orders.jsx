import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  Container, Paper, Typography, Box, Card, CardContent, Chip, 
  Divider, Alert, CircularProgress, Button
} from '@mui/material';
import { Receipt, ShoppingBag } from '@mui/icons-material';
import { api } from '../../api';

export default function Orders() {
  const nav = useNavigate();
  const [orders, setOrders] = useState([]);
  const [err, setErr] = useState('');
  const [needsAuth, setNeedsAuth] = useState(false);
  const [loading, setLoading] = useState(true);
  
  const load = async () => {
    try { 
      setLoading(true);
      setOrders(await api.myOrders());
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
  
  if (needsAuth) {
    return (
      <Container maxWidth="md" sx={{ py: 8, textAlign: 'center' }}>
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
      <Container maxWidth="md" sx={{ py: 8, textAlign: 'center' }}>
        <CircularProgress size={60} />
        <Typography sx={{ mt: 2 }} color="text.secondary">Loading orders...</Typography>
      </Container>
    );
  }
  
  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" gutterBottom fontWeight="bold">📦 My Orders</Typography>
        <Typography variant="body1" color="text.secondary">
          {orders.length === 0 ? 'No orders yet' : `${orders.length} order(s)`}
        </Typography>
      </Box>

      {err && <Alert severity="error" sx={{ mb: 3 }}>{err}</Alert>}

      {orders.length === 0 ? (
        <Paper sx={{ p: 6, textAlign: 'center' }}>
          <ShoppingBag sx={{ fontSize: 80, color: 'text.secondary', mb: 2 }} />
          <Typography variant="h6" gutterBottom>No Orders Yet</Typography>
          <Typography color="text.secondary" paragraph>
            Start shopping to see your orders here
          </Typography>
          <Button variant="contained" href="/">Browse Catalog</Button>
        </Paper>
      ) : (
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
          {orders.map((order) => (
            <Card key={order.orderId} elevation={2}>
              <CardContent>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                  <Box>
                    <Typography variant="overline" color="text.secondary">Order ID</Typography>
                    <Typography variant="h6" fontWeight="medium">{order.orderId}</Typography>
                  </Box>
                  <Chip label="Completed" color="success" size="small" />
                </Box>
                <Divider sx={{ my: 2 }} />
                <Box sx={{ display: 'flex', justifyContent: 'space-between', pt: 2, borderTop: '1px solid', borderColor: 'divider' }}>
                  <Typography variant="subtitle1" fontWeight="medium">Total Amount</Typography>
                  <Typography variant="h5" color="primary" fontWeight="bold">₹{order.total}</Typography>
                </Box>
              </CardContent>
            </Card>
          ))}
        </Box>
      )}
    </Container>
  );
}