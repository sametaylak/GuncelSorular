package com.sametaylak.guncelkartlar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        /*
            Initialize
         */

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-7058946815952372~5508095649");


        adView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        adView.loadAd(adRequest);

        MainFragment main = new MainFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, main).commit();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog.Builder(this)
                    .setMessage("Uygulamadan Çıkış Yapmak İstiyor musunuz?")
                    .setCancelable(false)
                    .setNeutralButton("Oy Ver", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName());
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }
                    })
                    .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            MainActivity.this.finish();
                        }
                    })
                    .setNegativeButton("Hayır", null)
                    .show();
        }
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;
        Class fragmentClass = null;

        if (id == R.id.nav_main) {
            fragmentClass = MainFragment.class;
        } else if (id == R.id.nav_fav) {
            fragmentClass = FavFragment.class;
        } else if (id == R.id.nav_idk) {
            fragmentClass = IdkFragment.class;
        } else if (id == R.id.nav_send_quest) {
            if(!haveNetworkConnection()) {
                Toast.makeText(this, "İnternet bağlantınız yok!", Toast.LENGTH_LONG).show();
                fragmentClass = MainFragment.class;
            } else {
                fragmentClass = SendFragment.class;
            }
        } else if (id == R.id.nav_go) {
            Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse( "http://www.guncelakademi.com/kpss-guncel-bilgiler" ) );
            startActivity( browse );
            fragmentClass = MainFragment.class;
        } else if (id == R.id.nav_contact) {
            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

            emailIntent.setType("plain/text");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"akademiguncel@gmail.com"});
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Başlık");
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "İçerik");

            startActivity(Intent.createChooser(emailIntent, "İletişim"));
            fragmentClass = MainFragment.class;
        } else if (id == R.id.nav_share) {
            final String appPackageName = getPackageName();
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + appPackageName);

            startActivity(Intent.createChooser(share, "Paylaş"));
            fragmentClass = MainFragment.class;
        } else if (id == R.id.nav_deneme) {
            final String appPackageName = "com.kpss.guncelbilgiler2017";
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
            fragmentClass = MainFragment.class;
        } else if (id == R.id.nav_coz) {
            Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse( "http://www.guncelakademi.com/test/guncel-bilgiler-testleri" ) );
            startActivity( browse );
            fragmentClass = MainFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
