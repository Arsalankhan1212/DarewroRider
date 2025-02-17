package com.darewro.riderApp.view.xmpp;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.darewro.riderApp.data.api.ApiCalls;
import com.darewro.riderApp.data.db.model.ChatMessage;
import com.darewro.riderApp.view.activities.ChatActivity;
import com.darewro.riderApp.view.activities.MainActivity;
import com.darewro.riderApp.view.activities.SplashActivity;
import com.darewro.riderApp.view.utils.AppUtils;
import com.darewro.riderApp.view.utils.DbUtils;
import com.darewro.riderApp.view.utils.FakeX509TrustManager;
import com.darewro.riderApp.view.utils.SharedPreferenceHelper;

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
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.address.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.jivesoftware.smackx.chatstates.provider.ChatStateExtensionProvider;
import org.jivesoftware.smackx.commands.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.delay.packet.DelayInformation;
import org.jivesoftware.smackx.delay.provider.DelayInformationProvider;
import org.jivesoftware.smackx.disco.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.disco.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.iqlast.packet.LastActivity;
import org.jivesoftware.smackx.iqprivate.PrivateDataManager;
import org.jivesoftware.smackx.muc.packet.GroupChatInvitation;
import org.jivesoftware.smackx.muc.provider.MUCAdminProvider;
import org.jivesoftware.smackx.muc.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.muc.provider.MUCUserProvider;
import org.jivesoftware.smackx.offline.OfflineMessageManager;
import org.jivesoftware.smackx.offline.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.offline.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.ping.PingFailedListener;
import org.jivesoftware.smackx.ping.PingManager;
import org.jivesoftware.smackx.privacy.provider.PrivacyProvider;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.sharedgroups.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.si.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.vcardtemp.provider.VCardProvider;
import org.jivesoftware.smackx.xdata.provider.DataFormProvider;
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
import java.util.TimerTask;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;


/**
 * Updated by gakwaya on Oct/08/2017.
 */
public class XmppClient {

    public static final String TOAST = "com.darewro.riderApp.toast";
    public static final String TOAST_Message = "com.darewro.riderApp.toast";
    private static final String TAG = "XmppXmppClient";
//    private static final String DOMAIN = "darewrochat.southeastasia.cloudapp.azure.com";
    private static final String DOMAIN = "api.darewro.com";

    public static ChatActivity chatActivity;
    public static MainActivity mainActivity;
    private static XmppClient instance = null;

