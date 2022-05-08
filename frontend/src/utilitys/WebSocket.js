import Stomp from "stompjs";
import SockJS from "sockjs-client/dist/sockjs";

let sockJS;
let stompClient;
let unsubscribe;

export default {
  connect: (authorization, onConnected, onError) => {
    sockJS = new SockJS(
      `${
        import.meta.env.BASE_URL
          ? import.meta.env.BASE_URL
          : "http://localhost:443"
      }/ws`
    );
    stompClient = Stomp.over(sockJS);
    stompClient.debug = null;
    stompClient.connect({ authorization }, onConnected, onError);
  },
  subscribe: (destination, onMessageReceived) => {
    if (unsubscribe) {
      unsubscribe();
    }
    ({ unsubscribe } = stompClient.subscribe(destination, onMessageReceived));
  },
  disconnect: (onDisconnect) => stompClient.disconnect(onDisconnect),
};
