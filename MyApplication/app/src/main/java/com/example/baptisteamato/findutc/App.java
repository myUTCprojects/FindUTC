package com.example.baptisteamato.findutc;


import android.app.Application;

import java.util.ArrayList;
import java.util.HashMap;

public class App extends Application {
//sauvegarde de la liste dans RechercheFragment (ne nécessite pas connexion + recherche des drawable à chaque fois)

    ArrayList<HashMap<String, String>> mEtabListItem = null;

    public ArrayList<HashMap<String, String>> getEtabListItem() {
        return mEtabListItem;
    }

    public void setEtabListItem(ArrayList<HashMap<String, String>> etabListItem) {
        this.mEtabListItem = etabListItem;
    }

}