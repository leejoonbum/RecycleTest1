package taca.com.recycleviewtest.model;

/**
  채팅 메세지 구조 -> 디비구조
 */

public class ChatMessage {

    String username;
    String message;

    public ChatMessage() {
    }

    public ChatMessage(String username, String message) {
        this.username = username;
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "username='" + username + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
