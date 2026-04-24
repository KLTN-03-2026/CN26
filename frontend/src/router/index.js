import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/authStore'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/auth/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/auth/Register.vue')
  },
  {
    path: '/verify-email',
    name: 'VerifyEmail',
    component: () => import('../views/auth/VerifyEmail.vue')
  },
  {
    path: '/resend-verification',
    name: 'ResendVerification',
    component: () => import('../views/auth/ResendVerification.vue')
  },
  {
    path: '/student',
    name: 'StudentDashboard',
    component: () => import('../views/student/Dashboard.vue'),
    meta: { requiresAuth: true, role: 'student' }
  },
  {
    path: '/student/profile',
    name: 'StudentProfile',
    component: () => import('../views/student/Profile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/student/exams',
    name: 'StudentExams',
    component: () => import('../views/student/ExamList.vue'),
    meta: { requiresAuth: true, role: 'student' }
  },
  {
    path: '/student/exam/:id',
    name: 'TakeExam',
    component: () => import('../views/student/TakeExam.vue'),
    meta: { requiresAuth: true, role: 'student' }
  },
  {
    path: '/student/results',
    name: 'StudentResults',
    component: () => import('../views/student/Results.vue'),
    meta: { requiresAuth: true, role: 'student' }
  },
  {
    path: '/student/result/:id',
    name: 'ViewResult',
    component: () => import('../views/student/ViewResult.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/student/teacher-request',
    name: 'TeacherRequest',
    component: () => import('../views/student/TeacherRequest.vue'),
    meta: { requiresAuth: true, role: 'student' }
  },
  {
    path: '/teacher',
    name: 'TeacherDashboard',
    component: () => import('../views/teacher/Dashboard.vue'),
    meta: { requiresAuth: true, role: 'teacher' }
  },
  {
    path: '/teacher/questions',
    name: 'ManageQuestions',
    component: () => import('../views/teacher/Questions.vue'),
    meta: { requiresAuth: true, role: 'teacher' }
  },
  {
    path: '/teacher/exams',
    name: 'ManageExams',
    component: () => import('../views/teacher/Exams.vue'),
    meta: { requiresAuth: true, role: 'teacher' }
  },
  {
    path: '/teacher/statistics/:id',
    name: 'ExamStatistics',
    component: () => import('../views/teacher/Statistics.vue'),
    meta: { requiresAuth: true, role: 'teacher' }
  },
  {
    path: '/teacher/profile',
    name: 'TeacherProfile',
    component: () => import('../views/teacher/Profile.vue'),
    meta: { requiresAuth: true, role: 'teacher' }
  },
  {
    path: '/admin',
    name: 'AdminDashboard',
    component: () => import('../views/admin/Dashboard.vue'),
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/admin/users',
    name: 'ManageUsers',
    component: () => import('../views/admin/Users.vue'),
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/admin/teacher-requests',
    name: 'TeacherRequests',
    component: () => import('../views/admin/TeacherRequests.vue'),
    meta: { requiresAuth: true, role: 'admin' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
  } else if (to.meta.role && (!authStore.user || authStore.user.role !== to.meta.role)) {
    // Nếu user null hoặc role không khớp → redirect về dashboard tương ứng hoặc login
    const userRole = authStore.user?.role
    if (userRole) {
      next(`/${userRole}`)
    } else {
      next('/login')
    }
  } else {
    next()
  }
})

export default router
