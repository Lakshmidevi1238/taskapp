import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import { useAuthStore } from "../stores/authStore";

let stompClient = null;

export function connectBoardSocket(boardId, onEvent) {
  if (!boardId) return;

  const store = useAuthStore.getState();

  if (!store.hydrated) {
    console.warn("Auth not hydrated — socket not started");
    return;
  }

  let token = store.accessToken;

 
  if (!token) {
    const raw = localStorage.getItem("taskapp_auth");
    if (raw) {
      const parsed = JSON.parse(raw);
      token = parsed?.state?.accessToken;
    }
  }

  if (!token) {
    console.warn("No JWT token — socket not started");
    return;
  }

  const socket = new SockJS(
    `http://localhost:8080/ws?token=${token}`
  );

  stompClient = new Client({
    webSocketFactory: () => socket,
    reconnectDelay: 3000,
    debug: () => {}
  });

  stompClient.onConnect = () => {
    console.log("WS connected");

    stompClient.subscribe(
      `/topic/boards/${boardId}`,
      msg => {
        const event = JSON.parse(msg.body);
        onEvent(event);
      }
    );
  };

  stompClient.onStompError = frame => {
    console.error("STOMP error", frame);
  };

  stompClient.activate();
}

export function disconnectSocket() {
  if (stompClient) {
    stompClient.deactivate();
    stompClient = null;
  }
}
