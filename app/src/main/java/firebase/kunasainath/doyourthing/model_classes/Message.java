package firebase.kunasainath.doyourthing.model_classes;

public class Message {
    private String sender, receiver, message, dateTime, seenOrDelivered;

    public Message(String sender, String receiver, String message, String dateTime, String seenOrDelivered) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.dateTime = dateTime;
        this.seenOrDelivered = seenOrDelivered;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getSeenOrDelivered() {
        return seenOrDelivered;
    }

    public void setSeenOrDelivered(String seenOrDelivered) {
        this.seenOrDelivered = seenOrDelivered;
    }
}
