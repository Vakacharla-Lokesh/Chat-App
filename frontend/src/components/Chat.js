import React, { useEffect, useState } from 'react';
import MessageList from './MessageList';
import MessageInput from './MessageInput';
import socket from '../socket';

export default function Chat({ username }) {
  const [messages, setMessages] = useState([]);

  useEffect(() => {
    socket.emit('join', { username });

    socket.on('message', msg => {
      setMessages(prev => [...prev, msg]);
    });

    return () => {
      socket.off('message');
    };
  }, [username]);

  const sendMessage = content => {
    socket.emit('message', { username, content });
  };

  return (
    <div className="p-4">
      <MessageList messages={messages} />
      <MessageInput onSend={sendMessage} />
    </div>
  );
}