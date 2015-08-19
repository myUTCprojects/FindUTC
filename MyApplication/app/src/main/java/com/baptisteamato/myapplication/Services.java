package com.baptisteamato.myapplication;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.baptisteamato.myapplication.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Services extends FragmentActivity{

    private Context mContext;

    public Services (Context context){
        mContext = context;
    }

    //renvoi du contenu de la page indiquée
    private String getData(String url) {
        AsyncHttpGet connect = new AsyncHttpGet(mContext);
        String jsonStr = null;
        try {
            connect.execute(url);
        } catch (Exception e) {
            return null;
        }
        try {
            jsonStr = connect.get();
        } catch (Exception e) {
            return null;
        }
        return jsonStr;
    }


    //renvoi du nombre de catégories
    // categories est vide en entrée, et contient le nom des catégories en sortie
    public int getCategories(String [] categories) {    //pour Carte
        String serverURL = mContext.getResources().getString(R.string.api_categories);
        serverURL += "?fields=name";
        String jsonStr = getData(serverURL);
        int j = 0;  //nbCategories
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray data = jsonObj.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonObject = data.getJSONObject(i);

                    categories[i] = jsonObject.optString("name").toString();
                    j++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return j;
    }


    // fonction surchargée
    // renvoi du nombre de catégories
    //categories vide en entrée, contient le nom des catégories en sortie
    //nbStores nul en entrée, contient le nombre de stores pour chaque catégorie
    public int getCategories(String categories[], String nbStores[]) {  //pour Recherche
        String serverURL = mContext.getResources().getString(R.string.api_categories);
        serverURL += "?fields=name,nbStores";
        String jsonStr = getData(serverURL);
        int j = 0;  //nbCategories
        if (jsonStr != null) {
            try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray data = jsonObj.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsonObject = data.getJSONObject(i);

                        categories[i] = jsonObject.optString("name").toString();
                        nbStores[i] = jsonObject.optString("nbStores").toString();
                        j++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return j;
        }
        else
            return 0;
    }


    //renvoi d'un tableau contenant l'ID, la catégorie, la catégorie au pluriel et la note du store en entrée
    public String[] getInfosStore(String nameStore) {
        String serverURL = mContext.getResources().getString(R.string.api_stores);
        serverURL += "?fields=name,idStore,nameCategory,rating";
        String jsonStr = getData(serverURL);
        String infos[] = new String[4];
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray data = jsonObj.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonObject = data.getJSONObject(i);
                    String name = jsonObject.optString("name").toString();
                    String idStore = jsonObject.optString("idStore").toString();
                    String nameCategory = jsonObject.optString("nameCategory").toString();
                    String rating = jsonObject.optString("rating").toString();
                    if (name.equals(nameStore)) {
                        infos[0] = idStore;
                        infos[1] = nameCategory;
                        infos[2] = getPlurielCategorie(nameCategory);
                        infos[3] = rating;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return infos;
    }


    //renvoi du pluriel d'une catégorie
    public String getPlurielCategorie(String categorie) {
        if (!categorie.equals("Tous")) {
            String serverURL = mContext.getResources().getString(R.string.api_categories);
            serverURL += "?fields=name,plural";
            String jsonStr = getData(serverURL);
            String pluralCategorie = "";
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray data = jsonObj.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsonObject = data.getJSONObject(i);
                        String name = jsonObject.optString("name").toString();
                        if (name.equals(categorie))
                            pluralCategorie = jsonObject.optString("plural").toString();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return pluralCategorie;
        }
        else
            return categorie;
    }


    //renvoi d'un tableau contenant le nom des stores dans une catégorie
    //infos est vide en entrée, et contient en sortie : rating, idStore, nbOpinions, description, adress, nameCategory, lat et lng pour chaque store
    public String[] getStoresCategorie(String category, String infos[][], int nbStores) {
        String serverURL = mContext.getResources().getString(R.string.api_stores);
        serverURL += "?fields=name,nameCategory,rating,idStore,nbOpinions,description,adress,lat,lng";
        String jsonStr = getData(serverURL);
        String stores[] = new String[nbStores];
        int j = 0;
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray data = jsonObj.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonObject = data.getJSONObject(i);
                    String nameCategory = jsonObject.optString("nameCategory").toString();
                    if ((category.equals("Tous")) || (nameCategory.equals(category))) {
                        stores[j] = jsonObject.optString("name").toString();
                        infos[j][0] = jsonObject.optString("rating").toString();
                        infos[j][1] = jsonObject.optString("idStore").toString();
                        infos[j][2] = jsonObject.optString("nbOpinions").toString();
                        infos[j][3] = jsonObject.optString("description").toString();
                        infos[j][4] = jsonObject.optString("adress").toString();
                        infos[j][5] = category;
                        infos[j][6] = jsonObject.optString("lat").toString();
                        infos[j][7] = jsonObject.optString("lng").toString();
                        j++;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return stores;
        }
        else
            return null;
    }


    //renvoi du nombre de stores dans une catégorie
    public int getNbStores(String nameCategory) {
        String serverURL = mContext.getResources().getString(R.string.api_categories);
        serverURL += "?fields=name,nbStores";
        String jsonStr = getData(serverURL);
        int nbStores = 0;
        int nbTot = 0;   //pour la catégorie "Tous"
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray data = jsonObj.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonObject = data.getJSONObject(i);
                    if (jsonObject.optString("name").toString().equals(nameCategory))
                        nbStores = Integer.parseInt(jsonObject.optString("nbStores").toString());
                    nbTot += Integer.parseInt(jsonObject.optString("nbStores").toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (nameCategory.equals("Tous"))
            return nbTot;
        else
            return nbStores;
    }


    //renvoi de l'ID du dernier avis affiché (pour savoir à partir du quel rechercher en cliquant sur "Plus d'avis")
    //date et commentary sont vides en entrées, et contiennent respectivement la date et le commentaire de chaque avis en sortie
    public String getAvis(String idStore, String lastId, String date[], String note[], String commentary[]) {
        String idLastOpinion = "";
        String serverURL = mContext.getResources().getString(R.string.api_store_opinion);
        serverURL += idStore;
        if (lastId != null)    //si l'on vient des avis précédents, avec le boutton "Plus d'avis"
            serverURL += "&before=" + lastId;  //on affiche les avis suivants
        String jsonStr = getData(serverURL);
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray data = jsonObj.getJSONArray("data");
                if (data.length() == 0)
                    return null;
                for(int i=0; i < data.length(); i++){
                    JSONObject jsonObject = data.getJSONObject(i);

                    date[i] = jsonObject.optString("date").toString();
                    note[i] = jsonObject.optString("rating").toString();
                    commentary[i] = jsonObject.optString("commentary").toString();
                    idLastOpinion =  jsonObject.optString("idOpinion").toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return idLastOpinion;
    }


    //renvoie d'un tableau contenant name, nameCategory, rating, nbOpinions, adresse, phone, hours description, lat et lng du store en entrée
    public String[] getFicheEtab(String idStore) {
        String ficheEtab[] = new String [10];
        String serverURL = mContext.getResources().getString(R.string.api_stores);
        serverURL += idStore + "?fields=name,nameCategory,rating,nbOpinions,adress,phone,hours,description,lat,lng";
        String jsonStr = getData(serverURL);
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray data = jsonObj.getJSONArray("data");
                for(int i=0; i < data.length(); i++){
                    JSONObject jsonObject = data.getJSONObject(i);

                    ficheEtab[0] = jsonObject.optString("name").toString();
                    ficheEtab[1] = jsonObject.optString("nameCategory").toString();
                    ficheEtab[2] = jsonObject.optString("rating").toString();
                    ficheEtab[3] = jsonObject.optString("nbOpinions").toString();
                    ficheEtab[4] = jsonObject.optString("adress").toString();
                    ficheEtab[5] = jsonObject.optString("phone").toString();
                    ficheEtab[6] = jsonObject.optString("hours").toString();
                    ficheEtab[7] = jsonObject.optString("description").toString();
                    ficheEtab[8] = jsonObject.optString("lat").toString();
                    ficheEtab[9] = jsonObject.optString("lng").toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ficheEtab;
    }


    //renvoi un booléen : true si une connexion Internet est activée, false sinon
    public boolean connectionActivee() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //convertit une note en rating sous forme d'étoiles
    public String getRating(String note) {
        String rating = "";
        switch (note) {
            case "1" :
                rating = "*";
                break;
            case "2" :
                rating = "* *";
                break;
            case "3" :
                rating = "* * *";
                break;
            case "4" :
                rating = "* * * *";
                break;
            case "5" :
                rating = "* * * * *";
                break;
            default:
                rating = "Non noté";
        }
        return rating;
    }

    //renvoie un tableau contenant le nom des établissements favoris
    public String[] getFavoris() {
        String res = null;
        try {

            /*File fileTemp = getActivity().getFileStreamPath(getActivity().getResources().getString(R.string.file_name));
            fileTemp.delete();*/

            File file = mContext.getFileStreamPath(mContext.getResources().getString(R.string.file_name));
            if (file.exists()) {

                InputStream inputStream = mContext.openFileInput(mContext.getResources().getString(R.string.file_name));

                if (inputStream != null) {    //si le fichier existe
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString = "";
                    StringBuilder stringBuilder = new StringBuilder();

                    while ((receiveString = bufferedReader.readLine()) != null) {
                        stringBuilder.append(receiveString);
                    }

                    inputStream.close();
                    res = stringBuilder.toString();
                }
            }
            else
                Log.d("vide", "ok");
        }
        catch (Exception e) {
            Log.d("error","ok");
        }
        if ((res != null)&&(res != ""))
            return res.split(";");  //on parse le fichier, contenant les noms des établissements séparés par ';'
        else
            return null;
    }

    //renvoi du nombre de favoris
    public int getNbFavoris() {
        String favoris[] = getFavoris();
        if (favoris == null)
            return 0;
        else
            return favoris.length;
    }

    //renvoi d'un tableau contenant pour chaque store des favoris : name, rating, idStore, nbOpinions, description, adress, nameCategory, lat et lng
    public String[][] getInfosFavoris(String listeFavoris[]) {
        String serverURL = mContext.getResources().getString(R.string.api_stores);
        serverURL += "?fields=name,nameCategory,rating,idStore,nbOpinions,description,adress,lat,lng";
        String jsonStr = getData(serverURL);
        String infos[][] = new String[listeFavoris.length][4];
        int j = 0;
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray data = jsonObj.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonObject = data.getJSONObject(i);
                    String name = jsonObject.optString("name").toString();
                    if (Arrays.asList(listeFavoris).contains(name)) {
                        infos[j][0] = name;
                        infos[j][1] = jsonObject.optString("rating").toString();
                        infos[j][2] = jsonObject.optString("idStore").toString();
                        infos[j][3] = jsonObject.optString("nbOpinions").toString();
                        j++;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return infos;
        }
        else
            return null;
    }
}