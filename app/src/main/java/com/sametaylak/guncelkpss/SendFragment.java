package com.sametaylak.guncelkpss;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class SendFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_send, container, false);

        Button btnSend = (Button) rootView.findViewById(R.id.btnSend);
        final EditText etName = (EditText) rootView.findViewById(R.id.etName);
        final EditText etQuest = (EditText) rootView.findViewById(R.id.etQuest);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SendQuest().execute(etQuest.getText().toString(), etName.getText().toString());
                etQuest.setText("");
                etName.setText("");
            }
        });

        return rootView;
    }

    public class SendQuest extends AsyncTask<String, Void, Void> {

        private final String USER_AGENT = "Mozilla/5.0";

        @Override
        protected Void doInBackground(String... params) {
            String url = "http://www.guncelakademi.com/mobilSite/api/postMobilSorular.php";
            URL obj = null;
            try {
                obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                //add reuqest header
                con.setRequestMethod("POST");
                con.setRequestProperty("User-Agent", USER_AGENT);
                con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

                String urlParameters = "gonderen=" + params[1] + "&icerik="+ params[0] + "&onay=0";

                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                int responseCode = con.getResponseCode();
                System.out.println("\nSending 'POST' request to URL : " + url);
                System.out.println("Post parameters : " + urlParameters);
                System.out.println("Response Code : " + responseCode);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Snackbar.make(getView(), "Gönderildi", Snackbar.LENGTH_SHORT).show();
        }
    }

}
