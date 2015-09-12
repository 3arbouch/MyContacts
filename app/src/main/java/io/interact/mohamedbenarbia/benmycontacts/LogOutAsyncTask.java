package io.interact.mohamedbenarbia.benmycontacts;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


import io.interact.mohamedbenarbia.benmycontacts.Login.LoginActivity;
import io.interact.mohamedbenarbia.benmycontacts.Util.SharedAttributes;
import io.interact.mohamedbenarbia.benmycontacts.Util.NetworkUtility;
import io.interact.mohamedbenarbia.benmycontacts.entry.MainActivity;

import org.apache.http.HttpResponse;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Async task that performs log out.
 *
 */


public class LogOutAsyncTask extends AsyncTask<Void, Void, Integer> {

    // Tag used for debugging.
    private static final String TAG_DEBUG = "SERVER RESPONSE LOG OUT";


    // Used to contain main activity context to lunch login activity and display a progress dialog for logging out!
    private Context context;

    // Shared preference used to retrieve nad delete authentication token.
    private SharedPreferences setting;


    /**
     *  Progress dialog to show while logging out
     */
    private ProgressDialog progressDialog ;


    public LogOutAsyncTask(Context context) {
        this.context = context;
        setting = this.context.getSharedPreferences(this.context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        progressDialog = new ProgressDialog(this.context);


    }



    /**
     * Show a progress dialog with a spinner while logging out
     */
    @Override
    protected void onPreExecute () {

        progressDialog.setMessage(context.getText(R.string.logging_out_message_spinner));
        progressDialog.show();

    }


    /**
     * Perform communication for the server for logging out and return the status code.
     *
     * @param params
     * @return
     */
    @Override
    protected Integer doInBackground(Void... params) {

        // Initialize status code to return

        int status = SharedAttributes.INTERNAL_ERROR ;


        //generate the request body
        JSONObject reqBody = this.logoutBody();

        // generate the headers
        HashMap<String,String> headers = new HashMap<>();
        headers.put(HTTP.CONTENT_TYPE, "application/json");

        // generate the url for the login service

        String url= SharedAttributes.BASE_URL+ SharedAttributes.LOGOUT_URI;


        if(reqBody!=null && headers!=null) {
            // Post data to server and get Response
            HttpResponse response = NetworkUtility.postMethod(url, headers, reqBody);

            if (response != null) {
                status = response.getStatusLine().getStatusCode();
                Log.d(TAG_DEBUG, "Returned status code: " + status);
            }

        }


        return status;
    }


    /**
     * Constructs the body of the logout request.
     *
     * @return JSONObject that contains the body of the request.
     */
    private JSONObject logoutBody() {


        JSONObject rBody = new JSONObject();

        // Get token from shared preferences.

        String token = setting.getString(String.valueOf(this.context.getText(R.string.token_key)), null);
        try {
            rBody.put("authToken", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rBody;

    }


    @Override
    protected void onPostExecute(Integer result) {

        Log.d(TAG_DEBUG, "Server responds with result" + result) ;

        // Dismiss progress dialog
        progressDialog.dismiss();



        if(result == SharedAttributes.NO_CONTENT) {

            Toast.makeText(this.context,this.context.getText(R.string.logging_out_message),Toast.LENGTH_SHORT).show(); ;
        }

        // Get shared preference preference editor
        SharedPreferences.Editor editor = setting.edit();

        // Delete the authentication token from shared preference
        editor.remove(this.context.getString(R.string.token_key)) ;
        editor.commit() ;

        // Lunch login activity
        Intent intent = new Intent(this.context,
                LoginActivity.class);
        this.context.startActivity(intent);
        ((MainActivity)context).finish();

    }
}