package org.rag4j.docling.spring_ai_docling;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class AiController {

    private final ChatClient chatClient;
    private final SyncMcpToolCallbackProvider toolCallbackProvider;

    public AiController(ChatClient.Builder chatClientBuilder, List<McpSyncClient> mcpSyncClients) {
        this.chatClient = chatClientBuilder.build();
        // Initialize MCP clients
        mcpSyncClients.forEach(McpSyncClient::initialize);
//        mcpSyncClients.forEach(client -> {
//            client.listTools().tools().forEach(tool -> {
//                System.out.printf("MCP Client Tool Loaded: %s - %s%n", tool.name(), tool.description());
//            });
//        });
        this.toolCallbackProvider = SyncMcpToolCallbackProvider.builder().mcpClients(mcpSyncClients).build();
    }

    @PostMapping("/ai")
    public String ai(@RequestBody UserInput userInput) {
        ToolCallback[] toolCallbacks = this.toolCallbackProvider.getToolCallbacks();
        Arrays.stream(toolCallbacks).forEach(toolCallback -> {
            String name = toolCallback.getToolDefinition().name();
            String description = toolCallback.getToolDefinition().description();

            System.out.printf("Using Tool Callback: %s - %s%n", name, description);
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
