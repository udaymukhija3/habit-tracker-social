import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import { CssBaseline } from '@mui/material';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

import { AuthProvider, useAuth } from './contexts/AuthContext';
import Header from './components/Layout/Header';
import LoginForm from './components/Auth/LoginForm';
import RegisterForm from './components/Auth/RegisterForm';
import Dashboard from './pages/Dashboard';
import HabitsPage from './pages/HabitsPage';
import FriendsPage from './pages/FriendsPage';
import CompetitionsPage from './pages/CompetitionsPage';
import NotificationsPage from './pages/NotificationsPage';
import ProfilePage from './pages/ProfilePage';

const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
  },
});

const queryClient = new QueryClient();

const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { user, isLoading } = useAuth();
  
  if (isLoading) {
    return <div>Loading...</div>;
  }
  
  return user ? <>{children}</> : <Navigate to="/login" />;
};

const PublicRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { user, isLoading } = useAuth();
  
  if (isLoading) {
    return <div>Loading...</div>;
  }
  
  return user ? <Navigate to="/" /> : <>{children}</>;
};

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <AuthProvider>
          <Router>
            <div className="App">
              <Header />
              <Routes>
                <Route path="/login" element={
                  <PublicRoute>
                    <LoginForm />
                  </PublicRoute>
                } />
                <Route path="/register" element={
                  <PublicRoute>
                    <RegisterForm />
                  </PublicRoute>
                } />
                <Route path="/" element={
                  <ProtectedRoute>
                    <Dashboard />
                  </ProtectedRoute>
                } />
                <Route path="/habits" element={
                  <ProtectedRoute>
                    <HabitsPage />
                  </ProtectedRoute>
                } />
                <Route path="/friends" element={
                  <ProtectedRoute>
                    <FriendsPage />
                  </ProtectedRoute>
                } />
                <Route path="/competitions" element={
                  <ProtectedRoute>
                    <CompetitionsPage />
                  </ProtectedRoute>
                } />
                <Route path="/notifications" element={
                  <ProtectedRoute>
                    <NotificationsPage />
                  </ProtectedRoute>
                } />
                <Route path="/profile" element={
                  <ProtectedRoute>
                    <ProfilePage />
                  </ProtectedRoute>
                } />
              </Routes>
            </div>
          </Router>
        </AuthProvider>
      </ThemeProvider>
    </QueryClientProvider>
  );
}

export default App;
