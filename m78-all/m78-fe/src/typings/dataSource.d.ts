declare namespace DataSource {
  interface DB {
    id: string
    host: string
    port: string
    database: string
    user: string
    pwd: string
    jdbcUrl: string
    cluster: string | null
    kerberos: string | null
    queue: string | null
    userName: string
    type: string
    customKnowledge: string
    createTime: string
    updateTime: string
  }

  interface Table {
    tableName: string
    columnInfoList: string | null
    type: string | null
    source: string | null
    pkName: string
  }

  interface Tree extends DB {
    id: string
    name: string
  }

  interface HistoryChat {
    id: string
    content: string
    mappingContent: string
  }

  interface ChatItem {
    hideMore: boolean
    error: boolean
    dateTime: string
    indexKey: string
    text: string
    loading: boolean
    inversion: boolean
  }
}
