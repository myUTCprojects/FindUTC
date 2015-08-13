package com.baptisteamato.myapplication;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baptisteamato.myapplication.R;


public class PageAccueilFragment extends Fragment {     //page de retour s'il y a un problème de connexion

    Services services;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        container.removeAllViews();

        services = new Services(getActivity());

        ((TextView) getActivity().findViewById(R.id.title)).setText("Find'UTC");
        ((ImageView) getActivity().findViewById(R.id.imageMenu)).setAlpha(128);
        ((ImageView) getActivity().findViewById(R.id.imageRechercher)).setAlpha(128);
        ((ImageView) getActivity().findViewById(R.id.imageCarte)).setAlpha(128);

        View view = inflater.inflate(R.layout.fragment_pageaccueil, container, false);

        return view;

    }
}
