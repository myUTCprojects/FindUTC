package com.baptisteamato.myapplication;


import android.graphics.LightingColorFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class RechercheFragment extends Fragment{

    ImageView currentOnglet;
    private ListView etabList;
    Services services;
    ArrayList<HashMap<String, String>> etabListItem;
    TextView chargement;
    Button favorits;

    public RechercheFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        container.removeAllViews();

        services = new Services(getActivity());

        /*---------------------------------*/
        ((TextView) getActivity().findViewById(R.id.title)).setText("Rechercher");

        //changement de couleur de onglets
        currentOnglet = (ImageView) getActivity().findViewById(R.id.imageRechercher);
        currentOnglet.setAlpha(255);
        currentOnglet.getDrawable().setColorFilter(new LightingColorFilter(0x00000000, 0xFF4D9D6A));

        ((ImageView) getActivity().findViewById(R.id.imageMenu)).setColorFilter(new LightingColorFilter(0x00000000, 0xFF000000));
        ((ImageView) getActivity().findViewById(R.id.imageMenu)).setAlpha(128);
        ((ImageView) getActivity().findViewById(R.id.imageCarte)).setColorFilter(new LightingColorFilter(0x00000000, 0xFF000000));
        ((ImageView) getActivity().findViewById(R.id.imageCarte)).setAlpha(128);

        ((Button) getActivity().findViewById(R.id.buttonLeft)).setVisibility(View.GONE);
        //((Button) getActivity().findViewById(R.id.buttonRight)).setVisibility(View.GONE);
        favorits = (Button) getActivity().findViewById(R.id.buttonRight);
        favorits.setVisibility(View.GONE);

        ((ImageView) getActivity().findViewById(R.id.flecheGauche)).setVisibility(View.GONE);


        ((LinearLayout) getActivity().findViewById(R.id.linearLayout)).setVisibility(View.VISIBLE);
        /*-----------------------------------*/

        View view = inflater.inflate(R.layout.fragment_recherche, container, false);

        /*------------------*/



        etabList = (ListView) view.findViewById(R.id.listviewRecherche);

        chargement = (TextView) getActivity().findViewById(R.id.chargement);
        chargement.setVisibility(View.VISIBLE);


        //on charge la liste APRES que le fragment soit affiché
        view.post(new Runnable() {
                      @Override
                      public void run() {
                          /*-------------Récupération des catégories---------------*/
                          String nbStores[] = new String[50];
                          String categories[] = new String[50];
                          int nbCategories = services.getCategories(categories, nbStores);
                          if (nbCategories == 0) {    //erreur (connexion coupée)
                              final FragmentTransaction ft = getFragmentManager().beginTransaction();
                              ft.replace(R.id.fragment_container, new PageAccueilFragment(), "PageAccueilFragment");
                              ft.commit();
                          } else {
                              etabListItem = ((App) getActivity().getApplication()).getEtabListItem();
                              if (etabListItem == null) { //si c'est la première fois qu'on accède à cette page, on crée la liste
                                  etabListItem = new ArrayList<HashMap<String, String>>();

                                  /*--------------------Création d'une ListView-------------------------*/

                                  HashMap<String, String> map;

                                  //On crée la catégorie "Tous"
                                  HashMap<String, String> mapTous = new HashMap<String, String>();
                                  mapTous.put("titre", "Tous");
                                  mapTous.put("nbStores", " (" + services.getNbStores("Tous") + ")");
                                  mapTous.put("img", String.valueOf(R.drawable.ic_tous));
                                  etabListItem.add(mapTous);

                                  //On crée les autres catégories
                                  //Chaque item sera de la forme "Catégorie (nbStores)" avec une image
                                  /*-------------Ajout des catégories dans la liste--------------*/
                                  for (int i = 0; i < nbCategories; i++) {
                                      map = new HashMap<String, String>();
                                      map.put("titre", categories[i]);
                                      map.put("nbStores", "(" + nbStores[i] + ")");
                                      //on récupère l'image correspondante : chaque fichier est de la forme "ic_nameCategorie.png"
                                      //on "parse" la catégorie : on remplace les 'ç' par 'c' et les ' ' par '_'
                                      String image = categories[i].toLowerCase();
                                      image = image.replace("ç", "c");
                                      image = image.replace("é", "e");
                                      image = image.replace(" ", "_");
                                      image = image.replace("-", "");
                                      int resourceId = getResources().getIdentifier("ic_" + image, "drawable", getResources().getString(R.string.package_name));
                                      if (resourceId != 0)    //image trouvée
                                          map.put("img", String.valueOf(resourceId));
                                      else    //image non trouvée
                                          map.put("img", String.valueOf(R.drawable.ic_tous));
                                      etabListItem.add(map);
                                  }

                                  ((App) getActivity().getApplication()).setEtabListItem(etabListItem);
                              }

                              /*------------------------Fin création ListView---------------------------*/

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
                              double screenInches = Math.sqrt(x+y);
                              /*------------------*/

                              SimpleAdapter mSchedule;

                              if (screenInches > 6) //tablette
                                  mSchedule = new SimpleAdapter(getActivity(), etabListItem, R.layout.rowlayoutlarge,
                                          new String[]{"img", "titre", "nbStores"}, new int[]{R.id.img, R.id.titre, R.id.nbStores});
                              else  //smartphone
                                  mSchedule = new SimpleAdapter(getActivity(), etabListItem, R.layout.rowlayout,
                                          new String[]{"img", "titre", "nbStores"}, new int[]{R.id.img, R.id.titre, R.id.nbStores});

                              etabList.setAdapter(mSchedule);

                              etabList.setOnItemClickListener(new AdapterView.OnItemClickListener()

                              {
                                  @Override
                                  public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                                      if (services.connectionActivee()) {
                                          //on récupère la HashMap contenant les infos de notre item (titre, img)
                                          HashMap<String, String> map = (HashMap<String, String>) etabList.getItemAtPosition(position);

                                          Bundle bundle = new Bundle();
                                          bundle.putString("nameCategory", map.get("titre"));

                                          Fragment recherche2 = new Recherche2Fragment();
                                          recherche2.setArguments(bundle);
                                          final FragmentTransaction ft = getFragmentManager().beginTransaction();
                                          ft.replace(R.id.fragment_container, recherche2, "Recherche2Fragment");
                                          ft.commit();
                                      } else {
                                          PageAccueilFragment newFragment = new PageAccueilFragment();
                                          FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                          transaction.replace(R.id.fragment_container, newFragment);
                                          transaction.commit();
                                      }
                                  }
                              });

                              favorits.setOnClickListener(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View v) {
                                      FavorisFragment favorisFragment = new FavorisFragment();
                                      final FragmentTransaction ft = getFragmentManager().beginTransaction();
                                      ft.replace(R.id.fragment_container, favorisFragment, "FavorisFragment");
                                      ft.commit();
                                  }
                              });
                          }

                          chargement.setVisibility(View.GONE);
                          favorits.setVisibility(View.VISIBLE);
                          favorits.setText("Favoris");
                      }
                  }
        );

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

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    MainActivityFragment mainActivityFragment = new MainActivityFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, mainActivityFragment);

                    transaction.commit();
                    return true;
                }
                return false;
            }
        });

    }
}