package com.baptisteamato.myapplication;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.LightingColorFilter;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.ClipboardManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baptisteamato.myapplication.R;

import java.io.File;
import java.io.OutputStreamWriter;
import java.util.Arrays;


public class FicheEtabFragment extends Fragment{

    View view;
    Services services;
    Button buttonRetour;
    Button buttonAdd;
    String name = "";
    String nameCategory = "";
    String rating = "";
    String nbOpinions = "";
    String adresse = "";
    String phone = "";
    String hours = "";
    String description = "";
    String lat = "";
    String lng = "";
    ImageView currentOnglet;
    Button buttonVoirAvis;
    Button buttonRedigerAvis;
    Button buttonVoirCarte;
    TextView TextName;
    TextView TextNameCategory;
    TextView TextRating;
    TextView TextAdresse;
    TextView TextPhone;
    TextView TextHoraires;
    TextView TextDescription;
    LinearLayout ll;
    ImageView phoneIm;

    public FicheEtabFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        services = new Services(getActivity());

        //changement de couleur des onglets
        currentOnglet = (ImageView) getActivity().findViewById(R.id.imageRechercher);

        currentOnglet.setAlpha(255);
        currentOnglet.getDrawable().setColorFilter(new LightingColorFilter(0x00000000, 0xFF4D9D6A));

        ((ImageView) getActivity().findViewById(R.id.imageMenu)).setColorFilter(new LightingColorFilter(0x00000000, 0xFF000000));
        ((ImageView) getActivity().findViewById(R.id.imageMenu)).setAlpha(128);
        ((ImageView) getActivity().findViewById(R.id.imageCarte)).setColorFilter(new LightingColorFilter(0x00000000, 0xFF000000));
        ((ImageView) getActivity().findViewById(R.id.imageCarte)).setAlpha(128);

        buttonRetour = (Button) getActivity().findViewById(R.id.buttonLeft);
        buttonRetour.setVisibility(View.VISIBLE);
        buttonAdd = (Button) getActivity().findViewById(R.id.buttonRight);
        buttonAdd.setVisibility(View.VISIBLE);
        buttonAdd.setText("");

        ((ImageView) getActivity().findViewById(R.id.flecheGauche)).setVisibility(View.VISIBLE);

        ((TextView) getActivity().findViewById(R.id.title)).setText("D�tails");

        //on affiche les onglets
        ((LinearLayout) getActivity().findViewById(R.id.linearLayout)).setVisibility(View.VISIBLE);

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
            view = inflater.inflate(R.layout.fragment_very_small_ficheetab, container, false);
        else {
            if (screenInches <= 5)
                view = inflater.inflate(R.layout.fragment_small_ficheetab, container, false);
            else {
                if (screenInches <= 6)
                    view = inflater.inflate(R.layout.fragment_ficheetab, container, false);
                else
                    view = inflater.inflate(R.layout.fragment_large_ficheetab, container, false);
            }
        }

        /*-----------find Views-------------*/
        buttonVoirAvis = (Button) view.findViewById(R.id.voir_avis);
        buttonRedigerAvis = (Button) view.findViewById(R.id.rediger_avis);
        buttonVoirCarte = (Button) view.findViewById(R.id.voir_carte);
        TextName = (TextView) view.findViewById(R.id.name);
        TextNameCategory = (TextView) view.findViewById(R.id.nameCategory);
        TextRating = (TextView) view.findViewById(R.id.rating);
        TextAdresse = (TextView) view.findViewById(R.id.textAdresse);
        TextPhone = (TextView) view.findViewById(R.id.textPhone);
        TextHoraires = (TextView) view.findViewById(R.id.textHoraires);
        TextDescription = (TextView) view.findViewById(R.id.textDescription);
        ll = (LinearLayout) view.findViewById(R.id.llHours);
        phoneIm = (ImageView) view.findViewById(R.id.ic_phone);


