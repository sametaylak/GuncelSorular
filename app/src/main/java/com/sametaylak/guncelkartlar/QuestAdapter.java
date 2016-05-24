package com.sametaylak.guncelkartlar;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class QuestAdapter extends BaseAdapter {

    private List<Quest> questList;
    private Context context;

    public QuestAdapter (Context mContext, List<Quest> mQuests) {
        questList = mQuests;
        context = mContext;
    }

    @Override
    public int getCount() {
        return questList.size();
    }

    @Override
    public Object getItem(int position) {
        return questList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final DBHelper db = new DBHelper(context);
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi = inflater.inflate(R.layout.item, parent, false);
        }

        final Quest q = questList.get(position);

        TextView txtQuest = (TextView) vi.findViewById(R.id.txtQuest);
        TextView txtSender = (TextView) vi.findViewById(R.id.txtSender);
        final ImageButton btnFav = (ImageButton) vi.findViewById(R.id.btnFav);

        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(db.searchQuest(q)) {
                    db.deleteFav(q);
                    btnFav.setImageResource(R.drawable.nonfav);
                } else {
                    db.insertFav(q);
                    btnFav.setImageResource(R.drawable.fav);
                }
            }
        });

        if(db.searchQuest(q)) {
            btnFav.setImageResource(R.drawable.fav);
        } else {
            btnFav.setImageResource(R.drawable.nonfav);
        }

        txtQuest.setText(Html.fromHtml(q.getQuest()));
        txtSender.setText(q.getSender());
        return vi;
    }
}
