<template>
    <div class="skill-list">
      <div class="skill-header">
        <h3 class="skills-title">技能列表</h3>
        <el-button 
          type="primary" 
          class="create-skill-btn"
          @click="handleCreateSkill"
          size="small"
        >
          创建技能
        </el-button>
      </div>
      
      <el-table 
        v-loading="loading" 
        :data="skills" 
        style="width: 100%"
        row-key="id"
        :expand-row-keys="expandedRows"
      >
        <el-table-column type="expand">
          <template #default="{ row }">
            <div class="skill-detail">
                <dl>
                    <dt>标签:</dt>
                    <dd>{{ row.tags || '无' }}</dd>
                </dl>
                <dl>
                    <dt>示例:</dt>
                    <dd>{{ row.examples || '无' }}</dd>
                </dl>
                <dl>
                    <dt>输出格式:</dt>
                    <dd>{{ row.outputSchema || '无' }}</dd>
                </dl>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="创建时间">
          <template #default="{ row }">
            {{ formatDate(row.ctime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button-group>
              <el-button size="small" type="primary" text @click="handleEdit(row)">
                编辑
              </el-button>
              <el-button size="small" type="danger" text @click="handleDelete(row)">
                删除
              </el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>
  
    </div>
      <!-- 创建/编辑技能对话框 -->
    <el-dialog
    v-model="dialogVisible"
    :title="editingSkill ? '编辑技能' : '创建技能'"
    width="50%"
    append-to-body
    class="skill-dialog"
    >
    <el-form ref="formRef" :model="skillForm" label-width="80px">
        <el-form-item label="名称" prop="name">
        <el-input v-model="skillForm.name" placeholder="请输入技能名称" />
        </el-form-item>
        <!-- <el-form-item label="技能ID" prop="skillId">
        <el-input v-model="skillForm.skillId" />
        </el-form-item> -->
        <el-form-item label="描述" prop="description">
        <el-input v-model="skillForm.description" type="textarea" placeholder="请输入技能描述" />
        </el-form-item>
        <el-form-item label="标签" prop="tags">
        <el-input v-model="skillForm.tags" placeholder="请输入标签" />
        </el-form-item>
        <el-form-item label="示例" prop="examples">
        <el-input v-model="skillForm.examples" type="textarea" placeholder="请输入使用示例" />
        </el-form-item>
        <el-form-item label="输出格式" prop="outputSchema">
        <el-input v-model="skillForm.outputSchema" type="textarea" placeholder="请输入输出格式（JSON Schema）" />
        </el-form-item>
    </el-form>
    <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
    </template>
    </el-dialog>
  </template>
  
  <script setup lang="ts">
  import { ref, onMounted } from 'vue'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import {
    getAgentSkills,
    createSkill,
    updateSkill,
    deleteSkill,
    type Skill,
    type CreateSkillRequest
  } from '@/api/agent'
  
  const props = defineProps<{
    agentId: number
  }>()
  
  const loading = ref(false)
  const skills = ref<Skill[]>([])
  const dialogVisible = ref(false)
  const editingSkill = ref<Skill | null>(null)
  const skillForm = ref<CreateSkillRequest>({
    name: '',
    skillId: '',
    description: '',
    tags: '',
    examples: '',
    outputSchema: ''
  })
  const expandedRows = ref<string[]>([])
  
  const fetchSkills = async () => {
    loading.value = true
    try {
      const { data } = await getAgentSkills(props.agentId)
      skills.value = data.data || []
    } catch (error) {
      console.error('获取技能列表失败:', error)
      ElMessage.error('获取技能列表失败')
    } finally {
      loading.value = false
    }
  }
  
  const handleCreateSkill = () => {
    editingSkill.value = null
    skillForm.value = {
      name: '',
      skillId: '',
      description: '',
      tags: '',
      examples: '',
      outputSchema: ''
    }
    dialogVisible.value = true
  }
  
  const handleEdit = (skill: Skill) => {
    editingSkill.value = skill
    skillForm.value = {
      name: skill.name,
      skillId: skill.id.toString(),
      description: skill.description,
      tags: skill.tags || '',
      examples: skill.examples || '',
      outputSchema: skill.outputSchema || ''
    }
    dialogVisible.value = true
  }
  
  const handleDelete = async (skill: Skill) => {
    try {
      await ElMessageBox.confirm('确定要删除该技能吗？', '提示', {
        type: 'warning'
      })
      await deleteSkill(skill.id)
      ElMessage.success('删除成功')
      fetchSkills()
    } catch (error) {
      console.error('删除技能失败:', error)
      ElMessage.error('删除技能失败')
    }
  }
  
  const handleSubmit = async () => {
    try {
      if (editingSkill.value) {
        await updateSkill(editingSkill.value.id, skillForm.value)
        ElMessage.success('更新成功')
      } else {
        await createSkill(props.agentId, skillForm.value)
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      fetchSkills()
    } catch (error) {
      console.error('操作失败:', error)
      ElMessage.error('操作失败')
    }
  }
  
  const formatDate = (date: string) => {
    return new Date(date).toLocaleString()
  }
  
  onMounted(() => {
    fetchSkills()
  })
  </script>
  
  <style scoped>
  
  .skill-header {
    display: flex;
    justify-content: space-between;
  }
  
  :deep(.el-table) {
    background: transparent;
    --el-table-tr-bg-color: transparent;
    --el-table-border-color: rgba(49, 232, 249, 0.2);
    --el-table-header-bg-color: rgba(48, 54, 61, 0.3);
    --el-table-header-text-color: #31e8f9;
    --el-table-text-color: #ffffff;
    --el-table-row-hover-bg-color: transparent;
  }
  
  .create-skill-btn {
    background: transparent;
    border: 1px solid #31e8f9;
    color: #31e8f9;
    transition: all 0.3s ease;
    
    &:hover {
      background: rgba(49, 232, 249, 0.1);
      border-color: rgba(49, 232, 249, 0.8);
      box-shadow: 0 0 15px rgba(49, 232, 249, 0.2);
      transform: translateY(-2px);
    }
  }
  </style>