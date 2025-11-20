import React from 'react';
import { render, fireEvent, waitFor } from '@testing-library/react-native';
import LoginScreen from '../src/screens/LoginScreen';
import { AuthProvider } from '../src/contexts/AuthContext';
import * as api from '../src/services/api';

jest.mock('../src/services/api');
jest.mock('@react-native-async-storage/async-storage', () =>
  require('@react-native-async-storage/async-storage/jest/async-storage-mock')
);

const mockNavigation = {
  navigate: jest.fn(),
};

const renderWithProviders = (component: React.ReactElement) => {
  return render(
    <AuthProvider>
      {component}
    </AuthProvider>
  );
};

describe('LoginScreen', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders login form correctly', () => {
    const { getByPlaceholderText, getByText } = renderWithProviders(
      <LoginScreen navigation={mockNavigation} />
    );

    expect(getByText('Habit Tracker')).toBeTruthy();
    expect(getByText('Sign in to your account')).toBeTruthy();
    expect(getByPlaceholderText('Username')).toBeTruthy();
    expect(getByPlaceholderText('Password')).toBeTruthy();
    expect(getByText('Sign In')).toBeTruthy();
  });

  it('shows error alert when fields are empty', async () => {
    const Alert = require('react-native').Alert;
    jest.spyOn(Alert, 'alert');

    const { getByText } = renderWithProviders(
      <LoginScreen navigation={mockNavigation} />
    );

    fireEvent.press(getByText('Sign In'));

    await waitFor(() => {
      expect(Alert.alert).toHaveBeenCalledWith('Error', 'Please fill in all fields');
    });
  });

  it('calls login function with correct credentials', async () => {
    const mockLogin = jest.spyOn(api.authAPI, 'login').mockResolvedValue({
      token: 'test-token',
      id: 1,
      username: 'testuser',
      email: 'test@example.com',
      role: 'USER',
    });

    const { getByPlaceholderText, getByText } = renderWithProviders(
      <LoginScreen navigation={mockNavigation} />
    );

    fireEvent.changeText(getByPlaceholderText('Username'), 'testuser');
    fireEvent.changeText(getByPlaceholderText('Password'), 'password');
    fireEvent.press(getByText('Sign In'));

    await waitFor(() => {
      expect(mockLogin).toHaveBeenCalledWith({
        username: 'testuser',
        password: 'password',
      });
    });

    mockLogin.mockRestore();
  });

  it('navigates to register screen', () => {
    const { getByText } = renderWithProviders(
      <LoginScreen navigation={mockNavigation} />
    );

    fireEvent.press(getByText("Don't have an account? Sign Up"));

    expect(mockNavigation.navigate).toHaveBeenCalledWith('Register');
  });
});

