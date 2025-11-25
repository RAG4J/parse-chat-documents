import { Box, Flex, Heading, Spacer } from '@chakra-ui/react';

function Navbar() {
  return (
    <Box bg="teal.500" px={4} py={3} color="white">
      <Flex align="center">
        <Heading size="md">Spring AI Docling</Heading>
        <Spacer />
      </Flex>
    </Box>
  );
}

export default Navbar;
