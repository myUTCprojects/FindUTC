package com.baptisteamato.myapplication;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baptisteamato.myapplication.R;

public class AddCommentFragment extends Fragment {

    Services services;
    String nom_etab_string = "";    //nom de l'établissement : soit obtenu depuis une fiche, soit écrit en EditText (--> déclaré en global)
    String idStore = "0";   //par défaut, si on accède à la rédaction de commentaire depuis le Menu
    Boolean etab_selectionne;
    Button annuler;

    public AddCommentFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        services = new Services(getActivity());

        /*---------------------------------*/
        ((TextView) getActivity().findViewById(R.id.title)).setText("Rédiger un avis");

        ((ImageView) getActivity().findViewById(R.id.imageMenu)).setAlpha(128);
        ((ImageView) getActivity().findViewById(R.id.imageRechercher)).setAlpha(128);
        ((ImageView) getActivity().findViewById(R.id.imageCarte)).setAlpha(128);


        Button envoyer = (Button) getActivity().findViewById(R.id.buttonRight);
        envoyer.setText("Envoyer");
        envoyer.setVisibility(View.VISIBLE);
        annuler = (Button) getActivity().findViewById(R.id.buttonLeft);
        annuler.setText("Annuler");
        annuler.setVisibility(View.VISIBLE);

        ((ImageView) getActivity().findViewById(R.id.flecheGauche)).setVisibility(View.GONE);

        //on n'affiche pas les onglets
        ((LinearLayout) getActivity().findViewById(R.id.linearLayout)).setVisibility(View.GONE);
        /*-----------------------------------*/

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
        final double screenInches = Math.sqrt(x+y);
        /*------------------*/

        View view;

        if (screenInches > 6) {
            envoyer.setTextSize(23);
            annuler.setTextSize(23);
            view = inflater.inflate(R.layout.fragment_large_addcomment, container, false);
        }
        else
            view = inflater.inflate(R.layout.fragment_addcomment, container, false);

        /*---------------------find Views------------------*/

        TextView titre_nom_etab = (TextView) view.findViewById(R.id.titre_nom_etab);
        final EditText nom_etab = (EditText) view.findViewById(R.id.nom_etab);
        final EditText commentaire = (EditText) view.findViewById(R.id.commentaire);
        final Button avis = (Button) view.findViewById(R.id.avis);
        final EditText login = (EditText) view.findViewById(R.id.login);

        /*----------------get Arguments-------------------*/
        Bundle bundle=getArguments();
        if (bundle != null) {
            etab_selectionne = bundle.getBoolean("etab_selectionne", false);
            if (etab_selectionne) {     //true si on accede a cette activite depuis une fiche d'etablissement --> pas besoin de l'EditText
                titre_nom_etab.setVisibility(View.GONE);
                nom_etab.setVisibility(View.GONE);
                nom_etab_string = "rempli";
                idStore = bundle.getString("idStore");
            }
        }
        else {
            etab_selectionne = false;
        }

        /*------------------Buttons Listeners--------------------*/
        avis.setOnClickListener(new View.OnClickListener() {    //choix de l'avis
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {
                        "*", "* *", "* * *", "* * * *", "* * * * *"
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                if (builder == null)
                    Log.i("nul", "nul");
                builder.setTitle("Quel est votre ressenti ?");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        avis.setText(items[item]);
                        if (screenInches > 6)
                            avis.setTextSize(35);
                        else
                            avis.setTextSize(25);
                        avis.setTextColor(getResources().getColor(R.color.findUTC));
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });


        envoyer.setOnClickListener(new View.OnClickListener() { //envoi de l'avis
            @Override
            public void onClick(View v) {

                if (!services.connectionActivee())
                    Toast.makeText(getActivity(), "L'application nécessite une connexion Internet", Toast.LENGTH_SHORT).show();
                else {
                    if (!etab_selectionne)  //si l'on accède depuis le Menu, on lit le nom depuis l'EditText
                        nom_etab_string = nom_etab.getText().toString();
                    String commentaire_string = commentaire.getText().toString();
                    String avis_string = avis.getText().toString();
                    String login_string = login.getText().toString();
                    String rating_string = "";
                    switch (avis_string) {
                        case "*":
                            rating_string = "1";
                            break;
                        case "* *":
                            rating_string = "2";
                            break;
                        case "* * *":
                            rating_string = "3";
                            break;
                        case "* * * *":
                            rating_string = "4";
                            break;
                        case "* * * * *":
                            rating_string = "5";
                            break;
                    }

                    /*----------------Vérification des saisies--------------*/
                    if ((nom_etab_string.equals(new String(""))) || (commentaire_string.equals(new String(""))) || (avis_string.equals(new String("Avis"))) || (login_string.equals(new String(""))))
                        Toast.makeText(getActivity(), "Certains champs n'ont pas été correctement remplis", Toast.LENGTH_SHORT).show();
                    else {  //envoi des données
                        AsyncHttpPost envoi = new AsyncHttpPost(getActivity());
                        envoi.execute(idStore, nom_etab_string, login_string, rating_string, commentaire_string);
                        int code = 500;     //500 : erreur interne à l'API, 200 : OK
                        try {
                            code = envoi.get();
                        } catch (Exception e) {
                            code = 0;
                        }

                        switch (code) {
                            case 200:   //retour MainActivityFragment
                                Toast.makeText(getActivity(), "Avis envoyé !", Toast.LENGTH_SHORT).show();
                                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                                ft.replace(R.id.fragment_container, new MainActivityFragment(), "MainActivityFragment");
                                ft.commit();
                                break;
                            case 400:
                                Toast.makeText(getActivity(), "Ce login n'existe pas.", Toast.LENGTH_SHORT).show();
                                break;
                            case 401:
                                Toast.makeText(getActivity(), "Connexion au serveur impossible.", Toast.LENGTH_SHORT).show();
                                break;
                            case 500:
                                Toast.makeText(getActivity(), "Erreur interne. Impossible d'envoyer l'avis.", Toast.LENGTH_SHORT).show();
                                break;
                            case 0:
                                Toast.makeText(getActivity(), "Temps de connection trop long. Veuillez réessayer plus tard.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        annuler.setOnClickListener(new View.OnClickListener() { //retour Menu
            @Override
            public void onClick(View v) {
                if (etab_selectionne) { //retour FicheEtabFragment
                    Bundle bundle = new Bundle();
                    bundle.putString("idStore", idStore);
                    bundle.putString("previousCategory",getArguments().getString("previousCategory"));
                    bundle.putString("previousCategoryPlural",getArguments().getString("previousCategoryPlural"));
                    if (getArguments().getBoolean("fromFavoris", false) == true)
                        bundle.putBoolean("fromFavoris", true);
                    FicheEtabFragment ficheEtabFragment = new FicheEtabFragment();
                    ficheEtabFragment.setArguments(bundle);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, ficheEtabFragment);
                    transaction.commit();
                }
                else {  //retour MainActivityFragment
                    MainActivityFragment mainActivityFragment = new MainActivityFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, mainActivityFragment);
                    transaction.commit();
                }
            }
        });
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        /*----------------Gestion du boutton Back---------------*/
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    annuler.performClick();
                    return true;
                }
                return false;
            }
        });
    }
}