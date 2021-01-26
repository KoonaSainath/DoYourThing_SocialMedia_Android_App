package firebase.kunasainath.doyourthing.viewpager_fragments;

import java.util.Comparator;

import firebase.kunasainath.doyourthing.model_classes.User;

public class ChatSorter implements Comparator<User> {
    @Override
    public int compare(User a, User b) {
        String firstDateTime = a.getDateTime();
        String secondDateTime = b.getDateTime();

        if(firstDateTime.compareTo(secondDateTime) >= 1){
            return -1;
        }else{
            return 1;
        }
    }
}
