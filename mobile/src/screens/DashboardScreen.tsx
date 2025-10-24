import React from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  Dimensions,
} from 'react-native';
import { useAuth } from '../contexts/AuthContext';
import { useQuery } from '@tanstack/react-query';
import { habitAPI } from '../services/api';

const { width } = Dimensions.get('window');

const DashboardScreen = () => {
  const { user } = useAuth();
  
  const { data: habits = [] } = useQuery({
    queryKey: ['habits'],
    queryFn: habitAPI.getHabits,
  });

  const stats = [
    { title: 'Active Habits', value: habits.length.toString(), color: '#1976d2' },
    { title: 'Current Streak', value: '12 days', color: '#4caf50' },
    { title: 'Friends', value: '8', color: '#ff9800' },
    { title: 'Competitions', value: '3', color: '#9c27b0' },
  ];

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.welcomeText}>
          Welcome back, {user?.username}!
        </Text>
        <Text style={styles.subtitle}>
          Track your habits and build better routines
        </Text>
      </View>

      <View style={styles.statsContainer}>
        {stats.map((stat, index) => (
          <View key={index} style={[styles.statCard, { backgroundColor: stat.color }]}>
            <Text style={styles.statValue}>{stat.value}</Text>
            <Text style={styles.statTitle}>{stat.title}</Text>
          </View>
        ))}
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Recent Habits</Text>
        {habits.length === 0 ? (
          <View style={styles.emptyState}>
            <Text style={styles.emptyText}>No habits yet</Text>
            <Text style={styles.emptySubtext}>
              Start by creating your first habit!
            </Text>
          </View>
        ) : (
          habits.slice(0, 3).map((habit) => (
            <View key={habit.id} style={styles.habitCard}>
              <Text style={styles.habitName}>{habit.name}</Text>
              <Text style={styles.habitType}>{habit.type}</Text>
            </View>
          ))
        )}
      </View>

      <View style={styles.quickActions}>
        <Text style={styles.sectionTitle}>Quick Actions</Text>
        <View style={styles.actionButtons}>
          <TouchableOpacity style={styles.actionButton}>
            <Text style={styles.actionButtonText}>Add Habit</Text>
          </TouchableOpacity>
          <TouchableOpacity style={styles.actionButton}>
            <Text style={styles.actionButtonText}>View Friends</Text>
          </TouchableOpacity>
        </View>
      </View>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  header: {
    padding: 20,
    backgroundColor: '#ffffff',
    marginBottom: 20,
  },
  welcomeText: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 8,
  },
  subtitle: {
    fontSize: 16,
    color: '#666',
  },
  statsContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    paddingHorizontal: 20,
    marginBottom: 20,
  },
  statCard: {
    width: (width - 60) / 2,
    padding: 20,
    borderRadius: 12,
    marginBottom: 10,
    marginRight: 10,
    alignItems: 'center',
  },
  statValue: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#ffffff',
    marginBottom: 4,
  },
  statTitle: {
    fontSize: 14,
    color: '#ffffff',
    textAlign: 'center',
  },
  section: {
    backgroundColor: '#ffffff',
    marginHorizontal: 20,
    marginBottom: 20,
    borderRadius: 12,
    padding: 20,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 16,
  },
  emptyState: {
    alignItems: 'center',
    paddingVertical: 40,
  },
  emptyText: {
    fontSize: 16,
    color: '#666',
    marginBottom: 8,
  },
  emptySubtext: {
    fontSize: 14,
    color: '#999',
  },
  habitCard: {
    backgroundColor: '#f8f9fa',
    padding: 16,
    borderRadius: 8,
    marginBottom: 12,
  },
  habitName: {
    fontSize: 16,
    fontWeight: '600',
    color: '#333',
    marginBottom: 4,
  },
  habitType: {
    fontSize: 14,
    color: '#666',
  },
  quickActions: {
    backgroundColor: '#ffffff',
    marginHorizontal: 20,
    marginBottom: 20,
    borderRadius: 12,
    padding: 20,
  },
  actionButtons: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  actionButton: {
    flex: 1,
    backgroundColor: '#1976d2',
    paddingVertical: 12,
    paddingHorizontal: 20,
    borderRadius: 8,
    marginHorizontal: 5,
    alignItems: 'center',
  },
  actionButtonText: {
    color: '#ffffff',
    fontSize: 16,
    fontWeight: '600',
  },
});

export default DashboardScreen;
