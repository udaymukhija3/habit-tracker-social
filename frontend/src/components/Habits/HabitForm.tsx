import React, { useState, useEffect } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Button,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Box,
  Typography,
} from '@mui/material';
import { Habit, HabitType, HabitFrequency } from '../../types';

interface HabitFormProps {
  open: boolean;
  onClose: () => void;
  onSubmit: (habit: Omit<Habit, 'id' | 'createdAt' | 'updatedAt'>) => void;
  habit?: Habit | null;
}

const HabitForm: React.FC<HabitFormProps> = ({ open, onClose, onSubmit, habit }) => {
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    type: HabitType.HEALTH,
    frequency: HabitFrequency.DAILY,
    targetValue: 1,
    targetUnit: 'times',
  });

  useEffect(() => {
    if (habit) {
      setFormData({
        name: habit.name,
        description: habit.description || '',
        type: habit.type,
        frequency: habit.frequency,
        targetValue: habit.targetValue,
        targetUnit: habit.targetUnit,
      });
    } else {
      setFormData({
        name: '',
        description: '',
        type: HabitType.HEALTH,
        frequency: HabitFrequency.DAILY,
        targetValue: 1,
        targetUnit: 'times',
      });
    }
  }, [habit]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === 'targetValue' ? parseInt(value) || 0 : value,
    }));
  };

  const handleSelectChange = (e: any) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit(formData);
    onClose();
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>
        {habit ? 'Edit Habit' : 'Create New Habit'}
      </DialogTitle>
      <form onSubmit={handleSubmit}>
        <DialogContent>
          <TextField
            autoFocus
            margin="dense"
            name="name"
            label="Habit Name"
            fullWidth
            variant="outlined"
            value={formData.name}
            onChange={handleChange}
            required
          />
          
          <TextField
            margin="dense"
            name="description"
            label="Description"
            fullWidth
            multiline
            rows={3}
            variant="outlined"
            value={formData.description}
            onChange={handleChange}
          />

          <Box sx={{ display: 'flex', gap: 2, mt: 2 }}>
            <FormControl fullWidth>
              <InputLabel>Type</InputLabel>
              <Select
                name="type"
                value={formData.type}
                label="Type"
                onChange={handleSelectChange}
              >
                {Object.values(HabitType).map((type) => (
                  <MenuItem key={type} value={type}>
                    {type}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>

            <FormControl fullWidth>
              <InputLabel>Frequency</InputLabel>
              <Select
                name="frequency"
                value={formData.frequency}
                label="Frequency"
                onChange={handleSelectChange}
              >
                {Object.values(HabitFrequency).map((freq) => (
                  <MenuItem key={freq} value={freq}>
                    {freq}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Box>

          <Box sx={{ display: 'flex', gap: 2, mt: 2 }}>
            <TextField
              name="targetValue"
              label="Target Value"
              type="number"
              fullWidth
              value={formData.targetValue}
              onChange={handleChange}
              required
            />
            <TextField
              name="targetUnit"
              label="Unit"
              fullWidth
              value={formData.targetUnit}
              onChange={handleChange}
              required
            />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={onClose}>Cancel</Button>
          <Button type="submit" variant="contained">
            {habit ? 'Update' : 'Create'}
          </Button>
        </DialogActions>
      </form>
    </Dialog>
  );
};

export default HabitForm;
