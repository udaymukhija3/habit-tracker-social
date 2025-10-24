import React from 'react';
import {
  Container,
  Typography,
  Grid,
  Card,
  CardContent,
  CardActions,
  Button,
  Box,
  Chip,
} from '@mui/material';
import {
  Add as AddIcon,
  TrendingUp,
  People,
  EmojiEvents,
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

const Dashboard: React.FC = () => {
  const { user } = useAuth();
  const navigate = useNavigate();

  const stats = [
    { title: 'Active Habits', value: '5', icon: <TrendingUp />, color: 'primary' },
    { title: 'Current Streak', value: '12 days', icon: <TrendingUp />, color: 'success' },
    { title: 'Friends', value: '8', icon: <People />, color: 'secondary' },
    { title: 'Competitions', value: '3', icon: <EmojiEvents />, color: 'warning' },
  ];

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={4}>
        <Typography variant="h4" component="h1">
          Welcome back, {user?.username}!
        </Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => navigate('/habits')}
        >
          Add Habit
        </Button>
      </Box>

      <Grid container spacing={3}>
        {stats.map((stat, index) => (
          <Grid item xs={12} sm={6} md={3} key={index}>
            <Card>
              <CardContent>
                <Box display="flex" alignItems="center" mb={2}>
                  <Box mr={2} color={`${stat.color}.main`}>
                    {stat.icon}
                  </Box>
                  <Typography variant="h6" component="h2">
                    {stat.title}
                  </Typography>
                </Box>
                <Typography variant="h4" component="div" color={`${stat.color}.main`}>
                  {stat.value}
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        ))}

        <Grid item xs={12} md={8}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Recent Habits
              </Typography>
              <Typography variant="body2" color="text.secondary">
                No habits yet. Start by creating your first habit!
              </Typography>
              <CardActions>
                <Button
                  variant="outlined"
                  onClick={() => navigate('/habits')}
                >
                  Manage Habits
                </Button>
              </CardActions>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Quick Actions
              </Typography>
              <Box display="flex" flexDirection="column" gap={1}>
                <Button
                  variant="outlined"
                  fullWidth
                  onClick={() => navigate('/habits')}
                >
                  View All Habits
                </Button>
                <Button
                  variant="outlined"
                  fullWidth
                  onClick={() => navigate('/friends')}
                >
                  Find Friends
                </Button>
                <Button
                  variant="outlined"
                  fullWidth
                  onClick={() => navigate('/competitions')}
                >
                  Join Competition
                </Button>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Container>
  );
};

export default Dashboard;
