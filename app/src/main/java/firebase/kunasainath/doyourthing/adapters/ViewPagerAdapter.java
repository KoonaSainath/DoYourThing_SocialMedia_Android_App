package firebase.kunasainath.doyourthing.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import firebase.kunasainath.doyourthing.viewpager_fragments.ChatsFragment;
import firebase.kunasainath.doyourthing.viewpager_fragments.HomeFragment;
import firebase.kunasainath.doyourthing.viewpager_fragments.PeopleFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {


    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return HomeFragment.newInstance();
            case 1:
                return ChatsFragment.newInstance();
            case 2:
                return PeopleFragment.newInstance();
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
                return "Home";
            case 1:
                return "Chats";
            case 2:
                return "People";
        }
        return "";
    }
}
