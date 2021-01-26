package firebase.kunasainath.doyourthing.model_classes;

import java.io.Serializable;

public class User implements Serializable {
    private String id, name;
    private int unreadMsgCount;

    public User(String id, String name, int unreadMsgCount) {
        this.id = id;
        this.name = name;
        this.unreadMsgCount = unreadMsgCount;
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
}
