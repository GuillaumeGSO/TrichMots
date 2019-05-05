package fr.rabejor.guillaume;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import android.content.res.AssetManager;
import android.os.AsyncTask;

class DictionnaireAsynTask extends AsyncTask<Object, String, List<String>> {

    private IDictionnaire callerDictionnaireActivity;
    private int nbCar;
    private String inputString;
    private String[] tabHint;

    private boolean isInputStringRenseigne = false;
    private boolean isTabHintRenseigne = false;

    private List<String> result = new ArrayList<>();

    /**
     * Param[0] = DictionnaireActivity <BR/>
     * Param[1] = nombre de caractères <BR/>
     * Param[2] = chaine input <BR/>
     * Param[3] = tableau des caractères présents
     */
    @Override
    protected List<String> doInBackground(Object... params) {
        try {

            // récupération des paramètres
            callerDictionnaireActivity = (IDictionnaire) params[0];
            nbCar = Integer.parseInt((String) params[1]);
            inputString = (String) params[2];
            tabHint = (String[]) params[3];

            // Analyse de ce qu'il y a faire.
            checkTodo();

            AssetManager asset = callerDictionnaireActivity.getAssets();

            publishProgress("Lecture du dictionnaire...");
            InputStreamReader file = new InputStreamReader(asset.open(nbCar + ".txt", 1), "ISO-8859-1");

            publishProgress("Analyse du  dictionnaire...");
            result = readFromFile(file);

            // Si il y a des lettres de renseigné
            if (isInputStringRenseigne) {
                publishProgress("Recherche mots ...");
                result = filtreListeAvecListeDeLettre(result, inputString);
            }

            // Si il y a des hint de renseigné
            if (isTabHintRenseigne) {
                publishProgress("Filtrage par caractères...");
                result = filtreListeParPositionCaractere(result, tabHint);
            }

        } catch (IOException | ClassCastException e) {
            cancel(true);
            e.printStackTrace();
        }
        return result;
    }

    private void checkTodo() {
        isTabHintRenseigne = isTabHintRenseigne();
        isInputStringRenseigne = StringUtils.isNotBlank(inputString);
    }

    private boolean isTabHintRenseigne() {
        if (tabHint != null && tabHint.length > 0) {
            for (String iCar : tabHint) {
                if (StringUtils.isNotBlank(iCar)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void onPostExecute(List<String> result) {

        super.onPostExecute(result);
        // Mettre à jour la liste de la réponse
        callerDictionnaireActivity.populateListeResult(result);
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);

        if (nbCar < callerDictionnaireActivity.getProgress().getMax()) {
            int nbrProgress = callerDictionnaireActivity.getProgress().getMax() - nbCar;
            callerDictionnaireActivity.getProgress().setProgress(nbrProgress);
        }
        if (callerDictionnaireActivity.getProgress() != null) {
            // Si la première valeur est un ., on affiche la seconde.
            if (".".equals(values[0])) {
                callerDictionnaireActivity.getProgress().setMessage(values[1]);
            } else if ("!".equals(values[0])) {
                callerDictionnaireActivity.getProgress().setProgress(Integer.parseInt(values[1]));
            } else {
                callerDictionnaireActivity.getProgress().setMessage(values[0]);
            }

        }
    }

    private List<String> readFromFile(InputStreamReader fileReader) throws IOException {

        BufferedReader br = new BufferedReader(fileReader);

        List<String> lResult = new ArrayList<>();

        try {
            String line = br.readLine();

            while (line != null) {
                lResult.add(line);
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            br.close();
        }
        return lResult;
    }

    private List<String> filtreListeAvecListeDeLettre(List<String> lstStr, String lstCar) {
        String lstCarEpure = epureMot(lstCar);

        List<String> lReturn = new ArrayList<>();
        int globalCompteur = 0;

        for (String iStr : lstStr) {
            String refListCar = lstCarEpure;
            int compteur = 0;
            String iStrEpure = epureMot(iStr);

            for (char car : iStrEpure.toCharArray()) {
                // Si un des caractères du mot testé n'est pas dans la liste, pas la peine de continuer
                if (StringUtils.contains(refListCar, car)) {
                    compteur++;
                    String st = String.valueOf(car);
                    refListCar = refListCar.replaceFirst(st, "");
                } else {
                    break;
                }
            }
            if (compteur == iStr.length()) {
                if (!isTabHintRenseigne) {
                    publishProgress(".", iStr + " (" + ++globalCompteur + ")");
                }
                lReturn.add(iStr);
            }
        }
        return lReturn;
    }

    private String epureMot(String line) {
        return translate(line.toLowerCase(Locale.FRANCE));
    }

    private String translate(String src) {
        StringBuilder result = new StringBuilder();
        if (src != null && src.length() != 0) {
            int index;
            char c;
            String chars = "àâäéèêëîïôöùûüç";
            String replace = "aaaeeeeiioouuuc";
            for (int i = 0; i < src.length(); i++) {
                c = src.charAt(i);
                if ((index = chars.indexOf(c)) != -1)
                    result.append(replace.charAt(index));
                else
                    result.append(c);
            }
        }
        return result.toString();
    }

    private List<String> filtreListeParPositionCaractere(List<String> lstStr, String[] tabStr) {
        List<String> lReturn = new ArrayList<>();
        int globalCompteur = 0;

        for (String iString : lstStr) {
            boolean checkLetter = false;
            for (int i = 0; i < tabStr.length; i++) {
                if (tabStr[i] != null && translate(iString.substring(i, i + 1)).equals(translate(tabStr[i]))) {
                    checkLetter = true;
                } else if (tabStr[i] == null) {
                    checkLetter = true;
                } else {
                    checkLetter = false;
                    break;
                }
            }
            if (checkLetter) {
                lReturn.add(iString);
                if (isTabHintRenseigne) {
                    publishProgress(".", iString + " (" + ++globalCompteur + ")");
                }
            }
        }
        return lReturn;

    }

}
