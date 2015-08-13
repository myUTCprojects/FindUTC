package com.baptisteamato.myapplication;


import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baptisteamato.myapplication.R;

import java.util.ArrayList;
import java.util.HashMap;

public class AvisFragment extends Fragment{

    Services services;
    Button buttonRetour;
    public static final int nbAvis = 5; //nombre d'avis affichés en une fois
    private ListView avisList;
    String idLastOpinion = "";  //pour accéder à "Plus d'avis"

    public AvisFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        services = new Services(getActivity());

        buttonRetour = (Button) getActivity().findViewById(R.id.buttonLeft);
        buttonRetour.setText("Détails");
        buttonRetour.setTextSize(17);
        ((ImageView) getActivity().findViewById(R.id.flecheGauche)).setVisibility(View.VISIBLE);
        Button buttonPlusAvis = (Button) getActivity().findViewById(R.id.buttonRight);
        buttonPlusAvis.setText("Plus d'avis");
        buttonPlusAvis.setVisibility(View.VISIBLE);

        ((TextView) getActivity().findViewById(R.id.title)).setText("Avis");

        /*----------------------------*/

        //on cherche la résolution de l'écran, pour adapter la vue
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;

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

        if (screenInches <= 5)
            view = inflater.inflate(R.layout.fragment_small_avis, container, false);
        else {
            if (screenInches <= 6)
                view = inflater.inflate(R.layout.fragment_avis, container, false);
            else
                view = inflater.inflate(R.layout.fragment_large_avis, container, false);
        }


        /*---------------------find Views------------------*/
        TextView TextName = (TextView) view.findViewById(R.id.name);
        TextView TextNameCategory = (TextView) view.findViewById(R.id.nameCategory);
        TextView TextRating = (TextView) view.findViewById(R.id.rating);

        /*---------------------get Arguments------------------*/
        final String idStore = getArguments().getString("idStore");
        final String name = getArguments().getString("name");
        final String nameCategory = getArguments().getString("nameCategory");
        final String previousCategory = getArguments().getString("previousCategory");
        final String previousCategoryPlural = getArguments().getString("previousCategoryPlural");
        final String rating = getArguments().getString("rating");

        /*---------------Remplissage des informations--------------*/
        TextName.setText(name);
        TextNameCategory.setText(nameCategory);
        TextRating.setText(services.getRating(rating));

        /*----------------Création d'une ListView-----------------------*/
        avisList = (ListView) view.findViewById(R.id.listviewAvis);
        ArrayList<HashMap<String, String>> avisListItem = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map;

        /*-----------------Récupération des avis--------------------------*/
        String date[] = new String[nbAvis];
        String note[] = new String[nbAvis];
        String commentary[] = new String[nbAvis];
        String lastId = getArguments().getString("lastId");
        idLastOpinion = services.getAvis(idStore, lastId, date, note, commentary);
        if (idLastOpinion == null) {
            Toast.makeText(getActivity(), "Plus d'avis disponible.", Toast.LENGTH_LONG).show();
            buttonPlusAvis.setVisibility(View.GONE);
        }
        else {  //affichage des avis suivants

        /*-----------------Ajout des avis dans la liste-------------------*/
            for (int i = 0; i < nbAvis; i++) {
                if (date[i] != null) {  //pour ne pas afficher des rows vides
                    map = new HashMap<String, String>();
                    map.put("date", date[i]);
                    map.put("note", services.getRating(note[i]));
                    map.put("commentary", commentary[i]);
                    avisListItem.add(map);
                }
            }

        /*------------------------Fin création ListView---------------------------*/
            SimpleAdapter mSchedule;

            if (screenInches > 6)   //tablette
                mSchedule = new SimpleAdapter(getActivity(), avisListItem, R.layout.rowlayoutlarge_avis,
                    new String[]{"date", "note", "commentary"}, new int[]{R.id.date, R.id.note, R.id.commentary});
            else    //smartphone
                mSchedule = new SimpleAdapter(getActivity(), avisListItem, R.layout.rowlayout_avis,
                        new String[]{"date", "note", "commentary"}, new int[]{R.id.date, R.id.note, R.id.commentary});


            avisList.setAdapter(mSchedule);

        }

        /*------------------Buttons Listeners--------------------*/
        buttonRetour.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putBoolean("fromAvis",true);
                bundle.putString("idStore", idStore);
                bundle.putString("previousCategory", previousCategory);
                bundle.putString("previousCategoryPlural", previousCategoryPlural);

                Fragment ficheEtab = new FicheEtabFragment();
                ficheEtab.setArguments(bundle);
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, ficheEtab, "FicheEtabFragment");
                ft.commit();
            }
        });

        buttonPlusAvis.setOnClickListener(new View.OnClickListener() {
            //on envoie les mêmes paramètres (pour entrer dans ListeAvis + l'ID du dernier avis affiché

            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("idStore", idStore);
                bundle.putString("name",name);
                bundle.putString("nameCategory",nameCategory);
                bundle.putString("previousCategory", previousCategory);
                bundle.putString("previousCategoryPlural", previousCategoryPlural);
                bundle.putString("rating", rating);
                bundle.putString("lastId", idLastOpinion);

                Fragment avis = new AvisFragment();
                avis.setArguments(bundle);
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, avis, "AvisFragment");
                ft.commit();
            }
        });


        return  view;
    }


    @Override
    public void onResume() {
        super.onResume();

        /*-------------Gestion du boutton Back----------------*/
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    buttonRetour.performClick();
                    return true;
                }
                return false;
            }
        });
    }
}
