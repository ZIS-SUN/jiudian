<template>
  <div class="announcements-page">
    <div class="page-header">
      <h2>公告管理</h2>
      <el-button type="primary" @click="showAddDialog">添加公告</el-button>
    </div>

    <!-- 搜索区域 -->
    <div class="search-bar">
      <el-select v-model="searchParams.type" placeholder="公告类型" clearable style="width: 150px">
        <el-option label="全部" value="" />
        <el-option label="通知" value="NOTICE" />
        <el-option label="优惠" value="PROMOTION" />
        <el-option label="维护" value="MAINTENANCE" />
        <el-option label="紧急" value="URGENT" />
      </el-select>
      <el-select v-model="searchParams.status" placeholder="状态" clearable style="width: 120px; margin-left: 10px">
        <el-option label="全部" value="" />
        <el-option label="启用" :value="1" />
        <el-option label="禁用" :value="0" />
      </el-select>
      <el-button @click="loadData" style="margin-left: 10px">搜索</el-button>
    </div>

    <!-- 表格 -->
    <el-table :data="tableData" style="width: 100%; margin-top: 20px" border>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="标题" min-width="200" />
      <el-table-column prop="type" label="类型" width="100">
        <template #default="{ row }">
          <el-tag :type="getTypeTag(row.type)">{{ getTypeText(row.type) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="priority" label="优先级" width="80">
        <template #default="{ row }">
          <el-tag v-if="row.priority === 1" type="danger">高</el-tag>
          <el-tag v-else-if="row.priority === 2" type="warning">中</el-tag>
          <el-tag v-else type="info">低</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="publisherName" label="发布者" width="100" />
      <el-table-column prop="createTime" label="创建时间" width="160" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="viewAnnouncement(row)">查看</el-button>
          <el-button link type="primary" size="small" @click="editAnnouncement(row)">编辑</el-button>
          <el-button link type="danger" size="small" @click="deleteAnnouncement(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="searchParams.page"
      v-model:page-size="searchParams.size"
      :total="total"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="loadData"
      @current-change="loadData"
      style="margin-top: 20px; justify-content: center"
    />

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="resetForm"
    >
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="formData.content"
            type="textarea"
            :rows="6"
            placeholder="请输入公告内容"
          />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="formData.type" placeholder="请选择公告类型">
            <el-option label="通知" value="NOTICE" />
            <el-option label="优惠" value="PROMOTION" />
            <el-option label="维护" value="MAINTENANCE" />
            <el-option label="紧急" value="URGENT" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-radio-group v-model="formData.priority">
            <el-radio :label="1">高</el-radio>
            <el-radio :label="2">中</el-radio>
            <el-radio :label="3">低</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="formData.status"
            :active-value="1"
            :inactive-value="0"
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 查看对话框 -->
    <el-dialog v-model="viewDialogVisible" title="查看公告" width="600px">
      <div class="view-content">
        <div class="view-item">
          <span class="view-label">标题：</span>
          <span class="view-value">{{ viewData.title }}</span>
        </div>
        <div class="view-item">
          <span class="view-label">类型：</span>
          <el-tag :type="getTypeTag(viewData.type)">{{ getTypeText(viewData.type) }}</el-tag>
        </div>
        <div class="view-item">
          <span class="view-label">优先级：</span>
          <el-tag v-if="viewData.priority === 1" type="danger">高</el-tag>
          <el-tag v-else-if="viewData.priority === 2" type="warning">中</el-tag>
          <el-tag v-else type="info">低</el-tag>
        </div>
        <div class="view-item">
          <span class="view-label">状态：</span>
          <el-tag :type="viewData.status === 1 ? 'success' : 'info'">
            {{ viewData.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </div>
        <div class="view-item">
          <span class="view-label">发布者：</span>
          <span class="view-value">{{ viewData.publisherName }}</span>
        </div>
        <div class="view-item">
          <span class="view-label">创建时间：</span>
          <span class="view-value">{{ viewData.createTime }}</span>
        </div>
        <div class="view-item">
          <span class="view-label">内容：</span>
          <div class="view-content-text">{{ viewData.content }}</div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getAnnouncementList,
  createAnnouncement,
  updateAnnouncement,
  deleteAnnouncement as deleteAnnouncementApi
} from '@/api/announcements'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 搜索参数
const searchParams = reactive({
  page: 1,
  size: 10,
  type: '',
  status: ''
})

// 表格数据
const tableData = ref([])
const total = ref(0)

// 对话框
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)

// 表单数据
const formData = reactive({
  id: null,
  title: '',
  content: '',
  type: 'NOTICE',
  priority: 2,
  status: 1
})

const viewData = ref({})

// 表单引用
const formRef = ref(null)

// 表单验证规则
const formRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }]
}

// 加载数据
const loadData = async () => {
  try {
    const res = await getAnnouncementList(searchParams)
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

// 显示添加对话框
const showAddDialog = () => {
  isEdit.value = false
  dialogTitle.value = '添加公告'
  dialogVisible.value = true
}

// 查看公告
const viewAnnouncement = (row) => {
  viewData.value = { ...row }
  viewDialogVisible.value = true
}

// 编辑公告
const editAnnouncement = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑公告'
  Object.assign(formData, row)
  dialogVisible.value = true
}

// 删除公告
const deleteAnnouncement = (row) => {
  ElMessageBox.confirm('确定要删除这条公告吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteAnnouncementApi(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const data = { ...formData }
        // 添加发布者信息
        if (!isEdit.value) {
          data.publisherId = userStore.userInfo?.id
          data.publisherName = userStore.userInfo?.realName || userStore.userInfo?.username
        }

        if (isEdit.value) {
          await updateAnnouncement(formData.id, data)
          ElMessage.success('更新成功')
        } else {
          await createAnnouncement(data)
          ElMessage.success('创建成功')
        }

        dialogVisible.value = false
        loadData()
      } catch (error) {
        ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  Object.assign(formData, {
    id: null,
    title: '',
    content: '',
    type: 'NOTICE',
    priority: 2,
    status: 1
  })
}

// 获取类型标签颜色
const getTypeTag = (type) => {
  const map = {
    NOTICE: '',
    PROMOTION: 'success',
    MAINTENANCE: 'warning',
    URGENT: 'danger'
  }
  return map[type] || ''
}

// 获取类型文本
const getTypeText = (type) => {
  const map = {
    NOTICE: '通知',
    PROMOTION: '优惠',
    MAINTENANCE: '维护',
    URGENT: '紧急'
  }
  return map[type] || type
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.announcements-page {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
}

.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.view-content {
  padding: 10px;
}

.view-item {
  margin-bottom: 15px;
}

.view-label {
  font-weight: bold;
  color: #606266;
  min-width: 80px;
  display: inline-block;
}

.view-value {
  color: #303133;
}

.view-content-text {
  margin-top: 10px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  white-space: pre-wrap;
  line-height: 1.6;
}
</style>