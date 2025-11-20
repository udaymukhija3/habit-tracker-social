import React from 'react';
import { render, waitFor } from '@testing-library/react-native';
import { AuthProvider, useAuth } from '../src/contexts/AuthContext';
import * as api from '../src/services/api';
import AsyncStorage from '@react-native-async-storage/async-storage';

jest.mock('../src/services/api');
jest.mock('@react-native-async-storage/async-storage', () =>
  require('@react-native-async-storage/async-storage/jest/async-storage-mock')
);

const TestComponent: React.FC = () => {
  const { user, token, isLoading } = useAuth();
  
  if (isLoading) {
    return null;
  }
  
  return (
    <>
      {user ? <>{user.username}</> : <>{'No user'}</>}
      {token ? <>{token}</> : <>{'No token'}</>}
    </>
  );
};

describe('AuthContext', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    AsyncStorage.clear();
  });

  it('provides initial state correctly', async () => {
    const { getByText } = render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );

    await waitFor(() => {
      expect(getByText('No user')).toBeTruthy();
      expect(getByText('No token')).toBeTruthy();
    });
  });

  it('loads user from AsyncStorage on mount', async () => {
    const mockUser = {
      id: 1,
      username: 'testuser',
      email: 'test@example.com',
      role: 'USER' as const,
      createdAt: new Date().toISOString(),
    };

    await AsyncStorage.setItem('token', 'test-token');
    await AsyncStorage.setItem('user', JSON.stringify(mockUser));

    const { getByText } = render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );

    await waitFor(() => {
      expect(getByText('testuser')).toBeTruthy();
      expect(getByText('test-token')).toBeTruthy();
    });
  });

  it('login updates user and token', async () => {
    const mockResponse = {
      token: 'new-token',
      id: 1,
      username: 'testuser',
      email: 'test@example.com',
      role: 'USER' as const,
    };

    jest.spyOn(api.authAPI, 'login').mockResolvedValue(mockResponse);

    const LoginTestComponent: React.FC = () => {
      const { login, user, token } = useAuth();
      
      React.useEffect(() => {
        login('testuser', 'password');
      }, [login]);

      return (
        <>
          {user ? <>{user.username}</> : <>{'No user'}</>}
          {token ? <>{token}</> : <>{'No token'}</>}
        </>
      );
    };

    const { getByText } = render(
      <AuthProvider>
        <LoginTestComponent />
      </AuthProvider>
    );

    await waitFor(() => {
      expect(getByText('testuser')).toBeTruthy();
      expect(getByText('new-token')).toBeTruthy();
    });
  });
});

