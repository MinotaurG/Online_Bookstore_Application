import { useState } from 'react';
import {
  Dialog, DialogTitle, DialogContent, DialogActions,
  Button, TextField, Box, Typography, FormControlLabel, Checkbox
} from '@mui/material';
import { toast } from 'react-toastify';

export default function BulkEditDialog({ open, onClose, selectedBooks, onComplete }) {
  const [changes, setChanges] = useState({
    price: '',
    stockQuantity: '',
    genre: '',
    applyPrice: false,
    applyStock: false,
    applyGenre: false
  });

  const handleSubmit = async () => {
    try {
      // Build updates array
      const updates = selectedBooks.map(book => {
        const update = { asin: book.asin };
        
        if (changes.applyPrice && changes.price) {
          update.price = parseFloat(changes.price);
        }
        if (changes.applyStock && changes.stockQuantity) {
          update.stockQuantity = parseInt(changes.stockQuantity);
        }
        if (changes.applyGenre && changes.genre) {
          update.genre = changes.genre;
        }
        
        return update;
      });

      const response = await fetch('/api/books/bulk', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ updates })
      });

      if (!response.ok) throw new Error(await response.text());

      const result = await response.json();
      toast.success(`Updated ${result.updated} books`);
      onComplete();
      onClose();
    } catch (err) {
      toast.error('Bulk update failed: ' + err.message);
    }
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>
        Bulk Edit {selectedBooks.length} Books
      </DialogTitle>
      
      <DialogContent>
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
          <Typography variant="body2" color="text.secondary">
            Select which fields to update for all selected books:
          </Typography>

          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <FormControlLabel
              control={
                <Checkbox
                  checked={changes.applyPrice}
                  onChange={(e) => setChanges({ ...changes, applyPrice: e.target.checked })}
                />
              }
              label="Update Price"
            />
            <TextField
              type="number"
              value={changes.price}
              onChange={(e) => setChanges({ ...changes, price: e.target.value })}
              disabled={!changes.applyPrice}
              placeholder="29.99"
              inputProps={{ step: "0.01", min: "0" }}
              size="small"
              fullWidth
            />
          </Box>

          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <FormControlLabel
              control={
                <Checkbox
                  checked={changes.applyStock}
                  onChange={(e) => setChanges({ ...changes, applyStock: e.target.checked })}
                />
              }
              label="Update Stock"
            />
            <TextField
              type="number"
              value={changes.stockQuantity}
              onChange={(e) => setChanges({ ...changes, stockQuantity: e.target.value })}
              disabled={!changes.applyStock}
              placeholder="100"
              inputProps={{ min: "0" }}
              size="small"
              fullWidth
            />
          </Box>

          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <FormControlLabel
              control={
                <Checkbox
                  checked={changes.applyGenre}
                  onChange={(e) => setChanges({ ...changes, applyGenre: e.target.checked })}
                />
              }
              label="Update Genre"
            />
            <TextField
              value={changes.genre}
              onChange={(e) => setChanges({ ...changes, genre: e.target.value })}
              disabled={!changes.applyGenre}
              placeholder="Fantasy"
              size="small"
              fullWidth
            />
          </Box>
        </Box>
      </DialogContent>

      <DialogActions>
        <Button onClick={onClose}>Cancel</Button>
        <Button
          variant="contained"
          onClick={handleSubmit}
          disabled={!changes.applyPrice && !changes.applyStock && !changes.applyGenre}
        >
          Update {selectedBooks.length} Books
        </Button>
      </DialogActions>
    </Dialog>
  );
}