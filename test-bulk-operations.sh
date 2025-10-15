#!/bin/bash

echo "Testing Bulk Operations..."
echo ""

BASE_DIR="/mnt/c/Dev/Online_Book_Store"

# CHANGE THIS TO YOUR ACTUAL ADMIN PASSWORD
ADMIN_PASSWORD="admin123"  # CHANGE THIS!

# 1. Login
echo "1. Logging in..."
LOGIN_RESPONSE=$(curl -c cookies.txt -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"'"$ADMIN_PASSWORD"'"}' \
  -s)

echo "$LOGIN_RESPONSE" | jq .

# Check if login succeeded
if echo "$LOGIN_RESPONSE" | jq -e '.isAdmin' > /dev/null 2>&1; then
  echo "Login successful!"
else
  echo "Login failed! Check your password."
  exit 1
fi
echo ""

# 2. Get current books
echo "2. Current books:"
BOOKS=$(curl -s http://localhost:8080/api/books)
echo "$BOOKS" | jq '.[] | {asin: .asin, title: .title, price: .price, stock: .stockQuantity}'
echo ""

# 3. Test bulk update
echo "3. Testing bulk update (price increase)..."
FIRST_ASIN=$(echo "$BOOKS" | jq -r '.[0].asin')
SECOND_ASIN=$(echo "$BOOKS" | jq -r '.[1].asin')

echo "   Updating ASINs: $FIRST_ASIN, $SECOND_ASIN"

UPDATE_RESPONSE=$(curl -b cookies.txt -X PUT http://localhost:8080/api/books/bulk \
  -H "Content-Type: application/json" \
  -d '{
    "updates": [
      {
        "asin": "'"$FIRST_ASIN"'",
        "price": 29.99,
        "stockQuantity": 100
      },
      {
        "asin": "'"$SECOND_ASIN"'",
        "price": 34.99
      }
    ]
  }' \
  -s)

echo "$UPDATE_RESPONSE" | jq .
echo ""

# 4. Verify updates
echo "4. Verifying updates..."
curl -s "http://localhost:8080/api/books/asin/$FIRST_ASIN" | jq '{asin: .asin, title: .title, price: .price, stock: .stockQuantity}'
echo ""

# 5. Test export JSON
echo "5. Testing JSON export..."
curl -b cookies.txt -s "http://localhost:8080/api/books/export/json" -o catalog-export.json
if [ -f catalog-export.json ]; then
  echo "   Exported to: catalog-export.json"
  echo "   Books count: $(cat catalog-export.json | jq '. | length')"
else
  echo "   Export failed"
fi
echo ""

# 6. Test export CSV
echo "6. Testing CSV export..."
curl -b cookies.txt -s "http://localhost:8080/api/books/export/csv" -o catalog-export.csv
if [ -f catalog-export.csv ] && [ -s catalog-export.csv ]; then
  echo "   Exported to: catalog-export.csv"
  head -5 catalog-export.csv
else
  echo "   Export failed"
  cat catalog-export.csv
fi
echo ""

# 7. Test filtered export
echo "7. Testing filtered export (Fantasy genre)..."
FANTASY_COUNT=$(curl -b cookies.txt -s "http://localhost:8080/api/books/export/json?genre=Fantasy" | jq '. | length')
echo "   Fantasy books: $FANTASY_COUNT"
echo ""

# 8. Test bulk delete
echo "8. Testing bulk delete..."
curl -b cookies.txt -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{"title":"Test Book 1","author":"Test","genre":"Test","price":9.99,"stockQuantity":1}' \
  -s > /dev/null

curl -b cookies.txt -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{"title":"Test Book 2","author":"Test","genre":"Test","price":9.99,"stockQuantity":1}' \
  -s > /dev/null

sleep 1

TEST_BOOKS=$(curl -s "http://localhost:8080/api/books/search?q=Test Book")
TEST_ASIN1=$(echo "$TEST_BOOKS" | jq -r '.[0].asin')
TEST_ASIN2=$(echo "$TEST_BOOKS" | jq -r '.[1].asin')

if [ "$TEST_ASIN1" != "null" ] && [ -n "$TEST_ASIN1" ]; then
  echo "   Created test books with ASINs: $TEST_ASIN1, $TEST_ASIN2"
  
  DELETE_RESPONSE=$(curl -b cookies.txt -X DELETE http://localhost:8080/api/books/bulk \
    -H "Content-Type: application/json" \
    -d '{
      "asins": ["'"$TEST_ASIN1"'", "'"$TEST_ASIN2"'"]
    }' \
    -s)
  
  echo "$DELETE_RESPONSE" | jq .
else
  echo "   Test books not created properly, skipping delete test"
fi
echo ""

echo "Bulk Operations Testing Complete!"
