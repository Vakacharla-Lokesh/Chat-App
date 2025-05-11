import React from 'react';

export default function MessageList({ messages }) {
  return (
    <div className="h-64 overflow-y-auto border p-2 mb-2">
      {messages.map((m, i) => (
        <div key={i} className="mb-1">
          <strong>{m.username}: </strong>{m.content}
        </div>
      ))}
    </div>
  );
}