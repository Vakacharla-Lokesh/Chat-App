import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

const SOCKET_URL = process.env.REACT_APP_SOCKET_URL || 'http://localhost:8080/ws-chat';
let stompClient = null;
let connected = false;
let subscribedTopics = new Set();
let messageCallbacks = [];

const socket = {
  connect: function(username) {
    if (connected) return Promise.resolve();
    
    return new Promise((resolve, reject) => {
      const socket = new SockJS(SOCKET_URL);
      stompClient = Stomp.over(socket);
      stompClient.debug = null; // Disable debug logs
      
      stompClient.connect(
        {
          username: username
        },
        frame => {
          connected = true;
          console.log('Connected to STOMP WebSocket');
          resolve(frame);
        },
        error => {
          console.error('STOMP error:', error);
          connected = false;
          reject(error);
        }
      );
    });
  },
  
  disconnect: function() {
    if (stompClient !== null) {
      stompClient.disconnect();
      stompClient = null;
      connected = false;
      subscribedTopics.clear();
    }
  },
  
  subscribe: function(topic, callback) {
    if (!connected) {
      console.error('Cannot subscribe, not connected to WebSocket');
      return null;
    }
    
    if (subscribedTopics.has(topic)) {
      // Already subscribed to this topic
      messageCallbacks.push({ topic, callback });
      return;
    }
    
    const subscription = stompClient.subscribe(topic, message => {
      const parsedMessage = JSON.parse(message.body);
      // Call all callbacks registered for this topic
      messageCallbacks
        .filter(cb => cb.topic === topic)
        .forEach(cb => cb.callback(parsedMessage));
    });
    
    subscribedTopics.add(topic);
    messageCallbacks.push({ topic, callback });
    
    return subscription;
  },
  
  unsubscribe: function(topic) {
    // Remove callbacks for this topic
    messageCallbacks = messageCallbacks.filter(cb => cb.topic !== topic);
    
    // If no more callbacks for this topic, unsubscribe from server
    if (!messageCallbacks.some(cb => cb.topic === topic)) {
      if (stompClient && connected) {
        // Find and remove actual subscription
        stompClient.unsubscribe(topic);
      }
      subscribedTopics.delete(topic);
    }
  },
  
  sendMessage: function(destination, message) {
    if (!connected) {
      console.error('Cannot send message, not connected to WebSocket');
      return;
    }
    
    stompClient.send(destination, {}, JSON.stringify(message));
  },
  
  // For compatibility with socket.io API
  on: function(event, callback) {
    if (event === 'connect' || event === 'connection') {
      if (connected) {
        callback();
      }
      // Will be called when connected
      return;
    }
    
    this.subscribe(`/topic/${event}`, callback);
  },
  
  emit: function(event, data) {
    this.sendMessage(`/app/${event}`, data);
  },
  
  off: function(event) {
    this.unsubscribe(`/topic/${event}`);
  },
  
  // Track connection status
  isConnected: function() {
    return connected;
  },
  
  // Auth info
  auth: {}
};

export default socket;