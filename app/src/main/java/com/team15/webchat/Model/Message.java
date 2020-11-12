package com.team15.webchat.Model;

public class Message {
    private CharSequence text;
    private long timestamp;
    private String sender;
    private String name;
    public Message(CharSequence text, String sender,String name) {
        this.text = text;
        this.sender = sender;
        this.name = name;
        timestamp = System.currentTimeMillis();
    }
    public CharSequence getText() {
        return text;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public CharSequence getSender() {
        return sender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
