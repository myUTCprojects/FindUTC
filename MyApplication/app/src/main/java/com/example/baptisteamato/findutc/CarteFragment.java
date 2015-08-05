package com.example.baptisteamato.findutc;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.baptisteamato.findutc.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class CarteFragment extends Fragment{

    ImageView currentOnglet;
    Services services;
    Button choix;
    String categories[];
    private GoogleMap map;
    boolean fromFiche;
    List<String> listItems;

    public CarteFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        services = new Services(getActivity());


        /*---------------------------------*/
        ((TextView) getActivity().findViewById(R.id.title)).setText("Carte");

        //changement de couleur des onglets
        currentOnglet = (ImageView) getActivity().findViewById(R.id.imageCarte);

        currentOnglet.setAlpha(255);
        currentOnglet.getDrawable().setColorFilter(new LightingColorFilter(0x00000000, 0xFF4D9D6A));

        ((ImageView) getActivity().findViewById(R.id.imageMenu)).setColorFilter(new LightingColorFilter(0x00000000, 0xFF000000));
        ((ImageView) getActivity().findViewById(R.id.imageMenu)).setAlpha(128);
        ((ImageView) getActivity().findViewById(R.id.imageRechercher)).setColorFilter(new LightingColorFilter(0x00000000, 0xFF000000));
        ((ImageView) getActivity().findViewById(R.id.imageRechercher)).setAlpha(128);

        ((Button) getActivity().findViewById(R.id.buttonLeft)).setVisibility(View.GONE);
        ((ImageView) getActivity().findViewById(R.id.flecheGauche)).setVisibility(View.GONE);


        ((LinearLayout) getActivity().findViewById(R.id.linearLayout)).setVisibility(View.VISIBLE);
        /*-----------------------------------*/

        View view = inflater.inflate(R.layout.fragment_carte, container, false);

        map = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();

        if (map != null) {

            map.setMyLocationEnabled(true);

            choix = (Button) view.findViewById(R.id.buttonTous);

            //Obtention de la catégorie choisie
            choix.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //création de la liste des catégories
                    listItems = ((App) getActivity().getApplication()).getListItems();
                    if (listItems == null) { //si c'est la première fois qu'on accède à cette page, on crée la liste

                        listItems = new ArrayList<String>();
                        listItems.add("Tous");
                        categories = new String[20];
                        int j = services.getCategories(categories);
                        if (j == 0)
                            Toast.makeText(getActivity(), "Impossible de charger les catégories. Veuillez vérifier votre connexion.", Toast.LENGTH_LONG).show();
                        else {
                            for (int i = 0; i < j; i++) {
                                if (categories[i] != null) {
                                    listItems.add(categories[i]);
                                }
                            }

                            //Création de l'AlertDialog affichant la liste des catégories
                            final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("Quels établissements afficher ?");
                            builder.setItems(items, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int item) {
                                    choix.setText(items[item]);
                                    //on modifie les markers de GoogleMaps
                                    //-------------------------Rencentrage de la caméra---------------------------
                                    double compiegne_lat = Double.parseDouble(getActivity().getResources().getString(R.string.compiegne_lat));
                                    double compiegne_lng = Double.parseDouble(getActivity().getResources().getString(R.string.compiegne_lng));
                                    LatLng compiegne = new LatLng(compiegne_lat, compiegne_lng);
                                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(compiegne, 14.0f));
                                    //------------------------------------------------------------------------------
                                    addMarkers(getActivity(), map, items[item].toString());
                                }
                            });

                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                        ((App) getActivity().getApplication()).setListItems(listItems);
                    }
                    else {
                        //Création de l'AlertDialog affichant la liste des catégories
                        final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Quels établissements afficher ?");
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                choix.setText(items[item]);
                                //on modifie les markers de GoogleMaps
                                //-------------------------Rencentrage de la caméra---------------------------
                                double compiegne_lat = Double.parseDouble(getActivity().getResources().getString(R.string.compiegne_lat));
                                double compiegne_lng = Double.parseDouble(getActivity().getResources().getString(R.string.compiegne_lng));
                                LatLng compiegne = new LatLng(compiegne_lat, compiegne_lng);
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(compiegne, 14.0f));
                                //------------------------------------------------------------------------------
                                addMarkers(getActivity(), map, items[item].toString());
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
            });


            //Initialisation de la position
            double compiegne_lat = Double.parseDouble(this.getResources().getString(R.string.compiegne_lat));
            double compiegne_lng = Double.parseDouble(this.getResources().getString(R.string.compiegne_lng));
            LatLng compiegne = new LatLng(compiegne_lat, compiegne_lng);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(compiegne, 14.0f));

            //Si l'on vient d'une fiche d'établissement, on place le marker correspondant
            Bundle bundle = getArguments();
            if (bundle != null) {
                fromFiche = bundle.getBoolean("fromFiche", false);
                if (fromFiche) {    //on ajoute le marker de l'établissement que l'on vient de consulter
                    String name = bundle.getString("name");
                    String nameCategory = bundle.getString("nameCategory");
                    final String ratingString = bundle.getString("rating");
                    String lat = bundle.getString("lat");
                    String lng = bundle.getString("lng");
                    LatLng coord = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));

                    //Création du marker
                    Marker marker = map.addMarker(new MarkerOptions()
                            .title(name)
                            .snippet(nameCategory)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                            .position(coord));
                    marker.showInfoWindow();
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(coord, 14.0f));

                    //Création de l'infowindow
                    map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                        @Override
                        public View getInfoWindow(Marker arg0) {
                            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View v;
                            //Gestion de la taille du infowindow, par rapport à la taille du titre
                            v = inflater.inflate(R.layout.marker, null);

                            TextView title = (TextView) v.findViewById(R.id.title);
                            TextView categ = (TextView) v.findViewById(R.id.categ);
                            TextView rating = (TextView) v.findViewById(R.id.rating);
                            title.setText(arg0.getTitle());
                            categ.setText(arg0.getSnippet());
                            rating.setText(services.getRating(ratingString));
                            if (rating.getText().toString().equals("Non noté"))
                                rating.setTextSize(13);

                            return v;
                        }

                        @Override
                        public View getInfoContents(Marker marker) {
                            return null;
                        }
                    });

                    //Listener de l'infowindow
                    map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker marker) {  //on accède à la fiche d'établissement --> passage des paramètres

                            String infosStore[] = services.getInfosStore(marker.getTitle());

                            Bundle bundle = new Bundle();
                            bundle.putBoolean("fromCarte", true);
                            bundle.putString("name", marker.getTitle());
                            bundle.putString("idStore", infosStore[0]);
                            bundle.putString("nameCategory", infosStore[1]);
                            bundle.putString("previousCategory", infosStore[2]);
                            bundle.putString("rating", infosStore[3]);

                            Fragment ficheEtabFragment = new FicheEtabFragment();
                            ficheEtabFragment.setArguments(bundle);
                            final FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.replace(R.id.fragment_container, ficheEtabFragment, "FicheEtabFragment");
                            ft.commit();
                        }
                    });
                }
            }
        }
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
                    if (fromFiche) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("fromCarte", true);
                        bundle.putString("name", getArguments().getString("name"));
                        bundle.putString("idStore", getArguments().getString("idStore"));
                        bundle.putString("nameCategory", getArguments().getString("nameCategory"));
                        bundle.putString("rating", getArguments().getString("rating"));
                        bundle.putString("previousCategory", getArguments().getString("previousCategory"));

                        FicheEtabFragment ficheEtabFragment = new FicheEtabFragment();
                        ficheEtabFragment.setArguments(bundle);
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, ficheEtabFragment);
                        transaction.commit();
                    }
                    else {
                        MainActivityFragment newFragment = new MainActivityFragment();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, newFragment);
                        transaction.commit();
                    }
                    return true;
                }
                return false;
            }
        });
    }


    //ajout des markers des établissements de la catégorie sélectionnée
    public void addMarkers(final Context c, GoogleMap map, String categorie) {
        map.clear();    //efface les markers précédents
        int nbStores = services.getNbStores(categorie);
        final String infos[][] = new String[nbStores][8];
        final String stores[] = services.getStoresCategorie(categorie, infos, nbStores);
        if (stores != null) {
            for (int i = 0; i < nbStores; i++) {
                if (stores[i] != null) {

                    /*------------Création du marker-------------*/
                    map.addMarker(new MarkerOptions()
                            .title(stores[i])       //stores[i] : name
                            .snippet(infos[i][5] + "." + infos[i][0])  // infos[i][5] : nameCategory, infos[i][0] : rating
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                            .position(new LatLng(Double.parseDouble(infos[i][6]), Double.parseDouble(infos[i][7]))));

                    /*------------Création de l'infowindow---------*/
                    map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                        @Override
                        public View getInfoWindow(Marker arg0) {
                            LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View v;

                            v = inflater.inflate(R.layout.marker, null);

                            TextView title = (TextView) v.findViewById(R.id.title);
                            TextView categ = (TextView) v.findViewById(R.id.categ);
                            TextView rating = (TextView) v.findViewById(R.id.rating);
                            title.setText(arg0.getTitle());

                            String snippet = arg0.getSnippet(); //snippet de la forme : categorie.rating
                            String categorie = snippet.substring(0, snippet.indexOf('.'));  //récupération de la catégorie
                            String ratingS = snippet.substring(snippet.indexOf('.')+1,snippet.length());//récupération du rating
                            categ.setText(categorie);
                            rating.setText(services.getRating(ratingS));
                            if (rating.getText().toString().equals("Non noté"))
                                rating.setTextSize(13);

                            return v;
                        }

                        @Override
                        public View getInfoContents(Marker marker) {
                            return null;
                        }
                    });

                    /*------------Listener de l'infowindow------------*/
                    map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker marker) {  //Redirection vers la fiche de l'établissement --> passage des paramètres

                            String infosStore[] = services.getInfosStore(marker.getTitle());

                            Bundle bundle = new Bundle();
                            bundle.putBoolean("fromCarte", true);
                            bundle.putString("name", marker.getTitle());
                            bundle.putString("idStore", infosStore[0]);
                            bundle.putString("nameCategory", infosStore[1]);
                            bundle.putString("previousCategory", infosStore[2]);
                            bundle.putString("rating", infosStore[3]);

                            Fragment ficheEtabFragment = new FicheEtabFragment();
                            ficheEtabFragment.setArguments(bundle);
                            if (getFragmentManager() == null)
                                Log.d("nul", "nul");
                            final FragmentTransaction ft = getFragmentManager().beginTransaction();
                            if (ft == null)
                                Log.d("nul2", "nul2");
                            ft.replace(R.id.fragment_container, ficheEtabFragment, "FicheEtabFragment");
                            ft.commit();
                        }
                    });
                }
            }
        }
        else
            Toast.makeText(getActivity(),"Une erreur s'est produite, vérifiez votre connexion Internet",Toast.LENGTH_LONG).show();
    }


}