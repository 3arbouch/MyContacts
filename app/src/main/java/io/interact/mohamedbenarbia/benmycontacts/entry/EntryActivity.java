package io.interact.mohamedbenarbia.benmycontacts.entry;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import io.interact.mohamedbenarbia.benmycontacts.Login.LoginActivity;
import io.interact.mohamedbenarbia.benmycontacts.R;

/**
 *  Starting activity of the application.
 *  If a the user is already connected: display Main Activity. Otherwise, display LoginActivity.
 */
public class EntryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        SharedPreferences setting = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);

        String token = setting.getString(String.valueOf(getText(R.string.token_key)), null) ;

        Log.d("Entry Activity", "Token saved in shared pref: " + token) ;
        if (token==null) {
            Intent intent = new Intent(this,
                    LoginActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this,
                    MainActivity.class);
            startActivity(intent);
        }
        (new TriggerTokenRetieverAsyncTask(this)).execute();
        this.finish();
    }


}
