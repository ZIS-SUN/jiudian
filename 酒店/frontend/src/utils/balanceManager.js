// 余额管理工具类
export class BalanceManager {
  constructor() {
    this.storageKey = 'userBalance'
    this.transactionsKey = 'userTransactions'
  }

  // 获取用户余额
  getBalance() {
    const balance = localStorage.getItem(this.storageKey)
    return balance ? parseFloat(balance) : 5000 // 默认余额5000元
  }

  // 设置用户余额
  setBalance(amount) {
    localStorage.setItem(this.storageKey, amount.toString())
    return true
  }

  // 扣减余额
  deductBalance(amount, description = '消费扣款') {
    const currentBalance = this.getBalance()

    if (currentBalance < amount) {
      return {
        success: false,
        message: '余额不足',
        currentBalance,
        requiredAmount: amount,
        shortfall: amount - currentBalance
      }
    }

    const newBalance = currentBalance - amount
    this.setBalance(newBalance)

    // 记录交易
    this.addTransaction({
      type: 'payment',
      description,
      amount: -amount,
      time: new Date().toISOString().replace('T', ' ').substring(0, 19),
      balanceAfter: newBalance
    })

    return {
      success: true,
      message: '支付成功',
      previousBalance: currentBalance,
      currentBalance: newBalance,
      paidAmount: amount
    }
  }

  // 增加余额（充值）
  addBalance(amount, description = '账户充值') {
    const currentBalance = this.getBalance()
    const newBalance = currentBalance + amount
    this.setBalance(newBalance)

    // 记录交易
    this.addTransaction({
      type: 'recharge',
      description,
      amount: amount,
      time: new Date().toISOString().replace('T', ' ').substring(0, 19),
      balanceAfter: newBalance
    })

    return {
      success: true,
      message: '充值成功',
      previousBalance: currentBalance,
      currentBalance: newBalance,
      rechargedAmount: amount
    }
  }

  // 检查余额是否足够
  checkBalance(amount) {
    const currentBalance = this.getBalance()
    return {
      sufficient: currentBalance >= amount,
      currentBalance,
      requiredAmount: amount,
      shortfall: amount > currentBalance ? amount - currentBalance : 0
    }
  }

  // 添加交易记录
  addTransaction(transaction) {
    const transactions = this.getTransactions()
    transaction.id = Date.now() + Math.random().toString(36).substr(2, 9)
    transactions.unshift(transaction) // 添加到开头

    // 只保留最近100条记录
    if (transactions.length > 100) {
      transactions.splice(100)
    }

    localStorage.setItem(this.transactionsKey, JSON.stringify(transactions))
    return transaction
  }

  // 获取交易记录
  getTransactions() {
    const transactions = localStorage.getItem(this.transactionsKey)
    return transactions ? JSON.parse(transactions) : []
  }

  // 获取交易统计
  getTransactionStats() {
    const transactions = this.getTransactions()
    const now = new Date()
    const thisMonth = now.getMonth()
    const thisYear = now.getFullYear()

    const stats = {
      totalTransactions: transactions.length,
      thisMonthSpending: 0,
      thisMonthRecharge: 0,
      totalSpending: 0,
      totalRecharge: 0
    }

    transactions.forEach(transaction => {
      const transactionDate = new Date(transaction.time)
      const amount = Math.abs(transaction.amount)

      if (transaction.type === 'payment') {
        stats.totalSpending += amount
        if (transactionDate.getMonth() === thisMonth && transactionDate.getFullYear() === thisYear) {
          stats.thisMonthSpending += amount
        }
      } else if (transaction.type === 'recharge') {
        stats.totalRecharge += amount
        if (transactionDate.getMonth() === thisMonth && transactionDate.getFullYear() === thisYear) {
          stats.thisMonthRecharge += amount
        }
      }
    })

    return stats
  }

  // 格式化金额显示
  formatAmount(amount) {
    return `¥${amount.toLocaleString()}`
  }

  // 获取余额等级
  getBalanceLevel() {
    const balance = this.getBalance()
    if (balance >= 50000) return { level: 'diamond', name: '钻石会员', color: '#1e90ff' }
    if (balance >= 20000) return { level: 'gold', name: '黄金会员', color: '#ffd700' }
    if (balance >= 10000) return { level: 'silver', name: '白银会员', color: '#c0c0c0' }
    if (balance >= 5000) return { level: 'bronze', name: '青铜会员', color: '#cd7f32' }
    return { level: 'basic', name: '普通用户', color: '#808080' }
  }

  // 清空所有数据（仅用于测试）
  clearAllData() {
    localStorage.removeItem(this.storageKey)
    localStorage.removeItem(this.transactionsKey)
  }
}

// 创建单例实例
const balanceManager = new BalanceManager()

export default balanceManager

// 导出常用方法
export const {
  getBalance,
  setBalance,
  deductBalance,
  addBalance,
  checkBalance,
  addTransaction,
  getTransactions,
  getTransactionStats,
  formatAmount,
  getBalanceLevel
} = balanceManager