<template>
  <div class="customers">
    <div class="page-header">
      <h2>客户管理</h2>
      <el-button type="primary" @click="showAddDialog = true">
        <el-icon><Plus /></el-icon>
        添加客户
      </el-button>
    </div>
    
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="客户姓名">
          <el-input v-model="searchForm.name" placeholder="请输入客户姓名" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="searchForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input v-model="searchForm.idCard" placeholder="请输入身份证号" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 客户列表 -->
    <el-card>
      <el-table :data="customers" :loading="loading" style="width: 100%">
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="gender" label="性别">
          <template #default="scope">
            {{ scope.row.gender }}
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="联系电话" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="idCard" label="身份证号" />
        <el-table-column prop="address" label="地址" />
        <el-table-column prop="registrationDate" label="注册时间" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="viewDetail(scope.row)">详情</el-button>
            <el-button size="small" @click="editCustomer(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteCustomer(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 添加客户对话框 -->
    <el-dialog 
      v-model="showAddDialog" 
      title="添加客户" 
      width="600px"
      :before-close="handleDialogClose"
    >
      <el-form 
        ref="addFormRef" 
        :model="addForm" 
        :rules="formRules" 
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="addForm.name" placeholder="请输入客户姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="addForm.gender" placeholder="请选择性别">
                <el-option label="男" value="男" />
                <el-option label="女" value="女" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="addForm.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="addForm.email" placeholder="请输入邮箱地址" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="addForm.idCard" placeholder="请输入身份证号" />
        </el-form-item>
        
        <el-form-item label="地址">
          <el-input v-model="addForm.address" placeholder="请输入地址" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleDialogClose">取消</el-button>
          <el-button type="primary" @click="handleAddCustomer" :loading="submitLoading">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 编辑客户对话框 -->
    <el-dialog 
      v-model="showEditDialog" 
      title="编辑客户" 
      width="600px"
      :before-close="handleEditDialogClose"
    >
      <el-form 
        ref="editFormRef" 
        :model="editForm" 
        :rules="formRules" 
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="editForm.name" placeholder="请输入客户姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="editForm.gender" placeholder="请选择性别">
                <el-option label="男" value="男" />
                <el-option label="女" value="女" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="editForm.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="editForm.email" placeholder="请输入邮箱地址" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="editForm.idCard" placeholder="请输入身份证号" />
        </el-form-item>
        
        <el-form-item label="地址">
          <el-input v-model="editForm.address" placeholder="请输入地址" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleEditDialogClose">取消</el-button>
          <el-button type="primary" @click="handleUpdateCustomer" :loading="submitLoading">
            保存
          </el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 客户详情对话框 -->
    <el-dialog 
      v-model="showDetailDialog" 
      title="客户详情" 
      width="500px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="姓名">{{ currentCustomer.name }}</el-descriptions-item>
        <el-descriptions-item label="性别">
          {{ currentCustomer.gender }}
        </el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentCustomer.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ currentCustomer.email }}</el-descriptions-item>
        <el-descriptions-item label="身份证号" :span="2">{{ currentCustomer.idCard }}</el-descriptions-item>
        <el-descriptions-item label="地址" :span="2">{{ currentCustomer.address }}</el-descriptions-item>
        <el-descriptions-item label="注册时间" :span="2">{{ currentCustomer.registrationDate }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getCustomers, 
  createCustomer, 
  updateCustomer, 
  getCustomerById,
  deleteCustomer as deleteCustomerApi 
} from '@/api/customers'
import { genderUtils } from '@/utils/enums'

const showAddDialog = ref(false)
const showEditDialog = ref(false)
const showDetailDialog = ref(false)
const loading = ref(false)
const submitLoading = ref(false)
const customers = ref([])
const currentCustomer = ref({})
const addFormRef = ref()
const editFormRef = ref()

const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const searchForm = reactive({
  name: '',
  phone: '',
  idCard: ''
})

