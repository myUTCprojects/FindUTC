package com.example.baptisteamato.findutc;


import android.app.Application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class App extends Application {
//sauvegarde de la liste dans RechercheFragment (ne n�cessite pas connexion + recherche des drawable � chaque fois)

    ArrayList<HashMap<String, String>> mEtabListItem = null;    //RechercheFragment
    List<String> mListItems = null;  //CarteFragment

    public ArrayList<HashMap<String, String>> getEtabListItem() {
        return mEtabListItem;
    }

    public void setEtabListItem(ArrayList<HashMap<String, String>> etabListItem) {
        this.mEtabListItem = etabListItem;
    }

    public List<String> getListItems() {
        return mListItems;
    }

    public void setListItems(List<String> listItems) {
        this.mListItems = listItems;
    }

}