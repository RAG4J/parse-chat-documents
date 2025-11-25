# Spring AI Docling

A demonstration project integrating [Spring AI](https://spring.io/projects/spring-ai) with [Docling](https://ds4sd.github.io/docling/) for intelligent PDF parsing and conversational AI capabilities. This project showcases how to build a modern RAG (Retrieval-Augmented Generation) application that can extract, process, and chat about PDF document content.

## 📖 Blog Post

For a detailed walkthrough and explanation of this project, read the accompanying blog post:

**[A Practical Walkthrough for Parsing PDFs and Chatting About Their Content](https://jettro.dev/a-practical-walkthrough-for-parsing-pdfs-and-chatting-about-their-content-ac02b6c71fb4)**

## 🎯 Features

- **PDF Processing**: Extract and parse PDF documents using Docling's MCP server
- **Spring AI Integration**: Leverage Spring AI's MCP client for seamless tool integration
- **Conversational AI**: Chat about document content using OpenAI models
- **REST API**: Simple HTTP endpoints for document interaction
- **React Frontend**: Modern UI built with React and Chakra UI
- **Docker Support**: Easy deployment with Docker Compose

## 🏗️ Architecture

The project consists of three main components:

1. **Spring Boot Backend** (Java 21)
   - Spring AI MCP client integration
   - REST API for document queries
   - OpenAI model integration

2. **Docling MCP Server** (Docker)
   - PDF parsing and conversion
   - Document indexing and retrieval
   - Exposed via MCP protocol

3. **React Frontend**
   - Chat interface for document interaction
   - Built with Chakra UI

## 🚀 Getting Started

### Prerequisites

- Java 21+
- Maven 3.6+
- Node.js 16+ (for frontend)
- Docker and Docker Compose
- OpenAI API key

### Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd spring-ai-docling
   ```

2. **Set up OpenAI API key**
   
   Create a `src/main/resources/application.properties` file:
   ```properties
   spring.ai.openai.api-key=${OPENAI_API_KEY}
   spring.ai.openai.chat.options.model=gpt-4
   ```

   Export your API key:
   ```bash
   export OPENAI_API_KEY=your-api-key-here
   ```

3. **Start Docling MCP server**
   ```bash
   docker-compose up -d
   ```
   
   The Docling server will be available at `http://localhost:8000`

4. **Place PDF documents**
   
   Add your PDF files to the `documents/` directory. These will be automatically mounted into the Docker container.

5. **Run the Spring Boot application**
   ```bash
   ./mvnw spring-boot:run
   ```
   
   The backend API will start on `http://localhost:8080`

6. **Run the frontend (optional)**
   ```bash
   cd frontend
   npm install
   npm start
   ```
   
   The React app will open at `http://localhost:3000`

## 📝 Usage

### Using the REST API

Send a POST request to `/ai` with your query:

```bash
curl -X POST http://localhost:8080/ai \
  -H "Content-Type: application/json" \
  -d '{
    "input": "Can you extract the contents of this pdf: /data/your-document.pdf"
  }'
```

### Example Queries

- Extract document content: `"Can you extract the contents of this pdf: /data/business-plan.pdf"`
- Ask about document: `"Can you translate the summary to Dutch for document <document-id>?"`
- List available tools: `"Can you tell me which tools are available for the MCP server docling?"`

See `test-calls.http` for more examples.

## 🐍 Python Experimentation

A Python virtual environment is included for experimenting with Docling directly:

```bash
source .venv/bin/activate
python
```

```python
from docling.document_converter import DocumentConverter

converter = DocumentConverter()
result = converter.convert("documents/your-document.pdf")
print(result.document.export_to_markdown())
```

## 🛠️ Technology Stack

- **Backend**: Spring Boot 3.5.8, Spring AI 1.1.0
- **Frontend**: React 18, Chakra UI
- **AI/ML**: OpenAI GPT-4, Docling
- **Infrastructure**: Docker, Docker Compose
- **Build Tools**: Maven, npm

## 📂 Project Structure

```
spring-ai-docling/
├── src/main/java/          # Spring Boot application
│   └── org/rag4j/docling/
├── frontend/               # React application
├── documents/              # PDF files (mounted in Docker)
├── .venv/                  # Python virtual environment
├── docker-compose.yml      # Docling server configuration
├── pom.xml                 # Maven dependencies
└── test-calls.http         # Example API calls
```

## 🤝 Contributing

Contributions are welcome! This is a demonstration project, so feel free to experiment and extend it.

## 📄 License

This project is provided as-is for educational and demonstration purposes.

## 🔗 Resources

- [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)
- [Docling Documentation](https://ds4sd.github.io/docling/)
- [Model Context Protocol](https://modelcontextprotocol.io/)
- [Blog Post: A Practical Walkthrough for Parsing PDFs and Chatting About Their Content](https://jettro.dev/a-practical-walkthrough-for-parsing-pdfs-and-chatting-about-their-content-ac02b6c71fb4)
