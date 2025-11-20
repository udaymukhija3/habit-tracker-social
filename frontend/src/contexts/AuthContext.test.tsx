import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import { AuthProvider, useAuth } from './AuthContext';
import * as api from '../services/api';

jest.mock('../services/api');

const TestComponent: React.FC = () => {
  const { user, token, isLoading } = useAuth();
  
  if (isLoading) {
    return <div>Loading...</div>;
  }
  
  return (
    <div>
      <div data-testid="user">{user ? user.username : 'No user'}</div>
      <div data-testid="token">{token || 'No token'}</div>
    </div>
  );
};

describe('AuthContext', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.clear();
  });

  it('provides initial state correctly', async () => {
    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );

    await waitFor(() => {
      expect(screen.getByTestId('user')).toHaveTextContent('No user');
      expect(screen.getByTestId('token')).toHaveTextContent('No token');
    });
  });

  it('loads user from localStorage on mount', async () => {
    const mockUser = {
      id: 1,
      username: 'testuser',
      email: 'test@example.com',
      role: 'USER' as const,
      createdAt: new Date().toISOString(),
    };

    localStorage.setItem('token', 'test-token');
    localStorage.setItem('user', JSON.stringify(mockUser));

    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );

    await waitFor(() => {
      expect(screen.getByTestId('user')).toHaveTextContent('testuser');
      expect(screen.getByTestId('token')).toHaveTextContent('test-token');
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
        <div>
          <div data-testid="user">{user ? user.username : 'No user'}</div>
          <div data-testid="token">{token || 'No token'}</div>
        </div>
      );
    };

    render(
      <AuthProvider>
        <LoginTestComponent />
      </AuthProvider>
    );

    await waitFor(() => {
      expect(screen.getByTestId('user')).toHaveTextContent('testuser');
      expect(screen.getByTestId('token')).toHaveTextContent('new-token');
    });
  });

  it('logout clears user and token', async () => {
    const mockUser = {
      id: 1,
      username: 'testuser',
      email: 'test@example.com',
      role: 'USER' as const,
      createdAt: new Date().toISOString(),
    };

    localStorage.setItem('token', 'test-token');
    localStorage.setItem('user', JSON.stringify(mockUser));

    const LogoutTestComponent: React.FC = () => {
      const { logout, user, token } = useAuth();
      
      React.useEffect(() => {
        logout();
      }, [logout]);

      return (
        <div>
          <div data-testid="user">{user ? user.username : 'No user'}</div>
          <div data-testid="token">{token || 'No token'}</div>
        </div>
      );
    };

    render(
      <AuthProvider>
        <LogoutTestComponent />
      </AuthProvider>
    );

    await waitFor(() => {
      expect(screen.getByTestId('user')).toHaveTextContent('No user');
      expect(screen.getByTestId('token')).toHaveTextContent('No token');
    });
  });
});

