package org.rag4j.docling.spring_ai_docling;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.client.transport.customizer.McpAsyncHttpClientRequestCustomizer;
import io.modelcontextprotocol.client.transport.customizer.McpSyncHttpClientRequestCustomizer;
import io.modelcontextprotocol.json.jackson.JacksonMcpJsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.mcp.client.common.autoconfigure.NamedClientMcpTransport;
import org.springframework.ai.mcp.client.common.autoconfigure.properties.McpClientCommonProperties;
import org.springframework.ai.mcp.client.common.autoconfigure.properties.McpStreamableHttpClientProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
@EnableConfigurationProperties({ McpStreamableHttpClientProperties.class, McpClientCommonProperties.class })
@ConditionalOnProperty(prefix = McpClientCommonProperties.CONFIG_PREFIX, name = "enabled", havingValue = "true",
        matchIfMissing = true)

public class OverrideConfig {
    private static final Logger logger = LoggerFactory.getLogger(OverrideConfig.class);

    @Bean
    public List<NamedClientMcpTransport> streamableHttpHttpClientTransports(
            McpStreamableHttpClientProperties streamableProperties, ObjectProvider<ObjectMapper> objectMapperProvider,
            ObjectProvider<McpSyncHttpClientRequestCustomizer> syncHttpRequestCustomizer,
            ObjectProvider<McpAsyncHttpClientRequestCustomizer> asyncHttpRequestCustomizer) {

        ObjectMapper objectMapper = objectMapperProvider.getIfAvailable(ObjectMapper::new);

        List<NamedClientMcpTransport> streamableHttpTransports = new ArrayList<>();

        for (Map.Entry<String, McpStreamableHttpClientProperties.ConnectionParameters> serverParameters : streamableProperties.getConnections()
                .entrySet()) {

            String baseUrl = serverParameters.getValue().url();
            String streamableHttpEndpoint = serverParameters.getValue().endpoint() != null
                    ? serverParameters.getValue().endpoint() : "/mcp";

            HttpClientStreamableHttpTransport.Builder transportBuilder = HttpClientStreamableHttpTransport
                    .builder(baseUrl)
                    .endpoint(streamableHttpEndpoint)
                    .clientBuilder(HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1))
                    .jsonMapper(new JacksonMcpJsonMapper(objectMapper));

            asyncHttpRequestCustomizer.ifUnique(transportBuilder::asyncHttpRequestCustomizer);
            syncHttpRequestCustomizer.ifUnique(transportBuilder::httpRequestCustomizer);
            if (asyncHttpRequestCustomizer.getIfUnique() != null && syncHttpRequestCustomizer.getIfUnique() != null) {
                logger.warn("Found beans of type %s and %s. Using %s.".formatted(
                        McpAsyncHttpClientRequestCustomizer.class.getSimpleName(),
                        McpSyncHttpClientRequestCustomizer.class.getSimpleName(),
                        McpSyncHttpClientRequestCustomizer.class.getSimpleName()));
            }

            HttpClientStreamableHttpTransport transport = transportBuilder.build();

            streamableHttpTransports.add(new NamedClientMcpTransport(serverParameters.getKey(), transport));
        }

        return streamableHttpTransports;
    }

}
