import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { StatusBar } from 'react-native';
import Icon from 'react-native-vector-icons/MaterialIcons';

import { AuthProvider, useAuth } from './src/contexts/AuthContext';
import LoginScreen from './src/screens/LoginScreen';
import RegisterScreen from './src/screens/RegisterScreen';
import DashboardScreen from './src/screens/DashboardScreen';
import HabitsScreen from './src/screens/HabitsScreen';
import FriendsScreen from './src/screens/FriendsScreen';
import CompetitionsScreen from './src/screens/CompetitionsScreen';
import NotificationsScreen from './src/screens/NotificationsScreen';
import ProfileScreen from './src/screens/ProfileScreen';

const Stack = createStackNavigator();
const Tab = createBottomTabNavigator();
const queryClient = new QueryClient();

const AuthStack = () => (
  <Stack.Navigator screenOptions={{ headerShown: false }}>
    <Stack.Screen name="Login" component={LoginScreen} />
    <Stack.Screen name="Register" component={RegisterScreen} />
  </Stack.Navigator>
);

const MainTabs = () => (
  <Tab.Navigator
    screenOptions={({ route }) => ({
      tabBarIcon: ({ focused, color, size }) => {
        let iconName: string;

        if (route.name === 'Dashboard') {
          iconName = 'dashboard';
        } else if (route.name === 'Habits') {
          iconName = 'check-circle';
        } else if (route.name === 'Friends') {
          iconName = 'people';
        } else if (route.name === 'Competitions') {
          iconName = 'emoji-events';
        } else if (route.name === 'Notifications') {
          iconName = 'notifications';
        } else if (route.name === 'Profile') {
          iconName = 'person';
        } else {
          iconName = 'help';
        }

        return <Icon name={iconName} size={size} color={color} />;
      },
      tabBarActiveTintColor: '#1976d2',
      tabBarInactiveTintColor: 'gray',
    })}
  >
    <Tab.Screen name="Dashboard" component={DashboardScreen} />
    <Tab.Screen name="Habits" component={HabitsScreen} />
    <Tab.Screen name="Friends" component={FriendsScreen} />
    <Tab.Screen name="Competitions" component={CompetitionsScreen} />
    <Tab.Screen name="Notifications" component={NotificationsScreen} />
    <Tab.Screen name="Profile" component={ProfileScreen} />
  </Tab.Navigator>
);

const AppNavigator = () => {
  const { user, isLoading } = useAuth();

  if (isLoading) {
    return null; // You can add a loading screen here
  }

  return (
    <NavigationContainer>
      {user ? <MainTabs /> : <AuthStack />}
    </NavigationContainer>
  );
};

const App = (): React.JSX.Element => {
  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <StatusBar barStyle="dark-content" backgroundColor="#ffffff" />
        <AppNavigator />
      </AuthProvider>
    </QueryClientProvider>
  );
};

export default App;