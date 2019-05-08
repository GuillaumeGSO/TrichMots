package fr.rabejor.guillaume;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

abstract class AbstractDictionnaire extends Activity implements IDictionnaire, View.OnClickListener {

    EditText etInput;
    Button bStart;
    ProgressDialog progress;
    TextView tvMessage;
    GridView gvResults;

    public static ArrayList<String> javaLstResult;

    public static final String LST_RESULT = "LST_RESULT";

    private AdView adView;

    private AdRequest adRequest = null;

    abstract void majBoutonStart();


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // sauvegarde de la liste des résultats
        savedInstanceState.putStringArrayList(LST_RESULT, javaLstResult);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public ProgressDialog getProgress() {
        return progress;
    }

    @Override
    public void populateListeResult(List<String> lst) {
        //Alimentation de la liste globale
        javaLstResult.clear();
        javaLstResult.addAll(lst);

        //init de la liste pour la view
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lst);
        gvResults.setAdapter(adapter);
        tvMessage.setText(new StringBuilder(getString(R.string.nbMots)).append(' ').append(lst.size()));
        if (progress != null) {
            progress.dismiss();
        }

        affichePub();

    }

    void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(bStart.getWindowToken(), 0);
    }

    @Override
    public void getTextDefinition(String item) {

        //String stringUrl = getString(R.string.urlLarousse) + item;
        //retrouver tous les <p class="def"> --> un peu galère, nécessite un parser Jsoup, par exemple.

        String stringUrl = getString(R.string.urlCnrtl) + item;


        // Gets the URL from the UI's text field.
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(stringUrl));
            startActivity(browserIntent);
            //TODO si appel à un service ou un site web parser...
            // new DefinitionMotAsynActivity().execute(stringUrl);
        } else {
            Toast.makeText(this, getString(R.string.noNetwork), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        etInput = (EditText) findViewById(R.id.etInput);

        etInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                majBoutonStart();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // rien à faire
            }

            @Override
            public void afterTextChanged(Editable s) {
                // rien à faire
            }
        });

        bStart = (Button) findViewById(R.id.bStart);
        bStart.setOnClickListener(this);
        bStart.setEnabled(false);

        tvMessage = (TextView) findViewById(R.id.tvMessage);

        gvResults = (GridView) findViewById(R.id.gvResults);

        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                getTextDefinition(((TextView) v).getText().toString());
            }
        });

        //MobileAds.initialize(this, R.string.banner_ad_unit_id);
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        adView = (AdView) findViewById(R.id.adView);
        //AdRequest adRequest = new AdRequest.Builder().build();
        adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("F2B693C7460CE7BA012C50D2F53543C5")  // My SonyXperiaU test phone
                .addTestDevice("F85B1DE0959EAB250489C3DC7DC89B0A")  // My Samsung Galaxy S5 test phone
                .build();

    }

    void affichePub() {
        boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        //Ne pas afficher la pub en paysage : plus assez de place pour la liste des résultats.
        if (getResources().getBoolean(R.bool.avecPub) && isPortrait) {
            adView.loadAd(adRequest);
        }


    }

    public void init() {
        javaLstResult = new ArrayList<>();
    }

}
