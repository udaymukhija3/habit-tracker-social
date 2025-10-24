import React from 'react';
import {
  Container,
  Typography,
  Box,
  Card,
  CardContent,
  CardActions,
  Button,
  Chip,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Divider,
} from '@mui/material';
import {
  Notifications as NotificationsIcon,
  PersonAdd,
  EmojiEvents,
  TrendingUp,
  Schedule,
} from '@mui/icons-material';

const NotificationsPage: React.FC = () => {
  // Mock data for now
  const notifications = [
    {
      id: 1,
      type: 'FRIEND_REQUEST',
      title: 'New Friend Request',
      message: 'John Doe wants to be your friend',
      status: 'UNREAD',
      createdAt: '2024-01-20T10:30:00Z',
    },
    {
      id: 2,
      type: 'STREAK_MILESTONE',
      title: 'Streak Milestone!',
      message: 'Congratulations! You\'ve completed 7 days of your morning workout streak',
      status: 'READ',
      createdAt: '2024-01-19T08:00:00Z',
    },
    {
      id: 3,
      type: 'COMPETITION_INVITE',
      title: 'Competition Invitation',
      message: 'You\'ve been invited to join the "30-Day Fitness Challenge"',
      status: 'UNREAD',
      createdAt: '2024-01-18T15:45:00Z',
    },
  ];

  const getNotificationIcon = (type: string) => {
    switch (type) {
      case 'FRIEND_REQUEST':
        return <PersonAdd color="primary" />;
      case 'STREAK_MILESTONE':
        return <TrendingUp color="success" />;
      case 'COMPETITION_INVITE':
        return <EmojiEvents color="warning" />;
      case 'REMINDER':
        return <Schedule color="info" />;
      default:
        return <NotificationsIcon />;
    }
  };

  const getNotificationColor = (type: string) => {
    switch (type) {
      case 'FRIEND_REQUEST':
        return 'primary';
      case 'STREAK_MILESTONE':
        return 'success';
      case 'COMPETITION_INVITE':
        return 'warning';
      case 'REMINDER':
        return 'info';
      default:
        return 'default';
    }
  };

  const unreadCount = notifications.filter(n => n.status === 'UNREAD').length;

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={4}>
        <Typography variant="h4" component="h1">
          Notifications
        </Typography>
        {unreadCount > 0 && (
          <Chip
            label={`${unreadCount} unread`}
            color="primary"
            variant="outlined"
          />
        )}
      </Box>

      {notifications.length === 0 ? (
        <Box textAlign="center" py={8}>
          <NotificationsIcon sx={{ fontSize: 64, color: 'text.secondary', mb: 2 }} />
          <Typography variant="h6" color="text.secondary" gutterBottom>
            No notifications yet
          </Typography>
          <Typography variant="body2" color="text.secondary">
            You'll receive notifications for friend requests, achievements, and more!
          </Typography>
        </Box>
      ) : (
        <Card>
          <List>
            {notifications.map((notification, index) => (
              <React.Fragment key={notification.id}>
                <ListItem
                  sx={{
                    backgroundColor: notification.status === 'UNREAD' ? 'action.hover' : 'transparent',
                    '&:hover': {
                      backgroundColor: 'action.selected',
                    },
                  }}
                >
                  <ListItemIcon>
                    {getNotificationIcon(notification.type)}
                  </ListItemIcon>
                  <ListItemText
                    primary={
                      <Box display="flex" alignItems="center" gap={1}>
                        <Typography variant="subtitle1" fontWeight={notification.status === 'UNREAD' ? 'bold' : 'normal'}>
                          {notification.title}
                        </Typography>
                        {notification.status === 'UNREAD' && (
                          <Chip
                            label="New"
                            size="small"
                            color={getNotificationColor(notification.type) as any}
                          />
                        )}
                      </Box>
                    }
                    secondary={
                      <Box>
                        <Typography variant="body2" color="text.secondary">
                          {notification.message}
                        </Typography>
                        <Typography variant="caption" color="text.secondary">
                          {new Date(notification.createdAt).toLocaleString()}
                        </Typography>
                      </Box>
                    }
                  />
                  <CardActions>
                    {notification.status === 'UNREAD' && (
                      <Button size="small" variant="outlined">
                        Mark as Read
                      </Button>
                    )}
                    <Button size="small" color="error">
                      Delete
                    </Button>
                  </CardActions>
                </ListItem>
                {index < notifications.length - 1 && <Divider />}
              </React.Fragment>
            ))}
          </List>
        </Card>
      )}
    </Container>
  );
};

export default NotificationsPage;
