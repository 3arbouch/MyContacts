package io.interact.mohamedbenarbia.benmycontacts.Login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


import io.interact.mohamedbenarbia.benmycontacts.Login.LoginActivity;
import io.interact.mohamedbenarbia.benmycontacts.R;
import io.interact.mohamedbenarbia.benmycontacts.Util.SharedAttributes;
import io.interact.mohamedbenarbia.benmycontacts.Util.NetworkUtility;
import io.interact.mohamedbenarbia.benmycontacts.entry.MainActivity;

import org.apache.http.HttpResponse;
;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import java.util.HashMap;

/**
 * AsyncTask used to perform communication with the server to authenticate.
 */
public class LoginAsyncTask extends AsyncTask<Void, Void, Integer> {

    // Tag used for debugging.
    private static final String TAG_DEBUG = "SERVER RESPONSE LOGIN";

    /**
     *  Progress dialog to show while logging in
     */
    private ProgressDialog progressDialog ;

    /**
     * Email and password used for authentication
     */
    private String email;
    private String password;

    // context for showing messages on the GUI
    private Context context;


    public LoginAsyncTask(String email, String password, Context context) {
        this.email = email;
        this.password = password;
        this.context = context;
        progressDialog = new ProgressDialog(this.context);


    }


    /**
     * Show a progress dialog with a spinner while logging in
     */
    @Override
    protected void onPreExecute () {

        progressDialog.setMessage(context.getText(R.string.logging_in_message));
        progressDialog.show();

    }

    /**
     * Perform the communication with the server and return the status code.
     *
     * @param params
     * @return Status of the login operation (Success, Unauthorized, IncorrectPassword ...)
     */
    @Override
    protected Integer doInBackground(Void... params) {

        //return internal error status by default
        int statusLogin = SharedAttributes.INTERNAL_ERROR ;



        //generate the request body
        JSONObject reqBody = null;
        try {
           reqBody = this.loginBody();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // generate the headers
        HashMap<String,String> headers = new HashMap<>();
        headers.put(HTTP.CONTENT_TYPE, "application/json");

        // generate the url for the login service
        String url= SharedAttributes.BASE_URL+ SharedAttributes.LOGIN_URI;

        if(reqBody!=null && headers!=null) {
            // Post data to server and get Response
            HttpResponse response = NetworkUtility.postMethod(url, headers, reqBody);

            if (response != null) {
                statusLogin = getStatus(response);
                // Save the token to shared preferences if successful connection
                if (statusLogin == SharedAttributes.OK_RESPONSE) {
                    String resBody = null;
                    try {
                        resBody = EntityUtils.toString(response.getEntity());
                        JSONObject jsonResponse = new JSONObject(resBody);
                        JSONObject tokenObject = jsonResponse.getJSONObject("token");
                        String token = tokenObject.getString("authToken");
                        Log.d(TAG_DEBUG, "Token returned " + token);

                        // Put the token in the shared preferences
                        SharedPreferences sharedPref = context.getSharedPreferences(
                                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(context.getString(R.string.token_key), token);
                        editor.commit();


                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return statusLogin;
    }


    /**
     * generates the request body for the login
     * @return JsonObject that encapsulates the body of the request
     */


    private JSONObject loginBody() throws JSONException {
        JSONObject resBody = new JSONObject();
        resBody.put("username", this.email);
        resBody.put("password", this.password);
        resBody.put("client", "Apriary");

        return resBody;
    }



    /**
     * Return the corresponding status depending on the received response from the server. Sould return one of the status defined in the class above.
     *
     * @param response Http repsonse received from the server
     * @return corresponding status ;
     */
    private int getStatus(HttpResponse response) {


        // Return internal error status by default (if response null or an exception occurred) ;

        int statusLogin = SharedAttributes.INTERNAL_ERROR;


        if (response != null) {
            // Get status code response
            statusLogin = response.getStatusLine().getStatusCode();

            // Log the status for debug
            Log.d(TAG_DEBUG, "status Line" + response.getStatusLine().toString());
            Log.e(TAG_DEBUG, "Replied with status code " + statusLogin); //prints the response's status code


            // Determine if it's an undefined user or a wrong password

            if (statusLogin == SharedAttributes.UNAUTHORIZED__RESPONSE) {


                String resBody = null;
                try {
                    resBody = EntityUtils.toString(response.getEntity());
                    JSONObject jsonResponse = new JSONObject(resBody) ;
                    String message = jsonResponse.getString("message") ;

                    Log.d(TAG_DEBUG, "Replied with entity " + message);

                    // Put corresponding status with respect to the received message

                    if (message.equals(SharedAttributes.USER_NOT_FOUND_MESSAGE)) statusLogin = SharedAttributes.USER_NOT_FOUND;
                    else if (message.equals(SharedAttributes.INCORRECT_PASSWORD_MESSAGE))
                        statusLogin = SharedAttributes.INCORRECT_PASSWORD;

                } catch (IOException e) {
                    e.printStackTrace();
                }
                catch(JSONException e){
                    e.printStackTrace();
                }

            }
        }
        return statusLogin;
    }

    @Override
    protected void onPostExecute(Integer result) {

        // Dismiss progress dialog
        progressDialog.dismiss();

        Log.d(TAG_DEBUG, "argumennt in OnpostExecute" + result);

        switch (result) {

            case SharedAttributes.INTERNAL_ERROR: {

                Toast.makeText(context, context.getText(R.string.internal_error_message), Toast.LENGTH_LONG).show();
                break ;

            }
            case SharedAttributes.OK_RESPONSE: {

                Toast.makeText(context, context.getText(R.string.login_success_message), Toast.LENGTH_LONG).show();
                // In case of successful log in: dismiss login activity
                Intent intent = new Intent(context,
                        MainActivity.class);
                context.startActivity(intent);
                ((LoginActivity)context).finish();
                break ;

            }

            case SharedAttributes.USER_NOT_FOUND: {

                Toast.makeText(context, context.getText(R.string.user_not_found_error_message), Toast.LENGTH_LONG).show();
                break ;

            }

            case SharedAttributes.INCORRECT_PASSWORD: {

                Toast.makeText(context, context.getText(R.string.incorrect_password_error_message), Toast.LENGTH_LONG).show();
                break ;

            }

            case SharedAttributes.NOT_FOUND__RESPONSE: {

                Toast.makeText(context, context.getText(R.string.not_found_error_message), Toast.LENGTH_LONG).show();
                break ;

            }

        }



    }

}
