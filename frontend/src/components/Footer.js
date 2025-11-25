import { Box, Text } from '@chakra-ui/react';

function Footer() {
  return (
    <Box bg="gray.100" px={4} py={3} textAlign="center" borderTop="1px" borderColor="gray.200">
      <Text fontSize="sm" color="gray.600">
        © {new Date().getFullYear()} Spring AI Docling. Powered by Chakra UI.
      </Text>
    </Box>
  );
}

export default Footer;
