package fr.example.livraison_chantier;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import fr.example.livraison_chantier.Mail;

/**
 * Classe qui créer une notification lors de l'envoie d'un mail automatique
 * (ATTENTION CETTE CLASSE N'EST PLUS UTILE DANS LES DERNIERES VERSIONS)
 */
@Deprecated
public class SendMailTask extends AsyncTask {

    private ProgressDialog statusDialog;
    private Activity sendMailActivity;

    /**
     * Initialise la vue
     * @param activity l'activité de l'application
     */
    public SendMailTask(Activity activity) {
        sendMailActivity = activity;

    }

    /**
     * Juste avant l'éxecution de la classe
     */
    protected void onPreExecute() {
        statusDialog = new ProgressDialog(sendMailActivity);
        statusDialog.setMessage("Getting ready...");
        statusDialog.setIndeterminate(false);
        statusDialog.setCancelable(false);
        statusDialog.show();
    }

    /**
     *  Pendant l'éxecution de la classe
     * @param args
     * @return null (la classe créer juste une notification)
     */
    @Override
    protected Object doInBackground(Object... args) {
        try {
            Log.i("SendMailTask", "About to instantiate GMail...");
            publishProgress("Processing input....");
            Mail androidEmail = new Mail(args[0].toString(),
                    args[1].toString(), (List) args[2], args[3].toString(),
                    args[4].toString());
            publishProgress("Preparing mail message....");
            androidEmail.createEmailMessage();
            publishProgress("Sending email....");
            androidEmail.sendEmail();
            publishProgress("Email Sent.");
            Log.i("SendMailTask", "Mail Sent.");
        } catch (Exception e) {
            publishProgress(e.getMessage());
            Log.e("SendMailTask", e.getMessage(), e);
        }
        return null;
    }

    /**
     * Met à jour le message de la notification
     * @param values
     */
    @Override
    public void onProgressUpdate(Object... values) {
        statusDialog.setMessage(values[0].toString());

    }

    /**
     * Fermeture de la notification après l'éxecution de la classe
     * @param result
     */
    @Override
    public void onPostExecute(Object result) {
        statusDialog.dismiss();
    }

}