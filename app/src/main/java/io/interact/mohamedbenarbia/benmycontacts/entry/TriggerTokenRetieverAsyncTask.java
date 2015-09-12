package io.interact.mohamedbenarbia.benmycontacts.entry;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import io.interact.mohamedbenarbia.benmycontacts.R;
import io.interact.mohamedbenarbia.benmycontacts.Util.NetworkUtility;
import io.interact.mohamedbenarbia.benmycontacts.Util.SharedAttributes;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

/**

 * Retrieves trigger token used for posting a new interaction to the server.s

 */
public class TriggerTokenRetieverAsyncTask extends AsyncTask <Void ,Void, Void>{
    String LOG_TAG=TriggerTokenRetieverAsyncTask.class.getName();
    Context context;

    public TriggerTokenRetieverAsyncTask(Context ctx) {
        this.context=ctx;
    }



    @Override
    protected Void doInBackground(Void... params) {

        //check if there is a connection or not
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) { // connection is available  ==> get interactions from the server then compare it to the list retrieved from the cache


            // generate the headers
            HashMap<String,String> headers = new HashMap<>();
            headers.put(HTTP.CONTENT_TYPE, "application/json");
            headers.put("Accept", "application/json");

            SharedPreferences setting = context.getSharedPreferences(context.getString(R.string.preference_file_key), context.MODE_PRIVATE);
            String token = setting.getString(String.valueOf(context.getText(R.string.token_key)), null) ;
            headers.put("authToken", token);



            String url= SharedAttributes.BASE_URL+ SharedAttributes.DEVICES_URI;
            // Post data to server and get Response

            HttpResponse resp = NetworkUtility.getMethod(url,headers);
            if (resp!=null && resp.getStatusLine().getStatusCode()==SharedAttributes.OK_RESPONSE) {

                try {
                    JSONArray resBody = new JSONArray(EntityUtils.toString(resp.getEntity()));
                    for(int i=0;i<resBody.length();i++) {
                        JSONObject obj=resBody.getJSONObject(i);
                        String triggerToken = obj.getString("triggerToken");
                        if(obj.getBoolean("active")&& triggerToken!=null) {

                            // Put the token in the shared preferences
                            SharedPreferences sharedPref = context.getSharedPreferences(
                                    context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("triggerToken", triggerToken);
                            editor.commit();
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }



        return null;
    }
}
