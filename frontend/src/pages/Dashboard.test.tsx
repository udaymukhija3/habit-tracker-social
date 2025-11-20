import React from 'react';
import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import Dashboard from './Dashboard';
import { AuthProvider } from '../contexts/AuthContext';

const mockUser = {
  id: 1,
  username: 'testuser',
  email: 'test@example.com',
  role: 'USER' as const,
  createdAt: new Date().toISOString(),
};

const renderWithProviders = (component: React.ReactElement) => {
  return render(
    <BrowserRouter>
      <AuthProvider>
        {component}
      </AuthProvider>
    </BrowserRouter>
  );
};

// Mock localStorage
const localStorageMock = (() => {
  let store: Record<string, string> = {};
  return {
    getItem: (key: string) => store[key] || null,
    setItem: (key: string, value: string) => {
      store[key] = value.toString();
    },
    removeItem: (key: string) => {
      delete store[key];
    },
    clear: () => {
      store = {};
    },
  };
})();

Object.defineProperty(window, 'localStorage', {
  value: localStorageMock,
});

describe('Dashboard', () => {
  beforeEach(() => {
    localStorage.clear();
    localStorage.setItem('user', JSON.stringify(mockUser));
  });

  it('renders welcome message with username', () => {
    renderWithProviders(<Dashboard />);
    expect(screen.getByText(/welcome back, testuser!/i)).toBeInTheDocument();
  });

  it('renders stats cards', () => {
    renderWithProviders(<Dashboard />);
    expect(screen.getByText(/active habits/i)).toBeInTheDocument();
    expect(screen.getByText(/current streak/i)).toBeInTheDocument();
    expect(screen.getByText(/friends/i)).toBeInTheDocument();
    expect(screen.getByText(/competitions/i)).toBeInTheDocument();
  });

  it('renders add habit button', () => {
    renderWithProviders(<Dashboard />);
    expect(screen.getByText(/add habit/i)).toBeInTheDocument();
  });

  it('renders quick actions section', () => {
    renderWithProviders(<Dashboard />);
    expect(screen.getByText(/quick actions/i)).toBeInTheDocument();
    expect(screen.getByText(/view all habits/i)).toBeInTheDocument();
    expect(screen.getByText(/find friends/i)).toBeInTheDocument();
    expect(screen.getByText(/join competition/i)).toBeInTheDocument();
  });
});

