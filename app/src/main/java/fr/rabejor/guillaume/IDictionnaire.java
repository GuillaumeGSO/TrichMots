package fr.rabejor.guillaume;

import java.util.List;

import android.app.ProgressDialog;
import android.content.res.AssetManager;

interface IDictionnaire {

	ProgressDialog getProgress();

	void populateListeResult(List<String> lst);

	AssetManager getAssets();

	void getTextDefinition(String item);

}
