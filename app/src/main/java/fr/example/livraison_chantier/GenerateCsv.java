package fr.example.livraison_chantier;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *Cette classe permet de créer un fichier csv dans le cache de la tablette.
 */
public class GenerateCsv {

    /**
     *Cette méthode permet de créer un fichier csv dans le cache de la tablette.
     * @param sFileName nom du fichier
     * @param fileContent corps du fichier
     */
    public static void generateCsvFile(File sFileName, String fileContent) {


        PrintWriter writer = null;
        try {
            writer=new PrintWriter(sFileName,"UTF-8");
            writer.flush();
            writer.write(fileContent);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }

    }
}