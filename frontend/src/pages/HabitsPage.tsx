import React, { useState } from 'react';
import {
  Container,
  Typography,
  Button,
  Box,
  Fab,
} from '@mui/material';
import { Add as AddIcon } from '@mui/icons-material';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { habitAPI } from '../services/api';
import { Habit } from '../types';
import HabitCard from '../components/Habits/HabitCard';
import HabitForm from '../components/Habits/HabitForm';

const HabitsPage: React.FC = () => {
  const [formOpen, setFormOpen] = useState(false);
  const [editingHabit, setEditingHabit] = useState<Habit | null>(null);
  const queryClient = useQueryClient();

  const { data: habits = [], isLoading } = useQuery({
    queryKey: ['habits'],
    queryFn: habitAPI.getHabits,
  });

  const createHabitMutation = useMutation({
    mutationFn: habitAPI.createHabit,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['habits'] });
      setFormOpen(false);
    },
  });

  const updateHabitMutation = useMutation({
    mutationFn: ({ id, habit }: { id: number; habit: Partial<Habit> }) =>
      habitAPI.updateHabit(id, habit),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['habits'] });
      setFormOpen(false);
      setEditingHabit(null);
    },
  });

  const deleteHabitMutation = useMutation({
    mutationFn: habitAPI.deleteHabit,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['habits'] });
    },
  });

  const completeHabitMutation = useMutation({
    mutationFn: habitAPI.completeHabit,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['habits'] });
    },
  });

  const handleCreateHabit = (habitData: Omit<Habit, 'id' | 'createdAt' | 'updatedAt'>) => {
    createHabitMutation.mutate(habitData);
  };

  const handleUpdateHabit = (habitData: Omit<Habit, 'id' | 'createdAt' | 'updatedAt'>) => {
    if (editingHabit) {
      updateHabitMutation.mutate({ id: editingHabit.id, habit: habitData });
    }
  };

  const handleEditHabit = (habit: Habit) => {
    setEditingHabit(habit);
    setFormOpen(true);
  };

  const handleDeleteHabit = (habitId: number) => {
    if (window.confirm('Are you sure you want to delete this habit?')) {
      deleteHabitMutation.mutate(habitId);
    }
  };

  const handleCompleteHabit = (habitId: number) => {
    completeHabitMutation.mutate(habitId, {
      completionDate: new Date().toISOString(),
    });
  };

  const handleCloseForm = () => {
    setFormOpen(false);
    setEditingHabit(null);
  };

  if (isLoading) {
    return (
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        <Typography>Loading habits...</Typography>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={4}>
        <Typography variant="h4" component="h1">
          My Habits
        </Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => setFormOpen(true)}
        >
          Add Habit
        </Button>
      </Box>

      {habits.length === 0 ? (
        <Box textAlign="center" py={8}>
          <Typography variant="h6" color="text.secondary" gutterBottom>
            No habits yet
          </Typography>
          <Typography variant="body2" color="text.secondary" paragraph>
            Start building better habits by creating your first one!
          </Typography>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={() => setFormOpen(true)}
          >
            Create Your First Habit
          </Button>
        </Box>
      ) : (
        <Box>
          {habits.map((habit) => (
            <HabitCard
              key={habit.id}
              habit={habit}
              onComplete={handleCompleteHabit}
              onEdit={handleEditHabit}
              onDelete={handleDeleteHabit}
            />
          ))}
        </Box>
      )}

      <HabitForm
        open={formOpen}
        onClose={handleCloseForm}
        onSubmit={editingHabit ? handleUpdateHabit : handleCreateHabit}
        habit={editingHabit}
      />
    </Container>
  );
};

export default HabitsPage;
