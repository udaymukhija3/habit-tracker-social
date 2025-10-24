import React, { useState } from 'react';
import {
  Container,
  Typography,
  Button,
  Box,
  TextField,
  Card,
  CardContent,
  CardActions,
  Avatar,
  Chip,
  Tabs,
  Tab,
} from '@mui/material';
import {
  PersonAdd as PersonAddIcon,
  Search as SearchIcon,
} from '@mui/icons-material';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { friendshipAPI } from '../services/api';
import { User, Friendship } from '../types';

const FriendsPage: React.FC = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [tabValue, setTabValue] = useState(0);
  const queryClient = useQueryClient();

  const { data: friends = [] } = useQuery({
    queryKey: ['friends'],
    queryFn: friendshipAPI.getFriends,
  });

  const { data: pendingRequests = [] } = useQuery({
    queryKey: ['pendingRequests'],
    queryFn: friendshipAPI.getPendingRequests,
  });

  const { data: sentRequests = [] } = useQuery({
    queryKey: ['sentRequests'],
    queryFn: friendshipAPI.getSentRequests,
  });

  const acceptRequestMutation = useMutation({
    mutationFn: friendshipAPI.acceptFriendRequest,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['pendingRequests'] });
      queryClient.invalidateQueries({ queryKey: ['friends'] });
    },
  });

  const declineRequestMutation = useMutation({
    mutationFn: friendshipAPI.declineFriendRequest,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['pendingRequests'] });
    },
  });

  const removeFriendMutation = useMutation({
    mutationFn: friendshipAPI.removeFriend,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['friends'] });
    },
  });

  const handleAcceptRequest = (friendshipId: number) => {
    acceptRequestMutation.mutate(friendshipId);
  };

  const handleDeclineRequest = (friendshipId: number) => {
    declineRequestMutation.mutate(friendshipId);
  };

  const handleRemoveFriend = (friendshipId: number) => {
    if (window.confirm('Are you sure you want to remove this friend?')) {
      removeFriendMutation.mutate(friendshipId);
    }
  };

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setTabValue(newValue);
  };

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={4}>
        <Typography variant="h4" component="h1">
          Friends
        </Typography>
        <Button
          variant="contained"
          startIcon={<PersonAddIcon />}
        >
          Add Friend
        </Button>
      </Box>

      <Box mb={3}>
        <TextField
          fullWidth
          placeholder="Search friends..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          InputProps={{
            startAdornment: <SearchIcon sx={{ mr: 1, color: 'text.secondary' }} />,
          }}
        />
      </Box>

      <Box sx={{ borderBottom: 1, borderColor: 'divider', mb: 3 }}>
        <Tabs value={tabValue} onChange={handleTabChange}>
          <Tab label={`Friends (${friends.length})`} />
          <Tab label={`Pending (${pendingRequests.length})`} />
          <Tab label={`Sent (${sentRequests.length})`} />
        </Tabs>
      </Box>

      {tabValue === 0 && (
        <Box>
          {friends.length === 0 ? (
            <Typography color="text.secondary" textAlign="center" py={4}>
              No friends yet. Start by sending friend requests!
            </Typography>
          ) : (
            <Box display="flex" flexDirection="column" gap={2}>
              {friends.map((friend) => (
                <Card key={friend.id}>
                  <CardContent>
                    <Box display="flex" alignItems="center" gap={2}>
                      <Avatar>
                        {friend.username.charAt(0).toUpperCase()}
                      </Avatar>
                      <Box flexGrow={1}>
                        <Typography variant="h6">
                          {friend.username}
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                          {friend.email}
                        </Typography>
                      </Box>
                      <Chip label="Friend" color="success" />
                    </Box>
                  </CardContent>
                  <CardActions>
                    <Button
                      color="error"
                      onClick={() => handleRemoveFriend(friend.id)}
                    >
                      Remove
                    </Button>
                  </CardActions>
                </Card>
              ))}
            </Box>
          )}
        </Box>
      )}

      {tabValue === 1 && (
        <Box>
          {pendingRequests.length === 0 ? (
            <Typography color="text.secondary" textAlign="center" py={4}>
              No pending friend requests.
            </Typography>
          ) : (
            <Box display="flex" flexDirection="column" gap={2}>
              {pendingRequests.map((request) => (
                <Card key={request.id}>
                  <CardContent>
                    <Box display="flex" alignItems="center" gap={2}>
                      <Avatar>
                        {request.requester.username.charAt(0).toUpperCase()}
                      </Avatar>
                      <Box flexGrow={1}>
                        <Typography variant="h6">
                          {request.requester.username}
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                          {request.requester.email}
                        </Typography>
                      </Box>
                      <Chip label="Pending" color="warning" />
                    </Box>
                  </CardContent>
                  <CardActions>
                    <Button
                      variant="contained"
                      color="success"
                      onClick={() => handleAcceptRequest(request.id)}
                    >
                      Accept
                    </Button>
                    <Button
                      color="error"
                      onClick={() => handleDeclineRequest(request.id)}
                    >
                      Decline
                    </Button>
                  </CardActions>
                </Card>
              ))}
            </Box>
          )}
        </Box>
      )}

      {tabValue === 2 && (
        <Box>
          {sentRequests.length === 0 ? (
            <Typography color="text.secondary" textAlign="center" py={4}>
              No sent friend requests.
            </Typography>
          ) : (
            <Box display="flex" flexDirection="column" gap={2}>
              {sentRequests.map((request) => (
                <Card key={request.id}>
                  <CardContent>
                    <Box display="flex" alignItems="center" gap={2}>
                      <Avatar>
                        {request.addressee.username.charAt(0).toUpperCase()}
                      </Avatar>
                      <Box flexGrow={1}>
                        <Typography variant="h6">
                          {request.addressee.username}
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                          {request.addressee.email}
                        </Typography>
                      </Box>
                      <Chip label="Sent" color="info" />
                    </Box>
                  </CardContent>
                </Card>
              ))}
            </Box>
          )}
        </Box>
      )}
    </Container>
  );
};

export default FriendsPage;
