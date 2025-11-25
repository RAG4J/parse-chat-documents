package org.rag4j.docling.spring_ai_docling;

import io.modelcontextprotocol.client.McpSyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class AiController {
    private static final String SYSTEM_PROMPT = """
            You are a helpful AI assistant with access to document analysis tools.
            When users ask you to analyze documents, the documents are located in the /data folder.
            You can access documents using their full path: /data/filename.pdf
            Use the available MCP tools to read and analyze document contents.
            """;

    private final Logger logger = LoggerFactory.getLogger(AiController.class);
    private final ChatClient chatClient;
    private final SyncMcpToolCallbackProvider toolCallbackProvider;
    private final ChatMemory chatMemory;

    public AiController(ChatClient.Builder chatClientBuilder, List<McpSyncClient> mcpSyncClients, ChatMemory chatMemory) {
        this.chatClient = chatClientBuilder.build();
        this.chatMemory = chatMemory;

        // Initialize MCP clients
        mcpSyncClients.forEach(McpSyncClient::initialize);

        if (logger.isTraceEnabled()) {
            mcpSyncClients.forEach(client -> {
                client.listTools().tools().forEach(tool -> {
                    logger.trace("MCP Client Tool Loaded: {} - {}", tool.name(), tool.description());
                });
            });
        }

        this.toolCallbackProvider = SyncMcpToolCallbackProvider.builder().mcpClients(mcpSyncClients).build();
    }

    @PostMapping("/ai")
    public String ai(@RequestBody UserInput userInput, HttpSession session) {
        // Get or set the user name in the session
        String userName = userInput.userName();
        if (userName != null && !userName.trim().isEmpty()) {
            session.setAttribute("userName", userName);
        } else {
            userName = (String) session.getAttribute("userName");
            if (userName == null) {
                userName = "anonymous";
            }
        }

        logger.debug("Processing request for user: {}", userName);

        ToolCallback[] toolCallbacks = this.toolCallbackProvider.getToolCallbacks();

        return chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(userInput.input())
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory).conversationId(userName).build())
                .toolCallbacks(toolCallbacks)
                .call()
                .content();
    }

    @GetMapping("/user/name")
    public UserNameResponse getUserName(HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        return new UserNameResponse(userName);
    }

    @PostMapping("/user/name")
    public UserNameResponse setUserName(@RequestBody UserNameRequest request, HttpSession session) {
        session.setAttribute("userName", request.userName());
        logger.info("User name set to: {}", request.userName());
        return new UserNameResponse(request.userName());
    }

    @PostMapping("/user/clear-memory")
    public ClearMemoryResponse clearMemory(HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        if (userName != null) {
            chatMemory.clear(userName);
            logger.info("Cleared chat memory for user: {}", userName);
            return new ClearMemoryResponse("Chat memory cleared for user: " + userName);
        }
        return new ClearMemoryResponse("No user name found in session. Chat memory not cleared.");
    }

    public record UserInput(String input, String userName) {
    }

    public record UserNameRequest(String userName) {
    }

    public record UserNameResponse(String userName) {
    }

    public record ClearMemoryResponse(String message) {
    }
}
