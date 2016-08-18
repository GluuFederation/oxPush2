package org.gluu.com.ox_push2.u2f.v2.entry;

/**
 * Created by nazaryavornytskyy on 4/26/16.
 */
public class Entry {

    private String issuer;
    private String userName;
    private String createdDate;

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
