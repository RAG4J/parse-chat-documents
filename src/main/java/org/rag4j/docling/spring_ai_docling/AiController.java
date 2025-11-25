package org.rag4j.docling.spring_ai_docling;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class AiController {
    private final Logger logger = LoggerFactory.getLogger(AiController.class);
    private final ChatClient chatClient;
    private final SyncMcpToolCallbackProvider toolCallbackProvider;

    public AiController(ChatClient.Builder chatClientBuilder, List<McpSyncClient> mcpSyncClients) {
        this.chatClient = chatClientBuilder.build();
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
    public String ai(@RequestBody UserInput userInput) {
        ToolCallback[] toolCallbacks = this.toolCallbackProvider.getToolCallbacks();

        Arrays.stream(toolCallbacks).forEach(toolCallback -> {
            String name = toolCallback.getToolDefinition().name();
            String description = toolCallback.getToolDefinition().description();
            logger.trace("Using Tool Callback: {} - {}", name, description);
        });

        ChatClient.CallResponseSpec responseSpec = this.chatClient.prompt()
                .user(userInput.input())
                .toolCallbacks(toolCallbacks)
                .call();

        return responseSpec.content();
    }

    public record UserInput(String input) {
    }
}
