package fr.example.livraison_chantier;
import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

/**
 * Classe permetttant de lire les différents fichiers interne à l'application
 */
public  class Utils {

    /**
     *
     */
    public Utils(){

    }
    /**
     * Méthode permettant de lires les fichiers JSON de l'application
     * @param context le context de l'application
     * @param fileName le nom du fichier voulue
     * @return une String du contenu du fichier JSON
     */
    public static String getJsonFromAssets(Context context, String fileName) {
        String jsonString;
        try {
            InputStream is = context.getAssets().open(fileName); // ouvre le fichier filename

            int size = is.available(); // retourne le nombre de bits lisibles par un read()
            byte[] buffer = new byte[size]; // créer un buffer à rentrer dans un read() afin de lire les
            // informations d'un fichier
            is.read(buffer); //lis les informations du fichier et les retourne dans le buffer
            is.close();//fermeture du fichier

            jsonString = new String(buffer, "UTF-8"); // créer un string en fonction des
            // informations lu dans le fichier
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return jsonString;// retourne le jsonString
    }
}