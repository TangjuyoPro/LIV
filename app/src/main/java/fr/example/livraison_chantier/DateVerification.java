package fr.example.livraison_chantier;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 *Cette Classe permet de créer une nouvelle Date en fonction de la date renseignée
 * et des contraintes liée aux zones, jours fériés, jours ouvrés...
 */
public class DateVerification {
    /**
     * Retourne la date de Paques en fonction de l'année demandée.
     * @param year l'année de renvoie de la date de Paques
     * @return LocalDate ( une date)
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static LocalDate paques(int year) {
        if (year < 1583) {
            throw new IllegalStateException();
        }
        int n = year % 19;
        int c = year / 100;
        int u = year % 100;
        int s = c / 4;
        int t = c % 4;
        int p = (c + 8) / 25;
        int q = (c - p + 1) / 3;
        int e = (19 * n + c - s - q + 15) % 30;
        int b = u / 4;
        int d = u % 4;
        int L = (32 + 2 * t + 2 * b - e - d) % 7;
        int h = (n + 11 * e + 22 * L) / 451;
        int m = (e + L - 7 * h + 114) / 31;
        int j = (e + L - 7 * h + 114) % 31;

        return LocalDate.of(year, m, j + 1);
    }

    /**
     * Retourne vrai ou faux, vrai si la date renseignée est férié et faux si la date renseignée n'est pas férié.
     * @param date la Date à tester
     * @return un boolean
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean ferie(LocalDate date) {
        final int day = date.getDayOfMonth();
        switch (date.getMonth()) {
            case JANUARY:
                if (day == 1) {
                    // Jour de l'an
                    return true;
                }
                break;
            case MAY:
                if (day == 1 || day == 8) {
                    // Fête du travail et Victoire 1945
                    return true;
                }
                break;
            case JULY:
                if (day == 14) {
                    // Fête Nationale
                    return true;
                }
                break;
            case AUGUST:
                if (day == 15) {
                    // Assomption
                    return true;
                }
                break;
            case NOVEMBER:
                if (day == 1 || day == 11) {
                    // Toussaint et Armistice 1918
                    return true;
                }
                break;
            case DECEMBER:
                if (day == 25) {
                    // Noël
                    return true;
                }
                break;
            default:
        }

        if (date.getMonthValue() < 7) {
            // Avant juillet on doit aussi vérifier les fêtes liées à Paques
            LocalDate paques = paques(date.getYear());
            int days = (int) ChronoUnit.DAYS.between(paques, date);
            switch (days) {
                case 0: // Paques
                case 1: // lundi de Pâques : 1 jour après Pâques
                case 39: // Ascension : 39 jours après Pâques
                case 49: // Pentecôte : 49 jours après Pâques
                case 50: // L. de Pentecôte : 50 jours après Paques
                    return true;
            }
        }
        return false;
    }

    public static String[] getJourOuvrés(LocalDateTime d,String zone){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            /*if (d.getDayOfWeek().toString().equals("SATURDAY") || d.getDayOfWeek().toString().equals("SUNDAY") || ferie((d.toLocalDate()))) {
                d = d.plusDays(1);
                return getJourOuvrés(d,zone);
            }
             */
            /*
            if(d.getDayOfWeek().toString().equals("FRIDAY")){
                if(zone.equals("2") || zone.equals("3")){
                    d = d.plusDays(1);
                    return getJourOuvrés(d,zone);
                }else {
                    if (d.getHour() < 10 || (d.getHour() == 00 && d.getMinute() == 0)){
                    }else{
                        d = d.plusDays(1);
                        return getJourOuvrés(d,zone);
                    }
                }
            }
            */
        }
        String[] result = new String[2];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            result[0] = d.getDayOfMonth() + "/" + d.getMonthValue() + "/" + d.getYear();
        }
        return result;
    }
    /**
     *
     * @return TRUE si l'heure et que par conséquent on prend l'heure demandé par le technicien
     */
    public static boolean SuperieurA12Heure(LocalDateTime one, LocalDateTime two){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ChronoUnit.HOURS.between(one,two) > 12){
                System.out.println(ChronoUnit.HOURS.between(one,two));
                return true;
            }
            System.out.println(ChronoUnit.HOURS.between(one,two));
            return false;
        }
        return true;
    }

    /**
     * Permet de calculer la date de livraison en fonction d'une date donnée, de l'heure, des minutes et de la zone.
     * Les contraintes de calcul sont définis dans un tableau excel accessible sur le reseau de l'entreprise
     *
     * Cette méthode permet de calculer les exceptions suivantes :
     *   - des week-end
     *   - des horaires particuliers du vendredi
     *
     * @param d Date renseigner pour calculer la date
     * @param zone zone de la demande de livraison
     * @param heure heure de la demande de livraison
     * @param minutes minutes de la demande de livraison
     * @return la date calculer
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static String CreateNewDate(LocalDate d,String zone,int heure,int minutes){
        String date ="";
        if(d.getDayOfWeek().toString().equals("FRIDAY") && !ferie(d)){
            if (zone.equals("1A")){
                if (heure < 10 || (heure == 10 && minutes == 0)){
                     date =  d.getDayOfMonth() + "/" + d.getMonthValue() + "/" + d.getYear();
                    return date;
                }else{
                    d = d.plusDays(1);
                    return CreateNewDate(d, zone,9,0);
                }
            }else if (zone.equals("1B")){
                if (heure < 10 || (heure == 10 && minutes == 0)){
                     date =  d.getDayOfMonth() + "/" + d.getMonthValue() + "/" + d.getYear();
                    return date;
                }else{
                    d = d.plusDays(1);
                    return CreateNewDate(d, zone,9,0);
                }
            }else if (zone.equals("2")){
                if (heure < 10 || (heure == 10 && minutes == 0)){
                    date =  d.getDayOfMonth() + "/" + d.getMonthValue() + "/" + d.getYear();
                    return date;
                }else{
                    d = d.plusDays(1);
                    return CreateNewDate(d, zone,9,0);
                }
            }else{
                d = d.plusDays(1);
                return CreateNewDate(d, zone,9,0);
            }
        }else {
            if (d.getDayOfWeek().toString().equals("SATURDAY") || d.getDayOfWeek().toString().equals("SUNDAY")) {
                d = d.plusDays(1);
                return CreateNewDate(d, zone,9,0);
            }
            if (ferie(d)) {
                d = d.plusDays(1);
                if(d.getDayOfWeek().toString().equals("FRIDAY") && !ferie(d))
                    return CreateNewDate(d,zone,8,30);
                else
                    return CreateNewDate(d, zone,9,0);
            }
            date =  d.getDayOfMonth() + "/" + d.getMonthValue() + "/" + d.getYear();
        }
        return date;
    }

    /**
     * Permet de calculer la date de livraison en fonction d'une date donnée, de l'heure, des minutes et de la zone.
     * Les contraintes de calcul sont définis dans un tableau excel accessible sur le réseau de l'entreprise
     *
     * Cette méthode permet de calculer le changement de jours en fonction de l'heure renseignée.
     *
     * @param stringZone la zone de la demande de livraison
     * @param heure l'heure souhaitée de la demande de livraison
     * @param minutes minutes souhaitées de la demande de livraison
     * @param jour jour souhaité de la demande de livraison
     * @param mois mois de la demande de livraison
     * @param annee année de la demande de livraison
     * @return renvoie un tableau comportant  : la date de livraison, l'heure de livraison, et l'état de la livraison
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String[] verifDate(String stringZone, int heure, int minutes, int jour, int mois, int annee){

        LocalDate myDate = LocalDate.of(annee,mois,jour);
        String firsDate = jour + "/" + mois + "/" + annee;
        boolean mois31 = mois == 1 || mois == 3 || mois == 5 || mois == 7 || mois == 8 || mois == 10 || mois == 12;


        String date = "";
        String heureComplete ="";
        String warning ="";

        switch (stringZone) {
            case "1A":
                if (heure < 8) {
                    date = CreateNewDate(myDate,stringZone,heure,minutes);
                    if (firsDate.equals(date))
                        heureComplete ="10:00:00";
                    else
                        heureComplete = "09:00:00";

                } else if ((heure < 10) || (heure == 10 && minutes == 0)) {
                    date = CreateNewDate(myDate,stringZone,heure,minutes);
                    if (minutes > 0 && minutes <=30){
                        if (firsDate.equals(date))
                            heureComplete = heure + 2 + ":30:00";
                        else
                            heureComplete = "09:00:00";
                    }else if (minutes == 0){

                        if (firsDate.equals(date))
                            heureComplete = heure + 2 + ":00:00";
                        else
                            heureComplete = "09:00:00";
                    }else {
                        if (firsDate.equals(date))
                            heureComplete = heure + 3 + ":00:00";
                        else
                            heureComplete = "09:00:00";
                    }
                } else if ((heure == 10 && minutes <= 30)) {
                    date = CreateNewDate(myDate,stringZone,heure,minutes);
                    if (firsDate.equals(date))
                        heureComplete = "13:30:00";
                    else
                        heureComplete = "09:00:00";
                } else if ((heure < 11) || (heure == 11 && minutes == 0)) {
                    date = CreateNewDate(myDate,stringZone,heure,minutes);
                    if (firsDate.equals(date))
                        heureComplete = "14:00:00";
                    else
                        heureComplete = "09:00:00";
                } else if ((heure < 13) || (heure == 13 && minutes <= 30)) {
                    date = CreateNewDate(myDate,stringZone,heure,minutes);
                    if (firsDate.equals(date))
                        heureComplete = "14:30:00";
                    else
                        heureComplete = "09:00:00";
                } else if (heure < 14 || (heure == 14 && minutes < 30)) {
                    date = CreateNewDate(myDate,stringZone,heure,minutes);
                    if (firsDate.equals(date))
                        heureComplete = "15:30:00";
                    else
                        heureComplete = "09:00:00";
                } else if (heure < 15) {
                    date = CreateNewDate(myDate,stringZone,heure,minutes);
                    if (firsDate.equals(date))
                        heureComplete = "16:00:00";
                    else
                        heureComplete = "09:00:00";
                } else {
                    if (mois31) {
                        if (jour != 31) {
                            myDate = myDate.plusDays(1);
                            date = CreateNewDate(myDate,stringZone,9,0);
                        } else {
                            if (mois != 12) {
                                if (mois < 9) {
                                    myDate = myDate.plusDays(1);
                                    date = CreateNewDate(myDate,stringZone,9,0);
                                } else {
                                    myDate = myDate.plusDays(1);
                                    date = CreateNewDate(myDate,stringZone,9,0);
                                }
                            } else {
                                myDate = myDate.plusDays(1);
                                date = CreateNewDate(myDate,stringZone,9,0);
                            }
                        }
                    } else {
                        if (jour != 30) {
                            myDate = myDate.plusDays(1);
                            date = CreateNewDate(myDate,stringZone,9,0);
                        } else {
                            myDate = myDate.plusDays(1);
                            date = CreateNewDate(myDate,stringZone,9,0);
                        }
                    }
                    heureComplete = "09:00:00";
                    warning = "/!\\ Attention Livraison à J+1 /!\\";
                }
                break;
            case "1B":
                if (heure < 8) {
                    date = CreateNewDate(myDate,stringZone,heure,minutes);
                    if (firsDate.equals(date))
                        heureComplete = "10:00:00";
                    else
                        heureComplete = "09:00:00";
                } else if ((heure < 10) || (heure == 10 && minutes == 0)) {
                    date = CreateNewDate(myDate,stringZone,heure,minutes);
                    if (minutes > 0 && minutes <=30){
                        if (firsDate.equals(date))
                            heureComplete = heure + 2 + ":30:00";
                        else
                            heureComplete = "09:00:00";
                    }else if (minutes == 0){
                        if (firsDate.equals(date))
                            heureComplete = heure + 2 + ":00:00";
                        else
                            heureComplete = "09:00:00";
                    }else {
                        if (firsDate.equals(date))
                            heureComplete = heure + 3 + ":00:00";
                        else
                            heureComplete = "09:00:00";
                    }
                } else if ((heure < 11) || (heure == 11 && minutes <= 30)) {
                    date = CreateNewDate(myDate,stringZone,heure,minutes);
                    if (firsDate.equals(date))
                        if(minutes<30)
                            heureComplete = heure + 3 + ":00:00";
                        else
                            heureComplete = heure + 3 + ":30:00";
                    else
                        heureComplete = "09:00:00";
                } else if ((heure < 13) || (heure == 13 && minutes <= 30)) {
                    date = CreateNewDate(myDate,stringZone,heure,minutes);
                    if (firsDate.equals(date))
                        heureComplete = "14:30:00";
                    else
                        heureComplete = "09:00:00";
                } else if ((heure < 14) || (heure == 14 && minutes == 0)) {
                    date = CreateNewDate(myDate,stringZone,heure,minutes);
                    if (firsDate.equals(date))
                        heureComplete = "15:30:00";
                    else
                        heureComplete = "09:00:00";
                } else if (heure == 14 && minutes <= 30) {
                    date = CreateNewDate(myDate,stringZone,heure,minutes);
                    if (firsDate.equals(date))
                        heureComplete = "16:00:00";
                    else
                        heureComplete = "09:00:00";
                } else {
                    if (mois31) {
                        if (jour != 31) {
                            myDate = myDate.plusDays(1);
                            date = CreateNewDate(myDate,stringZone,9,0);
                        } else {
                            if (mois != 12) {
                                if (mois < 9) {
                                    myDate = myDate.plusDays(1);
                                    date = CreateNewDate(myDate,stringZone,9,0);
                                } else {
                                    myDate = myDate.plusDays(1);
                                    date = CreateNewDate(myDate,stringZone,9,0);
                                }
                            } else {
                                myDate = myDate.plusDays(1);
                                date = CreateNewDate(myDate,stringZone,9,0);
                            }
                        }
                    } else {
                        if (jour != 30) {
                            myDate = myDate.plusDays(1);
                            date = CreateNewDate(myDate,stringZone,9,0);
                        } else {
                            myDate = myDate.plusDays(1);
                            date = CreateNewDate(myDate,stringZone,9,0);
                        }
                    }
                    heureComplete = "09:00:00";
                    warning = "/!\\ Attention Livraison à J+1 /!\\";
                }
                break;
            case "2":
                if (heure < 8) {
                    date = CreateNewDate(myDate,stringZone,heure,minutes);
                    if (firsDate.equals(date))
                        heureComplete = "11:00:00";
                    else
                        heureComplete = "09:00:00";
                } else if ((heure < 9) || (heure == 9 && minutes == 0)) {
                    date = CreateNewDate(myDate,stringZone,heure,minutes);
                    if (minutes > 0 && minutes <=30){
                        if (firsDate.equals(date))
                            heureComplete = heure + 3 + ":30:00";
                        else
                            heureComplete = "09:00:00";
                    }else if (minutes == 0){
                        if (firsDate.equals(date))
                            heureComplete = heure + 3 + ":00:00";
                        else
                            heureComplete = "09:00:00";
                    }else {
                        if (firsDate.equals(date))
                            heureComplete = heure + 4 + ":00:00";
                        else
                            heureComplete = "09:00:00";
                    }
                } else if (heure == 9 && minutes <= 30) {
                    date = CreateNewDate(myDate,stringZone,heure,minutes);
                    if (firsDate.equals(date))
                        heureComplete = "13:45:00";
                    else
                        heureComplete = "09:00:00";
                } else if ((heure < 10) || (heure == 10 && minutes == 0)) {

                    date = CreateNewDate(myDate,stringZone,heure,minutes);
                    if (firsDate.equals(date))
                        heureComplete = "14:00:00";
                    else
                        heureComplete = "09:00:00";
                } else if ((heure < 12) || (heure == 12 && minutes <= 30)) {
                    date = CreateNewDate(myDate,stringZone,heure,minutes);
                    if (firsDate.equals(date))
                        heureComplete = "14:30:00";
                    else
                        heureComplete = "09:00:00";
                } else if ((heure < 13) || (heure == 13 && minutes == 0)) {
                    date = CreateNewDate(myDate,stringZone,heure,minutes);
                    if (firsDate.equals(date))
                        heureComplete = "15:30:00";
                    else
                        heureComplete = "09:00:00";
                }else if ((heure < 14) || (heure == 13 && minutes <= 30)) {
                    date = CreateNewDate(myDate,stringZone,heure,minutes);
                    if (firsDate.equals(date))
                        heureComplete = "16:00:00";
                    else
                        heureComplete = "09:00:00";
                } else {
                    if (mois31) {
                        if (jour != 31) {
                            myDate = myDate.plusDays(1);
                            if (myDate.getDayOfWeek().toString().equals("FRIDAY") && !ferie(myDate)){
                                date = CreateNewDate(myDate,stringZone,8,30);
                                heureComplete = "08:30:00";
                            }else{
                                date = CreateNewDate(myDate,stringZone,9,0);
                                heureComplete = "09:00:00";
                            }

                        } else {
                            if (mois != 12) {
                                if (mois < 9) {
                                    myDate = myDate.plusDays(1);
                                    if (myDate.getDayOfWeek().toString().equals("FRIDAY") && !ferie(myDate)){
                                        date = CreateNewDate(myDate,stringZone,8,30);
                                        heureComplete = "08:30:00";
                                    }else{
                                        date = CreateNewDate(myDate,stringZone,9,0);
                                        heureComplete = "09:00:00";
                                    }

                                } else {
                                    myDate = myDate.plusDays(1);
                                    if (myDate.getDayOfWeek().toString().equals("FRIDAY")&& !ferie(myDate)){
                                        date = CreateNewDate(myDate,stringZone,8,30);
                                        heureComplete = "08:30:00";
                                    }else{
                                        date = CreateNewDate(myDate,stringZone,9,0);
                                        heureComplete = "09:00:00";
                                    }

                                }
                            } else {
                                myDate = myDate.plusDays(1);
                                if (myDate.getDayOfWeek().toString().equals("FRIDAY")&& !ferie(myDate)){
                                    date = CreateNewDate(myDate,stringZone,8,30);
                                    heureComplete = "08:30:00";
                                }else{
                                    date = CreateNewDate(myDate,stringZone,9,0);
                                    heureComplete = "09:00:00";
                                }

                            }
                        }
                    } else {
                        if (jour != 30) {
                            myDate = myDate.plusDays(1);
                            if (myDate.getDayOfWeek().toString().equals("FRIDAY")&& !ferie(myDate)){
                                date = CreateNewDate(myDate,stringZone,8,30);
                                heureComplete = "08:30:00";
                            }else{
                                date = CreateNewDate(myDate,stringZone,9,0);
                                heureComplete = "09:00:00";
                            }

                        } else {
                            myDate = myDate.plusDays(1);
                            if (myDate.getDayOfWeek().toString().equals("FRIDAY")&& !ferie(myDate)){
                                date = CreateNewDate(myDate,stringZone,8,30);
                                heureComplete = "08:30:00";
                            }else{
                                date = CreateNewDate(myDate,stringZone,9,0);
                                heureComplete = "09:00:00";
                            }
                        }
                    }

                    warning = "/!\\ Attention Livraison à J+1 /!\\";
                }
                break;
            case "3":
                if ((heure < 8) || (heure == 8 && minutes == 0)) {
                    date = CreateNewDate(myDate,stringZone,heure,minutes);
                    if (firsDate.equals(date))
                        heureComplete = "13:45:00";
                    else
                        heureComplete = "09:00:00";
                } else if (heure == 8 && minutes <= 30) {
                    date = CreateNewDate(myDate,stringZone,heure,minutes);
                    if (firsDate.equals(date))
                        heureComplete = "14:00:00";
                    else
                        heureComplete = "09:00:00";
                } else if ((heure < 11) || (heure == 11 && minutes == 0)) {
                    date = CreateNewDate(myDate,stringZone,heure,minutes);
                    if (firsDate.equals(date))
                        heureComplete = "14:30:00";
                    else
                        heureComplete = "09:00:00";
                } else if (heure == 11 && minutes <= 30) {
                    date = CreateNewDate(myDate,stringZone,heure,minutes);
                    if (firsDate.equals(date))
                        heureComplete = "15:30:00";
                    else
                        heureComplete = "09:00:00";
                } else if ((heure < 12) || (heure == 12 && minutes == 0)) {
                    date = CreateNewDate(myDate,stringZone,heure,minutes);
                    if (firsDate.equals(date))
                        heureComplete = "16:00:00";
                    else
                        heureComplete = "09:00:00";
                } else {
                    if (mois31) {
                        if (jour != 31) {
                            myDate = myDate.plusDays(1);
                            if ((heure < 14) || (heure == 14 && minutes <= 30))
                                date = CreateNewDate(myDate,stringZone,9,0);
                            else
                                date = CreateNewDate(myDate,stringZone,14,0);
                        } else {
                            if (mois != 12) {
                                if (mois < 9) {
                                    myDate = myDate.plusDays(1);
                                    if ((heure < 14) || (heure == 14 && minutes <= 30))
                                        date = CreateNewDate(myDate,stringZone,9,0);
                                    else
                                        date = CreateNewDate(myDate,stringZone,14,0);
                                } else {
                                    myDate = myDate.plusDays(1);
                                    if ((heure < 14) || (heure == 14 && minutes <= 30))
                                        date = CreateNewDate(myDate,stringZone,9,0);
                                    else
                                        date = CreateNewDate(myDate,stringZone,14,0);
                                }
                            } else {
                                myDate = myDate.plusDays(1);
                                if ((heure < 14) || (heure == 14 && minutes <= 30))
                                    date = CreateNewDate(myDate,stringZone,9,0);
                                else
                                    date = CreateNewDate(myDate,stringZone,14,0);
                            }
                        }
                    } else {
                        if (jour != 30) {
                            myDate = myDate.plusDays(1);
                            if ((heure < 14) || (heure == 14 && minutes < 30))
                                date = CreateNewDate(myDate,stringZone,9,0);
                            else
                                date = CreateNewDate(myDate,stringZone,14,0);
                        } else {
                            myDate = myDate.plusDays(1);
                            if ((heure < 14) || (heure == 14 && minutes < 30))
                                date = CreateNewDate(myDate,stringZone,9,0);
                            else
                                date = CreateNewDate(myDate,stringZone,14,0);
                        }
                    }
                    if ((heure < 14) || (heure == 14 && minutes < 30)) {
                        heureComplete = "09:00:00";
                    } else {
                        heureComplete = "14:00:00";
                    }
                    warning = "/!\\ Attention Livraison à J+1 /!\\";
                }
                break;
            default:
                date = "/!\\";
                heureComplete = "/!\\";
                warning = "/!\\ Attention Code Postal  non renseigné /!\\";
                break;
        }
        String[] result = new String[3];
        result[0] = date;
        result[1] = heureComplete;
        result[2] = warning;
        return result;
    }
}
