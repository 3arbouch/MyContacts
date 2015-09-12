package io.interact.mohamedbenarbia.benmycontacts.Interaction;



import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import io.interact.mohamedbenarbia.benmycontacts.InteractionArrayAdapter;
import io.interact.mohamedbenarbia.benmycontacts.R;
import io.interact.mohamedbenarbia.benmycontacts.Util.FileLogger;
import io.interact.mohamedbenarbia.benmycontacts.Util.SharedAttributes;
import io.interact.mohamedbenarbia.benmycontacts.Util.NetworkUtility;
import org.apache.http.HttpResponse;

import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import java.util.*;

/**
 * Retrieves user interactions. Handles online & offline cases.
 * A better description of this class can be found in the attached UML file.
 */
public class UserInteractionsRetrieverAsyncTask extends AsyncTask<Void, Void, ArrayList<UserInteraction>> {

    private final String LOG_TAG=UserInteractionsRetrieverAsyncTask.class.getName();

    public JSONObject filters;
    private DisplayInteractionsFragment fragment ;
    ArrayList<UserInteraction> interactionsFromCache= new ArrayList<>();
    ArrayList<UserInteraction> interactionsListFromServer=new ArrayList<>();

    public UserInteractionsRetrieverAsyncTask(DisplayInteractionsFragment a) {
        this.fragment = a;
    }

    /**
     * Display user's interaction form cache.
     */
    @Override
    protected void onPreExecute() {
        try {
            this.interactionsFromCache = fromCache2InteractionList(SharedAttributes.NAME_FILE_USER_INTERACTIONS);
            //add the list of interaction that was triggered offline
            ArrayList<UserInteraction> offlineInteractions=fromCache2InteractionList(SharedAttributes.NAME_FILE_USER_INTERACTIONS_TRIGGERED);
            this.interactionsFromCache.addAll(offlineInteractions);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        fragment.setListAdapter(new InteractionArrayAdapter(fragment.getActivity(),this.interactionsFromCache));
        Log.e(LOG_TAG, "size of the cached list" + interactionsFromCache.size());
    }

    /**
     *  Synchromize & update the content of the cache with the server if  a change occurred.
     * @param params
     * @return
     */

    @Override
    protected ArrayList<UserInteraction> doInBackground(Void... params) {

        //check if there is a connection or not
        ConnectivityManager conMan = (ConnectivityManager) this.fragment.getActivity().getSystemService(this.fragment.getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) { // connection is available  ==> get interactions from the server then compare it to the list retrieved from the cache

            JSONObject reqBody = this.interactionRequestBody();

            // generate the headers
            HashMap<String,String> headers = new HashMap<>();
            headers.put(HTTP.CONTENT_TYPE, "application/json");
            headers.put("Accept", "application/json");

            SharedPreferences setting = this.fragment.getActivity().getSharedPreferences(this.fragment.getActivity().getString(R.string.preference_file_key), this.fragment.getActivity().MODE_PRIVATE);

            String token = setting.getString(String.valueOf(this.fragment.getActivity().getText(R.string.token_key)), null) ;
            headers.put("authToken", token);


            // generate the url for the login service
            String url= SharedAttributes.BASE_URL+ SharedAttributes.INTERACTIONS_LIST_URI;
            // Post data to server and get Response

            HttpResponse resp = NetworkUtility.postMethod(url, headers, reqBody);

            try {
                JSONObject resBody = new JSONObject(EntityUtils.toString(resp.getEntity()));
                JSONArray jsonInteractions = resBody.getJSONArray("data");
                if (null!=jsonInteractions) {
                    Log.e(LOG_TAG, "Server response : " + jsonInteractions.toString());
                    this.interactionsListFromServer = jsonArray2InteractionsList(jsonInteractions);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //now compare the two lists
        //construct the list of strings that should be passed to the list adapter
        ArrayList<UserInteraction> toBeAddedtoCache=new ArrayList<>();
        toBeAddedtoCache.addAll(interactionsListFromServer);
        toBeAddedtoCache.removeAll(interactionsFromCache); // contains the interactions that are in the server but not in the cache
        // log the elements that need to be added to the log file
        Log.e(LOG_TAG, "size of the difference between cache and server lists :" + toBeAddedtoCache.size());
        FileLogger.getInstance(SharedAttributes.NAME_FILE_USER_INTERACTIONS).logList(toBeAddedtoCache);



        return toBeAddedtoCache;
    }

    /**
     * Update the new list if any change occurred.
     * @param toBeAddedtoCache
     */
    @Override
    protected void onPostExecute(ArrayList<UserInteraction> toBeAddedtoCache) {
        super.onPostExecute(toBeAddedtoCache);
        Log.e(LOG_TAG, "add the elements of the list to the array adapter");
        for (UserInteraction elem :toBeAddedtoCache) {

            ((InteractionArrayAdapter) fragment.getListAdapter()).add(elem);

        }

    }

    /**
     *  Takes the JSONArray retrieved from the server and constructs the list of user's interactions
     * @param jsonArray server response
     * @return List of UserInteraction that the server holds,
     *         if the response of the server is empty then returns an empty list
     */
    private ArrayList<UserInteraction> jsonArray2InteractionsList(JSONArray jsonArray){

        ArrayList<UserInteraction> listActivities= new ArrayList<>();

        for (int i=0; i<jsonArray.length(); i++) {
            try {
                JSONObject obj= (JSONObject)jsonArray.get(i);
                UserInteraction ua = new UserInteraction(obj);
                Log.e(LOG_TAG, "new user activity fetched : "+ua.toString());
                listActivities.add(ua);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return listActivities;
    }

    /**
     * Post the email and password to the server
     *
     * @return the http respsonse of the server. If an internal error occurs return null ;
     */
    private JSONObject interactionRequestBody() {

        JSONObject postRequestBody = new JSONObject();
        // Form the Json object to send to the server
        try {

            // Form the body of the postRequest
            this.filters=new JSONObject();
            this.filters.put("defaultOperator","OR");
            this.filters.put("filters", new JSONArray());
            postRequestBody.put("filters", this.filters);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postRequestBody;
    }

    /**
     *
     *  constructs a list of users interactions based on the cache of the user
     *  If the cache is empty or does not exist already then the returned list is empty
     * @return  list of UserInteraction that are logged in the cache, the list is empty if the cache is empty
     */
    private ArrayList<UserInteraction> fromCache2InteractionList(String fileName) throws JSONException {

        ArrayList<UserInteraction> res=new ArrayList<>();
        ArrayList < String > list = (ArrayList < String >) FileLogger.getInstance(fileName).fetchStringList();

        if(null != list) { // the cache already contains something
            Iterator<String> it = list.iterator();

            while (it.hasNext()) {
                JSONObject obj = new JSONObject(it.next());
                res.add(new UserInteraction(obj));
            }
        }
        return res;
    }



}