    static {
        try {
            Class.forName("org.jivesoftware.smack.ReconnectionManager");
        } catch (ClassNotFoundException ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    private final Context context;
    private final String mServiceName;
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
                /*if (mainActivity != null) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {
//                            mainActivity.updateList(null);
                            if (mainActivity != null) {
                                mainActivity.updateList();
                            } else {
                                AppUtils.log(getClass(), "MainActivity is null");
                            }
                        }
                    });
                }*/

            }

        }
    };
    private String mUsername;
    IncomingChatMessageListener incomingChatMessageListener = new IncomingChatMessageListener() {

        @Override
        public void newIncomingMessage(EntityBareJid messageFrom, final Message message, Chat chat) {
            ///ADDED

            savemessage(message);
        }
    };
    private String mPassword;
    private boolean connected = false;
    private boolean loggedIn = false;
    private boolean connecting = false;
    private boolean isToasted = true;
    private ProviderManager pm = new ProviderManager();
    private XMPPTCPConnection connection;
    private ChatManager chatManager;
    private XmppClient(Context context, String username, String password) {
        Log.d(TAG, "XmppClient Constructor called.");
        this.context = context;
        this.mUsername = username;
        this.mPassword = password;
        mServiceName = DOMAIN;


        initializeConnection();

    }

    public static XmppClient getInstance() {
        return instance;
    }

    public static synchronized XmppClient getInstance(Context context, String username, String password) {

        if (instance == null) {
            instance = new XmppClient(context, username, password);
        }
        return instance;
    }

    public XMPPTCPConnection getConnection() {
        return connection;
    }

    public void setConnection(XMPPTCPConnection connection) {
        this.connection = connection;
    }

    private void savemessage(Message message) {
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

            final ChatMessage chatMessage = new ChatMessage(message.getStanzaId(), message.getBody(), orderId, contactJid, timestamp);

            Element chatTextTypeExtension = message.getExtension(ChatTextTypeExtension.ELEMENT, ChatTextTypeExtension.NAMESPACE);

            if (chatTextTypeExtension != null) {
                chatMessage.setMediaType(ChatMessage.MediaType.TEXT);
            }

            Element chatImageTypeExtension = message.getExtension(ChatImageTypeExtension.ELEMENT, ChatImageTypeExtension.NAMESPACE);

            if (chatImageTypeExtension != null) {
                chatMessage.setBody(ApiCalls.BASE_URL_CHAT+chatMessage.getBody());
                chatMessage.setMediaType(ChatMessage.MediaType.IMAGE);
            }

            Element chatAudioTypeExtension = message.getExtension(ChatAudioTypeExtension.ELEMENT, ChatAudioTypeExtension.NAMESPACE);

            if (chatAudioTypeExtension != null) {
                chatMessage.setBody(ApiCalls.BASE_URL_CHAT+chatMessage.getBody());
                chatMessage.setMediaType(ChatMessage.MediaType.AUDIO);
            }


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
            } else {
                Intent i = new Intent(context, SplashActivity.class);
                i.putExtra("title", contactJid);
                i.putExtra("message", message.getBody());
                i.putExtra("orderId", message.getSubject());
                i.putExtra("EventType","Chat");
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                if(chatAudioTypeExtension !=null )
                    AppUtils.showChatNotification(context,"New Voice Note",contactJid,contactJid,i);
                else if(chatImageTypeExtension != null)
                    AppUtils.showChatNotification(context,"New Image",contactJid,contactJid,i);
                else
                AppUtils.showChatNotification(context, message.getBody(), contactJid, contactJid, i);

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
                        if (chatActivity != null && chatActivity.getOrderId().equalsIgnoreCase(orderId)) {
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

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public boolean isAuthenticated() {
        if(connection!=null)
        return connection.isAuthenticated();
        else
            return false;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public boolean isConnecting() {
        return connecting;
    }

    public void setConnecting(boolean connecting) {
        this.connecting = connecting;
    }

    public boolean isToasted() {
        return isToasted;
    }

    public void setIsToasted(boolean isToasted) {
        this.isToasted = isToasted;
    }


    public ChatActivity getChatActivity() {
        return chatActivity;
    }


    public MainActivity getMainActivity() {
        return mainActivity;
    }

    private void sendToast(String Message) {
        //Bundle up the intent and send the broadcast.
        Intent intent = new Intent(TOAST);
        intent.setPackage(context.getPackageName());
        intent.putExtra(TOAST_Message, "");
        context.sendBroadcast(intent);

    }

    public void configure(ProviderManager pm) {
        ProviderManager.addIQProvider("query", "jabber:iq:private", new PrivateDataManager.PrivateDataIQProvider());

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
        ProviderManager.addExtensionProvider("x", "jabber:x:conference", new GroupChatInvitation.Provider());
        ProviderManager.addIQProvider("query", "http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());
        ProviderManager.addIQProvider("query", "http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());
        ProviderManager.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());
        ProviderManager.addExtensionProvider("x", "http://jabber.org/protocol/muc#user", new MUCUserProvider());
        ProviderManager.addIQProvider("query", "http://jabber.org/protocol/muc#admin", new MUCAdminProvider());
        ProviderManager.addIQProvider("query", "http://jabber.org/protocol/muc#owner", new MUCOwnerProvider());
        ProviderManager.addExtensionProvider("x", "jabber:x:delay", new DelayInformationProvider());

        try {
            ProviderManager.addIQProvider("query", "jabber:iq:version", Class.forName("org.jivesoftware.smackx.packet.Version"));
        } catch (ClassNotFoundException e) {
            //  Not sure what's happening here.
        }

        ProviderManager.addIQProvider("vCard", "vcard-temp", new VCardProvider());
        ProviderManager.addIQProvider("offline", "http://jabber.org/protocol/offline", new OfflineMessageRequest.Provider());
        ProviderManager.addExtensionProvider("offline", "http://jabber.org/protocol/offline", new OfflineMessageInfo.Provider());
        ProviderManager.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());
        ProviderManager.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());
        ProviderManager.addIQProvider("sharedgroup", "http://www.jivesoftware.org/protocol/sharedgroup", new SharedGroupsInfo.Provider());
        ProviderManager.addExtensionProvider("addresses", "http://jabber.org/protocol/address", new MultipleAddressesProvider());
        ProviderManager.addIQProvider("si", "http://jabber.org/protocol/si", new StreamInitiationProvider());
        ProviderManager.addIQProvider("query", "http://jabber.org/protocol/bytestreams", new BytestreamsProvider());

        /*
        pm.addIQProvider("open","http://jabber.org/protocol/ibb", new IBBProviders.Open());
        pm.addIQProvider("close","http://jabber.org/protocol/ibb", new IBBProviders.Close());
        pm.addExtensionProvider("data","http://jabber.org/protocol/ibb", new IBBProviders.Data());
        */

        ProviderManager.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
        ProviderManager.addIQProvider("command", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider());
        ProviderManager.addExtensionProvider("malformed-action", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.MalformedActionError());
        ProviderManager.addExtensionProvider("bad-locale", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadLocaleError());
        ProviderManager.addExtensionProvider("bad-payload", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadPayloadError());
        ProviderManager.addExtensionProvider("bad-sessionid", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadSessionIDError());
        ProviderManager.addExtensionProvider("session-expired", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.SessionExpiredError());

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

            if (connection!=null && connection.isAuthenticated()) {

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

            if (connection!=null && connection.isAuthenticated()) {

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

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        InetAddress addr = null;
        try {
            addr = InetAddress.getByName(DOMAIN);


            final XMPPTCPConnectionConfiguration conf = XMPPTCPConnectionConfiguration.builder()
                    //.setServiceName(mServiceName)
                    .setXmppDomain(DOMAIN)
                    .setHost(DOMAIN)
                    .setPort(5222)
                    .setConnectTimeout(60000)

                    .setHostAddress(addr)
                    .setCompressionEnabled(false)
                    .setDebuggerEnabled(true)
                    .setSendPresence(true)
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)

                    //.setCustomSSLContext(getSSLContext())
                    //.setCustomX509TrustManager(new FakeX509TrustManager())
                    .setCompressionEnabled(true)
                    .build();

            Log.d(TAG, "Username : " + mUsername);
            Log.d(TAG, "Password : " + mPassword);
            Log.d(TAG, "Server : " + mServiceName);

            XMPPTCPConnection.setUseStreamManagementResumptiodDefault(true);
            XMPPTCPConnection.setUseStreamManagementDefault(true);
            configure(pm);
            connection = new XMPPTCPConnection(conf);
            connection.addConnectionListener(new XMPPConnectionListener());

            setupChatDefaultListeners();

        } catch (UnknownHostException e) {
            e.printStackTrace();
            Log.d("TAG11","UnkownHostException= "+e.getMessage());
        } catch (XmppStringprepException e) {
            e.printStackTrace();
            Log.d("TAG11","XmppStringprepException= "+e.getMessage());
        }
    }

    private void setupChatDefaultListeners() {
        PingManager pingManager = PingManager.getInstanceFor(connection);
        pingManager.setPingInterval(10); // seconds
        pingManager.registerPingFailedListener(new PingFailedListener() {
            @Override
            public void pingFailed() {
                try {
                    connection.connect();
                } catch (SmackException e) {
                    e.printStackTrace();
                    //Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    //Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                } catch (XMPPException e) {
                    e.printStackTrace();
                    //Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    //Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        DeliveryReceiptManager deliveryReceiptManager = DeliveryReceiptManager.getInstanceFor(this.connection);
        deliveryReceiptManager.setAutoReceiptMode(DeliveryReceiptManager.AutoReceiptMode.always);
        deliveryReceiptManager.autoAddDeliveryReceiptRequests();
        deliveryReceiptManager.addReceiptReceivedListener(receiptReceivedListener);

        Presence available = new Presence(Presence.Type.available);
        available.setPriority(1);

        try {
            Log.d(TAG, "Sending presence (available) " + available.toString());
            connection.sendStanza(available);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        chatManager = ChatManager.getInstanceFor(connection);

        ChatManager.getInstanceFor(connection).addIncomingListener(incomingChatMessageListener);

        ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(connection);
        ReconnectionManager.setEnabledPerDefault(true);
        reconnectionManager.enableAutomaticReconnection();

    }

    public void disconnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                connection.disconnect();
            }
        }).start();
    }

    public void connect(final String caller) {

        AsyncTask<Void, Void, Boolean> connectionThread = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected synchronized Boolean doInBackground(Void... arg0) {
                if (connection!=null&&connection.isConnected())
                    return false;
                connecting = true;

                showMessageOnMainThread("active");

                try {
                    connection.connect();
                    DeliveryReceiptManager dm = DeliveryReceiptManager.getInstanceFor(connection);
                    dm.autoAddDeliveryReceiptRequests();
                    dm.setAutoReceiptMode(DeliveryReceiptManager.AutoReceiptMode.always);
                    dm.addReceiptReceivedListener(receiptReceivedListener);
                    connected = true;
                } catch (IOException e) {
                    Log.d("TAG11","IO= "+ e.getMessage());
                    showMessageOnMainThread("Inactive");
                } catch (final SmackException e) {
                    Log.d("TAG11","Smack Exception 1= "+ e.getMessage());

                    showMessageOnMainThread("Inactive");
                } catch (final XMPPException e) {
                    Log.d("TAG11","XMPP Exception= "+ e.getMessage());

                    showMessageOnMainThread("Inactive");

                } catch (InterruptedException e) {
                    Log.d("TAG11","InterruptedException= "+ e.getMessage());
                    showMessageOnMainThread("Inactive");


                    e.printStackTrace();
                }
                catch (Exception e){
                    Log.d("TAG11","XmppClient exception= "+e.getMessage());
                }
                return connecting = false;
            }
        };
        connectionThread.execute();
    }


    private void showMessageOnMainThread(final String message) {
//        if (isToasted) {
//            if (!TextUtils.isEmpty(message)) {
//                new Handler(Looper.getMainLooper())
//                        .post(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
//                            }
//                        });
//            }
//        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            ((Service) context).startForeground(2, AppUtils.createNotification(context.getApplicationContext(), message, AppUtils.M_CONNECTION_CHANNEL_ID));
        } else {
            ((Service) context).startForeground(2, AppUtils.createNotification(context.getApplicationContext(), message, AppUtils.M_CONNECTION_CHANNEL_ID), ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);

        }

    }

    public void sendMessage(String body, final String toJid, String messageID, final String messageOrderId,ChatMessage.MediaType chatMessageType) {
        try {
            EntityBareJid jid = JidCreate.entityBareFrom(toJid + "@" + DOMAIN);

            Mychat = chatManager.chatWith(jid);
            Message message = new Message(jid, Message.Type.chat);
            message.setBody(body);
            message.setSubject(messageOrderId);
            message.setStanzaId(messageID);

            if(chatMessageType ==ChatMessage.MediaType.TEXT) {
                ChatTextTypeExtension chatTypeExtension = new ChatTextTypeExtension();
                message.addExtension(chatTypeExtension);
            }
            else if(chatMessageType ==ChatMessage.MediaType.IMAGE) {
                ChatImageTypeExtension chatTypeExtension = new ChatImageTypeExtension();
                message.addExtension(chatTypeExtension);
            }
            else if(chatMessageType ==ChatMessage.MediaType.AUDIO) {
                ChatAudioTypeExtension chatTypeExtension = new ChatAudioTypeExtension();
                message.addExtension(chatTypeExtension);
            }
            else
            {

            }

            try {
                if (connection!=null && connection.isAuthenticated()) {
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


    public void setCurrentChatState(ChatState state, String contactJid, String orderId) {

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
            message.setSubject(orderId);
            message.addExtension(extension);
            Mychat.send(message);

            //other stuff...

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void login() {

        Log.i("entered login", " ------------------------------------------");
        try {
            SASLAuthentication.blacklistSASLMechanism("SCRAM-SHA-1");
            SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");
            SASLAuthentication.unBlacklistSASLMechanism("PLAIN");
//            SASLAuthentication.blacklistSASLMechanism("PLAIN");
//            SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");
//            SASLAuthentication.blacklistSASLMechanism("SCRAM-SHA-1");
            connection.setPacketReplyTimeout(100000);
            connection.login(mUsername, mPassword);

            Log.i(TAG, "LOGIN, Yey! We're connected to the Xmpp server!");
        } catch (final XMPPException | SmackException | IOException e) {
//            showMessageOnMainThread("Error, " + e.getMessage());
            Log.e(TAG + " 1", e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
//            showMessageOnMainThread("Error, " + e.getMessage());
            Log.e(TAG + " 2", e.getMessage());
            e.printStackTrace();
        }
        Log.i("exited login", "------------------------------------------");
    }


    public enum ConnectionState {
        CONNECTED, AUTHENTICATED, CONNECTING, DISCONNECTING, DISCONNECTED
    }

    public enum LoggedInState {
        LOGGED_IN, LOGGED_OUT
    }

    public class XMPPConnectionListener implements ConnectionListener {
        final Handler handler = new Handler();
        Timer timer;
        TimerTask timerTask;

        @Override
        public void connected(XMPPConnection connection) {

            showMessageOnMainThread("Active");

            connected = true;
            if (connection!=null && !connection.isAuthenticated()) {
                login();
            }
        }

        @Override
        public void connectionClosed() {
            XmppClient.instance = null;

            showMessageOnMainThread("Inactive");

            connected = false;
//            chatCreated = false;
            loggedIn = false;
        }

        @Override
        public void connectionClosedOnError(Exception arg0) {
            showMessageOnMainThread("Inactive");

            connected = false;
            loggedIn = false;
            startTimer();
        }

        @Override
        public void reconnectingIn(int arg0) {
            //showMessageOnMainThread("Inactive");

            loggedIn = false;
        }

        @Override
        public void reconnectionFailed(Exception arg0) {
            showMessageOnMainThread("Inactive");
            connected = false;
            loggedIn = false;
            startTimer();
        }

        @Override
        public void reconnectionSuccessful() {
            showMessageOnMainThread("Active");
            connected = true;

            loggedIn = false;
        }

        public void initializeTimerTask() {
            timerTask = new TimerTask() {
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            if (!connection.isAuthenticated() || !connection.isConnected()) {
                                connect("reconnect..");
                            } else {
                                timer.cancel();
                            }
                        }
                    });
                }
            };
        }

        public void startTimer() {
            timer = new Timer();
            initializeTimerTask();
            timer.schedule(timerTask, 15000, 30000 /*,500/*5000*//*, 10000*/); //
        }

        @Override
        public void authenticated(XMPPConnection arg0, boolean arg1) {
            showMessageOnMainThread("Active");
            loggedIn = true;

/*
            OfflineMessageManager offlineMessageManager = new OfflineMessageManager(connection);

            try {
                int size = offlineMessageManager.getMessageCount();
                // Load all messages from the storage
                List<Message> messages = offlineMessageManager.getMessages();

                for (Message message : messages) {
                    savemessage(message);
                }

            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
*/

            setupChatDefaultListeners();

//            chatCreated = false;
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }).start();

            sendUnsentMessages();

        }
    }
    private void sendUnsentMessages() {
        List<ChatMessage> unSentChatMessages = DbUtils.getAllUnSentMessages();

        for (ChatMessage chatMessage : unSentChatMessages)
        {
            sendMessage(chatMessage.getBody(),chatMessage.getRecipientId(),chatMessage.getMessageId(),chatMessage.getOrderId(),chatMessage.getMediaType());
        }
        List<ChatMessage> undeliveredChatMessages =DbUtils.getAllUnDeliveredMessages();

        for (ChatMessage chatMessage : undeliveredChatMessages)
        {
            sendMessage(chatMessage.getBody(),chatMessage.getRecipientId(),chatMessage.getMessageId(),chatMessage.getOrderId(),chatMessage.getMediaType());
        }
    }

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

    public void sendTrackingMessage(String body, final String toJid, String messageID, final String messageOrderId,ChatMessage.MediaType chatMessageType) {
        try {

            if (connection != null && connection.isConnected() ){
                EntityBareJid jid = JidCreate.entityBareFrom(toJid + "@" + DOMAIN);

            Mychat = chatManager.chatWith(jid);
            Message message = new Message(jid, Message.Type.normal);
            message.setBody(body);
            message.setSubject(messageOrderId);
            message.setStanzaId(messageID);


            ChatTextTypeExtension chatTypeExtension = new ChatTextTypeExtension();
            message.addExtension(chatTypeExtension);


            try {
                if (connection!=null && connection.isAuthenticated()) {
                    Log.d(TAG, "Sending message to :" + toJid);
                    Mychat.send(message);

//                    List<ChatMessage> chatMessages = DbUtils.getMessageById(messageID);
//                    for (ChatMessage chatMessage : chatMessages) {
//                        chatMessage.setDeliveryStatus(ChatMessage.DeliveryStatus.NEW);
//                        chatMessage.save();
//                    }

//                    if (chatActivity != null) {
//                        new Handler(Looper.getMainLooper()).post(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (chatActivity != null && chatActivity.getOrderId().equalsIgnoreCase(messageOrderId)) {
//                                    chatActivity.reloadData();
//                                } else {
//                                    AppUtils.log(getClass(), "chatActivity is null");
//                                }
//                            }
//                        });
//                    }
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
        }
//            else if(connection != null && connection.isConnected() && !connection.isAuthenticated())
//                login();
//            else if(connection != null && !connection.isConnected())
//                connect("connecting");
//            else if(connection == null)
//                initializeConnection();
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
    }
}
