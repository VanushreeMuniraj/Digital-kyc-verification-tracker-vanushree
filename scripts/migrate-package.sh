#!/bin/bash

# Script to migrate from com.zeta to in.zeta package structure

OLD_PACKAGE="com/zeta/zea_opc_b03_digital_kyc"
NEW_PACKAGE="in/zeta/zea_opc_b03_digital_kyc"

OLD_PACKAGE_DOT="com.zeta.zea_opc_b03_digital_kyc"
NEW_PACKAGE_DOT="in.zeta.zea_opc_b03_digital_kyc"

SRC_DIR="src/main/java"
TEST_DIR="src/test/java"

echo "Starting package migration from $OLD_PACKAGE_DOT to $NEW_PACKAGE_DOT"

# Function to copy and update files
migrate_files() {
    local source_dir=$1
    local target_dir=$2
    
    if [ -d "$source_dir" ]; then
        echo "Migrating files from $source_dir to $target_dir"
        
        # Copy files
        cp -r "$source_dir"/* "$target_dir/" 2>/dev/null || true
        
        # Update package declarations in all Java files
        find "$target_dir" -name "*.java" -type f -exec sed -i '' "s/$OLD_PACKAGE_DOT/$NEW_PACKAGE_DOT/g" {} \;
    fi
}

# Migrate main source files
echo "Migrating main source files..."
migrate_files "$SRC_DIR/$OLD_PACKAGE" "$SRC_DIR/$NEW_PACKAGE"

# Migrate test files if they exist
if [ -d "$TEST_DIR/$OLD_PACKAGE" ]; then
    echo "Migrating test files..."
    mkdir -p "$TEST_DIR/$NEW_PACKAGE"
    migrate_files "$TEST_DIR/$OLD_PACKAGE" "$TEST_DIR/$NEW_PACKAGE"
fi

# Update pom.xml if needed
if [ -f "pom.xml" ]; then
    echo "Updating pom.xml..."
    sed -i '' "s/$OLD_PACKAGE_DOT/$NEW_PACKAGE_DOT/g" pom.xml
fi

# Update application properties files
echo "Updating application properties..."
find src/main/resources -name "*.properties" -o -name "*.yml" -o -name "*.yaml" | while read file; do
    sed -i '' "s/$OLD_PACKAGE_DOT/$NEW_PACKAGE_DOT/g" "$file"
done

echo "Migration complete!"
echo "Please review the changes and delete the old package directory manually if everything works:"
echo "rm -rf $SRC_DIR/$OLD_PACKAGE"
echo "rm -rf $TEST_DIR/$OLD_PACKAGE"
