export const getGenreGradient = (genre) => {
  const gradients = {
    'Programming': 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    'Fiction': 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    'Mystery': 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    'Science Fiction': 'linear-gradient(135deg, #30cfd0 0%, #330867 100%)',
    'Fantasy': 'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)',
    'default': 'linear-gradient(135deg, #a8c0ff 0%, #3f2b96 100%)'
  };
  return gradients[genre] || gradients.default;
};