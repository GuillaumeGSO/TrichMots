package fr.rabejor.guillaume;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Menu extends ListActivity {

	private final String[] classes = {"DictionnaireActivity", "ATrouActivity", "ScrabbleActivity" };
	private final String[] names = {"Tous les mots d'une longueur en connaissant des lettres", "En connaissant quelques lettres en plus", "Mode scrabble / anagramme / boggle"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(new ArrayAdapter<>(Menu.this, android.R.layout.simple_list_item_1, names));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent ourIntent;
		try {
			ourIntent = new Intent(Menu.this, Class.forName("fr.rabejor.guillaume." + classes[position]));
			startActivity(ourIntent);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			Toast.makeText(this, getString(R.string.erreur), Toast.LENGTH_SHORT).show();
		}
	}

}
