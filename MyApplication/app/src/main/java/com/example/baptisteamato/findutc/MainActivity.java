package com.example.baptisteamato.findutc;


import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.baptisteamato.findutc.R;


public class MainActivity extends FragmentActivity {    //Gestionnaire des fragments de l'application

    Services services;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        services = new Services(getApplicationContext());

        //Application de la police du titre
        TextView title = (TextView) findViewById(R.id.title);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Amatic-Bold.ttf");
        title.setTypeface(typeface);

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            //Premier fragment à apparaître : MainActivityFragment
            MainActivityFragment firstFragment = new MainActivityFragment();
            firstFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, firstFragment, "MainActivityFragment").commit();
        }


        /*-------------Buttons listeners---------------*/
        Button menu = (Button) findViewById(R.id.menu);
        Button rechercher = (Button) findViewById(R.id.rechercher);
        Button carte = (Button) findViewById(R.id.carte);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    MainActivityFragment mainActivityFragment = new MainActivityFragment();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, mainActivityFragment);
                    transaction.commit();
            }
        });

        rechercher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (services.connectionActivee()) {
                    RechercheFragment newFragment = new RechercheFragment();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, newFragment);
                    transaction.commit();
                }
                else {
                    PageAccueilFragment newFragment = new PageAccueilFragment();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, newFragment);
                    transaction.commit();
                }
            }
        });

        carte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (services.connectionActivee()) {
                    CarteFragment newFragment = new CarteFragment();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, newFragment);
                    transaction.commit();
                }
                else {
                    PageAccueilFragment newFragment = new PageAccueilFragment();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, newFragment);
                    transaction.commit();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
