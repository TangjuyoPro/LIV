package fr.example.livraison_chantier;

import static android.view.View.*;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.IOException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Classe principale, elle permet la liaison entre les autres classes et les différentes vues de l'application.
 */
public class MainActivity extends AppCompatActivity {


    // definition des différents REGEX
    private final String dateregex = "^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[13-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$";
    private final String hoursregex = "^(?:(?:([01]?\\d|2[0-3]):)?([0-5]?\\d):)?([0-5]?\\d)$";
   // private final String praxedoregex = "^([0-9]([0-9]*)((#[0-9]*)?))$";
    PopupWindow popupWindowDate = null;
    PopupWindow popupWindowHeure = null;
    private String currDate = "";
    private String currHour = "";
    private ImageView imgStock;
    private ImageView imgFournisseur;
    private ImageView imgENLFournisseur;
    private TextView Datesouhaitee;
    private TextView HeureSouhaitee;

    //DEFINITION DE TOUS LES ID POSSIBLE
    private TextView textZone;
    private TextView textService;
    //private TextView numPraxedo;
    private TextView jourProposee;
    private TextView HeureProposee;
    private RadioGroup radioGroup;
    private RadioGroup radioGroup2;
    private RadioGroup radioGroup3;
    private TextView numeroOxalys;
    private TextView fournisseur;
    private TextView lieuEnlevement;
    private TextView nombreColis;
    private TextView textdemandeur;
    private TextView textAdresse;
    private TextView textPostal;
    private TextView textdechet;
    private TextView textmaterielLourd;
    private TextView textcommentaires;
    private TextView texttechnicienSurPlace;
    private TextView textCI;
    private TextView possibleLivraison;
    private TextView textJourArrivee;
    private TextView textDateDemande;
    private TextView textHeureDemande;
    private TextView ValidationCommande;
    private String InitialDate;
    private String warningToast;
    private Snackbar snackbar;
    private TextView commStock;
    private TextView numeroOxalys2;
    private TextView consignes;
    private boolean vv = false;

    /**
     * Méthode qui se lance au démarrage de l'application, elle permet d'initialiser l'heure de la demande
     * et les différentes interactions de l'application.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.main);
        if (customTitleSupported) {
            getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.title_layout);
        }
        // MET LA DATE ET LHEURE DE DEMANDE
        clearCache();
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        int month = c.get(Calendar.MONTH) % 12 + 1;
        this.InitialDate = c.get(Calendar.DATE) + "/" + month + "/" + c.get(Calendar.YEAR);
        this.textDateDemande = findViewById(R.id.textDateDemande);
        this.textDateDemande.setText(InitialDate);
        this.Datesouhaitee = findViewById(R.id.dateSouhaitee);
        this.HeureSouhaitee = findViewById(R.id.heureSouhaitee);
        String time = c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);
        this.textHeureDemande = findViewById(R.id.textHeureDemande);
        this.textHeureDemande.setText(time);
        this.imgFournisseur = findViewById(R.id.imgFournisseur);
        this.imgENLFournisseur = findViewById(R.id.imgENLFournisseur);
        this.commStock = findViewById(R.id.commStock);
        this.numeroOxalys2 = findViewById(R.id.numeroOxalys2);
        this.consignes = findViewById(R.id.consignes);
        //VA CHERCHER TOUT LES ID DISPONIBLE
        this.textCI = findViewById(R.id.CI);
        this.textCI.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    findService(v);
                }
            }
        });
        this.imgStock = findViewById(R.id.imgStock);
        this.textCI.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                findService(getCurrentFocus());
            }
        });
        this.textZone = findViewById(R.id.textZone);
        this.textService = findViewById(R.id.textService);
        this.jourProposee = findViewById(R.id.jourProposeeText);
        this.HeureProposee = findViewById(R.id.heureProposeeText);
        this.radioGroup = findViewById(R.id.radioGroup);
        this.radioGroup2 = findViewById(R.id.radioGroup2);
        this.radioGroup3 = findViewById(R.id.radioGroup3);
        this.numeroOxalys = findViewById(R.id.numeroOxalys);
        this.fournisseur = findViewById(R.id.fournisseur);
        this.lieuEnlevement = findViewById(R.id.lieuEnlevement);
        this.nombreColis = findViewById(R.id.nombreColis);
        this.textdemandeur = findViewById(R.id.demandeur);
        this.textAdresse = findViewById(R.id.Adresse);
        this.textPostal = findViewById(R.id.codePostal);
        this.textPostal.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    findZone(v);
                }
            }
        });
        this.textPostal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                findZone(getCurrentFocus());
            }
        });
        this.textdechet = findViewById(R.id.dechets);
        this.textmaterielLourd = findViewById(R.id.materielLourd);
        this.textcommentaires = findViewById(R.id.commentaires);
        this.texttechnicienSurPlace = findViewById(R.id.technicienSurPlace);
        this.possibleLivraison = findViewById(R.id.possibleLivraison);
        this.textJourArrivee = findViewById(R.id.textJourArrivee);
        this.ValidationCommande = findViewById(R.id.ValidationCommande);

        Log.i("MainActivity", "\"La Date et l'heure de lancement de l'application correctement initialisé.");
    }


    public void UpdateView(View v){
        hideSoftKeyboard(MainActivity.this, v);
    }

    /**
     * Supprime les fichiers csv accumulés dans le cache de l'application ( à chaque demande de livraison un fichier csv
     * est créer dans le cache pour permettre l'envoi de fichier par mail). Cette méhode est lancé à chaque démarrage de l'application.
     */
    private void clearCache() {
        File file = getCacheDir();
        for (File f : file.listFiles()) {
            f.delete();
        }
    }

