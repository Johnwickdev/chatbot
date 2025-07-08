import React from "react";
import agents from "./agents";
import "./VirtualAssistantList.css";
import { useNavigate } from "react-router-dom";

export default function VirtualAssistantList() {
  const navigate = useNavigate();

  const handleAgentClick = (agent) => {
    // For now, go to a placeholder page with the agent name in the URL
    navigate(`/agent-live/${agent.id}`);
  };

  return (
    <div className="va-bg">
      <h2>Choose a Virtual Assistant</h2>
      <div className="va-list">
        {agents.map((agent) => (
          <div
            className={`va-card ${agent.status}`}
            key={agent.id}
            onClick={() => agent.status === "online" && handleAgentClick(agent)}
            style={{ opacity: agent.status === "offline" ? 0.5 : 1, cursor: agent.status === "online" ? "pointer" : "not-allowed" }}
          >
            <img src={agent.avatar} alt={agent.name} className="va-avatar" />
            <div>
              <div className="va-name">{agent.name}</div>
              <div className={`va-status ${agent.status}`}>{agent.status === "online" ? "Online" : "Offline"}</div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
