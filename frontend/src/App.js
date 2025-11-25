import { ChakraProvider, Box, Flex } from '@chakra-ui/react';
import Navbar from './components/Navbar';
import Footer from './components/Footer';
import ChatPage from './components/ChatPage';

function App() {
  return (
    <ChakraProvider>
      <Flex direction="column" minH="100vh">
        <Navbar />
        <Box flex="1" bg="gray.50">
          <ChatPage />
        </Box>
        <Footer />
      </Flex>
    </ChakraProvider>
  );
}

export default App;
