import { useState, useEffect } from 'react';
import {
  Box,
  VStack,
  Text,
  Button,
  Heading,
  useToast,
  Spinner,
  Badge,
} from '@chakra-ui/react';

function DocumentList({ onDocumentClick }) {
  const [documents, setDocuments] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const toast = useToast();

  useEffect(() => {
    fetchDocuments();
  }, []);

  const fetchDocuments = async () => {
    try {
      const response = await fetch('/documents');
      if (!response.ok) {
        throw new Error('Failed to fetch documents');
      }
      const data = await response.json();
      setDocuments(data.documents);
    } catch (error) {
      toast({
        title: 'Error',
        description: 'Failed to load documents',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    } finally {
      setIsLoading(false);
    }
  };

  const formatFileSize = (bytes) => {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
    return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
  };

  if (isLoading) {
    return (
      <Box p={4} textAlign="center">
        <Spinner color="teal.500" />
        <Text mt={2} fontSize="sm" color="gray.600">Loading documents...</Text>
      </Box>
    );
  }

  return (
    <Box
      borderWidth="1px"
      borderRadius="lg"
      p={4}
      bg="white"
      h="full"
      overflowY="auto"
    >
      <Heading size="sm" mb={4} color="teal.600">
        Available Documents
      </Heading>
      {documents.length === 0 ? (
        <Text fontSize="sm" color="gray.500">
          No documents available
        </Text>
      ) : (
        <VStack spacing={2} align="stretch">
          {documents.map((doc, index) => (
            <Button
              key={index}
              size="sm"
              variant="outline"
              justifyContent="space-between"
              onClick={() => onDocumentClick(doc.path)}
              _hover={{ bg: 'teal.50' }}
              textAlign="left"
              height="auto"
              py={2}
            >
              <VStack align="start" spacing={0} flex="1">
                <Text fontSize="sm" fontWeight="medium" noOfLines={1}>
                  {doc.name}
                </Text>
                <Badge colorScheme="gray" fontSize="xs">
                  {formatFileSize(doc.size)}
                </Badge>
              </VStack>
            </Button>
          ))}
        </VStack>
      )}
    </Box>
  );
}

export default DocumentList;
