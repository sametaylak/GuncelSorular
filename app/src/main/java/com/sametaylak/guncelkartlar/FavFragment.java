package com.sametaylak.guncelkartlar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.List;

public class FavFragment extends Fragment {

    private SwipeFlingAdapterView flingContainer;
    private List<Quest> al;
    private QuestAdapter qAdapter;
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
        al = db.getAllFavs();
        qAdapter = new QuestAdapter(getContext(), al);
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
                for (Quest q: db.getAllFavs()) {
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

}
