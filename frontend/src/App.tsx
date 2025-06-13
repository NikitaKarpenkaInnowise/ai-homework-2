import { useState, useEffect } from 'react';
import axios from 'axios';
import { User } from './types/user';
import './App.css';

function App() {
  const [users, setUsers] = useState<User[]>([]);
  const [selectedUser, setSelectedUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      const response = await axios.get<User[]>('https://jsonplaceholder.typicode.com/users');
      setUsers(response.data);
      setLoading(false);
    } catch (error) {
      console.error('Error fetching users:', error);
      setLoading(false);
    }
  };

  const handleDeleteUser = (userId: number) => {
    setUsers(users.filter(user => user.id !== userId));
  };

  const handleUserClick = (user: User) => {
    setSelectedUser(user);
  };

  const closeModal = () => {
    setSelectedUser(null);
  };

  if (loading) {
    return <div className="loading">Loading...</div>;
  }

  return (
    <div className="app">
      <h1>User Directory</h1>
      <div className="user-table">
        <table>
          <thead>
            <tr>
              <th>Name/Email</th>
              <th>Address</th>
              <th>Phone</th>
              <th>Website</th>
              <th>Company</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {users.map(user => (
              <tr key={user.id}>
                <td>
                  <div className="user-info">
                    <strong>{user.name}</strong>
                    <span>{user.email}</span>
                  </div>
                </td>
                <td>{`${user.address.street}, ${user.address.city}`}</td>
                <td>{user.phone}</td>
                <td>
                  <a href={`https://${user.website}`} target="_blank" rel="noopener noreferrer">
                    {user.website}
                  </a>
                </td>
                <td>{user.company.name}</td>
                <td>
                  <button onClick={() => handleUserClick(user)} className="view-btn">
                    View
                  </button>
                  <button onClick={() => handleDeleteUser(user.id)} className="delete-btn">
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {selectedUser && (
        <div className="modal-overlay" onClick={closeModal}>
          <div className="modal-content" onClick={e => e.stopPropagation()}>
            <button className="close-btn" onClick={closeModal}>×</button>
            <h2>{selectedUser.name}</h2>
            <div className="user-details">
              <div className="detail-group">
                <h3>Contact Information</h3>
                <p><strong>Username:</strong> {selectedUser.username}</p>
                <p><strong>Email:</strong> {selectedUser.email}</p>
                <p><strong>Phone:</strong> {selectedUser.phone}</p>
                <p><strong>Website:</strong> {selectedUser.website}</p>
              </div>
              
              <div className="detail-group">
                <h3>Address</h3>
                <p>{selectedUser.address.street}</p>
                <p>{selectedUser.address.suite}</p>
                <p>{selectedUser.address.city}, {selectedUser.address.zipcode}</p>
                <a 
                  href={`https://www.google.com/maps?q=${selectedUser.address.geo.lat},${selectedUser.address.geo.lng}`}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="map-link"
                >
                  View on Map
                </a>
              </div>

              <div className="detail-group">
                <h3>Company</h3>
                <p><strong>Name:</strong> {selectedUser.company.name}</p>
                <p><strong>Catch Phrase:</strong> {selectedUser.company.catchPhrase}</p>
                <p><strong>Business:</strong> {selectedUser.company.bs}</p>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default App; 