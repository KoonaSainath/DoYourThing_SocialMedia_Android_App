package firebase.kunasainath.doyourthing.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import firebase.kunasainath.doyourthing.viewpager_fragments.ChatsFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {


    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                ChatsFragment chatsFragment = ChatsFragment.newInstance();
                return chatsFragment;
            case 1:
                return ChatsFragment.newInstance();
            case 2:
                return ChatsFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0:
                return "Chats";
            case 1:
                return "Users";
            case 2:
                return "Posts";
        }
        return "";
    }
}
