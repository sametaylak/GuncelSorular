package com.sametaylak.guncelkartlar;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.List;

public class IdkFragment extends Fragment {

    private SwipeFlingAdapterView flingContainer;
    private List<Quest> al;
    private IdkAdapter qAdapter;
    private int i;

    InterstitialAd interstitialAd;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fav, container, false);
        DBHelper db = new DBHelper(getContext());

        interstitialAd = new InterstitialAd(getContext());
        interstitialAd.setAdUnitId("ca-app-pub-7058946815952372/6566026441");
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                interstitialAd.show();
            }
        });

        flingContainer = (SwipeFlingAdapterView) rootView.findViewById(R.id.frame);
        al = db.getAllIdk();
        qAdapter = new IdkAdapter(getContext(), al);
        flingContainer.setAdapter(qAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                if(i % 5 == 0) {
                    AdRequest adRequest = new AdRequest.Builder()
                            .build();
                    interstitialAd.loadAd(adRequest);
                }
                Log.d("LIST", "removed object!");
                al.remove(0);
                qAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                i++;
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                i++;
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                DBHelper db = new DBHelper(getContext());
                for (Quest q: db.getAllIdk()) {
                    al.add(q);
                }
                qAdapter.notifyDataSetChanged();
            }

            @Override
            public void onScroll(float v) {
            }
        });

        return rootView;
    }

    public class IdkAdapter extends BaseAdapter {
        private List<Quest> questList;
        private Context context;

        public IdkAdapter (Context mContext, List<Quest> mQuests) {
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
                vi = inflater.inflate(R.layout.item_idk, parent, false);
            }

            final Quest q = questList.get(position);

            TextView txtQuest = (TextView) vi.findViewById(R.id.txtQuest);
            TextView txtSender = (TextView) vi.findViewById(R.id.txtSender);
            final ImageButton btnDelete = (ImageButton) vi.findViewById(R.id.btnDelete);

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.deleteIdk(q);
                    flingContainer.getTopCardListener().selectRight();
                }
            });

            txtQuest.setText(q.getQuest());
            txtSender.setText(q.getSender());
            return vi;
        }
    }
}
