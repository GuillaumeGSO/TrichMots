package fr.rabejor.guillaume;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class DictionnaireActivity extends AbstractDictionnaire implements IDictionnaire, OnItemSelectedListener {

	private Spinner spNbCar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.dictonnaire);

		super.onCreate(savedInstanceState);

		init();

		if (savedInstanceState != null) {
			// Restauration des éléments propres à la classe.
			javaLstResult = savedInstanceState.getStringArrayList(LST_RESULT);
			if (javaLstResult.size() > 0) {
				populateListeResult(new ArrayList<String>(javaLstResult));
			}
		}

	}

	public void init() {
        super.init();
		spNbCar = (Spinner) findViewById(R.id.spNbCar);
		spNbCar.setOnItemSelectedListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bStart:

			progress = ProgressDialog.show(DictionnaireActivity.this, getString(R.string.recherche), getString(R.string.enCours));
			hideKeyboard();
			DictionnaireAsynTask asyncTask = new DictionnaireAsynTask();
			asyncTask.execute(DictionnaireActivity.this, spNbCar.getSelectedItem().toString(), etInput.getText()
					.toString(), null);

			break;
		}

	}

	@Override
	void majBoutonStart() {
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
		return etInput.getText().toString().length() >= Integer.valueOf(spNbCar.getSelectedItem().toString());
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		switch (arg0.getId()) {
		case R.id.spNbCar:
			majBoutonStart();
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		switch (arg0.getId()) {
		case R.id.spNbCar:
			bStart.setEnabled(false);
			break;
		}
	}

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

}
