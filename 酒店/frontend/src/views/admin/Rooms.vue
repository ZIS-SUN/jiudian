<template>
  <div class="rooms">
    <div class="page-header">
      <h2>客房管理</h2>
      <el-button type="primary" @click="showAddDialog = true">
        <el-icon><Plus /></el-icon>
        添加房间
      </el-button>
    </div>
    
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="房间号">
          <el-input v-model="searchForm.roomNumber" placeholder="请输入房间号" />
        </el-form-item>
        <el-form-item label="房型">
          <el-select v-model="searchForm.roomTypeId" placeholder="请选择房型" clearable>
            <el-option 
              v-for="roomType in roomTypes" 
              :key="roomType.id" 
              :label="roomType.typeName" 
              :value="roomType.id" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="可用" value="可用" />
            <el-option label="已入住" value="已入住" />
            <el-option label="维护中" value="维护中" />
            <el-option label="故障" value="故障" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 房间列表 -->
    <el-card>
      <el-table :data="rooms" :loading="loading" style="width: 100%">
        <el-table-column prop="roomNumber" label="房间号">
          <template #default="scope">
            <span class="room-number">{{ scope.row.roomNumber }}</span>
          </template>
        </el-table-column>
        <el-table-column label="房型">
          <template #default="scope">
            {{ scope.row.roomType?.typeName || '未知' }}
          </template>
        </el-table-column>
        <el-table-column label="价格">
          <template #default="scope">
            <span class="price-display">¥{{ scope.row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="editRoom(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteRoom(scope.row)">删除</el-button>
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
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
    
    <!-- 添加房间对话框 -->
    <el-dialog 
      v-model="showAddDialog" 
      title="添加房间" 
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
            <el-form-item label="房间号" prop="roomNumber">
              <el-input v-model="addForm.roomNumber" placeholder="请输入房间号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房型" prop="roomTypeId">
              <el-select v-model="addForm.roomTypeId" placeholder="请选择房型">
                <el-option 
                  v-for="roomType in roomTypes" 
                  :key="roomType.id" 
                  :label="roomType.typeName" 
                  :value="roomType.id" 
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="价格" prop="price">
              <el-input-number 
                v-model="addForm.price" 
                :min="1" 
                :precision="2" 
                placeholder="请输入价格" 
                style="width: 100%" 
              >
                <template #prepend>￥</template>
              </el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="addForm.status" placeholder="请选择状态">
                <el-option label="可用" value="可用" />
                <el-option label="维护中" value="维护中" />
                <el-option label="故障" value="故障" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="描述">
          <el-input 
            v-model="addForm.description" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入房间描述" 
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleDialogClose">取消</el-button>
          <el-button type="primary" @click="handleAddRoom" :loading="submitLoading">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 编辑房间对话框 -->
    <el-dialog 
      v-model="showEditDialog" 
      title="编辑房间" 
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
            <el-form-item label="房间号" prop="roomNumber">
              <el-input v-model="editForm.roomNumber" placeholder="请输入房间号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房型" prop="roomTypeId">
              <el-select v-model="editForm.roomTypeId" placeholder="请选择房型">
                <el-option 
                  v-for="roomType in roomTypes" 
                  :key="roomType.id" 
                  :label="roomType.typeName" 
                  :value="roomType.id" 
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="价格" prop="price">
              <el-input-number 
                v-model="editForm.price" 
                :min="1" 
                :precision="2" 
                placeholder="请输入价格" 
                style="width: 100%" 
              >
                <template #prepend>￥</template>
              </el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="editForm.status" placeholder="请选择状态">
                <el-option label="可用" value="可用" />
                <el-option label="已入住" value="已入住" />
                <el-option label="维护中" value="维护中" />
                <el-option label="故障" value="故障" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="描述">
          <el-input 
            v-model="editForm.description" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入房间描述" 
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleEditDialogClose">取消</el-button>
          <el-button type="primary" @click="handleUpdateRoom" :loading="submitLoading">
            保存
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getRooms, 
  createRoom,
  updateRoom,
  getRoomById,
  getRoomTypes, 
  deleteRoom as deleteRoomApi 
} from '@/api/rooms'
import { roomStatusUtils } from '@/utils/enums'

const showAddDialog = ref(false)
const showEditDialog = ref(false)
const loading = ref(false)
const submitLoading = ref(false)
const rooms = ref([])
const roomTypes = ref([])
const addFormRef = ref()
const editFormRef = ref()

const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const searchForm = reactive({
  roomNumber: '',
  roomTypeId: '',
  status: ''
})

const addForm = reactive({
  roomNumber: '',
  roomTypeId: '',
  price: '',
  status: '可用',
  description: ''
})

const editForm = reactive({
  id: '',
  roomNumber: '',
  roomTypeId: '',
  price: '',
  status: '',
  description: ''
})

const formRules = {
  roomNumber: [
    { required: true, message: '请输入房间号', trigger: 'blur' },
    { min: 1, max: 10, message: '房间号长度在 1 到 10 个字符', trigger: 'blur' }
  ],
  roomTypeId: [
    { required: true, message: '请选择房型', trigger: 'change' }
  ],
  price: [
    { required: true, message: '请输入价格', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 获取房间列表
const fetchRooms = async () => {
  try {
    loading.value = true
    
    // 将搜索参数中的中文状态转换为英文
    const searchParams = {
      current: pagination.value.current,
      size: pagination.value.size,
      roomNumber: searchForm.roomNumber,
      roomTypeId: searchForm.roomTypeId
    }
    
    if (searchForm.status) {
      searchParams.status = roomStatusUtils.toApi(searchForm.status)
    }
    
    const response = await getRooms(searchParams)
    
    // 将返回的数据中的状态转换为中文
    rooms.value = response.data.records.map(room => ({
      ...room,
      status: roomStatusUtils.toDisplay(room.status)
    }))
    pagination.value.total = response.data.total
  } catch (error) {
    console.error('获取房间列表失败:', error)
    ElMessage.error('获取房间列表失败')
  } finally {
    loading.value = false
  }
}

// 获取房型列表
const fetchRoomTypes = async () => {
  try {
    const response = await getRoomTypes()
    roomTypes.value = response.data
  } catch (error) {
    console.error('获取房型列表失败:', error)
    ElMessage.error('获取房型列表失败')
  }
}

const getStatusType = (status) => {
  return roomStatusUtils.getTagType(status)
}

const getStatusText = (status) => {
  return status || '未知'
}

const handleSearch = () => {
  pagination.value.current = 1
  fetchRooms()
}

const resetSearch = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
  pagination.value.current = 1
  fetchRooms()
}

const editRoom = async (room) => {
  try {
    const response = await getRoomById(room.id)
    const roomData = response.data
    
    // 填充编辑表单
    editForm.id = roomData.id
    editForm.roomNumber = roomData.roomNumber
    editForm.roomTypeId = roomData.roomTypeId
    editForm.price = roomData.price
    editForm.status = roomStatusUtils.toDisplay(roomData.status)
    editForm.description = roomData.description || ''
    
    showEditDialog.value = true
  } catch (error) {
    console.error('获取房间信息失败:', error)
    ElMessage.error('获取房间信息失败')
  }
}

// 添加房间
const handleAddRoom = async () => {
  try {
    await addFormRef.value.validate()
    submitLoading.value = true
    
    const roomData = {
      ...addForm,
      status: roomStatusUtils.toApi(addForm.status)
    }
    
    await createRoom(roomData)
    ElMessage.success('添加房间成功')
    showAddDialog.value = false
    resetAddForm()
    fetchRooms()
  } catch (error) {
    if (error !== false) {
      console.error('添加房间失败:', error)
      ElMessage.error('添加房间失败')
    }
  } finally {
    submitLoading.value = false
  }
}

// 更新房间
const handleUpdateRoom = async () => {
  try {
    await editFormRef.value.validate()
    submitLoading.value = true
    
    const { id, ...updateData } = editForm
    updateData.status = roomStatusUtils.toApi(updateData.status)
    
    await updateRoom(id, updateData)
    ElMessage.success('更新房间成功')
    showEditDialog.value = false
    fetchRooms()
  } catch (error) {
    if (error !== false) {
      console.error('更新房间失败:', error)
      ElMessage.error('更新房间失败')
    }
  } finally {
    submitLoading.value = false
  }
}

// 重置添加表单
const resetAddForm = () => {
  addForm.roomNumber = ''
  addForm.roomTypeId = ''
  addForm.price = ''
  addForm.status = '可用'
  addForm.description = ''
  
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

const deleteRoom = async (room) => {
  try {
    await ElMessageBox.confirm(`确定要删除房间 ${room.roomNumber} 吗？`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteRoomApi(room.id)
    ElMessage.success('删除成功')
    fetchRooms()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除房间失败:', error)
      ElMessage.error('删除房间失败')
    }
  }
}

const handlePageChange = (page) => {
  pagination.value.current = page
  fetchRooms()
}

const handleSizeChange = (size) => {
  pagination.value.size = size
  pagination.value.current = 1
  fetchRooms()
}

onMounted(() => {
  Promise.all([
    fetchRooms(),
    fetchRoomTypes()
  ])
})
</script>

<style scoped>
.rooms {
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
  animation: slideUp 0.6s ease-out;
  animation-delay: 0.2s;
  animation-fill-mode: both;
  flex-shrink: 0;
}

/* 主表格卡片特殊处理 */
.rooms > .el-card:last-of-type {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.rooms > .el-card:last-of-type :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 20px;
}

.rooms :deep(.el-table) {
  flex: 1;
  min-height: calc(100vh - 420px);
}

.rooms :deep(.el-table .el-table__body-wrapper) {
  max-height: calc(100vh - 480px);
  overflow-y: auto;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 20px;
}

/* 房间状态标签 - Apple Design */
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

:deep(.el-tag--info) {
  background: var(--color-bg-secondary);
  color: var(--color-text-secondary);
}

/* 价格显示 */
:deep(.price-display) {
  font-weight: 600;
  color: var(--color-text-primary);
  font-size: 1rem;
  letter-spacing: -0.01em;
}

/* 房间号显示 */
:deep(.room-number) {
  font-weight: 600;
  color: var(--color-primary);
  font-size: 0.875rem;
}

/* 表格样式覆盖 */
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

/* 输入框样式 */
:deep(.el-input-number) {
  width: 100%;
}

:deep(.el-input-number .el-input__wrapper) {
  border-radius: var(--radius-md);
  border: 1px solid var(--color-border);
  background: var(--color-bg-primary);
}

:deep(.el-input-number .el-input-group__prepend) {
  background: var(--color-bg-secondary);
  color: var(--color-text-secondary);
  border: none;
  font-weight: 500;
}

/* 选择器优化 */
:deep(.el-select) {
  width: 100%;
}

/* 按钮样式 */
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

/* 表单样式 */
:deep(.el-form--inline .el-form-item) {
  margin-right: 1.5rem;
  margin-bottom: 1rem;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--color-text-secondary);
  font-size: 0.875rem;
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

.rooms {
  animation: fadeIn 0.3s ease-out;
}

/* 表单布局优化 */
:deep(.el-form .el-row) {
  margin-bottom: 16px;
}

:deep(.el-textarea .el-textarea__inner) {
  border-radius: 8px;
  transition: all 0.3s ease;
}

:deep(.el-textarea .el-textarea__inner:focus) {
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.3);
}
</style>