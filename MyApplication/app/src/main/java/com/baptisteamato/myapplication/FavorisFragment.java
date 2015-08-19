
package com.baptisteamato.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;


import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


import com.baptisteamato.myapplication.R;
import com.baptisteamato.myapplication.Services;


public class FavorisFragment extends Fragment {

    Services services;
    Button retour_rechercher;
    private ListView etabList;
    ArrayList<HashMap<String, String>> etabListItem ;
    HashMap<String, String> map;
    String listeFavoris[];
    TextView chargement;
    Button buttonRight;

    public FavorisFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        services = new Services(getActivity());
        listeFavoris = services.getFavoris();

        retour_rechercher = (Button) getActivity().findViewById(R.id.buttonLeft);
        retour_rechercher.setText("Retour");
        retour_rechercher.setTextSize(17);
        retour_rechercher.setVisibility(View.VISIBLE);
        ((ImageView) getActivity().findViewById(R.id.flecheGauche)).setVisibility(View.VISIBLE);

        buttonRight = (Button) getActivity().findViewById(R.id.buttonRight);

        if (listeFavoris != null) {
            buttonRight.setVisibility(View.VISIBLE);
            buttonRight.setText("Vider");
        }
        else
            buttonRight.setVisibility(View.GONE);


        TextView titre = (TextView) getActivity().findViewById(R.id.title);
        titre.setText("Favoris");

        View view = inflater.inflate(R.layout.fragment_favorits, container, false);

        /*------------*/
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int w=dm.widthPixels;
        int h=dm.heightPixels;
        int dens=dm.densityDpi;
        double wi=(double)w/(double)dens;
        double hi=(double)h/(double)dens;
        double x = Math.pow(wi, 2);
        double y = Math.pow(hi,2);
        final double screenInches = Math.sqrt(x+y);
        /*------------------*/

        etabList = (ListView) view.findViewById(R.id.listviewFavorits);

        chargement = (TextView) getActivity().findViewById(R.id.chargement);
        chargement.setVisibility(View.VISIBLE);

        //on charge la liste APRES que le fragment soit affiché
        view.post(new Runnable() {
            @Override
            public void run() {

                /*----------------Création d'une ListView-----------------------*/
                etabListItem = new ArrayList<HashMap<String, String>>();

                /*-----------------Récupération des stores--------------------------*/
                if (listeFavoris != null) {    //s'il y a des favorits
                    String infosStore[][] = services.getInfosFavoris(listeFavoris);

                /*-----------------Ajout des stores dans la liste-------------------*/
                    for (int i = 0; i < listeFavoris.length; i++) {
                        map = new HashMap<String, String>();
                        map.put("name", infosStore[i][0]);
                        if (infosStore[i][1].equals("Pas encore d'avis")) {
                            map.put("rating", "");
                            map.put("nbAvis", "Non noté");
                        } else {
                            map.put("rating", infosStore[i][1] + "/5");
                            map.put("nbAvis", "(" + infosStore[i][3] + " avis)");
                        }
                        map.put("idStore", infosStore[i][2]);   //non affiché, permet d'être récupéré pour la prochaine page
                        etabListItem.add(map);
                    }

                /*------------------------Fin création ListView---------------------------*/

                    //on trie par ordre alphabétique
                    Collections.sort(etabListItem, new Comparator<HashMap<String, String>>() {
                        public int compare(HashMap<String, String> mapping1, HashMap<String, String> mapping2) {
                            return mapping1.get("name").compareTo(mapping2.get("name"));
                        }
                    });


                    SimpleAdapter mSchedule;

                    if (screenInches <= 5)
                        mSchedule = new SimpleAdapter(getActivity(), etabListItem, R.layout.rowlayoutsmall2,
                                new String[]{"name", "rating", "nbAvis"}, new int[]{R.id.name, R.id.rating, R.id.nbAvis});
                    else {
                        if (screenInches <= 6)
                            mSchedule = new SimpleAdapter(getActivity(), etabListItem, R.layout.rowlayout2,
                                    new String[]{"name", "rating", "nbAvis"}, new int[]{R.id.name, R.id.rating, R.id.nbAvis});
                        else    //tablette
                            mSchedule = new SimpleAdapter(getActivity(), etabListItem, R.layout.rowlayoutlarge2,
                                    new String[]{"name", "rating", "nbAvis"}, new int[]{R.id.name, R.id.rating, R.id.nbAvis});
                    }


                    etabList.setAdapter(mSchedule);

                /*------------------Buttons Listeners--------------------*/
                    etabList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                            if (services.connectionActivee()) {

                                //on récupère la HashMap contenant les infos de notre item (titre, img)
                                HashMap<String, String> map = (HashMap<String, String>) etabList.getItemAtPosition(position);

                                Bundle bundle = new Bundle();
                                bundle.putString("idStore", map.get("idStore"));
                                bundle.putBoolean("fromFavoris", true);
                                bundle.putString("previousCategory", "Favorits");
                                bundle.putString("previousCategoryPlural", "Favorits");

                                Fragment ficheEtab = new FicheEtabFragment();
                                ficheEtab.setArguments(bundle);
                                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                                ft.replace(R.id.fragment_container, ficheEtab, "FicheEtabFragment");
                                ft.commit();
                            } else {
                                PageAccueilFragment newFragment = new PageAccueilFragment();
                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_container, newFragment);
                                transaction.commit();
                            }
                        }
                    });

                } else {
                    Toast.makeText(getActivity(), "Vous n'avez actuellement aucun favori", Toast.LENGTH_LONG).show();
                }


                retour_rechercher.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        container.removeAllViews();
                        final FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.fragment_container, new RechercheFragment(), "RechercheFragment");
                        ft.commit();
                    }
                });

                if (listeFavoris != null) {
                    buttonRight.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("Voulez-vous vraiment supprimer tous vos favoris ?")
                                    .setCancelable(false)
                                    .setPositiveButton("Vider les favoris", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            try {
                                                OutputStreamWriter outputStreamWriterTemp = new OutputStreamWriter(getActivity().openFileOutput(getActivity().getResources().getString(R.string.file_name), Context.MODE_PRIVATE));
                                                outputStreamWriterTemp.write("");
                                                outputStreamWriterTemp.close();
                                                Toast.makeText(getActivity(), "Votre liste de favoris a bien été vidée.", Toast.LENGTH_SHORT).show();
                                                //on actualise le fragment
                                                FavorisFragment favorisFragment = new FavorisFragment();
                                                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                                                ft.replace(R.id.fragment_container, favorisFragment, "FavorisFragment");
                                                ft.commit();
                                            } catch (Exception e) {
                                                Toast.makeText(getActivity(), "Une erreur s'est produite. Veuillez réessayer.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    })
                                    .setNegativeButton("Annuler", null);
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    });
                }

                chargement.setVisibility(View.GONE);

            }
        });

        return view;
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
                    retour_rechercher.performClick();
                    return true;
                }
                return false;
            }
        });
    }

}

