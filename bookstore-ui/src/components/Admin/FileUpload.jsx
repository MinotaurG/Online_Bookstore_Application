import { useState } from 'react';
import {
  Box, Button, Paper, Typography, LinearProgress, Alert,
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow
} from '@mui/material';
import { CloudUpload, CheckCircle } from '@mui/icons-material';

export default function FileUpload({ onUploadComplete }) {
  const [file, setFile] = useState(null);
  const [preview, setPreview] = useState(null);
  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState('');
  const [dragOver, setDragOver] = useState(false);

  const handleFileChange = async (e) => {
    const selectedFile = e.target.files?.[0];
    if (!selectedFile) return;

    setError('');
    setFile(selectedFile);

    try {
      const text = await selectedFile.text();
      let books;

      if (selectedFile.name.endsWith('.json')) {
        books = JSON.parse(text);
      } else if (selectedFile.name.endsWith('.csv')) {
        books = parseCSV(text);
      } else {
        setError('Only JSON and CSV files are supported');
        return;
      }

      setPreview(Array.isArray(books) ? books : [books]);
    } catch (err) {
      setError('Failed to parse file: ' + err.message);
      console.error('Parse error:', err);
    }
  };

  const parseCSV = (text) => {
    const lines = text.trim().split('\n');
    const headers = lines[0].split(',').map(h => h.trim().replace(/"/g, ''));
    
    return lines.slice(1).filter(line => line.trim()).map(line => {
      const values = line.split(',').map(v => v.trim().replace(/"/g, ''));
      const book = {};
      
      headers.forEach((header, index) => {
        const value = values[index];
        const lowerHeader = header.toLowerCase();
        
        // Map CSV headers to Book fields
        if (lowerHeader === 'asin') {
          book.asin = value || undefined;
        } else if (lowerHeader === 'id') {
          book.id = value || undefined;
        } else if (lowerHeader === 'title') {
          book.title = value;
        } else if (lowerHeader === 'author') {
          book.author = value;
        } else if (lowerHeader === 'genre') {
          book.genre = value;
        } else if (lowerHeader === 'price') {
          book.price = parseFloat(value) || 0;
        } else if (lowerHeader === 'stock' || lowerHeader === 'stockquantity') {
          book.stockQuantity = parseInt(value) || 0;
        } else if (lowerHeader === 'isbn') {
          book.isbn = value || '';
        }
      });
      
      return book;
    });
  };

  const handleUpload = async () => {
  if (!preview) return;

  setUploading(true);
  setError('');

  try {
    // Clean books - convert empty strings to undefined
    const booksToUpload = preview.map(book => ({
      id: book.id && book.id.trim() ? book.id : undefined,  // Empty string → undefined
      asin: book.asin && book.asin.trim() ? book.asin : undefined,
      title: book.title,
      author: book.author || '',
      genre: book.genre || 'General',
      price: parseFloat(book.price) || 0,
      stockQuantity: parseInt(book.stockQuantity) || 0,
      isbn: book.isbn || ''
    }));

    console.log('Uploading:', booksToUpload);

    const response = await fetch('/api/books/bulk', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify(booksToUpload)
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText);
    }

    const result = await response.json();
    onUploadComplete?.(result);
    
    setFile(null);
    setPreview(null);
  } catch (err) {
    setError('Upload failed: ' + err.message);
    console.error('Upload error:', err);
  } finally {
    setUploading(false);
  }
};

  const handleDrop = (e) => {
    e.preventDefault();
    setDragOver(false);
    
    const droppedFile = e.dataTransfer.files[0];
    if (droppedFile) {
      handleFileChange({ target: { files: [droppedFile] } });
    }
  };

  return (
    <Box>
      <Paper
        sx={{
          p: 3,
          border: '2px dashed',
          borderColor: dragOver ? 'primary.main' : 'divider',
          bgcolor: dragOver ? 'action.hover' : 'background.paper',
          textAlign: 'center',
          cursor: 'pointer',
          transition: 'all 0.2s'
        }}
        onDragOver={(e) => { e.preventDefault(); setDragOver(true); }}
        onDragLeave={() => setDragOver(false)}
        onDrop={handleDrop}
      >
        <input
          type="file"
          accept=".json,.csv"
          onChange={handleFileChange}
          style={{ display: 'none' }}
          id="file-upload"
        />
        <label htmlFor="file-upload">
          <Box sx={{ cursor: 'pointer' }}>
            <CloudUpload sx={{ fontSize: 64, color: 'primary.main', mb: 2 }} />
            <Typography variant="h6" gutterBottom>
              Drop files here or click to upload
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Supports JSON and CSV files
            </Typography>
          </Box>
        </label>

        {file && (
          <Box sx={{ mt: 2 }}>
            <Typography variant="body2" color="success.main">
              <CheckCircle sx={{ fontSize: 16, verticalAlign: 'middle', mr: 0.5 }} />
              {file.name}
            </Typography>
          </Box>
        )}
      </Paper>

      {error && (
        <Alert severity="error" sx={{ mt: 2 }}>
          {error}
        </Alert>
      )}

      {preview && preview.length > 0 && (
        <Box sx={{ mt: 3 }}>
          <Typography variant="h6" gutterBottom>
            Preview ({preview.length} books)
          </Typography>
          
          <TableContainer component={Paper} sx={{ maxHeight: 400, mb: 2 }}>
            <Table stickyHeader size="small">
              <TableHead>
                <TableRow>
                  <TableCell>Title</TableCell>
                  <TableCell>Author</TableCell>
                  <TableCell>Genre</TableCell>
                  <TableCell>Price</TableCell>
                  <TableCell>Stock</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {preview.slice(0, 10).map((book, idx) => (
                  <TableRow key={idx}>
                    <TableCell>{book.title}</TableCell>
                    <TableCell>{book.author}</TableCell>
                    <TableCell>{book.genre}</TableCell>
                    <TableCell>${book.price}</TableCell>
                    <TableCell>{book.stockQuantity}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>

          {preview.length > 10 && (
            <Typography variant="caption" color="text.secondary">
              Showing first 10 of {preview.length} books
            </Typography>
          )}

          <Box sx={{ display: 'flex', gap: 2, mt: 2 }}>
            <Button
              variant="contained"
              onClick={handleUpload}
              disabled={uploading}
              fullWidth
            >
              {uploading ? 'Uploading...' : `Upload ${preview.length} Books`}
            </Button>
            <Button
              variant="outlined"
              onClick={() => { setFile(null); setPreview(null); setError(''); }}
              disabled={uploading}
            >
              Cancel
            </Button>
          </Box>

          {uploading && <LinearProgress sx={{ mt: 2 }} />}
        </Box>
      )}
    </Box>
  );
}