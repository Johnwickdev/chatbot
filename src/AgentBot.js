import React, { useState, useRef, useEffect } from "react";
import chatData from "./chatData.json";
import botAvatar from "./bot-avatar.jpg"; // Your custom avatar
import hpeHeaderLogo from "./hpe-logo-header.png"; // <--- Your header logo image
import "./AgentBot.css";

const NAV_LINKS = [
  { label: "Home", href: "#" },
  { label: "Contact", href: "#" },
  { label: "Help", href: "#" },
  { label: "Guide", href: "#" }
];

function findStep(id) {
  return chatData.find(q => q.id === id);
}

function AgentBot() {
  const [conversation, setConversation] = useState([
    { from: "bot", id: 1, message: findStep(1).message, options: findStep(1).options }
  ]);
  const [input, setInput] = useState("");
  const chatEndRef = useRef(null);

  useEffect(() => {
    if (chatEndRef.current) {
      chatEndRef.current.scrollIntoView({ behavior: "smooth" });
    }
  }, [conversation]);

  const lastBotMsg = conversation.slice().reverse().find(m => m.from === "bot");

  const handleOption = (opt) => {
    const next = findStep(opt.nextId);
    setConversation(prev => [
      ...prev,
      { from: "user", message: opt.label },
      ...(next
        ? [{ from: "bot", id: next.id, message: next.message, options: next.options }]
        : [])
    ]);
  };

  const handleSend = (e) => {
    e.preventDefault();
    const value = input.trim();
    if (!value) return;
    let matched = null;
    if (lastBotMsg && lastBotMsg.options) {
      matched = lastBotMsg.options.find(opt =>
        opt.label.toLowerCase().includes(value.toLowerCase())
      );
    }
    if (matched) {
      handleOption(matched);
    } else {
      setConversation(prev => [
        ...prev,
        { from: "user", message: value },
        { from: "bot", message: "Sorry, I didn’t get that. Please pick an option or try again.", options: lastBotMsg ? lastBotMsg.options : [] }
      ]);
    }
    setInput("");
  };

  return (
    <div className="bot-page">
      <div className="bot-header">
        <div className="hpe-logo-nav">
          <div className="hpe-logo">
            {/* Use real logo image instead of SVG/text */}
            <img src={hpeHeaderLogo} alt="HPE Logo" className="header-logo-img" />
          </div>
          <nav className="nav-menu">
            {NAV_LINKS.map((l) => (
              <a key={l.label} href={l.href}>{l.label}</a>
            ))}
          </nav>
        </div>
      </div>
      <div className="bot-main">
        <div className="bot-avatar-chat">
          <img
            src={botAvatar}
            alt="Chatbot Avatar"
            className="bot-avatar"
          />
          <div className="bot-chat">
            {conversation.map((msg, idx) =>
              msg.from === "bot" ? (
                <div className="bot-bubble" key={idx}>{msg.message}</div>
              ) : (
                <div className="bot-bubble user" key={idx}>{msg.message}</div>
              )
            )}
            {lastBotMsg && lastBotMsg.options && lastBotMsg.options.length > 0 && (
              <div className="bot-options">
                {lastBotMsg.options.map(opt =>
                  <button key={opt.label} className="option-btn" onClick={() => handleOption(opt)}>
                    {opt.label}
                  </button>
                )}
              </div>
            )}
            <div ref={chatEndRef}></div>
          </div>
        </div>
      </div>
      <form className="bot-input-bar" onSubmit={handleSend}>
        <input
          type="text"
          placeholder="Type a message"
          className="input-text"
          value={input}
          onChange={e => setInput(e.target.value)}
        />
        <button type="submit" className="send-btn">&#9658;</button>
      </form>
    </div>
  );
}

export default AgentBot;
