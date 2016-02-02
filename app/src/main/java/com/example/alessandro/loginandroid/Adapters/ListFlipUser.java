package com.example.alessandro.loginandroid.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.alessandro.loginandroid.Entity.User;
import com.example.alessandro.loginandroid.R;
import com.yalantis.flipviewpager.adapter.BaseFlipAdapter;
import com.yalantis.flipviewpager.utils.FlipSettings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Adpter per listview con coppie di utenti e funzionalita flip
 */
class ListFlipUser extends BaseFlipAdapter<User> {

    private final int PAGES = 3;
    private int[] IDS_INTEREST = {R.id.interest_1, R.id.interest_2, R.id.interest_3, R.id.interest_4};

    public ListFlipUser(Context context, List<User> items, FlipSettings settings) {
        super(context, items, settings);
    }

    @Override
    public View getPage(int position, View convertView, ViewGroup parent, User friend1, User friend2) {
        final FriendsHolder holder;

        if (convertView == null) {
            holder = new FriendsHolder();

           // convertView         = getLayoutInflater().inflate(R.layout.friends_merge_page, parent, false);
            holder.leftAvatar   = (TextView) convertView.findViewById(R.id.first);
            holder.rightAvatar  = (TextView) convertView.findViewById(R.id.second);
           // holder.infoPage     = getLayoutInflater().inflate(R.layout.friends_info, parent, false);
            holder.nickName     = (TextView) holder.infoPage.findViewById(R.id.nickname);

            for (int id : IDS_INTEREST)
                holder.interests.add((TextView) holder.infoPage.findViewById(id));
            convertView.setTag(holder);
        } else {
            holder = (FriendsHolder) convertView.getTag();
        }

        switch (position) {
            // Merged page with 2 friends
            case 1:
                holder.leftAvatar.setBackgroundResource(R.drawable.anastasia);
                holder.leftAvatar.setText(friend1.getRole());
                if (friend2 != null)
                    holder.rightAvatar.setBackgroundResource(R.drawable.anastasia);
                holder.rightAvatar.setText(friend2.getRole());
                break;
            default:
                fillHolder(holder, position == 0 ? friend1 : friend2);
                holder.infoPage.setTag(holder);
                return holder.infoPage;
        }
        return convertView;
    }

    @Override
    public int getPagesCount() {
            return PAGES;
    }
    private void fillHolder(FriendsHolder holder, User friend) {
        if (friend == null)
            return;
        Iterator<TextView> iViews = holder.interests.iterator();
        Iterator<String> iInterests = friend.getInterests().iterator();
        while (iViews.hasNext() && iInterests.hasNext())
            iViews.next().setText(iInterests.next());
        //holder.infoPage.setBackgroundColor(getResources().getColor(friend.getBackground()));
        holder.nickName.setText(friend.getName());
    }

    class FriendsHolder {
        TextView leftAvatar;
        TextView rightAvatar;
        View infoPage;

        List<TextView> interests = new ArrayList<>();
        TextView nickName;
        Button profile;
    }
}
