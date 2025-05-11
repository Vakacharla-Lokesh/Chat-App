import React, { useEffect, useState, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import MessageList from './MessageList';
import MessageInput from './MessageInput';
import socket from '../socket';

export default function Chat({ username }) {
  const [messages, setMessages] = useState([]);
  const [conversations, setConversations] = useState([]);
  const [currentConversation, setCurrentConversation] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const messageEndRef = useRef(null);

  useEffect(() => {
    // Redirect if no username
    if (!username) {
      navigate('/');
      return;
    }

    // Connect to WebSocket
    socket.connect(username)
      .then(() => {
        // Subscribe to personal messages
        socket.subscribe(`/user/${username}/queue/messages`, message => {
          setMessages(prev => [...prev, message]);
        });
        
        // Subscribe to global chat
        socket.subscribe('/topic/public', message => {
          setMessages(prev => [...prev, message]);
        });
        
        // Get conversation list
        fetchConversations();
        
        setLoading(false);
      })
      .catch(error => {
        console.error('Failed to connect:', error);
        setLoading(false);
      });
      
    return () => {
      socket.disconnect();
    };
  }, [username, navigate]);
  
  useEffect(() => {
    // Scroll to bottom when new messages arrive
    messageEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);
  
  const fetchConversations = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch('/api/conversations', {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      
      if (response.ok) {
        const data = await response.json();
        setConversations(data);
      }
    } catch (error) {
      console.error('Error fetching conversations:', error);
    }
  };
  
  const sendMessage = content => {
    const message = {
      sender: username,
      content,
      timestamp: new Date().toISOString()
    };
    
    if (currentConversation) {
      message.conversationId = currentConversation.id;
      socket.sendMessage(`/app/chat/${currentConversation.id}`, message);
    } else {
      socket.sendMessage('/app/chat.public', message);
    }
    
    // Optimistically add message to UI
    setMessages(prev => [...prev, message]);
  };

  if (loading) {
    return <div className="flex items-center justify-center h-screen">Loading...</div>;
  }

  return (
    <div className="flex h-screen bg-gray-100">
      {/* Sidebar */}
      <div className="w-64 bg-white border-r border-gray-200">
        <div className="p-4 border-b border-gray-200">
          <h2 className="text-lg font-semibold">Conversations</h2>
        </div>
        <div className="overflow-y-auto h-full">
          {conversations.map(conv => (
            <div 
              key={conv.id}
              className={`p-3 cursor-pointer hover:bg-gray-100 ${
                currentConversation?.id === conv.id ? 'bg-blue-50' : ''
              }`}
              onClick={() => setCurrentConversation(conv)}
            >
              <div className="font-medium">{conv.name || `Chat with ${conv.participantName}`}</div>
              <div className="text-sm text-gray-500 truncate">
                {conv.lastMessage || 'No messages yet'}
              </div>
            </div>
          ))}
        </div>
      </div>
      
      {/* Main chat area */}
      <div className="flex-1 flex flex-col">
        {/* Chat header */}
        <div className="bg-white p-4 border-b border-gray-200">
          <h3 className="text-lg font-semibold">
            {currentConversation ? 
              (currentConversation.name || `Chat with ${currentConversation.participantName}`) : 
              'Public Chat'}
          </h3>
        </div>
        
        {/* Messages */}
        <div className="flex-1 overflow-y-auto p-4 bg-gray-50">
          <MessageList 
            messages={messages} 
            currentUser={username} 
          />
          <div ref={messageEndRef} />
        </div>
        
        {/* Message input */}
        <div className="bg-white p-4 border-t border-gray-200">
          <MessageInput onSend={sendMessage} />
        </div>
      </div>
    </div>
  );
}