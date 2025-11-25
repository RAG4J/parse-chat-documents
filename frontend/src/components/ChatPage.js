import { useState } from 'react';
import {
  Box,
  VStack,
  HStack,
  Input,
  Button,
  Text,
  Container,
  useToast,
} from '@chakra-ui/react';

function ChatPage() {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const toast = useToast();

  const sendMessage = async () => {
    if (!input.trim()) return;

    const userMessage = { role: 'user', content: input };
    setMessages((prev) => [...prev, userMessage]);
    setInput('');
    setIsLoading(true);

    try {
      const response = await fetch('/ai', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ input: input }),
      });

      if (!response.ok) {
        throw new Error('Failed to get response from server');
      }

      const aiResponse = await response.text();
      const aiMessage = { role: 'assistant', content: aiResponse };
      setMessages((prev) => [...prev, aiMessage]);
    } catch (error) {
      toast({
        title: 'Error',
        description: error.message,
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    } finally {
      setIsLoading(false);
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      sendMessage();
    }
  };

  return (
    <Container maxW="container.xl" h="full" py={4}>
      <VStack h="full" spacing={4}>
        <Box
          flex="1"
          w="full"
          overflowY="auto"
          borderWidth="1px"
          borderRadius="lg"
          p={4}
          bg="white"
        >
          <VStack spacing={4} align="stretch">
            {messages.length === 0 ? (
              <Text color="gray.500" textAlign="center" mt={8}>
                Start a conversation by typing a message below
              </Text>
            ) : (
              messages.map((message, index) => (
                <Box
                  key={index}
                  alignSelf={message.role === 'user' ? 'flex-end' : 'flex-start'}
                  maxW="70%"
                >
                  <Box
                    bg={message.role === 'user' ? 'teal.500' : 'gray.100'}
                    color={message.role === 'user' ? 'white' : 'black'}
                    px={4}
                    py={2}
                    borderRadius="lg"
                  >
                    <Text whiteSpace="pre-wrap">{message.content}</Text>
                  </Box>
                </Box>
              ))
            )}
          </VStack>
        </Box>

        <HStack w="full" spacing={2}>
          <Input
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyPress={handleKeyPress}
            placeholder="Type your message..."
            disabled={isLoading}
            size="lg"
            bg="white"
          />
          <Button
            onClick={sendMessage}
            isLoading={isLoading}
            colorScheme="teal"
            size="lg"
            px={8}
          >
            Send
          </Button>
        </HStack>
      </VStack>
    </Container>
  );
}

export default ChatPage;
