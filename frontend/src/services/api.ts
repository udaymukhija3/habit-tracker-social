import axios from 'axios';
import { AuthResponse, LoginRequest, RegisterRequest, User, Habit, HabitCompletion, Notification, Friendship, Competition } from '../types';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle token expiration
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth API
export const authAPI = {
  login: (credentials: LoginRequest): Promise<AuthResponse> =>
    api.post('/auth/login', credentials).then(res => res.data),
  
  register: (userData: RegisterRequest): Promise<{ message: string }> =>
    api.post('/auth/register', userData).then(res => res.data),
};

// User API
export const userAPI = {
  getProfile: (): Promise<User> =>
    api.get('/users/profile').then(res => res.data),
  
  updateProfile: (userData: Partial<User>): Promise<User> =>
    api.put('/users/profile', userData).then(res => res.data),
};

// Habit API
export const habitAPI = {
  getHabits: (): Promise<Habit[]> =>
    api.get('/habits').then(res => res.data),
  
  getHabit: (id: number): Promise<Habit> =>
    api.get(`/habits/${id}`).then(res => res.data),
  
  createHabit: (habit: Omit<Habit, 'id' | 'createdAt' | 'updatedAt'>): Promise<Habit> =>
    api.post('/habits', habit).then(res => res.data),
  
  updateHabit: (id: number, habit: Partial<Habit>): Promise<Habit> =>
    api.put(`/habits/${id}`, habit).then(res => res.data),
  
  deleteHabit: (id: number): Promise<void> =>
    api.delete(`/habits/${id}`).then(res => res.data),
  
  completeHabit: (id: number, completion: Partial<HabitCompletion>): Promise<HabitCompletion> =>
    api.post(`/habits/${id}/complete`, completion).then(res => res.data),
};

// Notification API
export const notificationAPI = {
  getNotifications: (): Promise<Notification[]> =>
    api.get('/notifications').then(res => res.data),
  
  getUnreadNotifications: (): Promise<Notification[]> =>
    api.get('/notifications/unread').then(res => res.data),
  
  getUnreadCount: (): Promise<number> =>
    api.get('/notifications/count').then(res => res.data),
  
  markAsRead: (id: number): Promise<void> =>
    api.post(`/notifications/${id}/read`).then(res => res.data),
  
  markAllAsRead: (): Promise<void> =>
    api.post('/notifications/read-all').then(res => res.data),
  
  deleteNotification: (id: number): Promise<void> =>
    api.delete(`/notifications/${id}`).then(res => res.data),
};

// Friendship API
export const friendshipAPI = {
  getFriends: (): Promise<User[]> =>
    api.get('/friends').then(res => res.data),
  
  getPendingRequests: (): Promise<Friendship[]> =>
    api.get('/friends/requests/pending').then(res => res.data),
  
  getSentRequests: (): Promise<Friendship[]> =>
    api.get('/friends/requests/sent').then(res => res.data),
  
  sendFriendRequest: (userId: number): Promise<Friendship> =>
    api.post(`/friends/request/${userId}`).then(res => res.data),
  
  acceptFriendRequest: (friendshipId: number): Promise<Friendship> =>
    api.post(`/friends/accept/${friendshipId}`).then(res => res.data),
  
  declineFriendRequest: (friendshipId: number): Promise<Friendship> =>
    api.post(`/friends/decline/${friendshipId}`).then(res => res.data),
  
  removeFriend: (friendshipId: number): Promise<void> =>
    api.delete(`/friends/${friendshipId}`).then(res => res.data),
};

// Competition API
export const competitionAPI = {
  getCompetitions: (): Promise<Competition[]> =>
    api.get('/competitions').then(res => res.data),
  
  getCompetition: (id: number): Promise<Competition> =>
    api.get(`/competitions/${id}`).then(res => res.data),
  
  createCompetition: (competition: Omit<Competition, 'id' | 'participants'>): Promise<Competition> =>
    api.post('/competitions', competition).then(res => res.data),
  
  joinCompetition: (id: number): Promise<void> =>
    api.post(`/competitions/${id}/join`).then(res => res.data),
  
  leaveCompetition: (id: number): Promise<void> =>
    api.post(`/competitions/${id}/leave`).then(res => res.data),
};

export default api;
