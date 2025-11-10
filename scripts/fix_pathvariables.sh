#!/bin/bash
cd src/main/java/in/zeta/zea_opc_b03_digital_kyc/controller

# Fix all PathVariable Long without explicit names
for file in *.java; do
  sed -i '' 's/@PathVariable Long userId)/@PathVariable("userId") Long userId)/g' "$file"
  sed -i '' 's/@PathVariable Long verificationId)/@PathVariable("verificationId") Long verificationId)/g' "$file"
  sed -i '' 's/@PathVariable Long customerId)/@PathVariable("customerId") Long customerId)/g' "$file"
  sed -i '' 's/@PathVariable Long commentId)/@PathVariable("commentId") Long commentId)/g' "$file"
  sed -i '' 's/@PathVariable Long officerId)/@PathVariable("officerId") Long officerId)/g' "$file"
  sed -i '' 's/@PathVariable String userId)/@PathVariable("userId") String userId)/g' "$file"
  sed -i '' 's/@PathVariable String notificationId)/@PathVariable("notificationId") String notificationId)/g' "$file"
done

echo "Fixed PathVariable annotations"
