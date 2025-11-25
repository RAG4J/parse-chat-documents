package org.rag4j.docling.spring_ai_docling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/documents")
@CrossOrigin(origins = "http://localhost:3000")
public class DocumentsController {
    private final Logger logger = LoggerFactory.getLogger(DocumentsController.class);
    private final DocumentsProperties documentsProperties;

    public DocumentsController(DocumentsProperties documentsProperties) {
        this.documentsProperties = documentsProperties;
    }

    @GetMapping
    public DocumentsResponse listDocuments() {
        File folder = new File(documentsProperties.getFolder());
        
        if (!folder.exists() || !folder.isDirectory()) {
            logger.warn("Documents folder does not exist: {}", folder.getAbsolutePath());
            return new DocumentsResponse(List.of(), documentsProperties.getDataPath());
        }

        File[] files = folder.listFiles();
        List<DocumentInfo> documents = new ArrayList<>();

        if (files != null) {
            Arrays.stream(files)
                .filter(File::isFile)
                .forEach(file -> {
                    documents.add(new DocumentInfo(
                        file.getName(),
                        documentsProperties.getDataPath() + "/" + file.getName(),
                        file.length()
                    ));
                });
        }

        logger.debug("Found {} documents in folder: {}", documents.size(), folder.getAbsolutePath());
        return new DocumentsResponse(documents, documentsProperties.getDataPath());
    }

    public record DocumentInfo(String name, String path, long size) {}
    
    public record DocumentsResponse(List<DocumentInfo> documents, String dataPath) {}
}
