import { useState, useEffect } from 'react';
import {
  Box,
  VStack,
  HStack,
  Input,
  Button,
  Text,
  Container,
  useToast,
  Modal,
  ModalOverlay,
  ModalContent,
  ModalHeader,
  ModalBody,
  ModalFooter,
  FormControl,
  FormLabel,
  useDisclosure,
  Grid,
  GridItem,
} from '@chakra-ui/react';
import DocumentList from './DocumentList';

function ChatPage() {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [userName, setUserName] = useState('');
  const [tempUserName, setTempUserName] = useState('');
  const { isOpen, onOpen, onClose } = useDisclosure();
  const toast = useToast();

  useEffect(() => {
    // Check if user name exists in session
    fetch('/user/name')
      .then((res) => res.json())
      .then((data) => {
        if (data.userName) {
          setUserName(data.userName);
        } else {
          onOpen(); // Open the name modal if no name is set
        }
      })
      .catch(() => {
        onOpen(); // Open modal if there's an error
      });
  }, [onOpen]);

  const handleSaveUserName = async () => {
    if (!tempUserName.trim()) {
      toast({
        title: 'Name required',
        description: 'Please enter your name',
        status: 'warning',
        duration: 3000,
        isClosable: true,
      });
      return;
    }

    try {
      const response = await fetch('/user/name', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ userName: tempUserName }),
      });

      if (response.ok) {
        setUserName(tempUserName);
        onClose();
        toast({
          title: 'Name saved',
          description: `Welcome, ${tempUserName}!`,
          status: 'success',
          duration: 3000,
          isClosable: true,
        });
      }
    } catch (error) {
      toast({
        title: 'Error',
        description: 'Failed to save name',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    }
  };

  const handleChangeUserName = () => {
    setTempUserName(userName);
    onOpen();
  };

  const handleDocumentClick = (documentPath) => {
    const message = `Analyze the document: ${documentPath}`;
    setInput(message);
    toast({
      title: 'Document selected',
      description: `Added "${documentPath}" to your message`,
      status: 'info',
      duration: 2000,
      isClosable: true,
    });
  };

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
        body: JSON.stringify({ input: input, userName: userName }),
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
    <>
      <Modal isOpen={isOpen} onClose={userName ? onClose : undefined} closeOnOverlayClick={!!userName}>
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>{userName ? 'Change Your Name' : 'Welcome! Please Enter Your Name'}</ModalHeader>
          <ModalBody>
            <FormControl>
              <FormLabel>Name</FormLabel>
              <Input
                value={tempUserName}
                onChange={(e) => setTempUserName(e.target.value)}
                placeholder="Enter your name"
                onKeyPress={(e) => {
                  if (e.key === 'Enter') {
                    handleSaveUserName();
                  }
                }}
              />
            </FormControl>
          </ModalBody>
          <ModalFooter>
            {userName && (
              <Button variant="ghost" mr={3} onClick={onClose}>
                Cancel
              </Button>
            )}
            <Button colorScheme="teal" onClick={handleSaveUserName}>
              Save
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>

      <Container maxW="container.xl" h="full" py={4}>
        <Grid templateColumns="250px 1fr" gap={4} h="full">
          <GridItem>
            <DocumentList onDocumentClick={handleDocumentClick} />
          </GridItem>
          
          <GridItem>
            <VStack h="full" spacing={4}>
              <HStack w="full" justify="space-between" mb={2}>
                {userName && (
                  <HStack>
                    <Text fontSize="sm" color="gray.600">
                      Chatting as: <strong>{userName}</strong>
                    </Text>
                    <Button size="xs" variant="link" colorScheme="teal" onClick={handleChangeUserName}>
                      Change
                    </Button>
                  </HStack>
                )}
              </HStack>

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
                  placeholder="Type your message or click a document..."
                  disabled={isLoading || !userName}
                  size="lg"
                  bg="white"
                />
                <Button
                  onClick={sendMessage}
                  isLoading={isLoading}
                  disabled={!userName}
                  colorScheme="teal"
                  size="lg"
                  px={8}
                >
                  Send
                </Button>
              </HStack>
            </VStack>
          </GridItem>
        </Grid>
      </Container>
    </>
  );
}

export default ChatPage;
