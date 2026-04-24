export function useFormatters() {
  const formatDate = (dateString) => {
    const date = new Date(dateString)
    return date.toLocaleDateString('vi-VN')
  }

  const formatDateFull = (dateString) => {
    const date = new Date(dateString)
    return date.toLocaleDateString('vi-VN', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    })
  }

  const formatDateTime = (dateTimeString) => {
    const date = new Date(dateTimeString)
    return date.toLocaleString('vi-VN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    })
  }

  const formatTimeAgo = (dateString) => {
    const date = new Date(dateString)
    const now = new Date()
    const diff = Math.floor((now - date) / 1000)
    
    if (diff < 60) return 'Vừa xong'
    if (diff < 3600) return `${Math.floor(diff / 60)} phút trước`
    if (diff < 86400) return `${Math.floor(diff / 3600)} giờ trước`
    return `${Math.floor(diff / 86400)} ngày trước`
  }

  const formatTimeSpent = (seconds) => {
    if (!seconds) return '0 phút'
    const minutes = Math.floor(seconds / 60)
    const secs = seconds % 60
    return `${minutes}:${secs.toString().padStart(2, '0')}`
  }

  const getLevelText = (level) => {
    const map = {
      easy: 'Dễ',
      medium: 'Trung bình',
      hard: 'Khó'
    }
    return map[level] || level
  }

  const getRoleText = (role) => {
    const map = {
      student: 'Học sinh',
      teacher: 'Giáo viên',
      admin: 'Admin'
    }
    return map[role] || role
  }

  const getStatusText = (status) => {
    const map = {
      pending: 'Chờ duyệt',
      approved: 'Đã duyệt',
      rejected: 'Từ chối'
    }
    return map[status] || status
  }

  const getStatusClass = (status) => {
    const map = {
      pending: 'pending',
      approved: 'approved',
      rejected: 'rejected'
    }
    return map[status] || 'pending'
  }

  const getScoreClass = (score) => {
    if (score >= 80) return 'excellent'
    if (score >= 65) return 'good'
    if (score >= 50) return 'average'
    return 'poor'
  }

  return {
    formatDate,
    formatDateFull,
    formatDateTime,
    formatTimeAgo,
    formatTimeSpent,
    getLevelText,
    getRoleText,
    getStatusText,
    getStatusClass,
    getScoreClass
  }
}
