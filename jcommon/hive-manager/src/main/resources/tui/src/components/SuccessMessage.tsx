import React from 'react'
import { Box, Text } from 'ink'
import chalk from 'chalk'

interface SuccessMessageProps {
  message: string
}

export const SuccessMessage: React.FC<SuccessMessageProps> = ({ message }) => {
  return (
    <Box
      borderStyle="round"
      borderColor="green"
      padding={1}
      flexDirection="column"
    >
      <Text color="green" bold>
        {chalk.green('✓ Success')}
      </Text>
      <Text>{message}</Text>
    </Box>
  )
}
