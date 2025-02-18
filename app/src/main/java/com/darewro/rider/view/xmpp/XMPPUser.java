package com.darewro.rider.view.xmpp;

public class XMPPUser {
    public static final String XMPP_ID = "xmppId";
    public static final String XMPP_PASSWORD = "xmppPassword";

    String userid,password;

    public XMPPUser(String userid, String password) {
        this.userid = userid;
        this.password = password;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
