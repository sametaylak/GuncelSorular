package com.sametaylak.guncelkartlar;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {

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
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        i = 1;

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
        Button btnIdk = (Button) rootView.findViewById(R.id.btnIdk);
        Button btnIk = (Button) rootView.findViewById(R.id.btnIk);
        assert btnIdk != null;
        assert btnIk != null;
        btnIdk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(al.size() != 0) {
                    DBHelper db = new DBHelper(getContext());
                    db.insertIdk(al.get(0));
                    flingContainer.getTopCardListener().selectRight();
                }
            }
        });
        btnIk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(al.size() != 0) {
                    flingContainer.getTopCardListener().selectRight();
                }
            }
        });
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
                for (Quest q: db.getAllQuests()) {
                    al.add(q);
                }
                qAdapter.notifyDataSetChanged();
            }

            @Override
            public void onScroll(float v) {

            }
        });

        if(haveNetworkConnection())
            new getQuests().execute();
        else {
            DBHelper db = new DBHelper(getContext());
            al = db.getAllQuests();
            qAdapter = new QuestAdapter(getContext(), al);
            flingContainer.setAdapter(qAdapter);
            qAdapter.notifyDataSetChanged();
        }

        return rootView;
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public class getQuests extends AsyncTask<Void, Void, List<Quest>> {

        private final String USER_AGENT = "Mozilla/5.0";
        private List<Quest> getQ;
        DBHelper db = new DBHelper(getContext());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            db.deleteAllQuests();
        }

        @Override
        protected List<Quest> doInBackground(Void... params) {
            String url = "http://www.guncelakademi.com/mobilSite/api/getMobilSorular.php";
            try {
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", USER_AGENT);
                int responseCode = con.getResponseCode();
                System.out.println("\nSending 'GET' request to URL : " + url);
                System.out.println("Response Code : " + responseCode);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                String response = "";

                while ((inputLine = in.readLine()) != null) {
                    response += inputLine;
                }
                in.close();
                JSONArray jArray = new JSONArray(response);
                getQ = new ArrayList<Quest>();
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject mObj = jArray.getJSONObject(i);
                    db.insertQuest(new Quest(mObj.getInt("id"), mObj.getString("gonderen"), mObj.getString("soru"), mObj.getInt("onay")));
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return getQ;
        }

        @Override
        protected void onPostExecute(List<Quest> quests) {
            super.onPostExecute(quests);
            al = db.getAllQuests();
            qAdapter = new QuestAdapter(getContext(), al);
            flingContainer.setAdapter(qAdapter);
            qAdapter.notifyDataSetChanged();
        }
    }
}
