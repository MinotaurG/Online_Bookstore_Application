import { createTheme } from '@mui/material/styles';

// This function creates a theme based on mode (light or dark)
export const getTheme = (mode) => createTheme({
palette: {
mode: mode, // 'light' or 'dark'

// Colors for light mode
...(mode === 'light' && {
  primary: {
    main: '#2563eb',      // Blue
    light: '#60a5fa',     // Light blue
    dark: '#1e40af',      // Dark blue
  },
  secondary: {
    main: '#7c3aed',      // Purple
    light: '#a78bfa',     // Light purple
    dark: '#5b21b6',      // Dark purple
  },
  background: {
    default: '#f8fafc',   // Very light gray
    paper: '#ffffff',     // White
  },
  text: {
    primary: '#1e293b',   // Dark gray for text
    secondary: '#64748b', // Medium gray for secondary text
  },
}),

// Colors for dark mode
...(mode === 'dark' && {
  primary: {
    main: '#60a5fa',      // Lighter blue for dark mode
    light: '#93c5fd',
    dark: '#3b82f6',
  },
  secondary: {
    main: '#a78bfa',      // Lighter purple for dark mode
    light: '#c4b5fd',
    dark: '#8b5cf6',
  },
  background: {
    default: '#0f172a',   // Very dark blue
    paper: '#1e293b',     // Dark blue-gray
  },
  text: {
    primary: '#f1f5f9',   // Almost white
    secondary: '#cbd5e1', // Light gray
  },
}),

// These stay the same in both modes
success: {
  main: '#10b981',
},
error: {
  main: '#ef4444',
},
warning: {
  main: '#f59e0b',
},

},

// Font settings
typography: {
fontFamily: '"Inter", "Roboto", "Helvetica", "Arial", sans-serif',
h1: {
fontSize: '2.5rem',
fontWeight: 700,
},
h2: {
fontSize: '2rem',
fontWeight: 600,
},
h3: {
fontSize: '1.5rem',
fontWeight: 600,
},
},

// Border radius for cards, buttons, etc.
shape: {
borderRadius: 12,
},

// Custom styling for Material-UI components
components: {
MuiButton: {
styleOverrides: {
root: {
textTransform: 'none', // Don't make button text all UPPERCASE
fontWeight: 600,
borderRadius: 8,
},
},
},
MuiCard: {
styleOverrides: {
root: {
boxShadow: mode === 'light'
? '0 1px 3px 0 rgb(0 0 0 / 0.1), 0 1px 2px -1px rgb(0 0 0 / 0.1)'
: '0 4px 6px -1px rgb(0 0 0 / 0.3), 0 2px 4px -2px rgb(0 0 0 / 0.3)',
transition: 'transform 0.2s, box-shadow 0.2s',
'&:hover': {
transform: 'translateY(-4px)',
boxShadow: mode === 'light'
? '0 10px 15px -3px rgb(0 0 0 / 0.1), 0 4px 6px -4px rgb(0 0 0 / 0.1)'
: '0 20px 25px -5px rgb(0 0 0 / 0.3), 0 8px 10px -6px rgb(0 0 0 / 0.3)',
},
},
},
},
},
});
