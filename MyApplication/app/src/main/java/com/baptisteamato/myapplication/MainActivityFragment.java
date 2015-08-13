package com.baptisteamato.myapplication;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.LightingColorFilter;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baptisteamato.myapplication.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;


public class MainActivityFragment extends Fragment {

    Button rechercher;
    Button localiser;
    Button rediger_avis;
    ImageView currentOnglet;

    public MainActivityFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        container.removeAllViews();

        /*---------------------------------*/
        ((TextView) getActivity().findViewById(R.id.title)).setText("Find'UTC");

        //changement de couleur des onglets
        currentOnglet = (ImageView) getActivity().findViewById(R.id.imageMenu);

        currentOnglet.setAlpha(255);
        currentOnglet.getDrawable().setColorFilter(new LightingColorFilter(0x00000000, 0xFF4D9D6A));

        ((ImageView) getActivity().findViewById(R.id.imageRechercher)).setColorFilter(new LightingColorFilter(0x00000000, 0xFF000000));
        ((ImageView) getActivity().findViewById(R.id.imageRechercher)).setAlpha(128);
        ((ImageView) getActivity().findViewById(R.id.imageCarte)).setColorFilter(new LightingColorFilter(0x00000000, 0xFF000000));
        ((ImageView) getActivity().findViewById(R.id.imageCarte)).setAlpha(128);

        ((Button) getActivity().findViewById(R.id.buttonLeft)).setVisibility(View.GONE);
        ((ImageView) getActivity().findViewById(R.id.flecheGauche)).setVisibility(View.GONE);

        Button buttonRight = (Button) getActivity().findViewById(R.id.buttonRight);
        buttonRight.setVisibility(View.VISIBLE);
        buttonRight.setText("Plus d'infos");


        //on n'affiche pas les onglets
        ((LinearLayout) getActivity().findViewById(R.id.linearLayout)).setVisibility(View.GONE);

        /*-----------------------------------*/


        View view;

        /*------------*/
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int w=dm.widthPixels;
        int h=dm.heightPixels;
        int dens=dm.densityDpi;
        double wi=(double)w/(double)dens;
        double hi=(double)h/(double)dens;
        double x = Math.pow(wi,2);
        double y = Math.pow(hi,2);
        double screenInches = Math.sqrt(x+y);
        /*------------------*/

        if (screenInches <= 4)
            view = inflater.inflate(R.layout.fragment_small_main, container, false);
        else {
            if (screenInches <= 6)
                view = inflater.inflate(R.layout.fragment_main, container, false);
            else {  //tablette
                buttonRight.setTextSize(23);
                view = inflater.inflate(R.layout.fragment_large_main, container, false);
            }
        }


        /*-------------Buttons listeners----------------*/

        rechercher = (Button) view.findViewById(R.id.rechercher);
        localiser = (Button) view.findViewById(R.id.localiser);
        rediger_avis = (Button) view.findViewById(R.id.rediger_avis);

        rechercher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RechercheFragment rechercheFragment = new RechercheFragment();
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, rechercheFragment, "RechercheFragment");
                ft.commit();
            }
        });

        localiser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, new CarteFragment(), "CarteFragment");
                ft.commit();
            }
        });

        rediger_avis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, new AddCommentFragment(), "AddCommentFragment");
                ft.commit();
            }
        });

        buttonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Pour plus d'informations sur les mentions légales, les partenaires ou pour nous contacter, rendez-vous sur notre site web ! ")
                        .setCancelable(false)
                        .setPositiveButton("Accéder au site web", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Uri uri = Uri.parse("http://assos.utc.fr/findutc/");

                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Retour", null);
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        return view;
    }

}

