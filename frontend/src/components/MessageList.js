import React from 'react';

export default function MessageList({ messages, currentUser }) {
  const formatTime = (timestamp) => {
    if (!timestamp) return '';
    const date = new Date(timestamp);
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  };

  return (
    <div className="space-y-3">
      {messages.map((message, index) => {
        const isCurrentUser = message.sender === currentUser;
        
        return (
          <div 
            key={index}
            className={`flex ${isCurrentUser ? 'justify-end' : 'justify-start'}`}
          >
            <div 
              className={`max-w-xs md:max-w-md rounded-lg px-4 py-2 ${
                isCurrentUser 
                  ? 'bg-blue-500 text-white rounded-br-none' 
                  : 'bg-white border border-gray-200 rounded-bl-none'
              }`}
            >
              {!isCurrentUser && (
                <div className="font-medium text-sm text-gray-700 mb-1">
                  {message.sender}
                </div>
              )}
              <div>{message.content}</div>
              <div 
                className={`text-xs mt-1 text-right ${
                  isCurrentUser ? 'text-blue-100' : 'text-gray-500'
                }`}
              >
                {formatTime(message.timestamp)}
              </div>
            </div>
          </div>
        );
      })}
      {messages.length === 0 && (
        <div className="text-center text-gray-500 py-8">
          No messages yet. Start the conversation!
        </div>
      )}
    </div>
  );
}