import { useState } from 'react';
import { useNavigate, Link as RouterLink } from 'react-router-dom';
import { 
  Container,
  Paper,
  TextField,
  Button,
  Typography,
  Box,
  Alert,
  InputAdornment,
  IconButton,
  Link
} from '@mui/material';
import { 
  Visibility, 
  VisibilityOff, 
  PersonAdd,
  Login
} from '@mui/icons-material';
import { api } from '../api';

export default function Register() {
  const nav = useNavigate();
  const [form, setForm] = useState({ username: '', email: '', password: '' });
  const [showPassword, setShowPassword] = useState(false);
  const [err, setErr] = useState('');
  const [ok, setOk] = useState('');
  const [loading, setLoading] = useState(false);

  const onChange = e => setForm({ ...form, [e.target.name]: e.target.value });

  const submit = async (e) => {
    e.preventDefault();
    setErr(''); 
    setOk('');
    setLoading(true);
    
    if (!form.username || !form.password) {
      setErr('Username and password are required');
      setLoading(false);
      return;
    }
    
    try {
      await api.register(form.username, form.email, form.password);
      setOk('Account created successfully! Redirecting to login...');
      setTimeout(() => nav('/login'), 1500);
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
            bgcolor: 'secondary.main',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            mb: 2
          }}
        >
          <Typography variant="h3">📖</Typography>
        </Box>

        {/* Title */}
        <Typography variant="h4" fontWeight="bold" gutterBottom>
          Create Account
        </Typography>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
          Join our bookstore community today
        </Typography>

        {/* Messages */}
        {err && (
          <Alert severity="error" sx={{ width: '100%', mb: 2 }}>
            {err}
          </Alert>
        )}
        {ok && (
          <Alert severity="success" sx={{ width: '100%', mb: 2 }}>
            {ok}
          </Alert>
        )}

        {/* Registration Form */}
        <Box component="form" onSubmit={submit} sx={{ width: '100%' }}>
          <TextField
            fullWidth
            label="Username"
            name="username"
            placeholder="Choose a username"
            value={form.username}
            onChange={onChange}
            required
            autoFocus
            sx={{ mb: 2 }}
            helperText="Minimum 3 characters"
          />

          <TextField
            fullWidth
            label="Email"
            name="email"
            type="email"
            placeholder="your.email@example.com"
            value={form.email}
            onChange={onChange}
            sx={{ mb: 2 }}
            helperText="Optional but recommended"
          />

          <TextField
            fullWidth
            label="Password"
            name="password"
            type={showPassword ? 'text' : 'password'}
            placeholder="Create a strong password"
            value={form.password}
            onChange={onChange}
            required
            sx={{ mb: 3 }}
            helperText="Minimum 6 characters"
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
            startIcon={<PersonAdd />}
            sx={{ py: 1.5, mb: 2 }}
          >
            {loading ? 'Creating Account...' : 'Create Account'}
          </Button>

          <Box sx={{ textAlign: 'center' }}>
            <Typography variant="body2" color="text.secondary">
              Already have an account?{' '}
              <Link component={RouterLink} to="/login" underline="hover">
                Sign in here
              </Link>
            </Typography>
          </Box>
        </Box>
      </Paper>
    </Container>
  );
}