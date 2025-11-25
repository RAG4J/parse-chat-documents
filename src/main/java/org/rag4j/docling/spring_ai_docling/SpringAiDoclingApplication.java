package org.rag4j.docling.spring_ai_docling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringAiDoclingApplication {
    private static final Logger logger = LoggerFactory.getLogger(SpringAiDoclingApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringAiDoclingApplication.class, args);
	}

}
