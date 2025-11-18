#!/bin/bash

# Simple script to format files using VS Code's Prettier extension
# Usage: ./format.sh [file_or_directory]

echo "🎨 Formatting files with Prettier..."

if [ -z "$1" ]; then
    # Format all Java and FXML files in the project
    echo "Formatting all Java and FXML files..."
    
    # Find and format Java files
    find . -name "*.java" -not -path "*/target/*" -not -path "*/node_modules/*" | while read file; do
        echo "  ✓ $file"
        code --wait "$file" --command "editor.action.formatDocument" 2>/dev/null
    done
    
    # Find and format FXML files
    find . -name "*.fxml" -not -path "*/target/*" -not -path "*/node_modules/*" | while read file; do
        echo "  ✓ $file"
        code --wait "$file" --command "editor.action.formatDocument" 2>/dev/null
    done
    
    echo "✅ Done! All files formatted."
else
    # Format specific file or directory
    if [ -f "$1" ]; then
        echo "Formatting $1..."
        code --wait "$1" --command "editor.action.formatDocument" 2>/dev/null
        echo "✅ Done!"
    elif [ -d "$1" ]; then
        echo "Formatting files in $1..."
        find "$1" -name "*.java" -o -name "*.fxml" | while read file; do
            echo "  ✓ $file"
            code --wait "$file" --command "editor.action.formatDocument" 2>/dev/null
        done
        echo "✅ Done!"
    else
        echo "❌ Error: $1 is not a valid file or directory"
        exit 1
    fi
fi
