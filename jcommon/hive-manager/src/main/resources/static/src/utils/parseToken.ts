interface TokenTime {
  expiresAt: Date | null;
  issuedAt: Date | null;
}

/**
 * 解析JWT token中的时间信息
 * @param token JWT token字符串
 * @returns TokenTime 包含过期时间和签发时间的对象
 */
export function parseTokenTime(): TokenTime {
    const token = localStorage.getItem('token')
    if (!token) {
        return {
            expiresAt: null,
            issuedAt: null
        }
    }
  try {
    // 将token分割并获取payload部分
    const payload = token.split('.')[1];
    // 解码payload
    const decoded = JSON.parse(atob(payload));
    
    return {
      // exp是过期时间（以秒为单位的时间戳）
      expiresAt: decoded.exp ? new Date(decoded.exp * 1000) : null,
      // iat是签发时间（以秒为单位的时间戳）
      issuedAt: decoded.iat ? new Date(decoded.iat * 1000) : null
    };
  } catch (error) {
    console.error('解析token时间失败:', error);
    return {
      expiresAt: null,
      issuedAt: null
    };
  }
}

/**
 * 检查token是否过期
 * @param token JWT token字符串
 * @returns boolean token是否已过期
 */
export function isTokenExpired(): boolean {
  const { expiresAt } = parseTokenTime();
  if (!expiresAt) return true;
  
  return expiresAt.getTime() < Date.now();
}
