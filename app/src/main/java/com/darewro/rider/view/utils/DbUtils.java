package com.darewro.rider.view.utils;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.darewro.rider.data.db.model.ChatMessage;
import com.darewro.rider.data.db.model.OrderPath;
import com.darewro.rider.data.db.model.OrdersTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hassan on 4/22/2016.
 */
public class DbUtils {

    public static void deleteAllData() {
        try {
            new Delete().from(OrdersTable.class).execute();
            new Delete().from(OrderPath.class).execute();
            new Delete().from(ChatMessage.class).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<OrderPath> getAllOrderPaths() {
        List<OrderPath> orderPaths = new ArrayList<>();

        try {
            orderPaths = new Select().all().from(OrderPath.class).orderBy("timestamp ASC").execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orderPaths;
    }


    public static List<OrderPath> getAllOrderPathForOrder(String orderID) {
        List<OrderPath> orderPaths = new ArrayList<>();

        try {
            orderPaths = new Select().all().from(OrderPath.class).where("orderID = ?", orderID).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orderPaths;
    }

    public static void deleteOrderData(String orderID) {
        try {
            new Delete().from(OrderPath.class).where("orderID = ?", orderID).execute();
            new Delete().from(ChatMessage.class).where("orderId = ?", orderID).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<OrdersTable> getAllOrders() {
        List<OrdersTable> ordersTables = new ArrayList<>();

        try {
            ordersTables = new Select().all().from(OrdersTable.class).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ordersTables;
    }


    public static void deleteOrder(String orderID) {
        try {
            new Delete().from(OrdersTable.class).where("id = ?", orderID).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteOrders() {
        try {
            new Delete().from(OrdersTable.class).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<ChatMessage> getAllMessagesForOrder(String orderId) {

        List<ChatMessage> chatMessages = new ArrayList<>();
        try {
            chatMessages = new Select().all().from(ChatMessage.class).where("orderId = ?", orderId).orderBy("timestamp DESC").execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatMessages;
    }

    public static List<ChatMessage> getAllUnDeliveredMessages() {

        List<ChatMessage> chatMessages = new ArrayList<>();

        try {
            chatMessages =  new Select().all().from(ChatMessage.class).where("DeliveryStatus = ?",ChatMessage.DeliveryStatus.NEW).and("Type = ?",ChatMessage.Type.SENT).orderBy("orderId ASC, timestamp ASC").execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatMessages;
    }
    public static List<ChatMessage> getAllUnDeliveredMessagesForConversation(String conversationId) {

        List<ChatMessage> chatMessages = new ArrayList<>();

        try {
            chatMessages =  new Select().all().from(ChatMessage.class).where("conversationId = ?" , conversationId).and("DeliveryStatus = ?",ChatMessage.DeliveryStatus.NEW).and("Type = ?",ChatMessage.Type.SENT).orderBy("orderId ASC, timestamp ASC").execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatMessages;
    }

    public static List<ChatMessage> getAllUnReadMessagesForOrder(String orderId) {

        List<ChatMessage> chatMessages = new ArrayList<>();
        try {
            chatMessages = new Select().all().from(ChatMessage.class).where("orderId = ?", orderId).and("DeliveryStatus = ?", ChatMessage.DeliveryStatus.DELIVERED).and("Type = ?", ChatMessage.Type.SENT).orderBy("timestamp DESC").execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatMessages;
    }

    public static int getUnreadMessageCount(String orderId) {

        List<ChatMessage> chatMessages = new ArrayList<>();
        try {
            chatMessages = new Select().all().from(ChatMessage.class).where("orderId = ?", orderId).and("DeliveryStatus = ?", ChatMessage.DeliveryStatus.NEW).and("Type = ?", ChatMessage.Type.RECEIVED).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatMessages.size();
    }

    public static List<ChatMessage> getAllUnSentMessagesForOrder(String orderId) {

        List<ChatMessage> chatMessages = new ArrayList<>();
        try {
            chatMessages = new Select().all().from(ChatMessage.class).where("orderId = ?", orderId).and("DeliveryStatus = ?", ChatMessage.DeliveryStatus.UNSENT).and("Type = ?", ChatMessage.Type.SENT).orderBy("timestamp ASC").execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatMessages;
    }
    public static List<ChatMessage> getAllUnSentMessages() {

        List<ChatMessage> chatMessages = new ArrayList<>();
        try {
            chatMessages = new Select().all().from(ChatMessage.class).where("DeliveryStatus = ?", ChatMessage.DeliveryStatus.UNSENT).and("Type = ?", ChatMessage.Type.SENT).orderBy("orderId ASC, timestamp ASC").execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatMessages;
    }

    public static List<ChatMessage> getAllUnDeliveredMessagesForOrder(String orderId) {

        List<ChatMessage> chatMessages = new ArrayList<>();

        try {
            chatMessages = new Select().all().from(ChatMessage.class).where("orderId = ?", orderId).and("DeliveryStatus = ?", ChatMessage.DeliveryStatus.NEW).and("Type = ?", ChatMessage.Type.SENT).orderBy("timestamp ASC").execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatMessages;
    }

    public static List<ChatMessage> getMessageById(String messageId) {
        return new Select().from(ChatMessage.class).where("messageId = ?", messageId).execute();
    }

    public static void deleteAllTables() {
        new Delete().from(ChatMessage.class).execute();
        new Delete().from(OrdersTable.class).execute();
        new Delete().from(OrderPath.class).execute();
    }

    public static ChatMessage getChatMessage(String messageId) {
        try {
            return (ChatMessage) new Select().from(ChatMessage.class).where("messageId = ?", messageId).orderBy("date ASC").execute().get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
