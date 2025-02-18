package com.darewro.rider.view.xmpp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.darewro.rider.data.db.model.ChatMessage;
import com.darewro.rider.view.activities.ChatActivity;
import com.darewro.rider.view.customViews.NonInterruptableTask;
import com.darewro.rider.view.utils.AppUtils;
import com.darewro.rider.view.utils.DbUtils;
import com.darewro.rider.view.utils.SharedPreferenceHelper;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.ChatStateManager;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.jivesoftware.smackx.chatstates.provider.ChatStateExtensionProvider;
import org.jivesoftware.smackx.delay.packet.DelayInformation;
import org.jivesoftware.smackx.delay.provider.DelayInformationProvider;
import org.jivesoftware.smackx.offline.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.offline.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;
import org.jivesoftware.smackx.xhtmlim.provider.XHTMLExtensionProvider;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.List;
import java.util.Timer;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * Updated by gakwaya on Oct/08/2017.
 */
public class XmppConnection implements ConnectionListener {

    public static final String UI_AUTHENTICATED = "com.darewro.standalonechatmodule.uiauthenticated";
    public static final String SEND_MESSAGE = "com.darewro.standalonechatmodule.sendmessage";
    public static final String SEND_MESSAGE_READ_STATUS = "com.darewro.standalonechatmodule.sendmessagereadstatus";
    public static final String SEND_MESSAGE_READ_ALL_STATUS = "com.darewro.standalonechatmodule.sendmessagereadstatus";
    public static final String BUNDLE_MESSAGE_BODY = "b_body";
    public static final String BUNDLE_MESSAGE_ID = "b_id";
    public static final String BUNDLE_ORDER_ID = "b_order_id";
    public static final String BUNDLE_TO = "b_to";
    public static final String NEW_MESSAGE_CONVERSATION = "com.darewro.standalonechatmodule.newmessage.conversation";
    public static final String TOAST = "com.darewro.standalonechatmodule.toast";
    public static final String TOAST_Message = "com.darewro.standalonechatmodule.toast";
    public static final String CONNECTED_TO_XMPP = "com.darewro.standalonechatmodule.newmessage.connectedToXMPP";
    public static final String NEW_MESSAGE = "com.darewro.standalonechatmodule.newmessage.chat";
    public static final String USER_STATUS = "com.darewro.standalonechatmodule.userstatus.chat";
    public static final String BUNDLE_FROM_JID = "b_from";
    private static final String TAG = "XmppConnection";
//    private static final String DOMAIN = "darewrochat.southeastasia.cloudapp.azure.com";
    private static final String DOMAIN = "api.darewro.com";
    public static ChatActivity chatActivity;
    private static XmppConnection instance = null;
    final Handler handler = new Handler();
    private final Context context;
    private final String mUsername;
    private final String mPassword;
    private final String mServiceName;
    Timer timer = null;
    NonInterruptableTask timerTask = null;
    Chat Mychat;
    ReceiptReceivedListener receiptReceivedListener = new ReceiptReceivedListener() {
        @Override
        public void onReceiptReceived(Jid fromJid, Jid toJid, String receiptId, Stanza receipt) {
            Jid fromJid1 = fromJid;
            Jid toJid1 = toJid;
            String receiptId1 = receiptId;
            Stanza receipt1 = receipt;

            Jid fromJid2 = fromJid;

            String from = String.valueOf(fromJid.asBareJid());

            String contactJid = "";
            if (from.contains("/")) {
                contactJid = from.split("/")[0];
                Log.d(TAG, "The real Username is :" + contactJid);
                Log.d(TAG, "The message is from :" + from);
            } else {
                contactJid = from;
            }

            if (contactJid.contains("@")) {
                contactJid = from.split("@")[0];
                Log.d(TAG, "The real jid is :" + contactJid);
            } else {
                contactJid = from;
            }


            if (contactJid != null) {

//                List<ChatMessage> chatMessages = DbUtils.getAllMessagesForConversation(contactJid);
                List<ChatMessage> chatMessages = DbUtils.getMessageById(receiptId);

                for (ChatMessage c : chatMessages) {
                    c.setDeliveryStatus(ChatMessage.DeliveryStatus.DELIVERED);
                    c.save();
                }

                if (chatActivity != null) {
                    final String finalContactJid = contactJid;
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
//                            mainActivity.updateList(null);
                            if (chatActivity != null && chatActivity.getContactJid().equalsIgnoreCase(finalContactJid)) {
                                chatActivity.reloadData();
                            } else {
                                AppUtils.log(getClass(), "chatActivity is null");
                            }
                        }
                    });
                }


            }

        }
    };

    IncomingChatMessageListener incomingChatMessageListener = new IncomingChatMessageListener() {

        @Override
        public void newIncomingMessage(EntityBareJid messageFrom, final Message message, Chat chat) {
            ///ADDED

            String from = message.getFrom().toString();
            final String orderId = message.getSubject();

            String contactJid = "";
            if (from.contains("/")) {
                contactJid = from.split("/")[0];
                Log.d(TAG, "The real Username is :" + contactJid);
                Log.d(TAG, "The message is from :" + from);
            } else {
                contactJid = from;
            }

            if (contactJid.contains("@")) {
                contactJid = from.split("@")[0];
                Log.d(TAG, "The real jid is :" + contactJid);
            } else {
                contactJid = from;
            }

            Log.d(TAG, "message.getFrom() :" + contactJid);
            if (contactJid.equalsIgnoreCase(mUsername)) {
                return;
            }
            if (message.getBody() != null && !TextUtils.isEmpty(message.getBody())) {
                Log.d(TAG, "message.getBody() :" + message.getBody());

                String timestamp = String.valueOf(AppUtils.getCurrentTimeStamp());

                final ChatMessage chatMessage = new ChatMessage(message.getStanzaId(), message.getBody(),orderId, contactJid, timestamp);

                if (contactJid.equalsIgnoreCase(SharedPreferenceHelper.getString(XMPPUser.XMPP_ID, context))) {
                    chatMessage.setType(ChatMessage.Type.SENT);
                    chatMessage.setDeliveryStatus(ChatMessage.DeliveryStatus.NEW);
                } else {
                    chatMessage.setType(ChatMessage.Type.RECEIVED);
                    chatMessage.setDeliveryStatus(ChatMessage.DeliveryStatus.NEW);
                }

                chatMessage.save();

                if (chatActivity != null) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {
//                            mainActivity.updateList(null);
                            if (chatActivity != null && chatActivity.getOrderId().equalsIgnoreCase(orderId)) {
                                chatActivity.newMessage(chatMessage);
                            } else {
                                AppUtils.log(getClass(), "chatActivity is null");

                            }
                        }
                    });
                }


                Log.d(TAG, "Received message from :" + contactJid + " broadcast sent.");
                ///ADDED
                return;
            }

            final ChatStateExtension state = (ChatStateExtension) message.getExtension("http://jabber.org/protocol/chatstates");//jabber:x:event
            // if state (ChatStateExtension) !=null and is composing then call listener method if not error.
            if (state != null) {

                DelayInformation timestamp = message.getExtension("delay", "urn:xmpp:delay");
                if (timestamp == null)
                    timestamp = message.getExtension("x", "jabber:x:delay");

                if (timestamp != null && timestamp.getReason().equalsIgnoreCase("Offline Storage")) { //return if delay info is Offline Storage
                    return;
                }

                if (chatActivity != null) {
                    final String finalContactJid = contactJid;
                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {
//                            mainActivity.updateList(null);
                            if (chatActivity != null && chatActivity.getContactJid().equalsIgnoreCase(finalContactJid)) {
                                //update your typing listener
                                if (state.getElementName().equalsIgnoreCase("composing")) {
                                    chatActivity.setChatStatus("Typing...", finalContactJid);
                                } else {
                                    chatActivity.setChatStatus("", finalContactJid);
                                }
                            } else {
                                AppUtils.log(getClass(), "chatActivity is null");

                            }
                        }
                    });
                }
                return;
            }

            Element read = message.getExtension(CustomReadExtension.ELEMENT, CustomReadExtension.NAMESPACE);

            if (read != null) {

                List<ChatMessage> chatMessages = DbUtils.getAllUnReadMessagesForOrder(orderId);
//                List<ChatMessage> chatMessages = DbUtils.getAllMessagesForConversation(contactJid);

                for (final ChatMessage c : chatMessages) {
                    c.setDeliveryStatus(ChatMessage.DeliveryStatus.READ);
                    c.save();


                    if (chatActivity != null) {
                        final String finalContactJid = contactJid;
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
//                            mainActivity.updateList(null);
                                if (chatActivity != null && chatActivity.getOrderId().equalsIgnoreCase(orderId)) {
                                    chatActivity.read(c.getMessageId());
                                } else {
                                    AppUtils.log(getClass(), "chatActivity is null");

                                }
                            }
                        });
                    }
                }

                return;
            }
            Element readAll = message.getExtension(CustomReadAllExtension.ELEMENT, CustomReadAllExtension.NAMESPACE);

            if (readAll != null) {

                List<ChatMessage> chatMessages = DbUtils.getAllUnReadMessagesForOrder(orderId);
//                List<ChatMessage> chatMessages = DbUtils.getAllMessagesForConversation(contactJid);

                for (ChatMessage c : chatMessages) {
                    c.setDeliveryStatus(ChatMessage.DeliveryStatus.READ);
                    c.save();
                }

                if (chatActivity != null) {
                    final String finalContactJid = contactJid;
                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {
//                            mainActivity.updateList(null);
                            if (chatActivity != null && chatActivity.getOrderId().equalsIgnoreCase(orderId)) {
                                chatActivity.reloadData();
                            } else {
                                AppUtils.log(getClass(), "chatActivity is null");

                            }
                        }
                    });
                }
                return;

            }
        }
    };

    private ProviderManager pm = new ProviderManager();
    private XMPPTCPConnection mConnection;
    private BroadcastReceiver uiThreadMessageReceiver = null, readStatus = null, readAllStatus = null;//Receives messages from the ui thread.
    private ChatStateManager chatStateManager;
    private ChatManager chatManager;

    private XmppConnection(Context context, String username, String password) {
        Log.d(TAG, "RoosterConnection Constructor called.");
        this.context = context;
        this.mUsername = username;
        this.mPassword = password;

        mServiceName = DOMAIN;

        initializeConnection();

    }

    public static XmppConnection getInstance() {
        return instance;
    }

    public static synchronized XmppConnection getInstance(Context context, String username, String password) {

        if (instance == null) {
            instance = new XmppConnection(context, username, password);
        }
        return instance;
    }

    public XMPPTCPConnection getmConnection() {
        return mConnection;
    }

    public ChatActivity getChatActivity() {
        return chatActivity;
    }

    private void sendToast(String Message) {
        //Bundle up the intent and send the broadcast.
        Intent intent = new Intent(TOAST);
        intent.setPackage(context.getPackageName());
        intent.putExtra(TOAST_Message, "");
        context.sendBroadcast(intent);

    }

    public void startTimer() {
        if (timer == null) {
            timer = new Timer();
            initializeTimerTask();
            timer.schedule(timerTask, 15000, 30000 /*,500/*5000*//*, 10000*/); //
        }
    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    public void initializeTimerTask() {
        timerTask = new NonInterruptableTask() {
            @Override
            protected void doTaskWork() {
                if (mConnection != null && !mConnection.isConnected()) {
                    try {
                        connect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    } catch (SmackException e) {
                        e.printStackTrace();
                    }
                } else {
                    timer.cancel();
                }
            }
        };
    }

    public void configure(ProviderManager pm) {

//        pm.addIQProvider("query", "jabber:iq:private", new PrivateDataManager.PrivateDataIQProvider());

        try {
            ProviderManager.addIQProvider("query", "jabber:iq:time", Class.forName("org.jivesoftware.smackx.packet.Time"));
        } catch (ClassNotFoundException e) {
            Log.w("TestClient", "Can't load class for org.jivesoftware.smackx.packet.Time");
        }
        //pm.addExtensionProvider("x","jabber:x:roster", new RosterPacketProvider());
        //pm.addExtensionProvider("x","jabber:x:event", new MessageEventProvider());

        //  Chat State
        ProviderManager.addExtensionProvider(CustomReadExtension.ELEMENT, CustomReadExtension.NAMESPACE, new CustomReadExtensionProvider());
        ProviderManager.addExtensionProvider(CustomReadAllExtension.ELEMENT, CustomReadAllExtension.NAMESPACE, new CustomReadExtensionProvider());
        ProviderManager.addExtensionProvider("active", "http://jabber.org/protocol/chatstates", new ChatStateExtensionProvider());
        ProviderManager.addExtensionProvider("composing", "http://jabber.org/protocol/chatstates", new ChatStateExtensionProvider());
        ProviderManager.addExtensionProvider("paused", "http://jabber.org/protocol/chatstates", new ChatStateExtensionProvider());
        ProviderManager.addExtensionProvider("inactive", "http://jabber.org/protocol/chatstates", new ChatStateExtensionProvider());
        ProviderManager.addExtensionProvider("gone", "http://jabber.org/protocol/chatstates", new ChatStateExtensionProvider());

        ProviderManager.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im", new XHTMLExtensionProvider());
//        pm.addIQProvider("query", "http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());
//        pm.addIQProvider("query", "http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());
        ProviderManager.addExtensionProvider("x", "jabber:x:delay", new DelayInformationProvider());

        try {
            ProviderManager.addIQProvider("query", "jabber:iq:version", Class.forName("org.jivesoftware.smackx.packet.Version"));
        } catch (ClassNotFoundException e) {
            //  Not sure what's happening here.
        }

//        pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());
        ProviderManager.addIQProvider("offline", "http://jabber.org/proto" + "col/offline", new OfflineMessageRequest.Provider());
        ProviderManager.addExtensionProvider("offline", "http://jabber.org/protocol/offline", new OfflineMessageInfo.Provider());
//        pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());
//        pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());
//        pm.addExtensionProvider("addresses", "http://jabber.org/protocol/address", new MultipleAddressesProvider());
//        pm.addIQProvider("si", "http://jabber.org/protocol/si", new StreamInitiationProvider());
//        pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams", new BytestreamsProvider());

        /*
        pm.addIQProvider("open","http://jabber.org/protocol/ibb", new IBBProviders.Open());
        pm.addIQProvider("close","http://jabber.org/protocol/ibb", new IBBProviders.Close());
        pm.addExtensionProvider("data","http://jabber.org/protocol/ibb", new IBBProviders.Data());
        */

/*
        pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
        pm.addIQProvider("command", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider());
        pm.addExtensionProvider("malformed-action", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.MalformedActionError());
        pm.addExtensionProvider("bad-locale", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadLocaleError());
        pm.addExtensionProvider("bad-payload", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadPayloadError());
        pm.addExtensionProvider("bad-sessionid", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadSessionIDError());
        pm.addExtensionProvider("session-expired", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.SessionExpiredError());
*/
    }

    private void resendPendingMessages() {

        List<ChatMessage> unSentMessages = DbUtils.getAllUnSentMessages();
        List<ChatMessage> unDeliveredMessages = DbUtils.getAllUnDeliveredMessages();

        for (ChatMessage chatMessage:unSentMessages)
        {
            sendMessage(chatMessage.getBody(),chatMessage.getRecipientId(),chatMessage.getMessageId(),chatMessage.getOrderId());
        }

        for (ChatMessage chatMessage:unDeliveredMessages)
        {
            sendMessage(chatMessage.getBody(),chatMessage.getRecipientId(),chatMessage.getMessageId(),chatMessage.getOrderId());
        }
    }

    public void sendReadStatus(String userid,String orderId) {
        try {
            EntityBareJid jid = JidCreate.entityBareFrom(userid + "@" + DOMAIN);

            Mychat = chatManager.chatWith(jid);

            final Message message = new Message();
            message.setType(Message.Type.chat);
            message.setBody("");
            message.setSubject(orderId);

            CustomReadExtension customExtension = new CustomReadExtension();
            message.addExtension(customExtension);

//            Log.i("Message ", message.toXML().toString());

//            Log.i("Mychat", Mychat.toString());

            if (mConnection.isAuthenticated()) {

                Log.i("Chat Message Receiver", userid);

                Mychat.send(message);

            } else {
//                showMessageOnMainThread("You are not connected to server.");
//                login();
            }

        } catch (SmackException.NotConnectedException e) {
            Log.e(TAG, "SendMessage: , msg Not sent!-Not Connected!");
//            showMessageOnMainThread("You are not connected to server.");

        } catch (Exception e) {
//            showMessageOnMainThread("Error, " + e.getMessage());
            e.printStackTrace();
            Log.e(TAG, "" + "Not sent!" + e.getMessage());
        }

    }

    public void sendReadAllStatus(String userid,String orderId) {
        try {
            EntityBareJid jid = JidCreate.entityBareFrom(userid + "@" + DOMAIN);

            Mychat = chatManager.chatWith(jid);

            final Message message = new Message();
            message.setType(Message.Type.chat);
            message.setBody("");
            message.setSubject(orderId);

            CustomReadAllExtension customExtension = new CustomReadAllExtension();
            message.addExtension(customExtension);

//            Log.i("Message ", message.toXML().toString());

//            Log.i("Mychat", Mychat.toString());

            if (mConnection.isAuthenticated()) {

                Log.i("Chat Message Receiver", userid);

                Mychat.send(message);

            } else {
//                showMessageOnMainThread("You are not connected to server.");
//                login();
            }

        } catch (SmackException.NotConnectedException e) {
            Log.e(TAG, "SendMessage: , msg Not sent!-Not Connected!");
//            showMessageOnMainThread("You are not connected to server.");

        } catch (Exception e) {
//            showMessageOnMainThread("Error, " + e.getMessage());
            e.printStackTrace();
            Log.e(TAG, "" + "Not sent!" + e.getMessage());
        }

    }

    private void initializeConnection() {
        Log.d(TAG, "Connecting to server " + mServiceName);
        InetAddress addr = null;
        try {
            addr = InetAddress.getByName(DOMAIN);


            final XMPPTCPConnectionConfiguration conf = XMPPTCPConnectionConfiguration.builder()
                    //.setServiceName(mServiceName)
                    .setXmppDomain(DOMAIN)
                    .setHost(DOMAIN)
                    .setPort(5222)

                    .setHostAddress(addr)
                    .setCompressionEnabled(false)
                    .setDebuggerEnabled(true)

                    //Was facing this issue
                    //https://discourse.igniterealtime.org/t/connection-with-ssl-fails-with-java-security-keystoreexception-jks-not-found/62566
                    //.setKeystoreType(null) //This line seems to get rid of the problem

                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    //.setCustomSSLContext(getSSLContext())
                    //.setCustomX509TrustManager(new _FakeX509TrustManager())
                    //.setCompressionEnabled(true)
                    .build();

            Log.d(TAG, "Username : " + mUsername);
            Log.d(TAG, "Password : " + mPassword);
            Log.d(TAG, "Server : " + mServiceName);


            //Set up the ui thread broadcast message receiver.
            setupUiThreadBroadCastMessageReceiver();

            XMPPTCPConnection.setUseStreamManagementResumptiodDefault(true);
            XMPPTCPConnection.setUseStreamManagementDefault(true);
            configure(pm);
            mConnection = new XMPPTCPConnection(conf);
            mConnection.setPacketReplyTimeout(100000);
            mConnection.setReplyTimeout(100000);

            DeliveryReceiptManager deliveryReceiptManager = DeliveryReceiptManager.getInstanceFor(this.mConnection);
            deliveryReceiptManager.setAutoReceiptMode(DeliveryReceiptManager.AutoReceiptMode.always);
            deliveryReceiptManager.autoAddDeliveryReceiptRequests();
            deliveryReceiptManager.addReceiptReceivedListener(receiptReceivedListener);

            chatManager = ChatManager.getInstanceFor(mConnection);
            chatStateManager = ChatStateManager.getInstance(mConnection);

            ChatManager.getInstanceFor(mConnection).addIncomingListener(incomingChatMessageListener);
            ChatManager.getInstanceFor(mConnection).addListener(incomingChatMessageListener);

            mConnection.addConnectionListener(this);

            ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(mConnection);
            ReconnectionManager.setEnabledPerDefault(true);
            reconnectionManager.enableAutomaticReconnection();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
    }

    public void connect() throws IOException, XMPPException, SmackException {

        try {
            Log.d(TAG, "Calling connect() ");
            if (mConnection != null && !mConnection.isConnected())
                mConnection.connect();
            startTimer();
            //mConnection.login(mUsername,mPassword);
            Log.d(TAG, " login() Called ");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void checkForStatus(Message message, final String jid, ChatState state) {

        final Element composing = message.getExtension("composing", "http://jabber.org/protocol/chatstates");
        final Element paused = message.getExtension("paused", "http://jabber.org/protocol/chatstates");
        final Element inactive = message.getExtension("inactive", "http://jabber.org/protocol/chatstates");
        final Element active = message.getExtension("active", "http://jabber.org/protocol/chatstates");
        final Element gone = message.getExtension("gone", "http://jabber.org/protocol/chatstates");


        if ((composing != null || paused != null || inactive != null || active != null || gone != null) && message.getBody() == null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    String status = "";
                    if (composing != null) {
                        status = "typing";
                    }
                    if (paused != null) {
                        status = "paused";
                    }
                    if (inactive != null) {
                        status = "inactive";
                    }
                    if (active != null) {
                        status = "active";
                    }
                    if (gone != null) {
                        status = "gone";
                    }
                    if (jid.equalsIgnoreCase(mUsername)) {
                        status = "";
                    }

                    updateStatusOfUser(status, jid);

                }
            });
            return;
        }
    }

    private void updateStatusOfUser(String s, String contactJid) {
        Intent intent1 = new Intent(USER_STATUS);
        intent1.setPackage(context.getPackageName());
        intent1.putExtra(BUNDLE_FROM_JID, contactJid);
        intent1.putExtra(BUNDLE_MESSAGE_BODY, s);
        context.sendBroadcast(intent1);
    }

    private void setupUiThreadBroadCastMessageReceiver() {
        if (uiThreadMessageReceiver == null) {
            uiThreadMessageReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    //Check if the Intents purpose is to send the message.
                    String action = intent.getAction();
                    if (action.equals(SEND_MESSAGE)) {
                        //Send the message.
                        sendMessage(intent.getStringExtra(BUNDLE_MESSAGE_BODY), intent.getStringExtra(BUNDLE_TO), intent.getStringExtra(BUNDLE_MESSAGE_ID), intent.getStringExtra(BUNDLE_ORDER_ID));
                    }
                }
            };

            IntentFilter filter = new IntentFilter();
            filter.addAction(SEND_MESSAGE);
            context.registerReceiver(uiThreadMessageReceiver, filter);
        }

        if (readStatus == null) {
            readStatus = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    //Check if the Intents purpose is to send the message.
                    String action = intent.getAction();
                    if (action.equals(SEND_MESSAGE_READ_STATUS)) {
                        //Send the message.
                        sendReadStatus(intent.getStringExtra(BUNDLE_TO),intent.getStringExtra(BUNDLE_ORDER_ID));
                    }
                }
            };

            IntentFilter filter1 = new IntentFilter();
            filter1.addAction(SEND_MESSAGE_READ_STATUS);
            context.registerReceiver(readStatus, filter1);
        }

        if (readAllStatus == null) {
            readAllStatus = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    //Check if the Intents purpose is to send the message.
                    String action = intent.getAction();
                    if (action.equals(SEND_MESSAGE_READ_ALL_STATUS)) {
                        //Send the message.
                        sendReadAllStatus(intent.getStringExtra(BUNDLE_TO),intent.getStringExtra(BUNDLE_ORDER_ID));
                    }
                }
            };

            IntentFilter filter2 = new IntentFilter();
            filter2.addAction(SEND_MESSAGE_READ_ALL_STATUS);
            context.registerReceiver(readAllStatus, filter2);
        }
    }

    public void sendMessage(String body, final String toJid, String messageID, final String messageOrderId) {
        try {
            EntityBareJid jid = JidCreate.entityBareFrom(toJid + "@" + DOMAIN);

            Mychat = chatManager.chatWith(jid);
            Message message = new Message(jid, Message.Type.chat);
            message.setBody(body);
            message.setSubject(messageOrderId);
            message.setStanzaId(messageID);

            try {
                if (mConnection.isAuthenticated()) {
                    Log.d(TAG, "Sending message to :" + toJid);
                    Mychat.send(message);

                    List<ChatMessage> chatMessages = DbUtils.getMessageById(messageID);
                    for (ChatMessage chatMessage : chatMessages) {
                        chatMessage.setDeliveryStatus(ChatMessage.DeliveryStatus.NEW);
                        chatMessage.save();
                    }

                    if (chatActivity != null) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
//                            mainActivity.updateList(null);
                                if (chatActivity != null && chatActivity.getOrderId().equalsIgnoreCase(messageOrderId)) {
                                    chatActivity.reloadData();
                                } else {
                                    AppUtils.log(getClass(), "chatActivity is null");
                                }
                            }
                        });
                    }
                } else {
                    Log.e(TAG, "You are not connected to server.");
                    sendToast("You are not connected to server.");
                }
            } catch (SmackException.NotConnectedException e) {
                Log.e(TAG, "SendMessage: , msg Not sent!-Not Connected!");
                sendToast("SendMessage: , msg Not sent!-Not Connected!");

            } catch (Exception e) {
                sendToast("SendMessage Error, msg Not sent!\" + e.getMessage()");
                Log.e(TAG, "SendMessage Error, msg Not sent!" + e.getMessage());
            }

        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        Log.d(TAG, "Disconnecting from serser " + mServiceName);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean("xmpp_logged_in", false).commit();


        if (mConnection != null) {
            mConnection.disconnect();
        }

        mConnection = null;
        // Unregister the message broadcast receiver.
        if (uiThreadMessageReceiver != null) {
            try {

                context.unregisterReceiver(uiThreadMessageReceiver);
                context.unregisterReceiver(readAllStatus);
                context.unregisterReceiver(readStatus);
                uiThreadMessageReceiver = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (readAllStatus != null) {
            try {

                context.unregisterReceiver(readAllStatus);
                readAllStatus = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (readStatus != null) {
            try {

                context.unregisterReceiver(readStatus);
                readStatus = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void connected(XMPPConnection connection) {
        XmppConnectionService.sConnectionState = ConnectionState.CONNECTED;
        Log.d(TAG, "Connected Successfully");

//        SASLAuthentication.blacklistSASLMechanism("PLAIN");
//        SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");
//        SASLAuthentication.blacklistSASLMechanism("SCRAM-SHA-1");
        SASLAuthentication.blacklistSASLMechanism("SCRAM-SHA-1");
        SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");
        SASLAuthentication.unBlacklistSASLMechanism("PLAIN");
        ((Service) context).startForeground(9, AppUtils.createNotification(context.getApplicationContext(), "Connected", AppUtils.M_CONNECTION_CHANNEL_ID));

        try {

            if (mUsername != null && mPassword != null) {
                mConnection.login(mUsername, mPassword);
                ((Service) context).startForeground(9, AppUtils.createNotification(context.getApplicationContext(),  " Logged in : "+mUsername, AppUtils.M_CONNECTION_CHANNEL_ID));
                setupUiThreadBroadCastMessageReceiver();
            } else {
                Log.i("Xmpp Connection Login", "Username or Password is empty");
            }
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        XmppConnectionService.sConnectionState = ConnectionState.CONNECTED;
        Log.d(TAG, "Authenticated Successfully");
        ((Service) context).startForeground(9, AppUtils.createNotification(context.getApplicationContext(), "Authenticated", AppUtils.M_CONNECTION_CHANNEL_ID));

        resendPendingMessages();

        showMainActivity();
    }

    @Override
    public void connectionClosed() {
        XmppConnectionService.sConnectionState = ConnectionState.DISCONNECTED;
        Log.d(TAG, "Connectionclosed()");
        stopTimer();
        ((Service) context).startForeground(9, AppUtils.createNotification(context.getApplicationContext(), null, AppUtils.M_CONNECTION_CHANNEL_ID));

    }

    @Override
    public void connectionClosedOnError(Exception e) {
        XmppConnectionService.sConnectionState = ConnectionState.DISCONNECTED;
        startTimer();
        Log.d(TAG, "ConnectionClosedOnError, error " + e.toString());
        ((Service) context).startForeground(9, AppUtils.createNotification(context.getApplicationContext(), null, AppUtils.M_CONNECTION_CHANNEL_ID));

    }

    @Override
    public void reconnectingIn(int seconds) {
        XmppConnectionService.sConnectionState = ConnectionState.CONNECTING;
        Log.d(TAG, "ReconnectingIn() ");
        ((Service) context).startForeground(9, AppUtils.createNotification(context.getApplicationContext(), null, AppUtils.M_CONNECTION_CHANNEL_ID));

    }

    @Override
    public void reconnectionSuccessful() {
        XmppConnectionService.sConnectionState = ConnectionState.CONNECTED;
        Log.d(TAG, "ReconnectionSuccessful()");
        ((Service) context).startForeground(9, AppUtils.createNotification(context.getApplicationContext(), null, AppUtils.M_CONNECTION_CHANNEL_ID));

    }

    @Override
    public void reconnectionFailed(Exception e) {
        XmppConnectionService.sConnectionState = ConnectionState.DISCONNECTED;
        Log.d(TAG, "ReconnectionFailed()");
        ((Service) context).startForeground(9, AppUtils.createNotification(context.getApplicationContext(), null, AppUtils.M_CONNECTION_CHANNEL_ID));

    }

    private void showMainActivity() {
        Intent i = new Intent(UI_AUTHENTICATED);
        i.setPackage(context.getPackageName());
        context.sendBroadcast(i);
        Log.d(TAG, "Sent the broadcast that we are authenticated");

        Intent intent = new Intent(CONNECTED_TO_XMPP);
        intent.setPackage(context.getPackageName());
        context.sendBroadcast(intent);
    }

    ////////////
    public SSLContext getSSLContext() {

        CertificateFactory cf = null;

        SSLContext context = null;

        try {

            cf = CertificateFactory.getInstance("X.509");

            InputStream caInput = new BufferedInputStream((this.context).getAssets().open("cacert.crt"));

            Certificate ca = cf.generateCertificate(caInput);

            String keyStoreType = KeyStore.getDefaultType();

            KeyStore keyStore = KeyStore.getInstance(keyStoreType);

            keyStore.load(null, null);

            keyStore.setCertificateEntry("ca", ca);

            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);

            tmf.init(keyStore);

// Create an SSLContext that uses our TrustManager
            context = SSLContext.getInstance("TLS");

            context.init(null, tmf.getTrustManagers(), null);

        } catch (CertificateException e) {

            e.printStackTrace();

        } catch (KeyManagementException e) {

            e.printStackTrace();

        } catch (NoSuchAlgorithmException e) {

            e.printStackTrace();

        } catch (KeyStoreException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

        return context;

    }

    public void setCurrentChatState(ChatState state, String contactJid) {
        try {
            EntityBareJid jid = JidCreate.entityBareFrom(contactJid + "@" + DOMAIN);

            Mychat = chatManager.chatWith(jid);
//            Message msg = new Message();
//            msg.setType(Message.Type.chat);
//            msg.addExtension(new ChatStateExtension(state));
//            Mychat.send(msg);


            Message message = new Message();
            ChatStateExtension extension = new ChatStateExtension(state);
            message.setType(Message.Type.chat);
            message.setBody("");
            message.setSubject("");
            message.addExtension(extension);
            Mychat.send(message);

            //other stuff...

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public enum ConnectionState {
        CONNECTED, AUTHENTICATED, CONNECTING, DISCONNECTING, DISCONNECTED
    }


    public enum LoggedInState {
        LOGGED_IN, LOGGED_OUT
    }


}
