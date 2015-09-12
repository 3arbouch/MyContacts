package io.interact.mohamedbenarbia.benmycontacts.Login;


import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import io.interact.mohamedbenarbia.benmycontacts.R;


/**
 * A login view that presents a login via emaio and password to MyContacts.
 */

public class LoginActivity extends AppCompatActivity {

    /**
     *  User interface resources
     */
    private EditText emailView;
    private EditText passwordView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get the email and password view
        emailView = (EditText)findViewById(R.id.emailAdress) ;
        passwordView = (EditText)findViewById(R.id.password) ;

    }


    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true) ;
        // super.onBackPressed(); //
    }




    /**
     *  Called when Login button clicked.
     *  Attempt to login to myContacts: if the email/password field is empty, an error is generated and the user cannot login.
     *  Additionally, the form of the email is verified to match the email pattern.
     * @param view Login button
     */
    public void loginButtonClicked(View view) {

        // First check if there is an internet connection. Display a Toast if there is no internet connection

        ConnectivityManager conMan = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();
        if (netInfo == null || !netInfo.isConnected()) {
            Toast.makeText(this, getText(R.string.no_internet_connection_error_message), Toast.LENGTH_LONG).show(); ;
        }

        else {

            // Reset the error of the editText (email and password) of the login form
            emailView.setError(null);
            passwordView.setError(null);

            // Indicate if the fields (email and password) satisfies condition to attempt login (send login info to the server)
            boolean fieldsVerified = true;

            // Focus on the view where the error occurred (email or password view)
            View focus = null;

            // Get the contents of the email and password entered by the user
            String email = emailView.getText().toString();
            String password = passwordView.getText().toString();


            // Verify email field

            if (email.isEmpty()) {
                emailView.setError(getString(R.string.error_field_required));
                fieldsVerified = false;
                focus = emailView;
            } else if (!isEmailValid(email)) {
                emailView.setError(getString(R.string.error_invalid_email));
                fieldsVerified = false;
                focus = emailView;
            }


            // Verify password field

            if (password.isEmpty()) {
                passwordView.setError(getString(R.string.error_field_required));
                fieldsVerified = false;
                focus = passwordView;
            }

            // if fields verified try to connect, otherwise focus on the view where the error occured

            if (fieldsVerified) {
                LoginAsyncTask loginAsyncTask = new LoginAsyncTask(email, password, this);
                loginAsyncTask.execute();

            } else {
                focus.requestFocus();

            }
        }


    }

    /**
     * Verify the entered email. If it matches the email pattern.
     * @param email
     * @return true if match. False otherwise
     */
    private boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches() ;
    }



}
