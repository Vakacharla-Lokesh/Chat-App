import React, { useEffect, useState } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import socket from './socket';
import Chat from './components/Chat';

function App() {
  const [username, setUsername] = useState('');

  useEffect(() => {
    if (username) {
      socket.auth = { username };
      socket.connect();
    }
  }, [username]);

  return (
    <Router>
      <Routes>
        <Route path="/chat" element={<Chat username={username} />} />
        <Route
          path="/"
          element={
            <div className="p-4">
              <input
                className="border p-2"
                placeholder="Enter username"
                value={username}
                onChange={e => setUsername(e.target.value)}
              />
            </div>
          }
        />
      </Routes>
    </Router>
  );
}

export default App;