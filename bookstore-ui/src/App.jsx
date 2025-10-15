import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { useEffect, useMemo, useState, useCallback } from 'react';
import { ThemeProvider, CssBaseline, Box, CircularProgress } from '@mui/material';
import { ToastContainer } from 'react-toastify'; 
import 'react-toastify/dist/ReactToastify.css';
import { getTheme } from './theme';
import { api } from './api';

// Layout
import Nav from './components/Layout/Nav';

// Auth
import Login from './components/Auth/Login';
import ProtectedRoute from './components/Auth/ProtectedRoute';

// Books
import Catalog from './components/Books/Catalog';

// Cart
import Cart from './components/Cart/Cart';
import Checkout from './components/Cart/Checkout';

// Orders & History
import Orders from './components/Orders/Orders';
import History from './components/History/History';

// Recommendations
import Recommendations from './components/Recommendations/Recommendations';

// Pages
import Register from './pages/Register';
import Admin from './pages/Admin';
import AdminSeed from './pages/AdminSeed';

export default function App() {
  // User state
  const [user, setUser] = useState(null);
  const [userLoading, setUserLoading] = useState(true);

  // Cart state
  const [cart, setCart] = useState([]);
  const [preview, setPreview] = useState(null);

  // Books state
  const [allBooks, setAllBooks] = useState([]);
  const [loading, setLoading] = useState(false);

  // Dark mode state - check localStorage first, default to light mode
  const [darkMode, setDarkMode] = useState(() => {
    const saved = localStorage.getItem('darkMode');
    return saved ? JSON.parse(saved) : false;
  });

  // Create theme based on dark mode state
  const theme = useMemo(() => getTheme(darkMode ? 'dark' : 'light'), [darkMode]);

  // Toggle dark mode function
  const toggleDarkMode = () => {
    setDarkMode(prev => {
      const newMode = !prev;
      localStorage.setItem('darkMode', JSON.stringify(newMode));
      return newMode;
    });
  };

  // Load books from API
  const loadBooks = useCallback(async () => {
    setLoading(true);
    try {
      const fresh = await api.listBooks();
      setAllBooks(fresh);
      console.log('Books refreshed:', fresh.length, 'books loaded');
    } catch (e) {
      console.error('Failed to load books', e);
    } finally {
      setLoading(false);
    }
  }, []);

  // Create a map of book IDs to stock quantities
  const stockMap = useMemo(() => {
    const m = new Map();
    (allBooks || []).forEach(b => m.set(b.id, Math.max(0, b.stockQuantity ?? 0)));
    return m;
  }, [allBooks]);

  // Add item to cart
  const addToCart = useCallback((bookId, inc = 1) => {
    const stock = stockMap.get(bookId) ?? 0;

    if (stock <= 0) {
      alert('Sorry, this item is out of stock!');
      return;
    }

    setCart(prev => {
      const found = prev.find(i => i.bookId === bookId);
      
      if (found) {
        const newQty = Math.min(found.quantity + inc, stock);
        if (newQty === found.quantity) {
          alert(`Cannot add more. Only ${stock} items available in stock.`);
        }
        return prev.map(i => i.bookId === bookId ? {...i, quantity: newQty} : i);
      }
      
      return [...prev, { bookId, quantity: Math.min(inc, stock) }];
    });
  }, [stockMap]);

  // Clear cart
  const clearCart = useCallback(() => {
    setCart([]);
    setPreview(null);
  }, []);

  // Logout function
  const onLogout = async () => {
    try {
      await api.logout();
      setUser(null);
      setCart([]);
      setPreview(null);
      localStorage.removeItem('cart');
      window.location.href = '/login';
    } catch (e) {
      console.error('Logout failed', e);
    }
  };

  // Load user and books when app starts
  useEffect(() => {
    (async () => {
      try {
        setUserLoading(true);
        const me = await api.me();
        setUser(me);
        console.log('✅ User loaded:', me);
      } catch {
        console.log('ℹ️ No user logged in');
        setUser(null);
      } finally {
        setUserLoading(false);
      }
    })();

    loadBooks();
  }, [loadBooks]);

  // Save cart to localStorage whenever it changes
  useEffect(() => {
    if (cart.length > 0) {
      try {
        localStorage.setItem('cart', JSON.stringify(cart));
      } catch {}
    }
  }, [cart]);

  // Load cart from localStorage when app starts
  useEffect(() => {
    try {
      const saved = localStorage.getItem('cart');
      if (saved) {
        setCart(JSON.parse(saved));
      }
    } catch {}
  }, []);

  // Update cart preview whenever cart changes
  useEffect(() => {
    if (cart.length > 0) {
      (async () => {
        try {
          const p = await api.previewCart(cart);
          setPreview(p);
        } catch (e) {
          console.error('Failed to preview cart', e);
        }
      })();
    } else {
      setPreview(null);
    }
  }, [cart]);

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      
      <BrowserRouter>
        <Nav 
          user={user} 
          onLogout={onLogout} 
          cartCount={cart.length}
          darkMode={darkMode}
          toggleDarkMode={toggleDarkMode}
        />
        
        <Box sx={{ minHeight: '100vh', pb: 4 }}>
          {userLoading ? (
            <Box sx={{ 
              display: 'flex', 
              justifyContent: 'center', 
              alignItems: 'center', 
              minHeight: '80vh',
              flexDirection: 'column',
              gap: 2
            }}>
              <CircularProgress size={60} />
              <Box sx={{ color: 'text.secondary' }}>Loading...</Box>
            </Box>
          ) : (
            <Routes>
              <Route 
                path="/" 
                element={
                  <Catalog 
                    addToCart={addToCart} 
                    allBooks={allBooks}
                    loadBooks={loadBooks}
                    loading={loading}
                    user={user} 
                  />
                } 
              />
              
              <Route 
                path="/login" 
                element={<Login setUser={setUser} />} 
              />
              
              <Route 
                path="/register" 
                element={<Register />} 
              />
              
              <Route 
                path="/cart" 
                element={
                  <Box sx={{ 
                    display: 'grid', 
                    gridTemplateColumns: { xs: '1fr', md: '1fr 1fr' }, 
                    gap: 3, 
                    p: 3,
                    maxWidth: '1400px',
                    mx: 'auto'
                  }}>
                    <Cart 
                      cart={cart} 
                      setCart={setCart} 
                      setPreview={setPreview} 
                      stockMap={stockMap}
                      allBooks={allBooks}
                    />
                    <Checkout 
                      cart={cart} 
                      preview={preview} 
                      clearCart={clearCart} 
                      onCheckoutSuccess={loadBooks}
                    />
                  </Box>
                } 
              />
              
              <Route 
                path="/orders" 
                element={<Orders />} 
              />
              
              <Route 
                path="/history" 
                element={<History />} 
              />
              
              <Route 
                path="/recs" 
                element={<Recommendations />} 
              />
              
              <Route 
                path="/admin" 
                element={
                  user?.isAdmin ? (
                    <Admin onBooksUpdate={loadBooks} user={user} />
                  ) : (
                    <Navigate to="/login" replace />
                  )
                } 
              />
              
              <Route 
                path="/admin/seed" 
                element={
                  user?.isAdmin ? (
                    <AdminSeed onSeedComplete={loadBooks} />
                  ) : (
                    <Navigate to="/login" replace />
                  )
                } 
              />
            </Routes>
          )}
        </Box>

        <ToastContainer 
          position="bottom-right"
          autoClose={3000}
          hideProgressBar={false}
          theme={darkMode ? 'dark' : 'light'}
        />
      </BrowserRouter>
    </ThemeProvider>
  );
}