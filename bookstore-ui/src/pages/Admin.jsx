import { useState, useEffect } from 'react';
import { 
  Container, Paper, Typography, TextField, Button, Box, Grid,
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
  IconButton, Chip, Dialog, DialogTitle, DialogContent, DialogActions,
  Checkbox, Tabs, Tab, Alert
} from '@mui/material';
import { 
  Edit, Delete, Add, Save, Cancel, Download, Upload, 
  DeleteSweep, EditNote, SelectAll
} from '@mui/icons-material';
import { api } from '../api';
import { toast } from 'react-toastify';
import FileUpload from '../components/Admin/FileUpload';
import ExportDialog from '../components/Admin/ExportDialog';
import BulkEditDialog from '../components/Admin/BulkEditDialog';

export default function Admin({ onBooksUpdate }) {
  const [books, setBooks] = useState([]);
  const [selectedBooks, setSelectedBooks] = useState([]);
  const [editingBook, setEditingBook] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [loading, setLoading] = useState(false);
  const [tabValue, setTabValue] = useState(0);
  const [showExport, setShowExport] = useState(false);
  const [showBulkEdit, setShowBulkEdit] = useState(false);
  
  const [formData, setFormData] = useState({
    id: '',
    title: '',
    author: '',
    genre: '',
    price: '',
    stockQuantity: '',
    isbn: ''
  });

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

  const handleSelectAll = () => {
    if (selectedBooks.length === books.length) {
      setSelectedBooks([]);
    } else {
      setSelectedBooks(books.map(b => b.asin));
    }
  };

  const handleSelectBook = (asin) => {
    setSelectedBooks(prev =>
      prev.includes(asin)
        ? prev.filter(a => a !== asin)
        : [...prev, asin]
    );
  };

  const handleBulkDelete = async () => {
    if (!confirm(`Delete ${selectedBooks.length} books?`)) return;

    try {
      await api.bulkDeleteBooks(null, selectedBooks);
      toast.success(`Deleted ${selectedBooks.length} books`);
      setSelectedBooks([]);
      loadBooks();
      onBooksUpdate?.();
    } catch (e) {
      toast.error('Bulk delete failed');
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
      stockQuantity: book.stockQuantity?.toString() || '',
      isbn: book.isbn || ''
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
      stockQuantity: '',
      isbn: ''
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
      stockQuantity: '',
      isbn: ''
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
          stockQuantity: parseInt(formData.stockQuantity),
          isbn: formData.isbn || null
        })
      });

      if (!res.ok) throw new Error(await res.text());

      toast.success(editingBook ? 'Book updated!' : 'Book added!');
      handleCancel();
      loadBooks();
      onBooksUpdate?.();
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
      onBooksUpdate?.();
    } catch (e) {
      toast.error('Failed to delete book');
    }
  };

  const getSelectedBooksData = () => {
    return books.filter(b => selectedBooks.includes(b.asin));
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" fontWeight="bold">
          Book Management
        </Typography>
        <Box sx={{ display: 'flex', gap: 1 }}>
          <Button
            variant="outlined"
            startIcon={<Download />}
            onClick={() => setShowExport(true)}
          >
            Export
          </Button>
          <Button
            variant="contained"
            startIcon={<Add />}
            onClick={handleNew}
          >
            Add Book
          </Button>
        </Box>
      </Box>

      <Tabs value={tabValue} onChange={(e, v) => setTabValue(v)} sx={{ mb: 2 }}>
        <Tab label="Manage Books" />
        <Tab label="Import Books" />
      </Tabs>

      {tabValue === 0 && (
        <>
          {selectedBooks.length > 0 && (
            <Paper sx={{ p: 2, mb: 2, bgcolor: 'primary.50' }}>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Typography variant="body1">
                  {selectedBooks.length} book(s) selected
                </Typography>
                <Box sx={{ display: 'flex', gap: 1 }}>
                  <Button
                    variant="outlined"
                    size="small"
                    startIcon={<EditNote />}
                    onClick={() => setShowBulkEdit(true)}
                  >
                    Bulk Edit
                  </Button>
                  <Button
                    variant="outlined"
                    size="small"
                    color="error"
                    startIcon={<DeleteSweep />}
                    onClick={handleBulkDelete}
                  >
                    Delete Selected
                  </Button>
                  <Button
                    variant="text"
                    size="small"
                    onClick={() => setSelectedBooks([])}
                  >
                    Clear Selection
                  </Button>
                </Box>
              </Box>
            </Paper>
          )}

          <TableContainer component={Paper}>
            <Table>
              <TableHead>
                <TableRow sx={{ bgcolor: 'primary.main' }}>
                  <TableCell padding="checkbox">
                    <Checkbox
                      checked={selectedBooks.length === books.length && books.length > 0}
                      indeterminate={selectedBooks.length > 0 && selectedBooks.length < books.length}
                      onChange={handleSelectAll}
                      sx={{ color: 'white', '&.Mui-checked': { color: 'white' } }}
                    />
                  </TableCell>
                  <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>ASIN</TableCell>
                  <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Title</TableCell>
                  <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Author</TableCell>
                  <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Genre</TableCell>
                  <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Price</TableCell>
                  <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Stock</TableCell>
                  <TableCell sx={{ color: 'white', fontWeight: 'bold' }} align="right">Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {books.map((book) => (
                  <TableRow 
                    key={book.id}
                    selected={selectedBooks.includes(book.asin)}
                    sx={{ '&:hover': { bgcolor: 'action.hover' } }}
                  >
                    <TableCell padding="checkbox">
                      <Checkbox
                        checked={selectedBooks.includes(book.asin)}
                        onChange={() => handleSelectBook(book.asin)}
                      />
                    </TableCell>
                    <TableCell>
                      <Chip 
                        label={book.asin || 'N/A'} 
                        size="small" 
                        variant="outlined"
                        sx={{ fontFamily: 'monospace', fontSize: '0.75rem' }}
                      />
                    </TableCell>
                    <TableCell>
                      <Typography variant="subtitle2" fontWeight="medium">
                        {book.title}
                      </Typography>
                    </TableCell>
                    <TableCell>{book.author}</TableCell>
                    <TableCell>
                      <Chip label={book.genre || 'General'} size="small" />
                    </TableCell>
                    <TableCell>${book.price}</TableCell>
                    <TableCell>
                      <Chip 
                        label={book.stockQuantity}
                        color={book.stockQuantity < 5 ? 'error' : 'success'}
                        size="small"
                      />
                    </TableCell>
                    <TableCell align="right">
                      <IconButton color="primary" onClick={() => handleEdit(book)} size="small">
                        <Edit />
                      </IconButton>
                      <IconButton color="error" onClick={() => handleDelete(book)} size="small">
                        <Delete />
                      </IconButton>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </>
      )}

      {tabValue === 1 && (
        <Paper sx={{ p: 3 }}>
          <FileUpload
            onUploadComplete={(result) => {
              toast.success(`Imported ${result.count} books`);
              loadBooks();
              onBooksUpdate?.();
            }}
          />
        </Paper>
      )}

      {/* Edit/Add Dialog */}
      <Dialog open={showForm} onClose={handleCancel} maxWidth="sm" fullWidth>
        <form onSubmit={handleSubmit}>
          <DialogTitle>
            {editingBook ? 'Edit Book' : 'Add New Book'}
          </DialogTitle>
          
          <DialogContent>
            <Grid container spacing={2} sx={{ mt: 1 }}>
              <Grid item xs={12}>
                <TextField
                  fullWidth label="Title" name="title"
                  value={formData.title} onChange={handleChange}
                  required autoFocus
                />
              </Grid>

              <Grid item xs={12}>
                <TextField
                  fullWidth label="Author" name="author"
                  value={formData.author} onChange={handleChange}
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth label="Genre" name="genre"
                  value={formData.genre} onChange={handleChange}
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth label="Price" name="price" type="number"
                  value={formData.price} onChange={handleChange}
                  inputProps={{ step: "0.01", min: "0" }}
                  required
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth label="Stock" name="stockQuantity" type="number"
                  value={formData.stockQuantity} onChange={handleChange}
                  inputProps={{ min: "0" }}
                  required
                />
              </Grid>
              
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth label="ISBN" name="isbn"
                  value={formData.isbn} onChange={handleChange}
                />
              </Grid>
            </Grid>
          </DialogContent>

          <DialogActions sx={{ p: 2 }}>
            <Button onClick={handleCancel} startIcon={<Cancel />}>
              Cancel
            </Button>
            <Button type="submit" variant="contained" disabled={loading} startIcon={<Save />}>
              {loading ? 'Saving...' : (editingBook ? 'Update' : 'Add')}
            </Button>
          </DialogActions>
        </form>
      </Dialog>

      {/* Export Dialog */}
      <ExportDialog
        open={showExport}
        onClose={() => setShowExport(false)}
      />

      {/* Bulk Edit Dialog */}
      <BulkEditDialog
        open={showBulkEdit}
        onClose={() => setShowBulkEdit(false)}
        selectedBooks={getSelectedBooksData()}
        onComplete={() => {
          setSelectedBooks([]);
          loadBooks();
          onBooksUpdate?.();
        }}
      />
    </Container>
  );
}