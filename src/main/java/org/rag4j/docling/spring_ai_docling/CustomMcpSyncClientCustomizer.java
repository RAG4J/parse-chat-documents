package org.rag4j.docling.spring_ai_docling;

import io.modelcontextprotocol.client.McpClient;
import org.springframework.ai.mcp.customizer.McpSyncClientCustomizer;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class CustomMcpSyncClientCustomizer implements McpSyncClientCustomizer {
    @Override
    public void customize(String name, McpClient.SyncSpec spec) {
        spec.requestTimeout(Duration.ofMinutes(2)); // or longer for doc conversion
    }
}