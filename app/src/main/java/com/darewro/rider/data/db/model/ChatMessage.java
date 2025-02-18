package com.darewro.rider.data.db.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "MessageTable")
public class ChatMessage extends Model {
    @Column(name = "messageId", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    String messageId;
    @Column(name = "messageBody")
    String body;
    @Column(name = "orderId")
    String orderId;
    @Column(name = "recipientId")
    String recipientId;
    @Column(name = "timestamp")
    String timestamp;
    @Column(name = "Type")
    Type type;
    @Column(name = "DeliveryStatus")
    DeliveryStatus deliveryStatus;
    @Column(name = "MediaType")
    MediaType mediaType;
    @Column(name = "localPath")
    String localPath;

    public ChatMessage() {
    }

    public ChatMessage(String messageId, String body, String orderId, String recipientId, String timestamp) {
        this.recipientId = recipientId;
        this.messageId = messageId;
        this.orderId = orderId;
        this.body = body;
        this.timestamp = timestamp;
        this.mediaType = MediaType.TEXT;
        this.localPath = null;
    }

    public ChatMessage(String messageId, String body, String orderId, MediaType mediaType, String localpath, String recipientId, String timestamp) {
        this.messageId = messageId;
        this.body = body;
        this.orderId = orderId;
        this.timestamp = timestamp;
        this.mediaType = mediaType;
        this.localPath = localpath;
        this.recipientId = recipientId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public enum Type {
        SENT, RECEIVED
    }

    public enum DeliveryStatus {
        UNSENT, NEW, DELIVERED, READ
    }

    public enum MediaType {
        TEXT, IMAGE, AUDIO
    }
}
