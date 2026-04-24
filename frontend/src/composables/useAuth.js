import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/authStore'

export function useAuth() {
  const router = useRouter()
  const authStore = useAuthStore()

  const handleLogout = () => {
    authStore.logout()
    router.push('/login')
  }

  return {
    handleLogout
  }
}
