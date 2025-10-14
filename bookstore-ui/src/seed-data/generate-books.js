import fs from 'fs';

const authors = [
  'J.K. Rowling', 'Stephen King', 'Agatha Christie', 'Dan Brown', 
  'J.R.R. Tolkien', 'George R.R. Martin', 'Ernest Hemingway', 
  'Mark Twain', 'Jane Austen', 'Charles Dickens', 'Isaac Asimov',
  'Arthur C. Clarke', 'Philip K. Dick', 'William Shakespeare',
  'Leo Tolstoy', 'F. Scott Fitzgerald', 'Harper Lee', 'George Orwell'
];

const genres = [
  'Fiction', 'Mystery', 'Thriller', 'Science Fiction', 'Fantasy',
  'Romance', 'Horror', 'Historical Fiction', 'Biography', 'Self-Help',
  'Programming', 'Business', 'Philosophy', 'Poetry', 'Drama'
];

const titleWords = [
  'The Great', 'Mystery of', 'Secret', 'Lost', 'Hidden', 'Dark', 'Bright',
  'Ancient', 'Modern', 'Future', 'Past', 'Journey to', 'Tales of', 'Chronicles of',
  'Legend of', 'Adventures in', 'Story of', 'Dreams of', 'Shadows of', 'Light of'
];

const titleEndings = [
  'Kingdom', 'Empire', 'World', 'City', 'Forest', 'Mountain', 'Ocean', 'Sky',
  'Time', 'Space', 'Heart', 'Mind', 'Soul', 'Dream', 'Reality', 'Truth',
  'Mystery', 'Adventure', 'Journey', 'Quest', 'Discovery', 'Revolution'
];

function generateTitle() {
  const start = titleWords[Math.floor(Math.random() * titleWords.length)];
  const end = titleEndings[Math.floor(Math.random() * titleEndings.length)];
  return `${start} ${end}`;
}

function generateBooks(count) {
  const books = [];
  
  for (let i = 1; i <= count; i++) {
    const title = generateTitle();
    const author = authors[Math.floor(Math.random() * authors.length)];
    const genre = genres[Math.floor(Math.random() * genres.length)];
    const price = (Math.random() * 50 + 10).toFixed(2);
    const stockQuantity = Math.floor(Math.random() * 100);
    
    books.push({
      id: `b-${genre.toLowerCase().replace(/\s+/g, '-')}-${i}`,
      title: `${title} ${i}`,
      author,
      genre,
      price: parseFloat(price),
      stockQuantity
    });
  }
  
  return books;
}

// Generate 200 books
const books = generateBooks(200);

// Save to file
fs.writeFileSync(
  'books-large-dataset.json',
  JSON.stringify(books, null, 2)
);

console.log(`✅ Generated ${books.length} books`);
console.log(`📁 Saved to: books-large-dataset.json`);
console.log(`📊 Total size: ${(JSON.stringify(books).length / 1024).toFixed(2)} KB`);