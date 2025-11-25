# Spring AI Docling Frontend

A React-based chat interface for the Spring AI Docling application, built with Chakra UI.

## Features

- Clean and responsive chat interface
- User name management with modal dialog
- Session-based user identification
- Navigation bar and footer
- Real-time communication with Spring Boot backend
- Built with Chakra UI components
- Minimal custom styling

## Prerequisites

- Node.js (v14 or higher)
- npm or yarn
- Spring Boot backend running on port 8080

## Installation

```bash
npm install
```

## Development

Start the development server:

```bash
npm start
```

The app will open at [http://localhost:3000](http://localhost:3000).

The proxy is configured to forward API requests to `http://localhost:8080` where your Spring Boot application should be running.

## Building for Production

```bash
npm run build
```

This creates an optimized production build in the `build` folder.

## API Integration

The frontend communicates with the Spring Boot backend via:

- **POST /ai** - Sends user input and receives AI response
  - Request body: `{ "input": "your message" }`
  - Response: Plain text AI response

## Project Structure

```
frontend/
├── public/
│   └── index.html
├── src/
│   ├── components/
│   │   ├── Navbar.js        # Navigation bar component
│   │   ├── Footer.js        # Footer component
│   │   └── ChatPage.js      # Main chat interface
│   ├── App.js               # Main app component with layout
│   └── index.js             # Entry point
└── package.json
```
