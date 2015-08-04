package com.example.baptisteamato.findutc;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.baptisteamato.findutc.R;


public class PageAccueil extends Fragment {

    Services services;

    public PageAccueil() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        services = new Services(getActivity());

        View view = inflater.inflate(R.layout.fragment_pageaccueil, container, false);


        return view;

    }
}
