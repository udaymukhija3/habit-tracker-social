import axios from 'axios';
import { authAPI, habitAPI, notificationAPI, friendshipAPI } from './api';
import { HabitType, HabitFrequency } from '../types';

jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

describe('API Service', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.clear();
  });

  describe('authAPI', () => {
    it('login makes correct API call', async () => {
      const mockResponse = {
        data: {
          token: 'test-token',
          id: 1,
          username: 'testuser',
          email: 'test@example.com',
          role: 'USER',
        },
      };

      mockedAxios.create = jest.fn(() => ({
        post: jest.fn().mockResolvedValue(mockResponse),
        get: jest.fn(),
        put: jest.fn(),
        delete: jest.fn(),
        interceptors: {
          request: { use: jest.fn(), eject: jest.fn() },
          response: { use: jest.fn(), eject: jest.fn() },
        },
      })) as any;

      const result = await authAPI.login({ username: 'testuser', password: 'password' });
      expect(result).toEqual(mockResponse.data);
    });

    it('register makes correct API call', async () => {
      const mockResponse = { data: { message: 'User registered successfully!' } };

      mockedAxios.create = jest.fn(() => ({
        post: jest.fn().mockResolvedValue(mockResponse),
        get: jest.fn(),
        put: jest.fn(),
        delete: jest.fn(),
        interceptors: {
          request: { use: jest.fn(), eject: jest.fn() },
          response: { use: jest.fn(), eject: jest.fn() },
        },
      })) as any;

      const result = await authAPI.register({
        username: 'testuser',
        email: 'test@example.com',
        password: 'password',
        firstName: 'Test',
        lastName: 'User',
      });
      expect(result).toEqual(mockResponse.data);
    });
  });

  describe('habitAPI', () => {
    it('getHabits makes correct API call', async () => {
      const mockHabits = [
        {
          id: 1,
          name: 'Test Habit',
          type: HabitType.HEALTH,
          frequency: HabitFrequency.DAILY,
          targetValue: 1,
          targetUnit: 'times',
          isActive: true,
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString(),
          userId: 1,
        },
      ];

      mockedAxios.create = jest.fn(() => ({
        get: jest.fn().mockResolvedValue({ data: mockHabits }),
        post: jest.fn(),
        put: jest.fn(),
        delete: jest.fn(),
        interceptors: {
          request: { use: jest.fn(), eject: jest.fn() },
          response: { use: jest.fn(), eject: jest.fn() },
        },
      })) as any;

      const result = await habitAPI.getHabits();
      expect(result).toEqual(mockHabits);
    });
  });

  describe('notificationAPI', () => {
    it('getNotifications makes correct API call', async () => {
      const mockNotifications = [
        {
          id: 1,
          title: 'Test Notification',
          message: 'Test message',
          type: 'SYSTEM',
          status: 'UNREAD',
          createdAt: new Date().toISOString(),
          userId: 1,
        },
      ];

      mockedAxios.create = jest.fn(() => ({
        get: jest.fn().mockResolvedValue({ data: mockNotifications }),
        post: jest.fn(),
        delete: jest.fn(),
        interceptors: {
          request: { use: jest.fn(), eject: jest.fn() },
          response: { use: jest.fn(), eject: jest.fn() },
        },
      })) as any;

      const result = await notificationAPI.getNotifications();
      expect(result).toEqual(mockNotifications);
    });
  });

  describe('friendshipAPI', () => {
    it('getFriends makes correct API call', async () => {
      const mockFriends = [
        {
          id: 2,
          username: 'friend1',
          email: 'friend1@example.com',
          role: 'USER',
          createdAt: new Date().toISOString(),
        },
      ];

      mockedAxios.create = jest.fn(() => ({
        get: jest.fn().mockResolvedValue({ data: mockFriends }),
        post: jest.fn(),
        delete: jest.fn(),
        interceptors: {
          request: { use: jest.fn(), eject: jest.fn() },
          response: { use: jest.fn(), eject: jest.fn() },
        },
      })) as any;

      const result = await friendshipAPI.getFriends();
      expect(result).toEqual(mockFriends);
    });
  });
});

