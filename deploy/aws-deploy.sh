#!/bin/bash
echo "🚀 Starting AWS deployment..."
echo "📦 Building Docker images..."
docker-compose -f docker-compose.prod.yml build
echo "✅ Deployment script ready!"
echo "Run: docker-compose -f docker-compose.prod.yml up -d"
