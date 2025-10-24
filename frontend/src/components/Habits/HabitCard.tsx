import React from 'react';
import {
  Card,
  CardContent,
  CardActions,
  Typography,
  Button,
  Chip,
  Box,
  LinearProgress,
  IconButton,
} from '@mui/material';
import {
  CheckCircle,
  Edit,
  Delete,
  TrendingUp,
} from '@mui/icons-material';
import { Habit, HabitType } from '../../types';

interface HabitCardProps {
  habit: Habit;
  onComplete: (habitId: number) => void;
  onEdit: (habit: Habit) => void;
  onDelete: (habitId: number) => void;
  currentStreak?: number;
  completionRate?: number;
}

const HabitCard: React.FC<HabitCardProps> = ({
  habit,
  onComplete,
  onEdit,
  onDelete,
  currentStreak = 0,
  completionRate = 0,
}) => {
  const getTypeColor = (type: HabitType) => {
    const colors = {
      [HabitType.HEALTH]: 'success',
      [HabitType.PRODUCTIVITY]: 'primary',
      [HabitType.LEARNING]: 'info',
      [HabitType.SOCIAL]: 'secondary',
      [HabitType.FINANCE]: 'warning',
      [HabitType.MINDFULNESS]: 'default',
      [HabitType.CREATIVE]: 'error',
      [HabitType.MAINTENANCE]: 'default',
    };
    return colors[type] as any;
  };

  return (
    <Card sx={{ mb: 2, position: 'relative' }}>
      <CardContent>
        <Box display="flex" justifyContent="space-between" alignItems="flex-start" mb={2}>
          <Typography variant="h6" component="h2">
            {habit.name}
          </Typography>
          <Chip
            label={habit.type}
            color={getTypeColor(habit.type)}
            size="small"
          />
        </Box>

        {habit.description && (
          <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
            {habit.description}
          </Typography>
        )}

        <Box display="flex" justifyContent="space-between" alignItems="center" mb={1}>
          <Typography variant="body2">
            Target: {habit.targetValue} {habit.targetUnit} per {habit.frequency.toLowerCase()}
          </Typography>
          <Typography variant="body2" color="primary">
            Streak: {currentStreak} days
          </Typography>
        </Box>

        <LinearProgress
          variant="determinate"
          value={completionRate}
          sx={{ mb: 1 }}
        />

        <Typography variant="caption" color="text.secondary">
          {completionRate.toFixed(0)}% completion rate
        </Typography>
      </CardContent>

      <CardActions>
        <Button
          variant="contained"
          startIcon={<CheckCircle />}
          onClick={() => onComplete(habit.id)}
          sx={{ mr: 1 }}
        >
          Complete
        </Button>
        <IconButton
          onClick={() => onEdit(habit)}
          color="primary"
        >
          <Edit />
        </IconButton>
        <IconButton
          onClick={() => onDelete(habit.id)}
          color="error"
        >
          <Delete />
        </IconButton>
        <Box sx={{ flexGrow: 1 }} />
        <Chip
          icon={<TrendingUp />}
          label={`${currentStreak} day streak`}
          color="success"
          size="small"
        />
      </CardActions>
    </Card>
  );
};

export default HabitCard;