const addForm = reactive({
  name: '',
  gender: '',
  phone: '',
  email: '',
  idCard: '',
  address: ''
})

const editForm = reactive({
  id: '',
  name: '',
  gender: '',
  phone: '',
  email: '',
  idCard: '',
  address: ''
})

const formRules = {
  name: [
    { required: true, message: '请输入客户姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '请输入正确的身份证号', trigger: 'blur' }
  ]
}

// 获取客户列表
const fetchCustomers = async () => {
  try {
    loading.value = true
    const response = await getCustomers({
      current: pagination.value.current,
      size: pagination.value.size,
      ...searchForm
    })
    
    // 转换性别为中文显示
    customers.value = response.data.records.map(customer => ({
      ...customer,
      gender: genderUtils.toDisplay(customer.gender)
    }))
    pagination.value.total = response.data.total
  } catch (error) {
    console.error('获取客户列表失败:', error)
    ElMessage.error('获取客户列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.value.current = 1
  fetchCustomers()
}

const resetSearch = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
  pagination.value.current = 1
  fetchCustomers()
}

const viewDetail = async (customer) => {
  try {
    const response = await getCustomerById(customer.id)
    currentCustomer.value = {
      ...response.data,
      gender: genderUtils.toDisplay(response.data.gender)
    }
    showDetailDialog.value = true
  } catch (error) {
    console.error('获取客户详情失败:', error)
    ElMessage.error('获取客户详情失败')
  }
}

const editCustomer = async (customer) => {
  try {
    const response = await getCustomerById(customer.id)
    const customerData = response.data
    
    // 填充编辑表单
    Object.keys(editForm).forEach(key => {
      if (customerData.hasOwnProperty(key)) {
        if (key === 'gender') {
          editForm[key] = genderUtils.toDisplay(customerData[key])
        } else {
          editForm[key] = customerData[key]
        }
      }
    })
    
    showEditDialog.value = true
  } catch (error) {
    console.error('获取客户信息失败:', error)
    ElMessage.error('获取客户信息失败')
  }
}

// 添加客户
const handleAddCustomer = async () => {
  try {
    await addFormRef.value.validate()
    submitLoading.value = true
    
    const customerData = {
      ...addForm,
      gender: genderUtils.toApi(addForm.gender)
    }
    
    await createCustomer(customerData)
    ElMessage.success('添加客户成功')
    showAddDialog.value = false
    resetAddForm()
    fetchCustomers()
  } catch (error) {
    if (error !== false) { // 表单验证失败时不显示错误消息
      console.error('添加客户失败:', error)
      ElMessage.error('添加客户失败')
    }
  } finally {
    submitLoading.value = false
  }
}

// 更新客户
const handleUpdateCustomer = async () => {
  try {
    await editFormRef.value.validate()
    submitLoading.value = true
    
    const { id, ...updateData } = editForm
    updateData.gender = genderUtils.toApi(updateData.gender)
    
    await updateCustomer(id, updateData)
    ElMessage.success('更新客户成功')
    showEditDialog.value = false
    fetchCustomers()
  } catch (error) {
    if (error !== false) {
      console.error('更新客户失败:', error)
      ElMessage.error('更新客户失败')
    }
  } finally {
    submitLoading.value = false
  }
}

// 重置添加表单
const resetAddForm = () => {
  Object.keys(addForm).forEach(key => {
    addForm[key] = ''
  })
  nextTick(() => {
    addFormRef.value?.clearValidate()
  })
}

// 处理添加对话框关闭
const handleDialogClose = () => {
  showAddDialog.value = false
  resetAddForm()
}

// 处理编辑对话框关闭
const handleEditDialogClose = () => {
  showEditDialog.value = false
  nextTick(() => {
    editFormRef.value?.clearValidate()
  })
}

const deleteCustomer = async (customer) => {
  try {
    await ElMessageBox.confirm(`确定要删除客户 ${customer.name} 吗？`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteCustomerApi(customer.id)
    ElMessage.success('删除成功')
    fetchCustomers()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除客户失败:', error)
      ElMessage.error('删除客户失败')
    }
  }
}

const handleSizeChange = (size) => {
  pagination.value.size = size
  pagination.value.current = 1
  fetchCustomers()
}

const handleCurrentChange = (page) => {
  pagination.value.current = page
  fetchCustomers()
}

onMounted(() => {
  fetchCustomers()
})
</script>

<style scoped>
.customers {
  padding: 0;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 0 4px;
  flex-shrink: 0;
}

.page-header h2 {
  color: var(--color-text-primary);
  font-size: 1.75rem;
  font-weight: 700;
  margin: 0;
  letter-spacing: -0.02em;
}

.search-card {
  margin-bottom: 1rem;
  flex-shrink: 0;
}

.search-card :deep(.el-card__body) {
  padding: 24px;
}

.pagination {
  margin-top: 16px;
  text-align: center;
  padding: 16px 0;
  animation: fadeIn 0.3s ease-out;
  animation-delay: 0.2s;
  animation-fill-mode: both;
  flex-shrink: 0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 20px;
}

.el-descriptions {
  margin: 20px 0;
}

/* 表格样式优化 */
:deep(.el-table) {
  background: var(--color-bg-primary);
}

:deep(.el-table th) {
  background: var(--color-bg-secondary);
  color: var(--color-text-secondary);
  font-weight: 600;
  font-size: 0.875rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  border-bottom: 1px solid var(--color-border);
}

:deep(.el-table tbody tr) {
  transition: all var(--transition-fast);
  border-bottom: 1px solid var(--color-border);
}

:deep(.el-table tbody tr:hover) {
  background: var(--color-bg-secondary);
}

/* 按钮样式 - Apple Design */
:deep(.el-button--small) {
  border-radius: var(--radius-sm);
  font-weight: 500;
  transition: all var(--transition-fast);
  margin-right: 0.5rem;
  padding: 0.375rem 0.875rem;
}

:deep(.el-button--small:hover) {
  transform: translateY(-1px);
}

:deep(.el-button--danger) {
  background: var(--color-danger);
  border-color: var(--color-danger);
  color: white;
}

:deep(.el-button--danger:hover) {
  background: var(--color-danger);
  opacity: 0.9;
  box-shadow: var(--shadow-md);
}

/* 表单样式优化 */
:deep(.el-form--inline .el-form-item) {
  margin-right: 1.5rem;
  margin-bottom: 1rem;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--color-text-secondary);
  font-size: 0.875rem;
}


/* 空数据显示优化 */
:deep(.el-table__empty-block) {
  padding: 60px 0;
}

:deep(.el-table__empty-text) {
  color: #909399;
  font-size: 16px;
}


/* 卡片内容优化 */
:deep(.el-card__body) {
  padding: 20px;
}

/* 主表格卡片特殊处理 */
.customers > .el-card:last-of-type {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.customers > .el-card:last-of-type :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 20px;
}

.customers :deep(.el-table) {
  flex: 1;
  min-height: calc(100vh - 420px);
}

.customers :deep(.el-table .el-table__body-wrapper) {
  max-height: calc(100vh - 480px);
  overflow-y: auto;
}

/* 标签样式 - Apple Design */
:deep(.el-tag) {
  border-radius: var(--radius-xl);
  font-weight: 500;
  border: none;
  padding: 0.25rem 0.75rem;
  font-size: 0.75rem;
}

:deep(.el-tag--success) {
  background: rgba(52, 199, 89, 0.1);
  color: var(--color-success);
}

:deep(.el-tag--warning) {
  background: rgba(255, 149, 0, 0.1);
  color: var(--color-warning);
}

:deep(.el-tag--danger) {
  background: rgba(255, 59, 48, 0.1);
  color: var(--color-danger);
}

/* 简化动画 */
@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.customers {
  animation: fadeIn 0.3s ease-out;
}
</style>