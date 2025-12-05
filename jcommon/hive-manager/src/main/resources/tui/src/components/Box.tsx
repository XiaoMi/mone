import React, { type ReactNode } from 'react'
import { Box as InkBox, type BoxProps } from 'ink'
import chalk from 'chalk'

interface CustomBoxProps extends BoxProps {
  children?: ReactNode
  title?: string
  border?: boolean
  borderColor?: 'cyan' | 'green' | 'yellow' | 'red' | 'blue' | 'magenta'
}

export const Box: React.FC<CustomBoxProps> = ({
  children,
  title,
  border = true,
  borderColor = 'cyan',
  ...props
}) => {
  const colorMap = {
    cyan: chalk.cyan,
    green: chalk.green,
    yellow: chalk.yellow,
    red: chalk.red,
    blue: chalk.blue,
    magenta: chalk.magenta,
  }

  const colorFn = colorMap[borderColor]

  if (!border) {
    return <InkBox {...props}>{children}</InkBox>
  }

  return (
    <InkBox flexDirection="column" {...props}>
      {title && (
        <InkBox>
          {colorFn(`╔═══ ${title} ${'═'.repeat(Math.max(0, 60 - title.length))}╗`)}
        </InkBox>
      )}
      {!title && (
        <InkBox>
          {colorFn('╔' + '═'.repeat(68) + '╗')}
        </InkBox>
      )}
      <InkBox>
        {colorFn('║')}
        <InkBox paddingX={1} flexGrow={1}>
          {children}
        </InkBox>
        {colorFn('║')}
      </InkBox>
      <InkBox>
        {colorFn('╚' + '═'.repeat(68) + '╝')}
      </InkBox>
    </InkBox>
  )
}
