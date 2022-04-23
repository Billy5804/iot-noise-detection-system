import Stomp from "stompjs";
import SockJS from "sockjs-client/dist/sockjs";

let sockJS;
let stompClient;

export default {
  connect: (authorization, onConnected, onError) => {
    sockJS = new SockJS("http://localhost:443/ws");
    stompClient = Stomp.over(sockJS);
    stompClient.debug = null;
    stompClient.connect({ authorization }, onConnected, onError);
  },
  subscribe: (destination, onMessageReceived) =>
    stompClient.subscribe(destination, onMessageReceived),
};
