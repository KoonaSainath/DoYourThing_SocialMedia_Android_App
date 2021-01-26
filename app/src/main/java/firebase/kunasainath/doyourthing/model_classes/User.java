package firebase.kunasainath.doyourthing.model_classes;

import java.io.Serializable;

public class User implements Serializable {
    private String id, name;
    private int unreadMsgCount;
    private String dateTime;

    public User(String id, String name, int unreadMsgCount, String dateTime) {
        this.id = id;
        this.name = name;
        this.unreadMsgCount = unreadMsgCount;
        this.dateTime = dateTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUnreadMsgCount() {
        return unreadMsgCount;
    }

    public void setUnreadMsgCount(int unreadMsgCount) {
        this.unreadMsgCount = unreadMsgCount;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
