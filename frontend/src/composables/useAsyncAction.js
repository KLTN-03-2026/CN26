import { ref } from 'vue'

/**
 * Composable for handling async operations with loading and error states
 * Eliminates boilerplate code for try-catch-finally blocks
 * 
 * @returns {Object} { loading, error, execute }
 * 
 * @example
 * const { loading, error, execute } = useAsyncAction()
 * 
 * const loadData = () => execute(async () => {
 *   const response = await someService.getData()
 *   if (response.success) {
 *     data.value = response.data
 *   }
 * })
 */
export function useAsyncAction() {
  const loading = ref(false)
  const error = ref('')

  /**
   * Execute an async function with automatic loading and error handling
   * @param {Function} fn - Async function to execute
   * @returns {Promise<any>} Result of the async function
   */
  const execute = async (fn) => {
    try {
      loading.value = true
      error.value = ''
      return await fn()
    } catch (err) {
      error.value = err.message || err.error || 'Có lỗi xảy ra'
      console.error('useAsyncAction error:', err)
      throw err // Re-throw to allow caller to handle if needed
    } finally {
      loading.value = false
    }
  }

  return { loading, error, execute }
}
