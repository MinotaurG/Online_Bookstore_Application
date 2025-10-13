import { useState } from 'react';
import { 
  Container, Paper, Typography, TextField, Button, Box, Alert, Divider, Stack
} from '@mui/material';
import { CloudUpload, Delete, ContentCopy } from '@mui/icons-material';
import { toast } from 'react-toastify';

export default function AdminSeed({ onSeedComplete }) {
  const sampleData = [{
    "id": "b-sample-1",
    "title": "Sample Book",
    "author": "Author Name",
    "genre": "Fiction",
    "price": 29.99,
    "stockQuantity": 10
  }];

  const [text, setText] = useState(JSON.stringify(sampleData, null, 2));
  const [msg, setMsg] = useState('');
  const [loading, setLoading] = useState(false);

  const submit = async () => {
    setMsg('');
    setLoading(true);
    
    try {
      const payload = JSON.parse(text);
      
      if (!Array.isArray(payload)) {
        throw new Error('Data must be an array of books');
      }

      const res = await fetch('/api/books/bulk', {
        method: 'POST',
        headers: {'Content-Type':'application/json'},
        credentials: 'include',
        body: JSON.stringify(payload)
      });

      if (!res.ok) throw new Error(await res.text());
      
      const json = await res.json();
      toast.success(`Successfully seeded ${json.count} books!`);
      setMsg(`Seeded/updated: ${json.count} books`);
      
      if (onSeedComplete) onSeedComplete();
    } catch (e) { 
      toast.error(`Error: ${e.message}`);
      setMsg(`Error: ${e.message}`);
    } finally {
      setLoading(false);
    }
  };

  const copyExample = () => {
    const example = JSON.stringify(sampleData, null, 2);
    navigator.clipboard.writeText(example);
    toast.info('Example copied!');
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Paper sx={{ p: 4 }}>
        <Typography variant="h4" gutterBottom fontWeight="bold">
          Bulk Upload Books
        </Typography>
        
        <Alert severity="info" sx={{ my: 3 }}>
          Upload multiple books using JSON array format. Required: title, price, stockQuantity
        </Alert>

        <Stack direction="row" spacing={2} sx={{ mb: 2 }}>
          <Button variant="outlined" startIcon={<ContentCopy />} onClick={copyExample} size="small">
            Copy Example
          </Button>
          <Button variant="outlined" color="error" startIcon={<Delete />} onClick={() => setText('[]')} size="small">
            Clear
          </Button>
        </Stack>

        <TextField
          fullWidth
          multiline
          rows={15}
          value={text}
          onChange={(e) => setText(e.target.value)}
          sx={{ mb: 3, '& .MuiInputBase-input': { fontFamily: 'monospace' } }}
        />

        <Button
          fullWidth
          variant="contained"
          size="large"
          startIcon={<CloudUpload />}
          onClick={submit}
          disabled={loading}
          sx={{ py: 1.5 }}
        >
          {loading ? 'Uploading...' : 'Upload Books'}
        </Button>

        {msg && <Alert severity={msg.includes('✅') ? 'success' : 'error'} sx={{ mt: 3 }}>{msg}</Alert>}
      </Paper>
    </Container>
  );
}