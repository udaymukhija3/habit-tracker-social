export interface User {
  id: number;
  username: string;
  email: string;
  firstName?: string;
  lastName?: string;
  role: string;
  createdAt: string;
  lastLoginAt?: string;
}

export interface Habit {
  id: number;
  name: string;
  description?: string;
  type: HabitType;
  frequency: HabitFrequency;
  targetValue: number;
  targetUnit: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export enum HabitType {
  HEALTH = 'HEALTH',
  PRODUCTIVITY = 'PRODUCTIVITY',
  LEARNING = 'LEARNING',
  SOCIAL = 'SOCIAL',
  FINANCE = 'FINANCE',
  MINDFULNESS = 'MINDFULNESS',
  CREATIVE = 'CREATIVE',
  MAINTENANCE = 'MAINTENANCE'
}

export enum HabitFrequency {
  DAILY = 'DAILY',
  WEEKLY = 'WEEKLY',
  MONTHLY = 'MONTHLY'
}

export interface HabitCompletion {
  id: number;
  habitId: number;
  completionDate: string;
  value?: number;
  notes?: string;
}

export interface Streak {
  id: number;
  habitId: number;
  currentStreak: number;
  longestStreak: number;
  lastCompletionDate?: string;
}

export interface Notification {
  id: number;
  type: NotificationType;
  title: string;
  message: string;
  status: NotificationStatus;
  createdAt: string;
}

export enum NotificationType {
  FRIEND_REQUEST = 'FRIEND_REQUEST',
  FRIEND_ACCEPTED = 'FRIEND_ACCEPTED',
  COMPETITION_INVITE = 'COMPETITION_INVITE',
  STREAK_MILESTONE = 'STREAK_MILESTONE',
  REMINDER = 'REMINDER'
}

export enum NotificationStatus {
  UNREAD = 'UNREAD',
  READ = 'READ'
}

export interface Friendship {
  id: number;
  requester: User;
  addressee: User;
  status: FriendshipStatus;
  createdAt: string;
}

export enum FriendshipStatus {
  PENDING = 'PENDING',
  ACCEPTED = 'ACCEPTED',
  DECLINED = 'DECLINED'
}

export interface Competition {
  id: number;
  name: string;
  description?: string;
  type: CompetitionType;
  startDate: string;
  endDate: string;
  isActive: boolean;
  participants: CompetitionParticipant[];
}

export enum CompetitionType {
  STREAK = 'STREAK',
  COMPLETION_COUNT = 'COMPLETION_COUNT',
  TIME_BASED = 'TIME_BASED'
}

export interface CompetitionParticipant {
  id: number;
  user: User;
  score: number;
  rank: number;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  firstName?: string;
  lastName?: string;
}

export interface AuthResponse {
  token: string;
  id: number;
  username: string;
  email: string;
  role: string;
}
