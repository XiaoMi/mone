import React from 'react'
import { Box, Text } from 'ink'
import chalk from 'chalk'

interface ErrorMessageProps {
  message: string
}

export const ErrorMessage: React.FC<ErrorMessageProps> = ({ message }) => {
  return (
    <Box
      borderStyle="round"
      borderColor="red"
      padding={1}
      flexDirection="column"
    >
      <Text color="red" bold>
        {chalk.red('✗ Error')}
      </Text>
      <Text>{message}</Text>
    </Box>
  )
}
