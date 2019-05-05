package fr.rabejor.guillaume;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

public class ATrouActivity extends AbstractDictionnaire implements OnItemSelectedListener, OnCheckedChangeListener {

    private static final String TAB_CHAR = "TAB_CHAR";

    private LinearLayout content;
    private Spinner spNbCar;
    private ToggleButton toggleButton;

    private String[] tabString;

    private AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.mode_a_trou);

        super.onCreate(savedInstanceState);

        init();

        if (savedInstanceState != null) {
            // Restauration des éléments propres à la classe.
            tabString = savedInstanceState.getStringArray(TAB_CHAR);
            javaLstResult = savedInstanceState.getStringArrayList(LST_RESULT);
            if (javaLstResult.size() > 0) {
                populateListeResult(new ArrayList<String>(javaLstResult));
            }
        }
    }

    public void init() {
        super.init();
        content = (LinearLayout) findViewById(R.id.contenu);

        spNbCar = (Spinner) findViewById(R.id.spNbCar);
        spNbCar.setOnItemSelectedListener(this);

        toggleButton = (ToggleButton) findViewById(R.id.toggleButton1);
        toggleButton.setOnCheckedChangeListener(this);

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // sauvegarde des caractères renseignés
        savedInstanceState.putStringArray(TAB_CHAR, tabString);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    private void askForLetter(final Button button) {
        // Ouvrir dialog et entrer une lettre.
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        dialogBuilder.setTitle(getString(R.string.saisirCar));

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setGravity(Gravity.CENTER);
        input.setWidth(40);
        input.setFocusable(true);
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
        input.setText(button.getText());
        input.setSelectAllOnFocus(true);

        dialogBuilder.setView(input);


        dialogBuilder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                button.setText(value);
                tabString[button.getId()] = value.toLowerCase(Locale.FRANCE);
                majBoutonStart();
            }
        });

        dialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                majBoutonStart();
            }
        });

        dialogBuilder.setNeutralButton(getString(R.string.raz), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                button.setText(null);
                tabString[button.getId()] = null;
                majBoutonStart();
            }
        });

        // set the focus change listener of the EditText
        // this part will make the soft keyboard automatically visible
        input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        alertDialog = dialogBuilder.create();
        alertDialog.show();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bStart:

                String paramLettres = etInput.getText().toString();

                if (toggleButton.isChecked()) {
                    paramLettres = null;
                }

                progress = ProgressDialog.show(ATrouActivity.this, getString(R.string.recherche), getString(R.string.enCours));

                hideKeyboard();

                DictionnaireAsynTask asyncTask = new DictionnaireAsynTask();

                asyncTask.execute(ATrouActivity.this, spNbCar.getSelectedItem().toString(), paramLettres, tabString);

                break;

            default:
                askForLetter((Button) v);
                break;
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        switch (arg0.getId()) {
            case R.id.spNbCar:
                int nbBtn = Integer.valueOf(spNbCar.getSelectedItem().toString());
                content.removeAllViews();
                if (tabString == null || tabString.length != nbBtn) {
                    tabString = new String[nbBtn];
                }
                createBoutons(tabString);
                break;
        }
        majBoutonStart();
    }

    private void createBoutons(String[] str) {
        if (str != null) {
            for (int i = 0; i < str.length; i++) {
                Button bouton = new Button(ATrouActivity.this);
                bouton.setHeight(40);
                bouton.setWidth(40);
                bouton.setId(i);
                bouton.setText(str[i]);
                bouton.setOnClickListener(this);
                content.addView(bouton);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // Rien à faire

    }

    @Override
    protected void majBoutonStart() {
        if (testContenu()) {
            bStart.setEnabled(true);
            if (javaLstResult.size() == 0) {
                tvMessage.setText(null);
            }
        } else {
            bStart.setEnabled(false);
            tvMessage.setText(getString(R.string.hintMessage));
        }
    }

    private boolean testContenu() {

        boolean lReturn = false;
        if (toggleButton.isChecked()) {
            if (isTabRenseigne(tabString)) {
                lReturn = true;
            }
        } else {
            lReturn = etInput.getText().toString().length() >= Integer.valueOf(spNbCar.getSelectedItem().toString());
        }
        return lReturn;
    }

    private boolean isTabRenseigne(String[] uneTab) {
        if (uneTab != null) {
            for (String iStr : uneTab) {
                if (StringUtils.isNotBlank(iStr)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
        if (arg1) {
            Toast.makeText(this, getString(R.string.lettreIgnore), Toast.LENGTH_SHORT).show();
            etInput.setEnabled(false);
        } else {
            Toast.makeText(this, getString(R.string.lettrePriseEnCompte), Toast.LENGTH_SHORT).show();
            etInput.setEnabled(true);
        }
        majBoutonStart();
    }
}
