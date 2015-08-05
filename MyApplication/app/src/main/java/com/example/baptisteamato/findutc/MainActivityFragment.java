package com.example.baptisteamato.findutc;


import android.graphics.LightingColorFilter;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.baptisteamato.findutc.R;


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
        ((Button) getActivity().findViewById(R.id.buttonRight)).setVisibility(View.GONE);

        //on n'affiche pas les onglets
        ((LinearLayout) getActivity().findViewById(R.id.linearLayout)).setVisibility(View.GONE);

        /*-----------------------------------*/

        //on cherche la résolution de l'écran, pour adapter la vue
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;

        View view;

        if (height < 950)
            view = inflater.inflate(R.layout.fragment_small_main, container, false);
        else
            view = inflater.inflate(R.layout.fragment_main, container, false);

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

        return view;
    }

}

