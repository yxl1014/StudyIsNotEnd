#!/bin/bash

# 设置路径
PROTO_SOURCE_DIR="source"
JAVA_OUTPUT_DIR="../src/main/java/"
PROTOC="protoc"

# 创建输出目录（如果不存在）
mkdir -p "$JAVA_OUTPUT_DIR"

# 检查protoc是否存在
if ! command -v $PROTOC &> /dev/null; then
    echo "Error: protoc not found! Please install it first."
    echo "Install with: brew install protobuf"
    exit 1
fi

# 检查源文件夹是否存在
if [ ! -d "$PROTO_SOURCE_DIR" ]; then
    echo "Error: source folder not found!"
    exit 1
fi

# 统计处理的文件数量
FILE_COUNT=0

echo "Starting protobuf compilation..."
echo "========================================"

# 遍历source文件夹中的所有.proto文件
for proto_file in "$PROTO_SOURCE_DIR"/*.proto; do
    if [ -f "$proto_file" ]; then
        filename=$(basename "$proto_file")
        echo "Processing: $filename"
        
        $PROTOC --proto_path="$PROTO_SOURCE_DIR" --java_out="$JAVA_OUTPUT_DIR" "$proto_file"
        
        if [ $? -eq 0 ]; then
            echo "Successfully compiled: $filename"
            ((FILE_COUNT++))
        else
            echo "Failed to compile: $filename"
        fi
        echo
    fi
done

echo "========================================"
echo "Compilation completed!"
echo "Total files processed: $FILE_COUNT"
echo "Java files generated in: $JAVA_OUTPUT_DIR"

if [ $FILE_COUNT -gt 0 ]; then
    echo
    echo "Generated Java files:"
    ls -la "$JAVA_OUTPUT_DIR/po/" | head -20
fi
