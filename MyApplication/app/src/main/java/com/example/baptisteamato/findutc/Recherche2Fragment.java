package com.example.baptisteamato.findutc;


import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.example.baptisteamato.findutc.R;

import java.util.ArrayList;
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

        nameCategory = getArguments().getString("nameCategory");
        final String previousCategoryPlural = services.getPlurielCategorie(nameCategory);

        TextView titre = (TextView) getActivity().findViewById(R.id.title);
        titre.setText(previousCategoryPlural);
        if (titre.length() >= 15)
            titre.setTextSize(30);

        View view = inflater.inflate(R.layout.fragment_recherche2, container, false);


        /*----------------Création d'une ListView-----------------------*/
        etabList = (ListView) view.findViewById(R.id.listviewRecherche);
        etabListItem = new ArrayList<HashMap<String, String>>();

        /*-----------------Récupération des stores--------------------------*/
        nbStores = services.getNbStores(nameCategory);
        String infosStore[][] = new String[nbStores][8];
        String stores[] = services.getStoresCategorie(nameCategory, infosStore, nbStores);

        /*-----------------Ajout des stores dans la liste-------------------*/
        for(int i=0; i < nbStores; i++){
            map = new HashMap<String, String>();
            if (stores[i].length() >= 25)   //Si le nom de l'établissement est trop long, on tronque et on ajoute "..."
                map.put("name", stores[i].substring(0,15) + "...");
            else
                map.put("name", stores[i]);
            if (infosStore[i][0].equals("Pas encore d'avis"))
                map.put("rating", "Non noté");
            else
                map.put("rating", infosStore[i][0] + "/5");
            map.put("nbAvis", "(" + infosStore[i][2] + " avis)");
            map.put("idStore", infosStore[i][1]);   //non affiché, permet d'être récupéré pour la prochaine page
            etabListItem.add(map);
        }

        /*------------------------Fin création ListView---------------------------*/
        SimpleAdapter mSchedule;

        //on cherche la résolution de l'écran, pour adapter la vue
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        if (width < 530)
            mSchedule = new SimpleAdapter (getActivity(), etabListItem, R.layout.rowlayoutsmall2,
                    new String[] {"name", "rating", "nbAvis"}, new int[] {R.id.name, R.id.rating, R.id.nbAvis});
        else
            mSchedule = new SimpleAdapter (getActivity(), etabListItem, R.layout.rowlayout2,
                    new String[] {"name", "rating", "nbAvis"}, new int[] {R.id.name, R.id.rating, R.id.nbAvis});

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
                    bundle.putBoolean("fromListe", true);
                    bundle.putString("previousCategory", nameCategory);//nom de la catégorie d'où l'on vient
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

        editSearch = (EditText) view.findViewById(R.id.editSearch);
        editSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                editSearch.requestFocusFromTouch();
                return false;
            }
        });



        editSearch.addTextChangedListener(new TextWatcher() {   //Barre de recherche
            //principe : dès que l'on écrit quelque chose, on récupère la liste entière et on enlève ce qui ne correspond pas
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
                } else {    //on enlève les "mauvais" items
                    int nbElements = nbStores;
                    for (int i = 0; i < nbElements; i++) {  //on regarde si ce qui est écrit est contenu dans chaque item
                        if (!temp.get(i).get("name").toString().toLowerCase().contains(txt.toLowerCase())) {
                            temp.remove(i);
                            //on enlève un élément, donc on doit rester sur le même indice dans la liste et décrémenter nbElements
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
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
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
