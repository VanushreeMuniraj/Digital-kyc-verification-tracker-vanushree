#!/bin/bash

# Remove @PreAuthorize and Authentication parameters from all controllers

for file in src/main/java/in/zeta/zea_opc_b03_digital_kyc/controller/*.java; do
    # Remove @PreAuthorize lines
    sed -i '' '/@PreAuthorize/d' "$file"
    
    # Remove Authentication authentication) from method signatures
    sed -i '' 's/,\s*Authentication authentication)/)/g' "$file"
    sed -i '' 's/(Authentication authentication,/(/' "$file"
    sed -i '' 's/(Authentication authentication)/()/g' "$file"
    
    # Remove security imports
    sed -i '' '/import.*springframework.security/d' "$file"
    sed -i '' '/import.*UserPrincipal/d' "$file"
    sed -i '' '/import.*RequireOtp/d' "$file"
    
    # Remove authentication checks
    sed -i '' '/if (authentication == null/,/}/d' "$file"
    sed -i '' '/UserPrincipal userPrincipal/d' "$file"
done

echo "Authentication removed from all controllers"
