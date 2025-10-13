import { useState } from 'react'; import { Paper, Typography, Button, Divider, Box, List, ListItem, ListItemText, Alert, CircularProgress, Chip } from '@mui/material'; import { ShoppingBag, CheckCircle } from '@mui/icons-material'; import { motion } from 'framer-motion'; import { api } from '../../api'; import { toast } from 'react-toastify';

export default function Checkout({ cart, preview, clearCart, onCheckoutSuccess }) {
const [loading, setLoading] = useState(false);
const [orderComplete, setOrderComplete] = useState(false);
const [orderId, setOrderId] = useState(null);

const handleCheckout = async () => {
setLoading(true);

try {
  const order = await api.checkout(cart);
  
  clearCart();
  localStorage.removeItem('cart');
  
  if (onCheckoutSuccess) {
    await onCheckoutSuccess();
  }
  
  setOrderId(order.orderId);
  setOrderComplete(true);
  
  toast.success('Order placed successfully!', {
    position: 'top-center',
    autoClose: 5000
  });
} catch (e) {
  toast.error(e.message, { position: 'top-center' });
} finally {
  setLoading(false);
}

};

if (orderComplete) {
return (
<Paper sx={{ p: 4 }}>
<motion.div
initial={{ scale: 0 }}
animate={{ scale: 1 }}
transition={{ type: 'spring', duration: 0.5 }}
>
<Box sx={{ textAlign: 'center' }}>
<CheckCircle sx={{ fontSize: 80, color: 'success.main', mb: 2 }} />
<Typography variant="h4" gutterBottom fontWeight="bold">
Order Confirmed!
</Typography>
<Typography variant="body1" color="text.secondary" sx={{ mb: 3 }}>
Order ID: <Chip label={orderId} color="primary" />
</Typography>
<Button
variant="contained"
href="/"
sx={{ mt: 2 }}
>
Continue Shopping
</Button>
</Box>
</motion.div>
</Paper>
);
}

if (!preview || cart.length === 0) {
return (
<Paper sx={{ p: 4, textAlign: 'center' }}>
<Typography variant="h6" color="text.secondary">
Your cart is empty
</Typography>
</Paper>
);
}

return (
<Paper sx={{ p: 3 }}>
<Typography variant="h5" gutterBottom fontWeight="bold">
Checkout Summary
</Typography>

  <Divider sx={{ my: 2 }} />

  {/* Total Amount */}
  <Box sx={{ 
    my: 3, 
    p: 2, 
    bgcolor: 'primary.main',
    color: 'primary.contrastText',
    borderRadius: 2
  }}>
    <Typography variant="body2" sx={{ opacity: 0.9 }}>
      Total Amount
    </Typography>
    <Typography variant="h3" fontWeight="bold">
      ₹{preview.total || 0}
    </Typography>
  </Box>

  <Typography variant="h6" gutterBottom sx={{ mt: 3 }}>
    Order Items
  </Typography>
  
  <List sx={{ mb: 3 }}>
    {preview.items?.map((item) => {
      // Calculate total if not provided by backend
      const itemTotal = item.totalPrice || (item.unitPrice * item.quantity);
      
      return (
        <ListItem
          key={item.bookId}
          sx={{
            bgcolor: 'background.default',
            borderRadius: 2,
            mb: 1,
            border: '1px solid',
            borderColor: 'divider',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'stretch'
          }}
        >
          <Box sx={{ width: '100%' }}>
            <Typography variant="subtitle1" fontWeight="medium" gutterBottom>
              {item.title}
            </Typography>
            
            <Box sx={{ 
              display: 'flex', 
              justifyContent: 'space-between',
              alignItems: 'center',
              mt: 1
            }}>
              <Typography variant="body2" color="text.secondary">
                {item.quantity} × ₹{item.unitPrice}
              </Typography>
              <Typography variant="h6" color="primary" fontWeight="bold">
                ₹{itemTotal.toFixed(2)}
              </Typography>
            </Box>
          </Box>
        </ListItem>
      );
    })}
  </List>

  <Button
    fullWidth
    variant="contained"
    size="large"
    startIcon={loading ? <CircularProgress size={20} color="inherit" /> : <ShoppingBag />}
    onClick={handleCheckout}
    disabled={loading || !cart.length}
    sx={{ py: 1.5 }}
  >
    {loading ? 'Processing...' : 'Place Order'}
  </Button>
</Paper>

);
}