        //on charge les infos APRES que le fragment soit affich�
        view.post(new Runnable() {
            @Override
            public void run() {
                /*-----------get Arguments-------------*/
                final String idStore = getArguments().getString("idStore");
                final String previousCategory = getArguments().getString("previousCategory"); //nom de la cat�gorie d'o� l'on vient
                final String previousCategoryPlural = getArguments().getString("previousCategoryPlural"); //pluriel de la cat�gorie d'o� l'on vient
                String ficheEtab[] = services.getFicheEtab(idStore);
                final boolean fromCarte = getArguments().getBoolean("fromCarte", false);
                final boolean fromAvis = getArguments().getBoolean("fromAvis", false);
                final boolean fromListe = getArguments().getBoolean("fromListe", false);
                final boolean fromFavoris = getArguments().getBoolean("fromFavoris", false);

                /*------------R�cup�ration des informations de l'�tablissement-------------*/
                name = ficheEtab[0];
                nameCategory = ficheEtab[1];
                rating = ficheEtab[2];
                nbOpinions = ficheEtab[3];
                adresse = ficheEtab[4];
                phone = ficheEtab[5];
                hours = ficheEtab[6];
                description = ficheEtab[7];
                lat = ficheEtab[8];
                lng = ficheEtab[9];

                /*------------V�rification de l'activit� d'o� l'on vient--------------------*/

                if (fromCarte)
                    buttonRetour.setText("Carte");
                else {
                    if ((fromListe || fromAvis))
                        buttonRetour.setText(previousCategoryPlural);
                    else {
                        if (fromFavoris)
                            buttonRetour.setText("Favoris");
                        else {
                            ((Button) getActivity().findViewById(R.id.buttonLeft)).setVisibility(View.GONE);
                            ((ImageView) getActivity().findViewById(R.id.flecheGauche)).setVisibility(View.GONE);
                        }
                    }
                }

                //On redimensionne le boutton s'il est trop long
                if (buttonRetour.getText().length() >= 10)
                    buttonRetour.setTextSize(15);
                if (buttonRetour.getText().length() >= 15)
                    buttonRetour.setTextSize(13);

                /*-------------Remplissage des informations-----------*/
                buttonVoirAvis.append(" (" + nbOpinions + ")");
                TextName.setText(name);
                TextNameCategory.setText(nameCategory);
                TextRating.setText(services.getRating(rating));
                TextRating.append(" (" + nbOpinions + " avis)");
                if (!adresse.equals(""))
                    TextAdresse.setText(adresse);
                else
                    TextAdresse.setText("Pas d'adresse disponible.");
                if (!phone.equals(""))
                    TextPhone.setText(phone);
                else
                    TextPhone.setText("Pas de num�ro de t�l�phone disponible.");
                if (!hours.equals(""))
                    TextHoraires.setText(hours);
                else
                    ll.setVisibility(View.GONE);
                if (!description.equals(""))
                    TextDescription.setText(description);
                else
                    TextDescription.setText("Pas de description disponible");

                /*Boutton favoris*/
                final String listeFavoris[] = services.getFavoris();
                if ((listeFavoris == null)||(!Arrays.asList(listeFavoris).contains(name)))
                    buttonAdd.setText("+");
                else
                    buttonAdd.setText("-");

                /*--------Appui sur le num�ro de t�l�phone : copie du num�ro---------*/

                TextPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                        cm.setText(TextPhone.getText());
                        Toast.makeText(getActivity(), "Num�ro de t�l�phone copi�.", Toast.LENGTH_SHORT).show();
                    }
                });

                /*---------------Buttons Listeners------------------*/

