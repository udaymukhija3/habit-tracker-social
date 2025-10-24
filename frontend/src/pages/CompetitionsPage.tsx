import React from 'react';
import {
  Container,
  Typography,
  Button,
  Box,
  Card,
  CardContent,
  CardActions,
  Chip,
  Grid,
} from '@mui/material';
import {
  Add as AddIcon,
  EmojiEvents,
  People,
  Schedule,
} from '@mui/icons-material';

const CompetitionsPage: React.FC = () => {
  // Mock data for now
  const competitions = [
    {
      id: 1,
      name: '30-Day Fitness Challenge',
      description: 'Complete your daily workout for 30 days straight',
      type: 'STREAK',
      startDate: '2024-01-01',
      endDate: '2024-01-31',
      participants: 12,
      isActive: true,
    },
    {
      id: 2,
      name: 'Reading Marathon',
      description: 'Read for 1 hour every day this month',
      type: 'COMPLETION_COUNT',
      startDate: '2024-01-15',
      endDate: '2024-02-15',
      participants: 8,
      isActive: true,
    },
  ];

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={4}>
        <Typography variant="h4" component="h1">
          Competitions
        </Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
        >
          Create Competition
        </Button>
      </Box>

      {competitions.length === 0 ? (
        <Box textAlign="center" py={8}>
          <Typography variant="h6" color="text.secondary" gutterBottom>
            No competitions yet
          </Typography>
          <Typography variant="body2" color="text.secondary" paragraph>
            Join or create a competition to challenge yourself and others!
          </Typography>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
          >
            Create Competition
          </Button>
        </Box>
      ) : (
        <Grid container spacing={3}>
          {competitions.map((competition) => (
            <Grid item xs={12} md={6} key={competition.id}>
              <Card>
                <CardContent>
                  <Box display="flex" justifyContent="space-between" alignItems="flex-start" mb={2}>
                    <Typography variant="h6" component="h2">
                      {competition.name}
                    </Typography>
                    <Chip
                      label={competition.isActive ? 'Active' : 'Ended'}
                      color={competition.isActive ? 'success' : 'default'}
                      size="small"
                    />
                  </Box>
                  
                  <Typography variant="body2" color="text.secondary" paragraph>
                    {competition.description}
                  </Typography>

                  <Box display="flex" gap={2} mb={2}>
                    <Box display="flex" alignItems="center" gap={0.5}>
                      <People fontSize="small" color="action" />
                      <Typography variant="body2">
                        {competition.participants} participants
                      </Typography>
                    </Box>
                    <Box display="flex" alignItems="center" gap={0.5}>
                      <Schedule fontSize="small" color="action" />
                      <Typography variant="body2">
                        {competition.type}
                      </Typography>
                    </Box>
                  </Box>

                  <Typography variant="body2" color="text.secondary">
                    {new Date(competition.startDate).toLocaleDateString()} - {new Date(competition.endDate).toLocaleDateString()}
                  </Typography>
                </CardContent>
                <CardActions>
                  <Button variant="contained" startIcon={<EmojiEvents />}>
                    Join Competition
                  </Button>
                  <Button variant="outlined">
                    View Details
                  </Button>
                </CardActions>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}
    </Container>
  );
};

export default CompetitionsPage;
