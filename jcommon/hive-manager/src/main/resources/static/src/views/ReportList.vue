<template>
    <div class="report-list-container">
      <div class="cyber-grid"></div>
      <div class="circuit-point point-1"></div>
      <div class="circuit-point point-2"></div>
      
      <div class="dashboard-header">
        <div class="search-form">
          <el-input
            v-model="searchForm.appName"
            placeholder="请输入应用名称"
            clearable
            @clear="handleSearch"
            @keyup.enter="handleSearch"
            class="search-input"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button class="query-btn" @click="handleSearch">查询</el-button>
          <el-button class="reset-btn" @click="handleReset">重置</el-button>
        </div>
      </div>
  
      <div class="table-container">
        <div class="report-table">
          <div v-if="loading" class="loading-container">
            <div class="loading-spinner"></div>
            <span>加载中...</span>
          </div>
          <TransitionGroup name="list" tag="div" v-else>
            <div v-if="reportList.length === 0" :key="'empty'" class="empty-state">
              <div class="empty-content">
                <span class="empty-text">暂无调用记录</span>
              </div>
            </div>
  
            <div v-for="report in reportList"
                 :key="`${report.createdAt}-${report.methodName}`"
                 class="report-card"
                 @click="handleShowDetail(report)">
              <div class="report-info">
                <div class="report-avatar">
                  <div class="report-logo-placeholder">
                    {{ report.appName?.charAt(0)?.toUpperCase() || 'R' }}
                  </div>
                </div>
                <div class="report-details">
                  <h4>{{ report.appName || '-' }}</h4>
                  <p class="method-info">
                    <span class="label">方法:</span>
                    <span>{{ report.className }}.{{ report.methodName }}</span>
                  </p>
                  <p class="business-info">
                    <span class="label">业务:</span>
                    <span>{{ report.businessName || '-' }}</span>
                  </p>
                </div>
              </div>
  
              <div class="report-status">
                <span class="badge" :class="report.success ? 'success' : 'failed'">
                  {{ report.success ? '成功' : '失败' }}
                </span>
              </div>
  
              <div class="report-meta">
                <div class="meta-item">
                  <span class="meta-label">执行时间:</span>
                  <span class="meta-value">{{ report.executionTime }}ms</span>
                </div>
                <div class="meta-item">
                  <span class="meta-label">调用方式:</span>
                  <span class="meta-value">{{ getInvokeWayText(report.invokeWay) }}</span>
                </div>
                <div class="meta-item">
                  <span class="meta-label">主机:</span>
                  <span class="meta-value">{{ report.host || '-' }}</span>
                </div>
              </div>
  
              <div class="report-time">
                <time>{{ formatDate(report.createdAt) }}</time>
              </div>
            </div>
          </TransitionGroup>
        </div>
      </div>
  
      <!-- 分页 -->
      <div class="pagination-container" v-if="!loading && reportList.length > 0">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
  
      <!-- 详情抽屉 -->
      <el-drawer
        v-model="drawerVisible"
        title="调用记录详情"
        :size="600"
        direction="rtl"
      >
        <div v-if="selectedReport" class="report-detail">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="应用名称">
              {{ selectedReport.appName || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="业务名称">
              {{ selectedReport.businessName || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="类名">
              {{ selectedReport.className || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="方法名">
              {{ selectedReport.methodName || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="执行状态">
              <el-tag :type="selectedReport.success ? 'success' : 'danger'">
                {{ selectedReport.success ? '成功' : '失败' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="执行时间">
              {{ selectedReport.executionTime }}ms
            </el-descriptions-item>
            <el-descriptions-item label="调用方式">
              {{ getInvokeWayText(selectedReport.invokeWay) }}
            </el-descriptions-item>
            <el-descriptions-item label="类型">
              {{ getTypeText(selectedReport.type) }}
            </el-descriptions-item>
            <el-descriptions-item label="主机">
              {{ selectedReport.host || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">
              {{ formatDate(selectedReport.createdAt) }}
            </el-descriptions-item>
            <el-descriptions-item label="描述">
              {{ selectedReport.description || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="输入参数">
              <pre class="params-pre">{{ formatParams(selectedReport.inputParams) }}</pre>
            </el-descriptions-item>
            <el-descriptions-item label="错误信息" v-if="selectedReport.errorMessage">
              <el-alert
                :title="selectedReport.errorMessage"
                type="error"
                :closable="false"
              />
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </el-drawer>
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref, onMounted } from 'vue'
  import { ElMessage } from 'element-plus'
  import { Search } from '@element-plus/icons-vue'
  import { getReportList } from '@/api/report'
  import type { IReportListItem } from '@/api/report'
  import { useTheme } from '@/styles/theme/useTheme'
  
  const reportList = ref<IReportListItem[]>([])
  const loading = ref(false)
  const drawerVisible = ref(false)
  const selectedReport = ref<IReportListItem | null>(null)
  
  const searchForm = ref({
    appName: ''
  })
  
  const pagination = ref({
    page: 1,
    pageSize: 20,
    total: 0
  })
  
  // 获取主题
  const { currentTheme } = useTheme()
  
  // 获取调用记录列表
  const fetchReportList = async () => {
    loading.value = true
    try {
      const response = await getReportList({
        appName: searchForm.value.appName || '',
        page: pagination.value.page,
        pageSize: pagination.value.pageSize
      })
      if (response.data.code === 200) {
        const data = response.data.data
        // 处理不同的响应结构
        if (Array.isArray(data)) {
          reportList.value = data
          // 如果返回的数据长度等于 pageSize，可能还有更多数据
          // 这里使用一个较大的总数来显示分页（实际总数需要后端返回）
          if (data.length === pagination.value.pageSize) {
            pagination.value.total = pagination.value.page * pagination.value.pageSize + 1
          } else {
            pagination.value.total = (pagination.value.page - 1) * pagination.value.pageSize + data.length
          }
        } else if (data && typeof data === 'object' && 'list' in data) {
          // 如果返回的是 { list: [], total: number } 结构
          reportList.value = (data as any).list || []
          pagination.value.total = (data as any).total || 0
        } else {
          reportList.value = []
          pagination.value.total = 0
        }
      } else {
        ElMessage.error(response.data.message || '获取调用记录列表失败')
        reportList.value = []
        pagination.value.total = 0
      }
    } catch (error: any) {
      ElMessage.error(error.message || '获取调用记录列表失败')
      reportList.value = []
      pagination.value.total = 0
    } finally {
      loading.value = false
    }
  }
  
  const handleSearch = () => {
    pagination.value.page = 1
    fetchReportList()
  }
  
  const handleReset = () => {
    searchForm.value.appName = ''
    pagination.value.page = 1
    fetchReportList()
  }
  
  const handleSizeChange = (size: number) => {
    pagination.value.pageSize = size
    pagination.value.page = 1
    fetchReportList()
  }
  
  const handlePageChange = (page: number) => {
    pagination.value.page = page
    fetchReportList()
  }
  
  const handleShowDetail = (report: IReportListItem) => {
    selectedReport.value = report
    drawerVisible.value = true
  }
  
  const formatDate = (timestamp: number) => {
    if (!timestamp) return '-'
    return new Date(timestamp).toLocaleString('zh-CN')
  }
  
  const getInvokeWayText = (way: number) => {
    const wayMap: Record<number, string> = {
      1: '页面',
      2: '接口',
      3: '系统内部',
      4: '调试等'
    }
    return wayMap[way] || `方式${way}`
  }
  
  const getTypeText = (type: number) => {
    const typeMap: Record<number, string> = {
      1: 'Agent',
      2: 'Mcp',
      3: '其他',
    }
    return typeMap[type] || '其他'
  }
  
  const formatParams = (params: string) => {
    if (!params) return '-'
    try {
      const parsed = JSON.parse(params)
      return JSON.stringify(parsed, null, 2)
    } catch {
      return params
    }
  }
  
  onMounted(() => {
    fetchReportList()
  })
  </script>
  <style>
  .report-list-container .el-drawer__header{
      margin-bottom: 0;
      padding-bottom: 0 !important;
  }
  </style>
  
  <style scoped>
  .report-list-container {
    width: 100%;
    min-height: 100vh;
    background: var(--el-color-chat-background);
    color: var(--el-color-chat-text);
    padding: 0 20px 12px;
    position: relative;
    overflow: hidden;
  }
  
  
  .cyber-grid {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-image:
      linear-gradient(var(--el-color-chat-grid-color) 1px, transparent 1px),
      linear-gradient(90deg, var(--el-color-chat-grid-color) 1px, transparent 1px);
    background-size: 40px 40px;
    animation: gridMove 20s linear infinite;
    transform-origin: center;
    opacity: 0.3;
    pointer-events: none;
  }
  
  @keyframes gridMove {
    0% {
      transform: perspective(500px) rotateX(60deg) translateY(0);
    }
    100% {
      transform: perspective(500px) rotateX(60deg) translateY(40px);
    }
  }
  
  .circuit-point {
    position: absolute;
    width: 6px;
    height: 6px;
    background: var(--el-color-chat-link-color);
    border-radius: 50%;
    filter: blur(1px);
    box-shadow:
      0 0 10px var(--el-color-chat-link-color),
      0 0 20px var(--el-color-chat-link-color),
      0 0 30px var(--el-color-chat-link-color-light);
    opacity: 0;
    z-index: 0;
  }
  
  .circuit-point::before {
    content: '';
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 12px;
    height: 12px;
    background: var(--el-color-chat-link-color-light);
    border-radius: 50%;
    filter: blur(2px);
  }
  
  .circuit-point::after {
    content: '';
    position: absolute;
    top: 50%;
    right: 50%;
    width: 40px;
    height: 2px;
    background: linear-gradient(
      270deg,
      var(--el-color-chat-link-color),
      var(--el-color-chat-link-color-light)
    );
    transform-origin: right center;
    transform: translateY(-50%);
    filter: blur(1px);
  }
  
  .point-1 {
    bottom: 40px;
    left: 40px;
    animation: movePoint1 15s linear infinite;
  }
  
  .point-1::after {
    animation: tailRotate1 15s linear infinite;
  }
  
  .point-2 {
    bottom: 40px;
    right: 40px;
    animation: movePoint2 15s linear infinite;
  }
  
  .point-2::after {
    animation: tailRotate2 15s linear infinite;
  }
  
  @keyframes movePoint1 {
    0% {
      transform: translate(0, 0);
      opacity: 0;
    }
    5% {
      opacity: 1;
    }
    15% {
      transform: translate(0, -160px);
    }
    30% {
      transform: translate(120px, -160px);
    }
    45% {
      transform: translate(120px, -320px);
    }
    60% {
      transform: translate(240px, -320px);
    }
    75% {
      transform: translate(240px, -480px);
    }
    90% {
      transform: translate(360px, -480px);
      opacity: 1;
    }
    95% {
      opacity: 0;
    }
    100% {
      transform: translate(360px, -480px);
      opacity: 0;
    }
  }
  
  @keyframes movePoint2 {
    0% {
      transform: translate(0, 0);
      opacity: 0;
    }
    5% {
      opacity: 1;
    }
    15% {
      transform: translate(0, -200px);
    }
    30% {
      transform: translate(-160px, -200px);
    }
    45% {
      transform: translate(-160px, -360px);
    }
    60% {
      transform: translate(-280px, -360px);
    }
    75% {
      transform: translate(-280px, -520px);
    }
    90% {
      transform: translate(-400px, -520px);
      opacity: 1;
    }
    95% {
      opacity: 0;
    }
    100% {
      transform: translate(-400px, -520px);
      opacity: 0;
    }
  }
  
  @keyframes tailRotate1 {
    0%, 15% { transform: translateY(-50%) rotate(-90deg); }
    15.1%, 30% { transform: translateY(-50%) rotate(0deg); }
    30.1%, 45% { transform: translateY(-50%) rotate(-90deg); }
    45.1%, 60% { transform: translateY(-50%) rotate(0deg); }
    60.1%, 75% { transform: translateY(-50%) rotate(-90deg); }
    75.1%, 90% { transform: translateY(-50%) rotate(0deg); }
  }
  
  @keyframes tailRotate2 {
    0%, 15% { transform: translateY(-50%) rotate(-90deg); }
    15.1%, 30% { transform: translateY(-50%) rotate(180deg); }
    30.1%, 45% { transform: translateY(-50%) rotate(-90deg); }
    45.1%, 60% { transform: translateY(-50%) rotate(180deg); }
    60.1%, 75% { transform: translateY(-50%) rotate(-90deg); }
    75.1%, 90% { transform: translateY(-50%) rotate(180deg); }
  }
  
  .dashboard-header {
    display: flex;
    justify-content: flex-start;
    align-items: center;
    margin-bottom: 12px;
    position: relative;
    z-index: 1;
  }
  
  .search-form {
    display: flex;
    align-items: center;
    gap: 12px;
  }
  
  .search-input {
    width: 300px;
  }
  
  .search-input :deep(.el-input__wrapper) {
    background-color: var(--el-color-chat-window-background);
    border: 1px solid var(--el-color-chat-link-color);
    border-radius: 8px;
    box-shadow: none;
    transition: all 0.3s ease;
  }
  
  .search-input :deep(.el-input__wrapper:hover) {
    border-color: var(--el-color-chat-link-color);
    box-shadow: 0 0 0 2px var(--el-color-chat-link-color-light),
                0 0 15px var(--el-color-chat-link-color-light),
                0 0 30px var(--el-color-chat-link-color-light);
  }
  
  .search-input :deep(.el-input__wrapper.is-focus) {
    border-color: var(--el-color-chat-link-color);
    box-shadow: 0 0 0 2px var(--el-color-chat-link-color-light),
                0 0 15px var(--el-color-chat-link-color-light),
                0 0 30px var(--el-color-chat-link-color-light);
  }
  
  .search-input :deep(.el-input__inner) {
    color: var(--el-color-chat-text);
  }
  
  .search-input :deep(.el-input__prefix) {
    color: var(--el-color-chat-link-color);
  }
  
  .query-btn,
  .reset-btn {
    padding: 10px 20px;
    border-radius: 8px;
    font-weight: 500;
    transition: all 0.3s;
    border: 1px solid var(--el-color-chat-link-color);
  }
  
  .query-btn {
    background: var(--el-color-background-gradient);
    color: var(--el-color-white);
    border: none;
  }
  
  .query-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 0 20px var(--el-color-background-gradient);
  }
  
  .reset-btn {
    background: var(--el-color-chat-window-background);
    color: var(--el-color-chat-text);
  }
  
  .reset-btn:hover {
    background: var(--el-color-chat-link-color-light);
    color: var(--el-color-chat-link-color);
    border-color: var(--el-color-chat-link-color);
    box-shadow: 0 0 10px var(--el-color-chat-link-color-light);
  }
  
  .table-container {
    height: calc(100vh - 180px);
    overflow-y: auto;
    position: relative;
    z-index: 1;
  }
  
  .report-table {
    position: relative;
  }
  
  .loading-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 200px;
    color: var(--el-color-chat-link-color);
  }
  
  .loading-spinner {
    width: 40px;
    height: 40px;
    margin-bottom: 16px;
    border: 3px solid var(--el-color-chat-link-color-light);
    border-top: 3px solid var(--el-color-chat-link-color);
    border-radius: 50%;
    animation: spin 1s linear infinite;
  }
  
  @keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
  }
  
  .empty-state {
    width: 240px;
    height: 80px;
    margin: 120px auto 0;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .empty-state .empty-text {
    display: inline-block;
    width: 100%;
    text-align: center;
    color: var(--el-color-chat-text-secondary);
  }
  
  .report-card {
    background: var(--el-color-chat-window-background);
    border: 1px solid var(--el-color-chat-link-color);
    border-radius: 12px;
    padding: 20px;
    margin-bottom: 20px;
    display: grid;
    grid-template-columns: 2fr 1fr 1.5fr 1fr;
    gap: 20px;
    align-items: center;
    transition: all 0.3s;
    cursor: pointer;
    position: relative;
    z-index: 1;
    min-width: 0;
  }
  
  .report-card > * {
    min-width: 0;
  }
  
  .report-card:hover {
    transform: translateX(4px);
    border-color: var(--el-color-chat-link-color);
    box-shadow: 0 0 20px var(--el-color-chat-link-color-light);
  }
  
  .report-info {
    display: flex;
    align-items: center;
    gap: 15px;
    min-width: 0;
    flex: 1;
  }
  
  .report-avatar {
    width: 50px;
    height: 50px;
    border-radius: 10px;
    overflow: hidden;
    background: var(--el-color-background-gradient);
    flex-shrink: 0;
  }
  
  .report-logo-placeholder {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24px;
    font-weight: bold;
    color: #0d1117;
  }
  
  .report-details {
    min-width: 0;
    flex: 1;
  }
  
  .report-details h4 {
    font-size: 18px;
    margin-bottom: 8px;
    color: var(--el-color-chat-link-color);
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  
  .report-details p {
    color: var(--el-color-chat-text-secondary);
    font-size: 14px;
    margin: 4px 0;
    display: flex;
    align-items: center;
    min-width: 0;
  }
  
  .report-details .label {
    color: var(--el-color-chat-text-secondary);
    margin-right: 6px;
    flex-shrink: 0;
  }
  
  .report-details p > span:not(.label) {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    min-width: 0;
    flex: 1;
  }
  
  .report-status {
    display: flex;
    justify-content: center;
  }
  
  .badge {
    padding: 6px 12px;
    border-radius: 4px;
    font-size: 14px;
    display: inline-block;
    font-weight: 500;
  }
  
  .badge.success {
    background: var(--el-color-chat-link-color-light);
    color: var(--el-color-success);
    border: 1px solid var(--el-color-success);
  }
  
  .badge.failed {
    background: var(--el-color-danger-light);
    color: var(--el-color-danger);
    border: 1px solid var(--el-color-danger);
  }
  
  .report-meta {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }
  
  .meta-item {
    display: flex;
    align-items: center;
    font-size: 13px;
    min-width: 0;
  }
  
  .meta-label {
    color: var(--el-color-chat-text-secondary);
    margin-right: 8px;
    min-width: 70px;
    flex-shrink: 0;
  }
  
  .meta-value {
    color: var(--el-color-chat-text);
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    min-width: 0;
    flex: 1;
  }
  
  .report-time {
    text-align: right;
    color: var(--el-color-chat-text-secondary);
    font-size: 13px;
    min-width: 0;
  }
  
  .report-time time {
    display: inline-block;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    max-width: 100%;
  }
  
  .pagination-container {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
    position: relative;
    z-index: 1;
  }
  
  /* 列表过渡动画 */
  .list-enter-active,
  .list-leave-active {
    transition: all 0.3s ease;
  }
  
  .list-enter-from {
    opacity: 0;
    transform: translateY(-10px);
  }
  
  .list-leave-to {
    opacity: 0;
    transform: translateY(10px);
  }
  
  /* 详情抽屉样式 */
  .report-detail {
    padding: 0;
  }
  
  .params-pre {
    background: var(--el-color-chat-window-background);
    border: 1px solid var(--el-color-chat-link-color-light);
    border-radius: 4px;
    padding: 12px;
    margin: 0;
    overflow-x: auto;
    font-size: 12px;
    color: var(--el-color-chat-text);
    max-height: 300px;
    overflow-y: auto;
  }
  
  /* Element Plus 组件样式覆盖 */
  :deep(.el-drawer) {
    background: var(--el-color-chat-window-background) !important;
    backdrop-filter: blur(20px);
    border-left: 1px solid var(--el-color-chat-link-color);
  }
  
  :deep(.el-drawer__header) {
    color: var(--el-color-chat-text);
    border-bottom: 1px solid var(--el-color-chat-link-color-light);
    padding: 20px;
    background-color: var(--el-color-chat-window-background) !important;
  }
  
  :deep(.el-drawer__title) {
    color: var(--el-color-chat-link-color);
    font-weight: 600;
  }
  
  :deep(.el-drawer__close-btn) {
    color: var(--el-color-chat-text-secondary);
  }
  
  :deep(.el-drawer__close-btn:hover) {
    color: var(--el-color-chat-link-color);
  }
  
  :deep(.el-drawer__body) {
    color: var(--el-color-chat-text);
    padding: 20px;
    background-color: var(--el-color-chat-window-background) !important;
  }
  
  :deep(.el-descriptions__label) {
    color: var(--el-color-chat-text-secondary);
    background-color: var(--el-color-chat-window-background) !important;
    font-weight: 500;
  }
  
  :deep(.el-descriptions__content) {
    color: var(--el-color-chat-text);
    background-color: var(--el-color-chat-window-background) !important;
  }
  
  :deep(.el-descriptions__table) {
    border-color: var(--el-color-chat-link-color-light);
    background-color: transparent;
  }
  
  :deep(.el-descriptions__table td),
  :deep(.el-descriptions__table th) {
    border-color: var(--el-color-chat-link-color-light);
    background-color: var(--el-color-chat-window-background) !important;
  }
  
  :deep(.el-descriptions__table .el-descriptions__cell) {
    padding: 12px;
    background-color: var(--el-color-chat-window-background) !important;
  }
  
  :deep(.el-descriptions__table .el-descriptions__label-cell) {
    background-color: var(--el-color-chat-window-background) !important;
  }
  
  :deep(.el-descriptions__table .el-descriptions__content-cell) {
    background-color: var(--el-color-chat-window-background) !important;
  }
  
  :deep(.el-button) {
    background-color: var(--el-color-chat-window-background);
    border-color: var(--el-color-chat-link-color-light);
    color: var(--el-color-chat-text);
  }
  
  :deep(.el-button:hover) {
    background-color: var(--el-color-chat-link-color-light);
    border-color: var(--el-color-chat-link-color);
    color: var(--el-color-chat-link-color);
  }
  
  :deep(.el-button.is-plain) {
    background-color: transparent;
  }
  
  :deep(.el-input__wrapper) {
    background-color: var(--el-color-chat-window-background);
    border-color: var(--el-color-chat-link-color-light);
  }
  
  :deep(.el-input__inner) {
    color: var(--el-color-chat-text);
  }
  
  :deep(.el-input__wrapper.is-focus) {
    border-color: var(--el-color-chat-link-color);
    box-shadow: 0 0 0 2px var(--el-color-chat-link-color-light);
  }
  
  :deep(.el-pagination) {
    color: var(--el-color-chat-text);
  }
  
  :deep(.el-pagination .el-pager li) {
    background-color: var(--el-color-chat-window-background);
    color: var(--el-color-chat-text);
    border-color: var(--el-color-chat-link-color-light);
  }
  
  :deep(.el-pagination .el-pager li:hover) {
    color: var(--el-color-chat-link-color);
  }
  
  :deep(.el-pagination .el-pager li.is-active) {
    background-color: var(--el-color-chat-link-color-light);
    color: var(--el-color-chat-link-color);
    border-color: var(--el-color-chat-link-color);
  }
  
  :deep(.el-pagination button) {
    background-color: var(--el-color-chat-window-background);
    color: var(--el-color-chat-text);
    border-color: var(--el-color-chat-link-color-light);
  }
  
  :deep(.el-pagination button:hover) {
    color: var(--el-color-chat-link-color);
  }
  
  :deep(.el-pagination .el-pagination__total),
  :deep(.el-pagination .el-pagination__jump) {
    color: var(--el-color-chat-text-secondary);
  }
  
  :deep(.el-pagination .el-select .el-input__wrapper) {
    background-color: var(--el-color-chat-window-background);
    border-color: var(--el-color-chat-link-color-light);
  }
  
  :deep(.el-pagination .el-select .el-input__inner) {
    color: var(--el-color-chat-text);
  }
  
  /* 响应式设计 */
  @media (max-width: 1024px) {
    .report-card {
      grid-template-columns: 1fr;
      gap: 15px;
    }
    
    .report-meta {
      flex-direction: row;
      flex-wrap: wrap;
    }
    
    .report-time {
      text-align: left;
    }
  }
  </style>
  