                //Au clic de l'image t�l�phone, on pr�pare l'appel
                phoneIm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), "Chargement...", Toast.LENGTH_SHORT).show();
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:" + TextPhone.getText().toString()));
                        startActivity(callIntent);
                    }
                });


                buttonRetour.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (fromCarte) {    //si l'on vient de la Carte, retour vers la carte
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("fromFiche", true);
                            bundle.putString("adresse", adresse);
                            bundle.putString("idStore", idStore);
                            bundle.putString("previousCategory", previousCategory);
                            bundle.putString("name", name);
                            bundle.putString("nameCategory", nameCategory);
                            bundle.putString("rating", rating);
                            bundle.putString("lat", lat);
                            bundle.putString("lng", lng);

                            Fragment carte = new CarteFragment();
                            carte.setArguments(bundle);
                            final FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.replace(R.id.fragment_container, carte, "CarteFragment");
                            ft.commit();
                        } else {
                            if ((fromListe)||(fromAvis)) {
                                Bundle bundle = new Bundle();
                                bundle.putString("nameCategory", previousCategory);

                                Fragment rechercher2 = new Recherche2Fragment();
                                rechercher2.setArguments(bundle);
                                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                                ft.replace(R.id.fragment_container, rechercher2, "Recherche2Fragment");
                                ft.commit();
                            } else {
                                if (fromFavoris) {
                                    Fragment favoris = new FavorisFragment();
                                    final FragmentTransaction ft = getFragmentManager().beginTransaction();
                                    ft.replace(R.id.fragment_container, favoris, "FavorisFragment");
                                    ft.commit();
                                }
                                else {
                                    MainActivityFragment mainActivityFragment = new MainActivityFragment();
                                    final FragmentTransaction ft = getFragmentManager().beginTransaction();
                                    ft.replace(R.id.fragment_container, mainActivityFragment, "MainActivityFragment");
                                    ft.commit();
                                }
                            }
                        }
                    }
                });

                buttonAdd.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (buttonAdd.getText().equals("+")) {
                            /*WRITE DATA*/
                            try {
                                File file = getActivity().getFileStreamPath(getActivity().getResources().getString(R.string.file_name));
                                if (file.exists()) {
                                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getActivity().openFileOutput(getActivity().getResources().getString(R.string.file_name), Context.MODE_APPEND));
                                    outputStreamWriter.append(name + ";");
                                    outputStreamWriter.close();
                                } else {
                                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getActivity().openFileOutput(getActivity().getResources().getString(R.string.file_name), Context.MODE_PRIVATE));
                                    outputStreamWriter.write(name + ";");
                                    outputStreamWriter.close();
                                }
                                Toast.makeText(getActivity(), "Etablissement ajout� aux favoris", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(getActivity(), "Une erreur s'est produite. Veuillez r�essayer.", Toast.LENGTH_SHORT).show();
                            }
                            buttonAdd.setText("-");
                        }
                        else {
                            //DELETE DATA
                            try {
                                OutputStreamWriter outputStreamWriterTemp = new OutputStreamWriter(getActivity().openFileOutput(getActivity().getResources().getString(R.string.file_name), Context.MODE_PRIVATE));
                                outputStreamWriterTemp.write("");
                                outputStreamWriterTemp.close();
                                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getActivity().openFileOutput(getActivity().getResources().getString(R.string.file_name), Context.MODE_APPEND));

                                for (int i=0; i<listeFavoris.length; i++) {
                                    if (!listeFavoris[i].equals(name))
                                        outputStreamWriter.append(listeFavoris[i] + ";");
                                }
                                outputStreamWriter.close();
                                Toast.makeText(getActivity(), "Etablissement retir� des favoris", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(getActivity(), "Une erreur s'est produite. Veuillez r�essayer.", Toast.LENGTH_SHORT).show();
                            }
                            buttonAdd.setText("+");
                        }
                    }
                });

                buttonVoirAvis.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("idStore", idStore);
                        bundle.putString("name", name);
                        bundle.putString("nameCategory", nameCategory);
                        bundle.putString("previousCategory", previousCategory);
                        bundle.putString("previousCategoryPlural", previousCategoryPlural);
                        bundle.putString("rating", rating);
                        if (fromFavoris)
                            bundle.putBoolean("fromFavoris", true);

                        Fragment avis = new AvisFragment();
                        avis.setArguments(bundle);
                        final FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.fragment_container, avis, "AvisFragment");
                        ft.commit();
                    }
                });

                buttonRedigerAvis.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("etab_selectionne", true);
                        bundle.putString("idStore", idStore);
                        bundle.putString("previousCategory", previousCategory);
                        bundle.putString("previousCategoryPlural", previousCategoryPlural);
                        if (fromFavoris)
                            bundle.putBoolean("fromFavoris", true);

                        Fragment addComment = new AddCommentFragment();
                        addComment.setArguments(bundle);
                        final FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.fragment_container, addComment, "AddCommentFragment");
                        ft.commit();
                    }
                });

                buttonVoirCarte.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("fromFiche", true);
                        bundle.putString("adresse", adresse);
                        bundle.putString("name", name);
                        bundle.putString("nameCategory", nameCategory);
                        bundle.putString("rating", rating);
                        bundle.putString("lat", lat);
                        bundle.putString("lng", lng);

                        Fragment carte = new CarteFragment();
                        carte.setArguments(bundle);
                        final FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.fragment_container, carte, "CarteFragment");
                        ft.commit();
                    }
                });

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

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    buttonRetour.performClick();
                    return true;
                }
                return false;
            }
        });
    }

}
