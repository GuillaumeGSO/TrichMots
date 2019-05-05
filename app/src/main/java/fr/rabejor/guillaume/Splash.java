package fr.rabejor.guillaume;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.widget.TextView;

public class Splash extends Activity {

	private TextView tvVersion;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		//Affiche la version
        tvVersion = (TextView) findViewById(R.id.tvVersionName);
        String version = new StringBuilder(getString(R.string.app_VersionName)).append("-").append(BuildConfig.VERSION_CODE).toString();
        tvVersion.setText(version);

		Thread timer = new Thread() {
			@Override
			public void run() {
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					Intent openMainMenu = new Intent("fr.rabejor.guillaume.MAINMENU");
					startActivity(openMainMenu);
				}
			}
		};

        timer.start();
	}
}
