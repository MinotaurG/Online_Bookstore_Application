import { useState } from 'react'; import { useNavigate, Link as RouterLink } from 'react-router-dom'; import { Container, Paper, TextField, Button, Typography, Box, Alert, InputAdornment, IconButton, Link, Divider } from '@mui/material'; import { Visibility, VisibilityOff, Login as LoginIcon, PersonAdd } from '@mui/icons-material'; import { api } from '../../api';

export default function Login({ setUser }) {
const nav = useNavigate();
const [username, setUsername] = useState('');
const [password, setPassword] = useState('');
const [showPassword, setShowPassword] = useState(false);
const [err, setErr] = useState('');
const [loading, setLoading] = useState(false);

const submit = async (e) => {
e.preventDefault();
setErr('');
setLoading(true);

try {
  await api.login(username, password);
  const me = await api.me();
  setUser(me);
  nav('/');
} catch (e) { 
  setErr(e.message);
} finally {
  setLoading(false);
}

};

return (
<Container maxWidth="sm" sx={{ py: 8 }}>
<Paper
elevation={3}
sx={{
p: 4,
display: 'flex',
flexDirection: 'column',
alignItems: 'center'
}}
>
{/* Logo/Icon */}
<Box
sx={{
width: 80,
height: 80,
borderRadius: '50%',
bgcolor: 'primary.main',
display: 'flex',
alignItems: 'center',
justifyContent: 'center',
mb: 2
}}
>
<Typography variant="h3">📚</Typography>
</Box>

    {/* Title */}
    <Typography variant="h4" fontWeight="bold" gutterBottom>
      Welcome Back
    </Typography>
    <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
      Sign in to your bookstore account
    </Typography>

    {/* Error Message */}
    {err && (
      <Alert severity="error" sx={{ width: '100%', mb: 2 }}>
        {err}
      </Alert>
    )}

    {/* Login Form */}
    <Box component="form" onSubmit={submit} sx={{ width: '100%' }}>
      <TextField
        fullWidth
        label="Username"
        placeholder="Enter your username"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
        required
        autoFocus
        sx={{ mb: 2 }}
      />

      <TextField
        fullWidth
        label="Password"
        type={showPassword ? 'text' : 'password'}
        placeholder="Enter your password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        required
        sx={{ mb: 3 }}
        InputProps={{
          endAdornment: (
            <InputAdornment position="end">
              <IconButton
                onClick={() => setShowPassword(!showPassword)}
                edge="end"
              >
                {showPassword ? <VisibilityOff /> : <Visibility />}
              </IconButton>
            </InputAdornment>
          ),
        }}
      />

      <Button
        type="submit"
        fullWidth
        variant="contained"
        size="large"
        disabled={loading}
        startIcon={<LoginIcon />}
        sx={{ py: 1.5, mb: 2 }}
      >
        {loading ? 'Signing in...' : 'Sign In'}
      </Button>

      <Divider sx={{ my: 2 }}>OR</Divider>

      <Button
        fullWidth
        variant="outlined"
        size="large"
        component={RouterLink}
        to="/register"
        startIcon={<PersonAdd />}
        sx={{ py: 1.5 }}
      >
        Create New Account
      </Button>
    </Box>

    {/* Footer */}
    <Box sx={{ mt: 3, textAlign: 'center' }}>
      <Typography variant="body2" color="text.secondary">
        Demo credentials:
      </Typography>
      <Typography variant="caption" color="text.secondary">
        Admin: admin / admin123
      </Typography>
    </Box>
  </Paper>
</Container>

);
}