import { IconButton, Tooltip } from '@mui/material';
import { Brightness4, Brightness7 } from '@mui/icons-material';

/**
 * A button that toggles between light and dark mode
 * 
 * Props:
 * - darkMode: boolean (true if dark mode is on)
 * - toggleDarkMode: function (called when button is clicked)
 */
export default function DarkModeToggle({ darkMode, toggleDarkMode }) {
  return (
    <Tooltip title={darkMode ? "Switch to Light Mode" : "Switch to Dark Mode"}>
      <IconButton 
        onClick={toggleDarkMode} 
        color="inherit"
        sx={{ ml: 1 }}
      >
        {/* Show moon icon in light mode, sun icon in dark mode */}
        {darkMode ? <Brightness7 /> : <Brightness4 />}
      </IconButton>
    </Tooltip>
  );
}