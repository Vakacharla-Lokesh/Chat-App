import React, { useState } from 'react';

export default function MessageInput({ onSend }) {
  const [text, setText] = useState('');

  const handleSubmit = e => {
    e.preventDefault();
    if (text.trim()) {
      onSend(text.trim());
      setText('');
    }
  };

  return (
    <form onSubmit={handleSubmit} className="flex items-center">
      <input
        type="text"
        className="flex-1 border border-gray-300 rounded-l-lg py-2 px-4 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
        value={text}
        onChange={e => setText(e.target.value)}
        placeholder="Type a message..."
      />
      <button 
        type="submit" 
        className="bg-blue-500 hover:bg-blue-600 text-white font-medium py-2 px-6 rounded-r-lg transition duration-150 ease-in-out"
        disabled={!text.trim()}
      >
        Send
      </button>
    </form>
  );
}