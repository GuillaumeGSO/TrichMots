package fr.rabejor.guillaume;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ScrabbleActivity extends AbstractDictionnaire {

    private List<String> lstResult = new ArrayList<>();

    private int nbCar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.scrabble);

        super.onCreate(savedInstanceState);

        tvMessage.setText(null);

        init();

        if (savedInstanceState != null) {
            // Restauration des éléments propres à la classe.
            javaLstResult = savedInstanceState.getStringArrayList(LST_RESULT);
            if (javaLstResult.size() > 0) {
                populateListeResult(new ArrayList<String>(javaLstResult));
            }
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bStart:

                String paramLettres = etInput.getText().toString();
                lstResult = new ArrayList<>();
                nbCar = paramLettres.length();

                progress = new ProgressDialog(ScrabbleActivity.this);
                progress.setCanceledOnTouchOutside(true);
                progress.setMax(nbCar);
                progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progress.setTitle(getString(R.string.recherche));
                progress.show();

                hideKeyboard();

                for (int i = nbCar; i > 1; i--) {
                    DictionnaireAsynTask asyncTask = new DictionnaireAsynTask();
                    asyncTask.execute(ScrabbleActivity.this, Integer.valueOf(i).toString(), paramLettres, null);
                }
                break;
        }
    }

    @Override
    public void majBoutonStart() {
        if (testContenu()) {
            bStart.setEnabled(true);
        } else {
            bStart.setEnabled(false);
        }
    }

    private boolean testContenu() {
        return etInput.getText().toString().length() > 1;
    }

    @Override
    public void populateListeResult(List<String> lst) {

        lstResult.addAll(lst);

        javaLstResult.clear();
        javaLstResult.addAll(lstResult);

        ArrayAdapter adapter = getCustomArrayAdapter(lst);

        gvResults.setAdapter(adapter);

        tvMessage.setText(new StringBuilder(getString(R.string.nbMots)).append(" ").append(lstResult.size()));

        if (progress != null && nbCar - progress.getProgress() <= 2) {
            progress.dismiss();
        }
        affichePub();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    private ArrayAdapter<String> getCustomArrayAdapter(List<String> lst){

        /** ancien adapter
        ArrayAdapter adapter = new ArrayAdapter<>(ScrabbleActivity.this,
                android.R.layout.simple_list_item_1, lstResult);
        **/
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, lstResult) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.BLACK);
                text.setPadding(0, 0, 0, 0);
                return view;
            }
        };

    return adapter;
    }

}
