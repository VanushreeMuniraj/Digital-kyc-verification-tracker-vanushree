#!/bin/bash

echo "Testing login endpoints..."
echo ""

echo "1. Testing Admin Login:"
curl -X POST http://localhost:8080/api/v1/users/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' \
  -w "\nStatus: %{http_code}\n\n"

echo "2. Testing Officer Login:"
curl -X POST http://localhost:8080/api/v1/users/login \
  -H "Content-Type: application/json" \
  -d '{"username":"officer1","password":"officer123"}' \
  -w "\nStatus: %{http_code}\n\n"

echo "3. Testing Customer Login:"
curl -X POST http://localhost:8080/api/v1/users/login \
  -H "Content-Type: application/json" \
  -d '{"username":"customer1","password":"customer123"}' \
  -w "\nStatus: %{http_code}\n\n"

echo "4. Testing Invalid Login:"
curl -X POST http://localhost:8080/api/v1/users/login \
  -H "Content-Type: application/json" \
  -d '{"username":"invalid","password":"wrong"}' \
  -w "\nStatus: %{http_code}\n\n"