    /**
     * Permet de cacher le clavier tactile de l'application s'il est lancé.
     * @param activity l'activité de l'application de base
     * @param view Vue actuelle de l'application
     */
    public static void hideSoftKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    /**
     * Permet la lecture du fichier interne à l'application regroupant les CI et leurs Services respectifs.
     * Renvoie le Service liée au CI renseigné par l'utilisateur.
     * @param view Vue actuelle de l'application
     */
    public void findService(View view) {
        String tmpci = this.textCI.getText().toString().toUpperCase();
        String ci = "";
        try {
            ci = tmpci.substring(0, 2);
        } catch (StringIndexOutOfBoundsException e) {
            ci = "";
        }
        if (tmpci.length() != 6) {
            ci = "";
        }
        JsonElement j = null;
        String jsonFile = Utils.getJsonFromAssets(getApplicationContext(), "service.json");
        if (jsonFile == null) {
            Log.i("MainActivty", "Erreur à la lecture de service.json");
        } else {
            JsonObject jsonObject = JsonParser.parseString(jsonFile).getAsJsonObject();
            try {
                j = jsonObject.get(ci);
            } catch (StringIndexOutOfBoundsException e) {
                this.textService.setText("CI non valide");
            }
        }
        String service = null;
        if (j != null) {
            service = j.getAsString();
        }
        try {
            if (service != null && !service.equals("")) {
                this.textService.setText(service);
            } else if (service != null && service.equals("")) {
                this.textService.setText("");
            } else {
                this.textService.setText("CI non valide");
            }
            Log.i("MainActivity", "\"Lecture du fichier service.JSON correct!");
        } catch (NullPointerException e) {
            this.textService.setText("CI Non Valide");
        }
    }

