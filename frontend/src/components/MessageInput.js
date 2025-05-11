import React, { useState } from 'react';

export default function MessageInput({ onSend }) {
  const [text, setText] = useState('');

  const submit = e => {
    e.preventDefault();
    if (text.trim()) onSend(text);
    setText('');
  };

  return (
    <form onSubmit={submit} className="flex">
      <input
        className="flex-grow border p-2"
        value={text}
        onChange={e => setText(e.target.value)}
        placeholder="Type a message..."
      />
      <button type="submit" className="ml-2 p-2 bg-blue-500 text-white rounded">
        Send
      </button>
    </form>
  );
}