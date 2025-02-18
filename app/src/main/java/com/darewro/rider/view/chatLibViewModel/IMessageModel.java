package com.darewro.rider.view.chatLibViewModel;

import com.darewro.rider.data.db.model.ChatMessage;

import java.util.Date;

public class IMessageModel implements com.stfalcon.chatkit.commons.models.IMessage {
    private String id;
    private String text;
    private Author author;
    private Date createdAt;
    private ChatMessage chatMessage;

    public ChatMessage getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public Author getUser() {
        return author;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
