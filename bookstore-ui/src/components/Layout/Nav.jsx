import { Link } from 'react-router-dom'; 
import { AppBar, Toolbar, Button, Box, Badge, Typography, Container, useTheme } from '@mui/material'; 
import { ShoppingCart, History, Recommend, Receipt, AdminPanelSettings, Login, PersonAdd } from '@mui/icons-material'; 
import DarkModeToggle from './DarkModeToggle';

export default function Nav({ user, onLogout, cartCount = 0, darkMode, toggleDarkMode }) {
const theme = useTheme();

return (
<AppBar
position="sticky"
elevation={1}
sx={{
backgroundColor: theme.palette.background.paper,
color: theme.palette.text.primary,
borderBottom: `1px solid ${theme.palette.divider}`
}}
>
<Container maxWidth="xl">
<Toolbar sx={{ justifyContent: 'space-between' }}>
{/* Left side - Logo */}
<Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
<Typography
variant="h6"
component={Link}
to="/"
sx={{
textDecoration: 'none',
color: 'primary.main',
fontWeight: 700,
display: 'flex',
alignItems: 'center',
gap: 1
}}
>
📚 BookStore
</Typography>
</Box>

      {/* Center - Navigation Links */}
      <Box sx={{ display: 'flex', gap: 1 }}>
        <Button 
          component={Link} 
          to="/" 
          color="inherit"
          startIcon={<span>📖</span>}
        >
          Catalog
        </Button>
        
        <Button 
          component={Link} 
          to="/cart" 
          color="inherit"
          startIcon={
            <Badge badgeContent={cartCount} color="error">
              <ShoppingCart />
            </Badge>
          }
        >
          Cart
        </Button>
        
        <Button 
          component={Link} 
          to="/orders" 
          color="inherit"
          startIcon={<Receipt />}
        >
          Orders
        </Button>
        
        <Button 
          component={Link} 
          to="/history" 
          color="inherit"
          startIcon={<History />}
        >
          History
        </Button>
        
        <Button 
          component={Link} 
          to="/recs" 
          color="inherit"
          startIcon={<Recommend />}
        >
          Recommendations
        </Button>
      </Box>

      {/* Right side - User menu + Dark mode toggle */}
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
        {/* Dark Mode Toggle */}
        <DarkModeToggle darkMode={darkMode} toggleDarkMode={toggleDarkMode} />
        
        {/* User info */}
        <Typography variant="body2" sx={{ mr: 1 }}>
          {user ? `Hi, ${user.username}` : 'Welcome'}
        </Typography>
        
        {/* Admin button */}
        {user?.isAdmin && (
          <Button 
            component={Link} 
            to="/admin" 
            variant="outlined"
            size="small"
            startIcon={<AdminPanelSettings />}
          >
            Admin
          </Button>
        )}
        
        {/* Login/Logout buttons */}
        {user ? (
          <Button 
            onClick={onLogout} 
            variant="contained"
            size="small"
          >
            Logout
          </Button>
        ) : (
          <>
            <Button 
              component={Link} 
              to="/register"
              variant="outlined"
              size="small"
              startIcon={<PersonAdd />}
            >
              Register
            </Button>
            <Button 
              component={Link} 
              to="/login"
              variant="contained"
              size="small"
              startIcon={<Login />}
            >
              Login
            </Button>
          </>
        )}
      </Box>
    </Toolbar>
  </Container>
</AppBar>

);
}