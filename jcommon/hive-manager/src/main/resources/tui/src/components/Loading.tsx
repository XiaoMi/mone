import React from 'react'
import { Box, Text } from 'ink'
import Spinner from 'ink-spinner'
import chalk from 'chalk'

interface LoadingProps {
  text?: string
}

export const Loading: React.FC<LoadingProps> = ({ text = 'Loading...' }) => {
  return (
    <Box justifyContent="center" alignItems="center" minHeight={10}>
      <Text>
        <Text color="cyan">
          <Spinner type="dots" />
        </Text>
        {' '}
        {chalk.cyan(text)}
      </Text>
    </Box>
  )
}
