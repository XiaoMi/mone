#!/bin/bash

# Hive Manager TUI Setup Script
# This script helps you set up the TUI environment quickly

set -e

echo "════════════════════════════════════════════"
echo "    Hive Manager TUI Setup Script"
echo "════════════════════════════════════════════"
echo ""

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Check Node.js version
echo -e "${CYAN}Checking Node.js version...${NC}"
if ! command -v node &> /dev/null; then
    echo -e "${RED}❌ Node.js is not installed${NC}"
    echo "Please install Node.js 18+ from https://nodejs.org"
    exit 1
fi

NODE_VERSION=$(node -v | cut -d'v' -f2 | cut -d'.' -f1)
if [ "$NODE_VERSION" -lt 18 ]; then
    echo -e "${RED}❌ Node.js version is too old ($NODE_VERSION)${NC}"
    echo "Please upgrade to Node.js 18+"
    exit 1
fi
echo -e "${GREEN}✓ Node.js $(node -v) detected${NC}"
echo ""

# Check npm
echo -e "${CYAN}Checking npm...${NC}"
if ! command -v npm &> /dev/null; then
    echo -e "${RED}❌ npm is not installed${NC}"
    exit 1
fi
echo -e "${GREEN}✓ npm $(npm -v) detected${NC}"
echo ""

# Install dependencies
echo -e "${CYAN}Installing dependencies...${NC}"
npm install
echo -e "${GREEN}✓ Dependencies installed${NC}"
echo ""

# Setup environment file
echo -e "${CYAN}Setting up environment configuration...${NC}"
if [ ! -f .env ]; then
    cp .env.example .env
    echo -e "${GREEN}✓ .env file created${NC}"
    echo ""

    # Prompt for configuration
    echo -e "${YELLOW}Would you like to configure the API endpoints now? (y/n)${NC}"
    read -r response
    if [[ "$response" =~ ^([yY][eE][sS]|[yY])$ ]]; then
        echo ""
        echo -e "${CYAN}Enter API Base URL (default: http://localhost:8080/agent-manager):${NC}"
        read -r api_url
        api_url=${api_url:-http://localhost:8080/agent-manager}

        echo -e "${CYAN}Enter WebSocket Base URL (default: ws://localhost:8080/agent-manager):${NC}"
        read -r ws_url
        ws_url=${ws_url:-ws://localhost:8080/agent-manager}

        cat > .env << EOF
API_BASE_URL=$api_url
WS_BASE_URL=$ws_url
EOF
        echo -e "${GREEN}✓ Configuration saved${NC}"
    fi
else
    echo -e "${YELLOW}⚠ .env file already exists, skipping${NC}"
fi
echo ""

# Type checking
echo -e "${CYAN}Running type check...${NC}"
npm run type-check
echo -e "${GREEN}✓ Type check passed${NC}"
echo ""

# Success message
echo "════════════════════════════════════════════"
echo -e "${GREEN}✓ Setup completed successfully!${NC}"
echo "════════════════════════════════════════════"
echo ""
echo "Next steps:"
echo ""
echo -e "  1. ${CYAN}npm run dev${NC}     - Start in development mode"
echo -e "  2. ${CYAN}npm run build${NC}   - Build for production"
echo -e "  3. ${CYAN}npm start${NC}       - Run production build"
echo ""
echo "For more information, see README.md"
echo ""
