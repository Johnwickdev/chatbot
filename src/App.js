import React from "react";
import { BrowserRouter as Router, Routes, Route, useNavigate } from "react-router-dom";
import AgentBot from "./AgentBot";
import logo from "./hpe-logo-header.png"; // Your PNG logo file in src/
import "./App.css";

const NAV_LINKS = [
  { label: "Home", href: "#" },
  { label: "Contact", href: "#" },
  { label: "Help", href: "#" },
  { label: "Guide", href: "#" }
];

function LandingPage() {
  const navigate = useNavigate();
  return (
    <div className="app-background">
      {/* Navigation at top right */}
      <nav className="nav-menu-landing">
        {NAV_LINKS.map((l) => (
          <a key={l.label} href={l.href}>{l.label}</a>
        ))}
      </nav>
      <div className="landing-center">
        <img src={logo} alt="HPE Logo" className="landing-logo" />
        <div className="landing-buttons">
          <button className="landing-btn" onClick={() => navigate("/agent")}>
            Talk to Chatbot
          </button>
          <button className="landing-btn">
            Talk to Virtual Assistant
          </button>
        </div>
      </div>
    </div>
  );
}

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/agent" element={<AgentBot />} />
      </Routes>
    </Router>
  );
}

export default App;
