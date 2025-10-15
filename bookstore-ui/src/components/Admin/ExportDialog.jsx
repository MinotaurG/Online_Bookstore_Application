import { useState } from 'react';
import {
  Dialog, DialogTitle, DialogContent, DialogActions,
  Button, FormControl, InputLabel, Select, MenuItem,
  TextField, Box, RadioGroup, FormControlLabel, Radio
} from '@mui/material';
import { Download } from '@mui/icons-material';
import { api } from '../../api';
import { toast } from 'react-toastify';

export default function ExportDialog({ open, onClose }) {
  const [format, setFormat] = useState('json');
  const [filters, setFilters] = useState({
    genre: '',
    author: '',
    minStock: '',
    maxStock: ''
  });
  const [loading, setLoading] = useState(false);

  const handleExport = async () => {
    setLoading(true);
    
    try {
      // Clean filters (remove empty values)
      const cleanFilters = {};
      Object.keys(filters).forEach(key => {
        if (filters[key]) cleanFilters[key] = filters[key];
      });

      // Get blob based on format
      const blob = format === 'json' 
        ? await api.exportBooksJSON(cleanFilters)
        : await api.exportBooksCSV(cleanFilters);

      // Download file
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `catalog-${Date.now()}.${format}`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      window.URL.revokeObjectURL(url);

      toast.success(`Catalog exported as ${format.toUpperCase()}`);
      onClose();
    } catch (err) {
      toast.error('Export failed: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>Export Catalog</DialogTitle>
      
      <DialogContent>
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
          <FormControl component="fieldset">
            <RadioGroup value={format} onChange={(e) => setFormat(e.target.value)} row>
              <FormControlLabel value="json" control={<Radio />} label="JSON" />
              <FormControlLabel value="csv" control={<Radio />} label="CSV" />
            </RadioGroup>
          </FormControl>

          <TextField
            label="Genre Filter"
            value={filters.genre}
            onChange={(e) => setFilters({ ...filters, genre: e.target.value })}
            placeholder="e.g., Fantasy, Fiction"
            helperText="Leave empty for all genres"
          />

          <TextField
            label="Author Filter"
            value={filters.author}
            onChange={(e) => setFilters({ ...filters, author: e.target.value })}
            placeholder="e.g., Tolkien"
            helperText="Partial match supported"
          />

          <Box sx={{ display: 'flex', gap: 2 }}>
            <TextField
              label="Min Stock"
              type="number"
              value={filters.minStock}
              onChange={(e) => setFilters({ ...filters, minStock: e.target.value })}
              placeholder="0"
              fullWidth
            />
            <TextField
              label="Max Stock"
              type="number"
              value={filters.maxStock}
              onChange={(e) => setFilters({ ...filters, maxStock: e.target.value })}
              placeholder="100"
              fullWidth
            />
          </Box>
        </Box>
      </DialogContent>

      <DialogActions>
        <Button onClick={onClose} disabled={loading}>
          Cancel
        </Button>
        <Button
          variant="contained"
          onClick={handleExport}
          disabled={loading}
          startIcon={<Download />}
        >
          {loading ? 'Exporting...' : 'Export'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}