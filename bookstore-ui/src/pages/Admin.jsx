import { useState, useEffect } from 'react';
import { 
  Container,
  Paper,
  Typography,
  TextField,
  Button,
  Box,
  Grid,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  IconButton,
  Chip,
  Alert,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Divider
} from '@mui/material';
import { Edit, Delete, Add, Save, Cancel } from '@mui/icons-material';
import { api } from '../api';
import { toast } from 'react-toastify';

export default function Admin({ onBooksUpdate }) {
  const [books, setBooks] = useState([]);
  const [editingBook, setEditingBook] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [loading, setLoading] = useState(false);
  
  const [formData, setFormData] = useState({
    id: '',
    title: '',
    author: '',
    genre: '',
    price: '',
    stockQuantity: ''
  });

  // Load books
  useEffect(() => {
    loadBooks();
  }, []);

  const loadBooks = async () => {
    try {
      const data = await api.listBooks();
      setBooks(data);
    } catch (e) {
      toast.error('Failed to load books');
    }
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleEdit = (book) => {
    setFormData({
      id: book.id,
      title: book.title,
      author: book.author || '',
      genre: book.genre || '',
      price: book.price?.toString() || '',
      stockQuantity: book.stockQuantity?.toString() || ''
    });
    setEditingBook(book);
    setShowForm(true);
  };

  const handleNew = () => {
    setFormData({
      id: '',
      title: '',
      author: '',
      genre: '',
      price: '',
      stockQuantity: ''
    });
    setEditingBook(null);
    setShowForm(true);
  };

  const handleCancel = () => {
    setShowForm(false);
    setEditingBook(null);
    setFormData({
      id: '',
      title: '',
      author: '',
      genre: '',
      price: '',
      stockQuantity: ''
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const res = await fetch('/api/books', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({
          id: formData.id || undefined,
          title: formData.title,
          author: formData.author,
          genre: formData.genre,
          price: parseFloat(formData.price),
          stockQuantity: parseInt(formData.stockQuantity)
        })
      });

      if (!res.ok) throw new Error(await res.text());

      toast.success(editingBook ? 'Book updated!' : 'Book added!');
      handleCancel();
      loadBooks();
      if (onBooksUpdate) onBooksUpdate();
    } catch (e) {
      toast.error(`Error: ${e.message}`);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (book) => {
    if (!confirm(`Delete "${book.title}"?`)) return;

    try {
      await api.deleteBookById(book.id);
      toast.success('Book deleted!');
      loadBooks();
      if (onBooksUpdate) onBooksUpdate();
    } catch (e) {
      toast.error('Failed to delete book');
    }
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      {/* Header */}
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
        <Typography variant="h4" fontWeight="bold">
          📚 Book Management
        </Typography>
        <Button
          variant="contained"
          startIcon={<Add />}
          onClick={handleNew}
          size="large"
        >
          Add New Book
        </Button>
      </Box>

      {/* Books Table */}
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow sx={{ bgcolor: 'primary.main' }}>
              <TableCell sx={{ color: 'primary.contrastText', fontWeight: 'bold' }}>Title</TableCell>
              <TableCell sx={{ color: 'primary.contrastText', fontWeight: 'bold' }}>Author</TableCell>
              <TableCell sx={{ color: 'primary.contrastText', fontWeight: 'bold' }}>Genre</TableCell>
              <TableCell sx={{ color: 'primary.contrastText', fontWeight: 'bold' }}>Price</TableCell>
              <TableCell sx={{ color: 'primary.contrastText', fontWeight: 'bold' }}>Stock</TableCell>
              <TableCell sx={{ color: 'primary.contrastText', fontWeight: 'bold' }} align="right">Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {books.map((book) => (
              <TableRow 
                key={book.id}
                sx={{ '&:hover': { bgcolor: 'action.hover' } }}
              >
                <TableCell>
                  <Typography variant="subtitle2" fontWeight="medium">
                    {book.title}
                  </Typography>
                </TableCell>
                <TableCell>{book.author}</TableCell>
                <TableCell>
                  <Chip label={book.genre || 'General'} size="small" />
                </TableCell>
                <TableCell>₹{book.price}</TableCell>
                <TableCell>
                  <Chip 
                    label={book.stockQuantity}
                    color={book.stockQuantity < 5 ? 'error' : 'success'}
                    size="small"
                  />
                </TableCell>
                <TableCell align="right">
                  <IconButton 
                    color="primary" 
                    onClick={() => handleEdit(book)}
                    size="small"
                  >
                    <Edit />
                  </IconButton>
                  <IconButton 
                    color="error" 
                    onClick={() => handleDelete(book)}
                    size="small"
                  >
                    <Delete />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      {/* Add/Edit Dialog */}
      <Dialog 
        open={showForm} 
        onClose={handleCancel}
        maxWidth="sm"
        fullWidth
      >
        <form onSubmit={handleSubmit}>
          <DialogTitle>
            {editingBook ? '✏️ Edit Book' : '➕ Add New Book'}
          </DialogTitle>
          
          <DialogContent>
            <Grid container spacing={2} sx={{ mt: 1 }}>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Title"
                  name="title"
                  value={formData.title}
                  onChange={handleChange}
                  required
                  autoFocus
                />
              </Grid>

              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Author"
                  name="author"
                  value={formData.author}
                  onChange={handleChange}
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Genre"
                  name="genre"
                  value={formData.genre}
                  onChange={handleChange}
                  placeholder="e.g., Programming, Fiction"
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Price (₹)"
                  name="price"
                  type="number"
                  value={formData.price}
                  onChange={handleChange}
                  inputProps={{ step: "0.01", min: "0" }}
                  required
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Stock Quantity"
                  name="stockQuantity"
                  type="number"
                  value={formData.stockQuantity}
                  onChange={handleChange}
                  inputProps={{ min: "0" }}
                  required
                />
              </Grid>

              {editingBook && (
                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label="Book ID"
                    name="id"
                    value={formData.id}
                    disabled
                    helperText="Cannot be changed"
                  />
                </Grid>
              )}
            </Grid>
          </DialogContent>

          <DialogActions sx={{ p: 2 }}>
            <Button 
              onClick={handleCancel}
              startIcon={<Cancel />}
            >
              Cancel
            </Button>
            <Button 
              type="submit"
              variant="contained"
              disabled={loading}
              startIcon={<Save />}
            >
              {loading ? 'Saving...' : (editingBook ? 'Update' : 'Add Book')}
            </Button>
          </DialogActions>
        </form>
      </Dialog>

      {/* Quick Link to Bulk Seed */}
      <Box sx={{ mt: 4 }}>
        <Alert severity="info">
          Need to add multiple books at once? 
          <Button href="/admin/seed" sx={{ ml: 2 }}>
            Go to Bulk Upload
          </Button>
        </Alert>
      </Box>
    </Container>
  );
}