    /**
     * Permet la lecture du fichier interne à l'application regroupant les codes postaux en fonction de leurs zones.
     * Renvoie la zone liée au code postal renseigné par l'utilisateur.
     * @param view Vue actuelle de l'application
     */
    public void findZone(View view) {
        String postal = this.textPostal.getText().toString();
        String jsonFile = null;
        JsonElement j = null;
        try {
            jsonFile = Utils.getJsonFromAssets(getApplicationContext(), "zone.json");
            JsonObject jsonObject = JsonParser.parseString(jsonFile).getAsJsonObject();
            j = jsonObject.get(postal);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Problème de lecture des fichiers internes!", Toast.LENGTH_LONG).show();
            Log.i("MainActivity", "\"Lecture du fichier zone.JSON incorrect!");
        }

        Log.i("MainActivity", "\"Lecture du fichier zone.JSON correct!");
        String zone = null;
        if (j != null) {
            zone = j.getAsString();
        }
        if (zone == null) {
            this.textZone.setText("...");
            this.possibleLivraison.setText("/!\\\n Code Postal non renseigné ou invalide \n/!\\");

            Log.i("MainActivity", "\"La livraison est possible ! ( zone assez proche)");

            this.possibleLivraison.setVisibility(VISIBLE);
        } else if (!zone.equals("")) {
            this.textZone.setText(zone);
            this.possibleLivraison.setText("La livraison est possible !");
            this.possibleLivraison.setVisibility(VISIBLE);
            Log.i("MainActivity", "\"La livraison n'est pas possible !");
        } else {
            this.textZone.setText("...");
            this.possibleLivraison.setText("/!\\\nVeuillez renseigné un code postal \n/!\\");
            this.possibleLivraison.setVisibility(VISIBLE);
        }
        if (!this.currDate.equals("") && !this.currHour.equals("")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                System.out.println("ahhhhhbon");
                AfficherHeureDateLivraison(view);
            }
        }
    }

    /**
     * Rend visible les différents champs de saisies utilisés quand l'utilisateur rentre une demande de livraison fournisseur
     * @param view Vue actuelle de l'application
     */
    public void radioButtonFournisseur(View view) {
        this.imgFournisseur.setVisibility(VISIBLE);
        this.imgENLFournisseur.setVisibility(VISIBLE);
        this.imgStock.setVisibility(INVISIBLE);
        this.numeroOxalys.setVisibility(VISIBLE);
        this.nombreColis.setVisibility(VISIBLE);
        this.fournisseur.setVisibility(VISIBLE);
        this.lieuEnlevement.setVisibility(VISIBLE);
        this.consignes.setVisibility(INVISIBLE);
        this.commStock.setVisibility(INVISIBLE);
        this.numeroOxalys2.setVisibility(INVISIBLE);

        Log.i("MainActivity", "\"Boutton Fournisseur Sélectionner!");
    }

    /**
     * Rend visible les différents champs de saisies utilisés quand l'utilisateur rentre une demande de livraison dépot
     * @param view Vue actuelle de l'application
     */
    public void radioButtonDepot(View view) {
        this.imgFournisseur.setVisibility(INVISIBLE);
        this.imgENLFournisseur.setVisibility(INVISIBLE);
        this.imgStock.setVisibility(INVISIBLE);
        this.numeroOxalys.setVisibility(INVISIBLE);
        this.nombreColis.setVisibility(INVISIBLE);
        this.fournisseur.setVisibility(INVISIBLE);
        this.lieuEnlevement.setVisibility(INVISIBLE);
        this.consignes.setVisibility(VISIBLE);
        this.commStock.setVisibility(INVISIBLE);
        this.numeroOxalys2.setVisibility(VISIBLE);
        Log.i("MainActivity", "\"Boutton Dépot Sélectionner!");
    }

    /**
     * Rend visible les différents champs de saisies utilisés quand l'utilisateur rentre une demande de livraison Stock
     * @param view Vue actuelle de l'application
     */
    public void radioButtonStock(View view) {
        this.imgFournisseur.setVisibility(INVISIBLE);
        this.imgENLFournisseur.setVisibility(INVISIBLE);
        this.imgStock.setVisibility(VISIBLE);
        this.numeroOxalys.setVisibility(INVISIBLE);
        this.nombreColis.setVisibility(INVISIBLE);
        this.fournisseur.setVisibility(INVISIBLE);
        this.lieuEnlevement.setVisibility(INVISIBLE);
        this.consignes.setVisibility(INVISIBLE);
        this.commStock.setVisibility(VISIBLE);
        this.numeroOxalys2.setVisibility(INVISIBLE);
        Log.i("MainActivity", "\"Boutton Stock Sélectionner!");
    }

    /**
     * Ferme la fenêtre de renseignement de date
     * Si l'heure et la date sont renseigné lance la méthode de calcul de date.
     * @param view Vue actuelle de l'application
     */
    public void findDate(View view) {

        this.popupWindowDate.dismiss();
        this.popupWindowDate = null;
        if (!this.currHour.equals("")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AfficherHeureDateLivraison(view);
            }
        }
    }

    /**
     * Ferme la fenêtre de renseignement de l'heure.
     * Si l'heure et la date sont renseigné appelle la méthode de calcul de date.
     * @param view
     */
    public void findHour(View view) {

        this.popupWindowHeure.dismiss();
        this.popupWindowHeure = null;
        if (!this.currDate.equals("")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AfficherHeureDateLivraison(view);
            }
        }
    }

    /**
     * Méthode qui permet l'affichage de la fenêtre de sélection de Date.
     * @param view Vue actuelle de l'application
     */
    public void findJourLivraison(View view) {

        if(this.possibleLivraison.getText().toString().equals("")){
            findZone(view);
        }else if (this.possibleLivraison.getText().toString().equals("/!\\\nVeuillez renseigné un code postal \n/!\\")) {
            return;
        }else{
            hideSoftKeyboard(MainActivity.this, view);
            if (this.popupWindowDate != null) {
            } else {
                if(this.popupWindowHeure != null){
                    this.popupWindowHeure.dismiss();
                    this.popupWindowHeure = null;
                }
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.calendar, null);
                this.popupWindowDate = new PopupWindow(popupView, 500, 520, false);
                this.popupWindowDate.showAtLocation(view, Gravity.CENTER, 0, 0);

                CalendarView calendarView = this.popupWindowDate.getContentView().findViewById(R.id.simpleCalendarView);
                calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

                    @Override
                    public void onSelectedDayChange(CalendarView view, int year, int month,
                                                    int dayOfMonth) {
                        String curDate = String.valueOf(dayOfMonth);
                        String Year = String.valueOf(year);
                        String Month = String.valueOf(month + 1);
                        currDate = curDate + "/" + Month + "/" + Year;
                        Log.i("date", Year + "/" + Month + "/" + curDate);
                    }
                });
                if (currDate.equals("")) {
                    currDate = InitialDate;
                }
            }
        }
    }

    /**
     * Méthode qui permet l'affichage de la fenêtre de sélection de l'heure.
     * @param view Vue actuelle de l'application
     */
    public void afficherHeureLivraison(View view) {
        if(this.possibleLivraison.getText().toString().equals("")){
            findZone(view);
        }else if(this.possibleLivraison.getText().toString().equals("/!\\\nVeuillez renseigné un code postal \n/!\\")) {
            return;
        }else{
                hideSoftKeyboard(MainActivity.this, view);
                if (this.popupWindowHeure != null) {
                    return;
                } else {
                    if(this.popupWindowDate != null){
                        this.popupWindowDate.dismiss();
                        this.popupWindowDate = null;
                    }
                    LayoutInflater inflater = (LayoutInflater)
                            getSystemService(LAYOUT_INFLATER_SERVICE);
                    View popupView = inflater.inflate(R.layout.horloge, null);
                    this.popupWindowHeure = new PopupWindow(popupView, 500, 520, false);
                    this.popupWindowHeure.setBackgroundDrawable(view.getBackground());
                    this.popupWindowHeure.showAtLocation(view, Gravity.CENTER, 0, 0);


                    TimePicker timePicker = this.popupWindowHeure.getContentView().findViewById(R.id.timePicker);
                    timePicker.setIs24HourView(true); // 24H Mode.
                    timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {

                        @Override
                        public void onTimeChanged(TimePicker view, int heure, int minutes) {
                            String hour = String.valueOf(heure);
                            String minuts = String.valueOf(minutes);
                            currHour = hour + ":" + minuts;
                            Log.i("date", minutes + "/" + heure);
                        }
                    });

                    if (currHour.equals("")) {
                        currHour = this.textHeureDemande.getText().toString();
                    }
                }
            }
        }


    /**
     * Méthode qui sélectionne les différentes données rentrées par l'utilisateur
     * et qui les traitent et renseignent à la classe DateVérification afin de calculer l'heure
     * et la date de livraison.
     * @param v Vue actuelle de l'application
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void AfficherHeureDateLivraison(View v) {

        Log.i("MainActivity", "\"Démarrage de la procédure de recherche du Jour de Livraison ....!");
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        String date = "";
        String heureComplete = "";
        String warning = "";
        //janvier = 1 fevrier = 2 mars = 3 avril = 4 mai =5 juin = 6 juillet =7 aout= 8 octobre =9 septembre = 10 novembre =11 decembre= 12
        //TextView textZone =  findViewById(R.id.textZone);

        // informations en string
        String stringDateSouhaitee = this.currDate;
        String stringHoursSouhaitee = this.currHour;
        String stringZone = this.textZone.getText().toString();


        if (stringDateSouhaitee.matches(this.dateregex) && stringHoursSouhaitee.matches(this.hoursregex)) {
            // selection de la partie du string pour evaluer
            int heure = Integer.parseInt(this.currHour.split(":")[0]);
            int minutes = Integer.parseInt(this.currHour.split(":")[1]);
            int jour = Integer.parseInt(this.currDate.split("/")[0]);
            int mois = Integer.parseInt(this.currDate.split("/")[1]);
            int annee = Integer.parseInt(this.currDate.split(("/"))[2]);

            LocalDateTime voulu = LocalDateTime.of(annee,mois,jour,heure,minutes);
            String userTimeZone = "Europe/Paris";
            LocalDateTime now = LocalDateTime.now(ZoneId.of(userTimeZone));

            this.Datesouhaitee.setText(Html.fromHtml("Date de la livraison souhaitée :  \n"+String.format("<b>%s</b>", jour+"/"+mois+"/"+annee)));
            this.HeureSouhaitee.setText(Html.fromHtml("Heure de la livraison souhaitée : \n"+String.format("<b>%s</b>", heure + ":" + minutes +":00")));
            if (voulu.getDayOfWeek().toString().equals("FRIDAY") && (stringZone.equals("2") || stringZone.equals("3"))){
                date = "/!\\";
                heureComplete = "/!\\";
                warningToast = "/!\\ Attention pas de livraison en zone 2 et 3 le Vendredi /!\\";
                this.Datesouhaitee.setTextColor(Color.RED);
                this.HeureSouhaitee.setTextColor(Color.RED);
            }else{
                if(voulu.isBefore(now)){
                    warningToast ="/!\\Veuillez renseigner une Date qui n'est pas encore passée /!\\";
                }else{
                    if(!DateVerification.SuperieurA12Heure(now,voulu)){
                        String[] result = DateVerification.verifDate(stringZone, heure, minutes, jour, mois, annee);
                        date = result[0];
                        heureComplete = result[1];
                        warning = result[2];
                        warningToast = warning;
                    }else{
                        String[] tmp;
                        tmp = DateVerification.getJourOuvrés(voulu,stringZone);
                        date = tmp[0];
                        boolean b = (heure > 13 && heure < 15) || (heure > 8 && heure < 11) || (heure == 13 && minutes > 15) || (heure == 11 && minutes <= 45) || (heure == 15 && minutes <=30);
                        if (voulu.getDayOfWeek().toString().equals("SATURDAY") || voulu.getDayOfWeek().toString().equals("SUNDAY") || (voulu.getDayOfWeek().toString().equals("FRIDAY") && (heure>10 || heure == 10 && minutes>0))){
                            date = "/!\\";
                            heureComplete = "/!\\";
                            warning = "/!\\ Attention pas de livraison après le vendredi 10h /!\\";
                            warningToast = warning;
                            this.Datesouhaitee.setText(Html.fromHtml("<font color='red'>"+this.Datesouhaitee.getText().toString()+"</font>"), TextView.BufferType.SPANNABLE);
                            this.HeureSouhaitee.setText(Html.fromHtml("<font color='red'>"+this.HeureSouhaitee.getText().toString()+"</font>"), TextView.BufferType.SPANNABLE);
                        }else{
                            if (!voulu.getDayOfWeek().toString().equals("FRIDAY")){
                                if (b){
                                    heureComplete = heure + ":" + minutes + ":00";
                                    warning = "";
                                    warningToast = warning;
                                }else if(heure < 8 ) {
                                    date = "/!\\";
                                    heureComplete =  "/!\\";
                                    warning = "/!\\ Attention les livraisons ne sont pas disponibles avant 8h /!\\";
                                    warningToast = warning;
                                    this.Datesouhaitee.setText(Html.fromHtml("<font color='red'>"+this.Datesouhaitee.getText().toString()+"</font>"), TextView.BufferType.SPANNABLE);
                                    this.HeureSouhaitee.setText(Html.fromHtml("<font color='red'>"+this.HeureSouhaitee.getText().toString()+"</font>"), TextView.BufferType.SPANNABLE);
                                }else if (heure >15 || (heure ==15 && minutes > 30)){
                                    date = "/!\\";
                                    heureComplete = "/!\\";
                                    warning = "/!\\ Attention les livraisons ne sont pas disponibles après 15h30 (Lundi au Jeudi) /!\\";
                                    warningToast = warning;
                                    this.Datesouhaitee.setText(Html.fromHtml("<font color='red'>"+this.Datesouhaitee.getText().toString()+"</font>"), TextView.BufferType.SPANNABLE);
                                    this.HeureSouhaitee.setText(Html.fromHtml("<font color='red'>"+this.HeureSouhaitee.getText().toString()+"</font>"), TextView.BufferType.SPANNABLE);
                                }else{
                                    date = "/!\\";
                                    heureComplete = "/!\\";
                                    warning = "/!\\ Attention les livraisons ne sont pas disponibles de 11h45 à 13h15 (Lundi au Jeudi) /!\\";
                                    warningToast = warning;
                                    this.Datesouhaitee.setText(Html.fromHtml("<font color='red'>"+this.Datesouhaitee.getText().toString()+"</font>"), TextView.BufferType.SPANNABLE);
                                    this.HeureSouhaitee.setText(Html.fromHtml("<font color='red'>"+this.HeureSouhaitee.getText().toString()+"</font>"), TextView.BufferType.SPANNABLE);
                                }
                            }else {
                                if (stringZone.equals("2") || stringZone.equals("3")){
                                    date = "/!\\";
                                    heureComplete = "/!\\";
                                    warning = "/!\\ Pas de livraison en zone 2 et 3 le vendredi matin";
                                    warningToast = warning;
                                    this.Datesouhaitee.setText(Html.fromHtml("<font color='red'>"+this.Datesouhaitee.getText().toString()+"</font>"), TextView.BufferType.SPANNABLE);
                                    this.HeureSouhaitee.setText(Html.fromHtml("<font color='red'>"+this.HeureSouhaitee.getText().toString()+"</font>"), TextView.BufferType.SPANNABLE);
                                }else if (heure > 10 || (heure == 10  && minutes >0)){
                                    date = "/!\\";
                                    heureComplete = "/!\\";
                                    warning = "/!\\ Pas de livraison après 10h le vendredi matin";
                                    warningToast = warning;
                                    this.Datesouhaitee.setText(Html.fromHtml("<font color='red'>"+this.Datesouhaitee.getText().toString()+"</font>"), TextView.BufferType.SPANNABLE);
                                    this.HeureSouhaitee.setText(Html.fromHtml("<font color='red'>"+this.HeureSouhaitee.getText().toString()+"</font>"), TextView.BufferType.SPANNABLE);
                                }else if(heure < 8 ) {
                                    date = "/!\\";
                                    heureComplete =  "/!\\";
                                    warning = "/!\\ Attention les livraisons ne sont pas disponibles avant 8h /!\\";
                                    warningToast = warning;
                                    this.Datesouhaitee.setText(Html.fromHtml("<font color='red'>"+this.Datesouhaitee.getText().toString()+"</font>"), TextView.BufferType.SPANNABLE);
                                    this.HeureSouhaitee.setText(Html.fromHtml("<font color='red'>"+this.HeureSouhaitee.getText().toString()+"</font>"), TextView.BufferType.SPANNABLE);
                                }else{
                                    heureComplete = heure + ":" + minutes + ":00";
                                    warning = "";
                                    warningToast = warning;
                                }
                            }
                        }
                    }
                }
            }



            Log.i("MainActivity", "Format Heure et Date renseigner.");
            this.jourProposee.setText(date);
            this.HeureProposee.setText(heureComplete);
            this.textJourArrivee.setText(warningToast);
            this.textJourArrivee.setVisibility(VISIBLE);
            this.Datesouhaitee.setTextColor(Color.BLACK);
            this.HeureSouhaitee.setTextColor(Color.BLACK);


        }
    }


    /**
     * Permet de lancer une notification de rappel de l'heure et date de livraison.
     * Cette méthode est lancé automatiquement lorsque la livraison n'a pas lieu le jour même de la demande.
     * @param view Vue actuelle de l'application
     */
    public void verifDateBienComprise(View view){
        hideSoftKeyboard(MainActivity.this,view);
        if (warningToast !=null && warningToast.equals("/!\\ Attention Livraison à J+1 /!\\")){
            snackbar = Snackbar
                    .make(this.HeureProposee.getRootView(), "Attention Livraison prévu le :" + this.jourProposee.getText().toString() + "", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snackbar.dismiss();
                        }
                    });
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
            snackbar.addCallback(new Snackbar.Callback(){
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                        submitbuttonHandler(view);
                }
            });
        }else{
            submitbuttonHandler(view);
        }


        if (warningToast !=null && warningToast.equals("/!\\ Attention les livraisons sont disponibles de 8h à 11h45 et de 13h30 à 15h30 /!\\")){
            snackbar = Snackbar
                    .make(this.HeureProposee.getRootView(), "Attention L'heure de livraison est :" + this.HeureProposee.getText().toString() + "", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snackbar.dismiss();
                        }
                    });
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
            snackbar.addCallback(new Snackbar.Callback(){
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    submitbuttonHandler(view);
                }
            });
        }else{
            submitbuttonHandler(view);
        }
    }

    /**
     * Méthode qui permet de récupérer toutes les informations rentrées depuis le début et vérifie s'il ne manque pas d'information nécessaire au traitement.
     * -- Si il ne manque pas d'informations, envoie par mail de la demande de livraison
     * -- Si il manque des informations la demande se bloque et informe l'utilisateur de ce qu'il manque.
     * @param view Vue actuelle de l'application
     */
    public void submitbuttonHandler(View view) {
        //boolean de verif
        boolean valideCommande = true;
        this.ValidationCommande.setText("");
        this.ValidationCommande.setVisibility(INVISIBLE);

        // toString des Textes obligatoire
        String materiel = this.commStock.getText().toString();
        String technicienSurPlace = this.texttechnicienSurPlace.getText().toString();
        String commentaires = this.textcommentaires.getText().toString();
        String materielLourd = this.textmaterielLourd.getText().toString();
        String dechet = this.textdechet.getText().toString();
        String CodePostal = this.textPostal.getText().toString();
        String adresse = this.textAdresse.getText().toString();
        String demandeur = this.textdemandeur.getText().toString();
        String zone = this.textZone.getText().toString();
        String service = this.textService.getText().toString();
        String dateproposee = this.jourProposee.getText().toString();
        String heureproposee = this.HeureProposee.getText().toString();
        String numOxa = this.numeroOxalys.getText().toString();
        String four = this.fournisseur.getText().toString();
        String lieuENL = this.lieuEnlevement.getText().toString();
        String nbreColis = this.nombreColis.getText().toString();

        //radio button numero 1
        String checkedBox = "";
        int checkbox = this.radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(checkbox);
        if (radioButton != null) {
            checkedBox = radioButton.getText().toString();
        }
        //radio button numéro 2
        String checkedBox2 = "";
        int checkbox2 = this.radioGroup2.getCheckedRadioButtonId();
        RadioButton radioButton2 = findViewById(checkbox2);
        if (radioButton2 != null) {
            checkedBox2 = radioButton2.getText().toString();
        }
        //radio button numéro 3
        String checkedBox3 = "";
        int checkbox3 = this.radioGroup3.getCheckedRadioButtonId();
        RadioButton radioButton3 = findViewById(checkbox3);
        if (radioButton3 != null) {
            checkedBox3 = radioButton3.getText().toString();
        }


        if (adresse.equals("")) {

            this.ValidationCommande.setText("L'adresse n'est pas renseignée.");
            this.ValidationCommande.setVisibility(VISIBLE);
            valideCommande = false;
        }


        if (demandeur.equals("")) {
            //TextView ValidationCommande =  findViewById(R.id.ValidationCommande);
            this.ValidationCommande.setText("Le demandeur n'est pas renseigné.");
            this.ValidationCommande.setVisibility(VISIBLE);
            valideCommande = false;
        }

        // vérification zone renseignée

        if (!zone.equals("1B") && !zone.equals("1A") && !zone.equals("2") && !zone.equals("3")) {
            //TextView ValidationCommande =  findViewById(R.id.ValidationCommande);
            this.ValidationCommande.setText("La géolocalisation renseignée ne permet pas la livraison sur chantier.");
            this.ValidationCommande.setVisibility(VISIBLE);
            valideCommande = false;
        }

        // vérification service renseignée

        if (service.equals("CI non valide") || service.equals("")) {
            //TextView ValidationCommande =  findViewById(R.id.ValidationCommande);
            this.ValidationCommande.setText("Le CI renseignée n'est pas correct.");
            this.ValidationCommande.setVisibility(VISIBLE);
            valideCommande = false;
        }


        // vérification date renseignée

        if (!this.currDate.matches(this.dateregex)) {
            //TextView ValidationCommande =  findViewById(R.id.ValidationCommande);
            this.ValidationCommande.setText("La date renseignée n'est pas valide.");
            this.ValidationCommande.setVisibility(VISIBLE);
            valideCommande = false;
        }

        // vérification heure renseignée

        if (!this.currHour.matches(this.hoursregex)) {
            //TextView ValidationCommande =  findViewById(R.id.ValidationCommande);
            this.ValidationCommande.setText("L'heure renseignée n'est pas valide.");
            this.ValidationCommande.setVisibility(VISIBLE);
            valideCommande = false;
        }


        // vérification checkbox ( si les champs renseignées en fonction du checkbox sont bien renseignée

        if (checkedBox.equals("Enlèvement Fournisseur")) {
            //commande fournisseur
            if (four.equals("")) {
                Log.i("MainActivity", "fournisseur non renseigné");
                //TextView ValidationCommande =  findViewById(R.id.ValidationCommande);
                this.ValidationCommande.setText("Le Fourniseur n'est pas renseigné.");
                this.ValidationCommande.setVisibility(VISIBLE);
                valideCommande = false;
            }
            if (lieuENL.equals("")) {
                Log.i("MainActivity", "Lieu d'Enlevement non renseigné");
                //TextView ValidationCommande =  findViewById(R.id.ValidationCommande);
                this.ValidationCommande.setText("Lieu d'Enlevement n'est pas renseigné.");
                this.ValidationCommande.setVisibility(VISIBLE);
                valideCommande = false;
            }
        } else {
            if (checkedBox.equals("Enlèvement dépot")) {
            } else {
                //commande stock
                // verif num oxalys ????
                if (materiel.equals("")) {
                    Log.i("MainActivity", "Numéro Oxalys non renseigné");
                    //TextView ValidationCommande =  findViewById(R.id.ValidationCommande);
                    this.ValidationCommande.setText("Le matériel souhaité n'est pas renseigné.");
                    this.ValidationCommande.setVisibility(VISIBLE);
                    valideCommande = false;
                }
            }
        }

        if(dateproposee.equals("/!\\") || heureproposee.equals("/!\\")){
            this.ValidationCommande.setText("L'heure et la Date demandées ne répondent pas aux critères.");
            this.ValidationCommande.setVisibility(VISIBLE);
            valideCommande = false;
        }



        // comment est-ce qu'on paramètre

        if (checkedBox2.equals("")) {
            checkedBox2 = "Non Renseigné";
        }
        if (checkedBox3.equals("")) {
            checkedBox3 = "Non Renseigné";
        }
        String body = "Body vide";
        if (checkedBox.equals("Enlèvement Fournisseur")) {
            body = "Demandeur de la Livraison Chantier : ;" + demandeur + "\n"
                    + "Adresse de Livraison : ;" + adresse + "\n"
                    + "Code Postal : ;" + CodePostal + "\n"
                    + "Zone : ;" + zone + "\n"
                    + "CI : ;" + this.textCI.getText().toString() + "\n"
                    + "Livraison prévue le :;" + dateproposee + "\n"
                    + "A : ;" + heureproposee + "\n"
                    + "Type de Commande :;" + checkedBox + "\n"
                    + "     - Numéro de commande Oxalys :;" + numOxa + "\n"
                    + "     - Fournisseur :;" + four + "\n"
                    + "     - Lieu d'Enlèvement :;" + lieuENL + "\n"
                    + "     - Nombres de Colis :;" + nbreColis + "\n"
                    + "Informations supplémentaires :;\n"
                    + "      - Des déchets seront-ils à reprendre : ;" + checkedBox3 + "\n"
                    + "      - Commentaires(interphone, n° d'étages, stationnement, contraintes particulières) : ;" + commentaires + "\n"
                    + "      - Le technicien sera-t-il sur place lors de la livraison : ;" + checkedBox2 + "\n"
                    + "      - Si le matériel à livrer est lourd, combien de personne seront sur place pour aider la manutention : ;" + materielLourd + "\n"
            ;
        } else if (checkedBox.equals("Enlèvement dépot")) {
            body = "Demandeur de la Livraison Chantier : ;" + demandeur + "\n"
                    + "Adresse de Livraison : ;" + adresse + "\n"
                    + "Code Postal : ;" + CodePostal + "\n"
                    + "Zone : ;" + zone + "\n"
                    + "CI : ;" + this.textCI.getText().toString() + "\n"
                    + "Livraison prévue le :;" + dateproposee + "\n"
                    + "A : ;" + heureproposee + "\n"
                    + "Type de Commande :;" + checkedBox + "\n"
                    + "     - Numéro de commande Oxalys (optionnel) :;" + numeroOxalys2.getText().toString() + "\n"
                    + "     - Consignes :;" + consignes.getText().toString() + "\n"
                    + "Informations supplémentaires :;\n"
                    + "      - Des déchets seront-ils à reprendre : ;" + dechet + "\n"
                    + "      - Commentaires(interphone, n° d'étages, stationnement, contraintes particulières) : ;" + commentaires + "\n"
                    + "      - Le technicien sera-t-il sur place lors de la livraison : ;" + technicienSurPlace + "\n"
                    + "      - Si le matériel à livrer est lourd,combien de personne seront sur place pour aider la manutention : ;" + materielLourd + "\n"
            ;
        } else {
            body = "Demandeur de la Livraison Chantier : ;" + demandeur + "\n"
                    + "Adresse de Livraison : ;" + adresse + "\n"
                    + "Code Postal : ;" + CodePostal + "\n"
                    + "Zone : ;" + zone + "\n"
                    + "CI : ;" + this.textCI.getText().toString() + "\n"
                    + "Livraison prévue le :;" + dateproposee + "\n"
                    + "A : ;" + heureproposee + "\n"
                    + "Type de Commande :;" + checkedBox + "\n"
                    + "     - Informations sur les matériaux STOCK :;" + materiel + "\n"
                    + "Informations supplémentaires :;\n"
                    + "      - Des déchets seront-ils à reprendre : ;" + dechet + "\n"
                    + "      - Commentaires(interphone, n° d'étages, stationnement, contraintes particulières) : ;" + commentaires + "\n"
                    + "      - Le technicien sera-t-il sur place lors de la livraison : ;" + technicienSurPlace + "\n"
                    + "      - Si le matériel à livrer est lourd, combien de personne seront sur place pour aider la manutention : ;" + materielLourd + "\n"
            ;
        }
        if (valideCommande) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("plain/text");
            File data = null;
            try {
                data = File.createTempFile("LIV", ".csv");
                GenerateCsv.generateCsvFile(data, body);

                Uri uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", data);

                i.putExtra(Intent.EXTRA_STREAM, uri);
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"MAILING LIST"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Livraison Chantier");
                i.putExtra(Intent.EXTRA_TEXT, "Bonjour ,\n" +
                        "Tu trouveras ci-joint la livraison chantier de " + this.textdemandeur.getText().toString() + "\n\n" +
                        "Cordialement,\n");
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(i, "E-mail"));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

