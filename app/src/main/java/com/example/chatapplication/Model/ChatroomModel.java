package com.example.chatapplication.Model;

import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// model class for chat room
public class ChatroomModel {
    private String chatroomId;

    private List<String> userIds;
    private Timestamp lastMessageTimestamp;
    private String lastMessageSenderId;
    private String lastMessage;
    private Map<String , Integer> unreadMessages;
    private Map<String, Boolean> isInActiveChat;


    public ChatroomModel() {
    }



    public ChatroomModel(String chatroomId, List<String> userIds, Timestamp lastMessageTimestamp, String lastMessageSenderId) {
        this.chatroomId = chatroomId;
        this.userIds = userIds;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.lastMessageSenderId = lastMessageSenderId;

        this.unreadMessages = new HashMap<>();
        this.isInActiveChat = new HashMap<>();

        if (userIds != null) {
            for (String userId : userIds) {
                unreadMessages.put(userId, 0);
                isInActiveChat.put(userId, false);
            }
        }
    }

    public boolean isUserInChat(String userId) {
        return isInActiveChat != null && Boolean.TRUE.equals(isInActiveChat.get(userId));
    }
    public Map<String, Boolean> getIsInActiveChat() {
        return isInActiveChat;
    }

    public void setIsInActiveChat(Map<String, Boolean> isInActiveChat) {
        this.isInActiveChat = isInActiveChat;
    }

    public Map<String, Integer> getUnreadMessages() {
        return unreadMessages;
    }

    public void setUnreadMessages(Map<String, Integer> unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

    public int getUnreadCountForUser(String userId) {
        if (unreadMessages == null || userId == null) {
            return 0;
        }
        Integer count = unreadMessages.get(userId);
        return count != null ? count : 0;
    }


    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(String chatroomId) {
        this.chatroomId = chatroomId;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public Timestamp getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(Timestamp lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public String getLastMessageSenderId() {
        return lastMessageSenderId;
    }

    public void setLastMessageSenderId(String lastMessageSenderId) {
        this.lastMessageSenderId = lastMessageSenderId;
    }
}
