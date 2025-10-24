import React, { useState } from 'react';
import {
  Container,
  Typography,
  Card,
  CardContent,
  Avatar,
  Box,
  Grid,
  Chip,
  Button,
  TextField,
} from '@mui/material';
import {
  Edit as EditIcon,
  Save as SaveIcon,
} from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';

const ProfilePage: React.FC = () => {
  const { user } = useAuth();
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState({
    firstName: user?.firstName || '',
    lastName: user?.lastName || '',
    email: user?.email || '',
  });

  const handleEdit = () => {
    setIsEditing(true);
  };

  const handleSave = () => {
    // TODO: Implement profile update
    setIsEditing(false);
  };

  const handleCancel = () => {
    setFormData({
      firstName: user?.firstName || '',
      lastName: user?.lastName || '',
      email: user?.email || '',
    });
    setIsEditing(false);
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        Profile
      </Typography>

      <Grid container spacing={3}>
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent sx={{ textAlign: 'center' }}>
              <Avatar
                sx={{ width: 100, height: 100, mx: 'auto', mb: 2 }}
              >
                {user?.username.charAt(0).toUpperCase()}
              </Avatar>
              <Typography variant="h5" gutterBottom>
                {user?.username}
              </Typography>
              <Chip
                label={user?.role}
                color="primary"
                variant="outlined"
              />
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={8}>
          <Card>
            <CardContent>
              <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
                <Typography variant="h6">
                  Personal Information
                </Typography>
                {!isEditing ? (
                  <Button
                    variant="outlined"
                    startIcon={<EditIcon />}
                    onClick={handleEdit}
                  >
                    Edit
                  </Button>
                ) : (
                  <Box gap={1}>
                    <Button
                      variant="contained"
                      startIcon={<SaveIcon />}
                      onClick={handleSave}
                      sx={{ mr: 1 }}
                    >
                      Save
                    </Button>
                    <Button
                      variant="outlined"
                      onClick={handleCancel}
                    >
                      Cancel
                    </Button>
                  </Box>
                )}
              </Box>

              <Grid container spacing={2}>
                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label="First Name"
                    name="firstName"
                    value={formData.firstName}
                    onChange={handleChange}
                    disabled={!isEditing}
                  />
                </Grid>
                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label="Last Name"
                    name="lastName"
                    value={formData.lastName}
                    onChange={handleChange}
                    disabled={!isEditing}
                  />
                </Grid>
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label="Email"
                    name="email"
                    value={formData.email}
                    onChange={handleChange}
                    disabled={!isEditing}
                  />
                </Grid>
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label="Username"
                    value={user?.username}
                    disabled
                  />
                </Grid>
              </Grid>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Container>
  );
};

export default ProfilePage;
