import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import HabitCard from './HabitCard';
import { Habit, HabitType, HabitFrequency } from '../../types';

const mockHabit: Habit = {
  id: 1,
  name: 'Test Habit',
  description: 'Test Description',
  type: HabitType.HEALTH,
  frequency: HabitFrequency.DAILY,
  targetValue: 1,
  targetUnit: 'times',
  isActive: true,
  createdAt: new Date().toISOString(),
  updatedAt: new Date().toISOString(),
  userId: 1,
};

describe('HabitCard', () => {
  const mockOnComplete = jest.fn();
  const mockOnEdit = jest.fn();
  const mockOnDelete = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders habit information correctly', () => {
    render(
      <HabitCard
        habit={mockHabit}
        onComplete={mockOnComplete}
        onEdit={mockOnEdit}
        onDelete={mockOnDelete}
        currentStreak={5}
        completionRate={75}
      />
    );

    expect(screen.getByText('Test Habit')).toBeInTheDocument();
    expect(screen.getByText('Test Description')).toBeInTheDocument();
    expect(screen.getByText(/target: 1 times per daily/i)).toBeInTheDocument();
    expect(screen.getByText(/streak: 5 days/i)).toBeInTheDocument();
    expect(screen.getByText(/75% completion rate/i)).toBeInTheDocument();
  });

  it('calls onComplete when complete button is clicked', () => {
    render(
      <HabitCard
        habit={mockHabit}
        onComplete={mockOnComplete}
        onEdit={mockOnEdit}
        onDelete={mockOnDelete}
      />
    );

    fireEvent.click(screen.getByText('Complete'));
    expect(mockOnComplete).toHaveBeenCalledWith(1);
  });

  it('calls onEdit when edit button is clicked', () => {
    render(
      <HabitCard
        habit={mockHabit}
        onComplete={mockOnComplete}
        onEdit={mockOnEdit}
        onDelete={mockOnDelete}
      />
    );

    const editButton = screen.getByLabelText(/edit/i);
    fireEvent.click(editButton);
    expect(mockOnEdit).toHaveBeenCalledWith(mockHabit);
  });

  it('calls onDelete when delete button is clicked', () => {
    render(
      <HabitCard
        habit={mockHabit}
        onComplete={mockOnComplete}
        onEdit={mockOnEdit}
        onDelete={mockOnDelete}
      />
    );

    const deleteButton = screen.getByLabelText(/delete/i);
    fireEvent.click(deleteButton);
    expect(mockOnDelete).toHaveBeenCalledWith(1);
  });

  it('displays streak information correctly', () => {
    render(
      <HabitCard
        habit={mockHabit}
        onComplete={mockOnComplete}
        onEdit={mockOnEdit}
        onDelete={mockOnDelete}
        currentStreak={10}
      />
    );

    expect(screen.getByText(/10 day streak/i)).toBeInTheDocument();
  });
});

