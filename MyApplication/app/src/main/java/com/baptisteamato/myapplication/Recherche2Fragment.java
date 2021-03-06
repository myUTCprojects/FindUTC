package com.baptisteamato.myapplication;


import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Display;
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

import com.baptisteamato.myapplication.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


public class Recherche2Fragment extends Fragment{

    Services services;
    private ListView etabList;
    EditText editSearch;
    boolean ecrit = false;
    HashMap<String, String> map;
    ArrayList<HashMap<String, String>> etabListItem ;
    int nbStores;
    String nameCategory;
    Button retour_rechercher;
    TextView chargement;

    public Recherche2Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        services = new Services(getActivity());

        retour_rechercher = (Button) getActivity().findViewById(R.id.buttonLeft);
        retour_rechercher.setText("Retour");
        retour_rechercher.setTextSize(17);
        retour_rechercher.setVisibility(View.VISIBLE);
        ((ImageView) getActivity().findViewById(R.id.flecheGauche)).setVisibility(View.VISIBLE);

        ((Button) getActivity().findViewById(R.id.buttonRight)).setVisibility(View.GONE);

        nameCategory = getArguments().getString("nameCategory");
        final String previousCategoryPlural = services.getPlurielCategorie(nameCategory);

        TextView titre = (TextView) getActivity().findViewById(R.id.title);
        titre.setText(previousCategoryPlural);
        if (titre.length() >= 15)
            titre.setTextSize(30);

        View view = inflater.inflate(R.layout.fragment_recherche2, container, false);

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

        editSearch = (EditText) view.findViewById(R.id.editSearch);
        if (screenInches > 6) {  //tablette
            editSearch.setTextSize(25);
        }


        etabList = (ListView) view.findViewById(R.id.listviewRecherche);

        chargement = (TextView) getActivity().findViewById(R.id.chargement);
        chargement.setVisibility(View.VISIBLE);

        //on charge la liste APRES que le fragment soit affich�
        view.post(new Runnable() {
            @Override
            public void run() {
                /*----------------Cr�ation d'une ListView-----------------------*/
                etabListItem = new ArrayList<HashMap<String, String>>();

                /*-----------------R�cup�ration des stores--------------------------*/
                nbStores = services.getNbStores(nameCategory);
                String infosStore[][] = new String[nbStores][8];
                String stores[] = services.getStoresCategorie(nameCategory, infosStore, nbStores);

                /*-----------------Ajout des stores dans la liste-------------------*/
                for(int i=0; i < nbStores; i++){
                    map = new HashMap<String, String>();
                    if (stores[i].length() >= 25)   //Si le nom de l'�tablissement est trop long, on tronque et on ajoute "..."
                        map.put("name", stores[i].substring(0,15) + "...");
                    else
                        map.put("name", stores[i]);
                    if (infosStore[i][0].equals("Pas encore d'avis")) {
                        map.put("rating", "");
                        map.put("nbAvis", "Non not�");
                    }
                    else {
                        map.put("rating", infosStore[i][0] + "/5");
                        map.put("nbAvis", "(" + infosStore[i][2] + " avis)");
                    }
                    map.put("idStore", infosStore[i][1]);   //non affich�, permet d'�tre r�cup�r� pour la prochaine page
                    etabListItem.add(map);
                }

                /*------------------------Fin cr�ation ListView---------------------------*/

                //on trie par ordre alphab�tique
                Collections.sort(etabListItem, new Comparator<HashMap<String, String>>() {
                    public int compare(HashMap<String, String> mapping1, HashMap<String, String> mapping2) {
                        return mapping1.get("name").compareTo(mapping2.get("name"));
                    }
                });


                SimpleAdapter mSchedule;

                if (screenInches <= 5)
                    mSchedule = new SimpleAdapter (getActivity(), etabListItem, R.layout.rowlayoutsmall2,
                            new String[] {"name", "rating", "nbAvis"}, new int[] {R.id.name, R.id.rating, R.id.nbAvis});
                else {
                    if (screenInches <= 6)
                        mSchedule = new SimpleAdapter (getActivity(), etabListItem, R.layout.rowlayout2,
                                new String[] {"name", "rating", "nbAvis"}, new int[] {R.id.name, R.id.rating, R.id.nbAvis});
                    else    //tablette
                        mSchedule = new SimpleAdapter (getActivity(), etabListItem, R.layout.rowlayoutlarge2,
                                new String[] {"name", "rating", "nbAvis"}, new int[] {R.id.name, R.id.rating, R.id.nbAvis});
                }


                etabList.setAdapter(mSchedule);

                /*------------------Buttons Listeners--------------------*/
                etabList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                        if (services.connectionActivee()) {

                            //on r�cup�re la HashMap contenant les infos de notre item (titre, img)
                            HashMap<String, String> map = (HashMap<String, String>) etabList.getItemAtPosition(position);

                            Bundle bundle = new Bundle();
                            bundle.putString("idStore", map.get("idStore"));
                            bundle.putBoolean("fromListe", true);
                            bundle.putString("previousCategory", nameCategory);//nom de la cat�gorie d'o� l'on vient
                            bundle.putString("previousCategoryPlural", previousCategoryPlural); //pluriel (pour le prochain boutton retour)

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

                retour_rechercher.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        container.removeAllViews();
                        final FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.fragment_container, new RechercheFragment(), "RechercheFragment");
                        ft.commit();
                    }
                });

                editSearch.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        editSearch.requestFocusFromTouch();
                        return false;
                    }
                });



                editSearch.addTextChangedListener(new TextWatcher() {   //Barre de recherche
                    //principe : d�s que l'on �crit quelque chose, on r�cup�re la liste enti�re et on enl�ve ce qui ne correspond pas
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                        ecrit = true;
                        String txt = editSearch.getText().toString();
                        ArrayList<HashMap<String, String>> temp = new ArrayList<HashMap<String, String>>(etabListItem); //copie, mais pas pointeur
                        int length = txt.length();
                        if (length == 0) {  //on affiche tout
                            SimpleAdapter mSchedule = new SimpleAdapter(getActivity(), etabListItem, R.layout.rowlayout2,
                                    new String[]{"name", "rating", "nbAvis"}, new int[]{R.id.name, R.id.rating, R.id.nbAvis});
                            etabList.setAdapter(mSchedule);
                        } else {    //on enl�ve les "mauvais" items
                            int nbElements = nbStores;
                            for (int i = 0; i < nbElements; i++) {  //on regarde si ce qui est �crit est contenu dans chaque item
                                if (!temp.get(i).get("name").toString().toLowerCase().contains(txt.toLowerCase())) {
                                    temp.remove(i);
                                    //on enl�ve un �l�ment, donc on doit rester sur le m�me indice dans la liste et d�cr�menter nbElements
                                    i--;
                                    nbElements--;
                                }
                            }

                            SimpleAdapter mSchedule = new SimpleAdapter(getActivity(), temp, R.layout.rowlayout2,
                                    new String[]{"name", "rating", "nbAvis"}, new int[]{R.id.name, R.id.rating, R.id.nbAvis});
                            etabList.setAdapter(mSchedule);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                });

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
