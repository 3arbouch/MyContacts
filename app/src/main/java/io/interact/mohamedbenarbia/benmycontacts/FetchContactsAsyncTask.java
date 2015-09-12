package io.interact.mohamedbenarbia.benmycontacts;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;


import org.apache.http.HttpResponse;

import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.interact.mohamedbenarbia.benmycontacts.Contacts.Contact;
import io.interact.mohamedbenarbia.benmycontacts.Contacts.ContactsAdapter;
import io.interact.mohamedbenarbia.benmycontacts.Contacts.DisplayContactsFragement;
import io.interact.mohamedbenarbia.benmycontacts.Util.FileLogger;
import io.interact.mohamedbenarbia.benmycontacts.Util.NetworkUtility;
import io.interact.mohamedbenarbia.benmycontacts.Util.SharedAttributes;

/**
 * AsyncTask used to fetch contacts from server.
 */


public class FetchContactsAsyncTask extends AsyncTask<Void, Void, List<Contact>> {


    private static final String DEBUG_TAG = "FETCH CONTACTS";

    /**
     * Associated fragment responsible for displaying the contact list.
     */
    private DisplayContactsFragement displayConotactsFragement;


    public FetchContactsAsyncTask(DisplayContactsFragement fragement) {

        this.displayConotactsFragement = fragement;


    }


    List<Contact> contactsFromCahce = new ArrayList<>();
    List<Contact> contactsFromServer = new ArrayList<>();

    @Override
    protected void onPreExecute() {
        this.contactsFromCahce = getContactFromCache();
        ContactsAdapter contactsAdapter = new ContactsAdapter(displayConotactsFragement.getActivity(), this.contactsFromCahce);
        contactsAdapter.sort(new Contact());
        this.displayConotactsFragement.setListAdapter(contactsAdapter);


    }


    @Override
    protected List<Contact> doInBackground(Void... params) {


        // First, check if an internet connection exists. If it's the case, fetch contacts from the server and display new content if different form the cache. Otherwise, content from the cash will be displayed.

        ConnectivityManager conMan = (ConnectivityManager) this.displayConotactsFragement.getActivity().getSystemService(this.displayConotactsFragement.getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();
        if (!(netInfo == null || !netInfo.isConnected())) {

            HttpResponse response = fetchContacts();

            Log.d(DEBUG_TAG, "Response status" + response.getStatusLine().getStatusCode());

            try {
                String resBody = EntityUtils.toString(response.getEntity());
                JSONObject jsonResponse = new JSONObject(resBody);
                JSONArray jsonarray = jsonResponse.getJSONArray("data");


                for (int i = 0; i < jsonarray.length() - 1; i++) {

                    JSONObject contactJsonObject = jsonarray.getJSONObject(i);
                    Contact contact = new Contact(contactJsonObject);
                    this.contactsFromServer.add(contact);
                    Log.d(DEBUG_TAG, "Contact fetched number: " + i + " info: " + contact.toString());

                }


            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        //now compare the two lists
        //construct the list of strings that should be passed to the list adapter
        List<Contact> toBeAddedtoCache = new ArrayList<>();
        toBeAddedtoCache.addAll(this.contactsFromServer);
        toBeAddedtoCache.removeAll(this.contactsFromCahce); // contains the interactions that are in the server but not in the cache
        // log the elements that need to be added to the log file

        return toBeAddedtoCache;
    }


    /**
     * Get a list of contacts as list of JSONObject formatted to a string from list of Contacts to write it in cache ;
     */
    private List<String> getListOfContactsJSONFromat(List<Contact> listOfContacts) {
        List<String> listOfContactsJSONFromat = new ArrayList<>();

        for (Contact contact : listOfContacts) {
            listOfContactsJSONFromat.add(contact.getJson().toString());
        }

        return listOfContactsJSONFromat;
    }


    /**
     * Perform post request to the server to fetch contacts
     *
     * @return
     */
    private HttpResponse fetchContacts() {

        // Form the string URI
        String uri = SharedAttributes.BASE_URL + SharedAttributes.CONTACTS_URI;

        // Get token from shared preferences

        SharedPreferences setting = this.displayConotactsFragement.getActivity().getSharedPreferences(this.displayConotactsFragement.getActivity().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String token = setting.getString(String.valueOf(this.displayConotactsFragement.getActivity().getText(R.string.token_key)), null);


        // Form headers in a Hash map
        HashMap<String, String> headers = new HashMap<>();
        headers.put(HTTP.CONTENT_TYPE, "application/json");
        headers.put("Accept", "application/json");
        headers.put("authToken", token);


        return NetworkUtility.getMethod(uri, headers);


    }

    private List<Contact> getContactFromCache() {
        List<Contact> listOfContactsFromCache = new ArrayList<>();

        List<String> listOfContactsJSONFormat = FileLogger.getInstance(SharedAttributes.NAME_FILE_CONTACTS).fetchStringList();

        if (listOfContactsJSONFormat != null) {
            for (String contactJSON : listOfContactsJSONFormat) {
                try {
                    Contact contact = new Contact(new JSONObject(contactJSON.toString()));
                    listOfContactsFromCache.add(contact);

                    Log.d(DEBUG_TAG, "Contact fetched from Cache: " + contact.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


        return listOfContactsFromCache;
    }


    @Override
    protected void onPostExecute(List<Contact> listOfContactsToBeAdded) {
        // Check if there is something to add i.e new contact in the server different from the ones cached.
        if (!listOfContactsToBeAdded.isEmpty()) {

            // if there is a change with the cache: Update the cache
            FileLogger.getInstance(SharedAttributes.NAME_FILE_CONTACTS).logList(getListOfContactsJSONFromat(listOfContactsToBeAdded));


            // Get the index of the contacts and insert in the list to be displayed.

            ContactsAdapter adapter = (ContactsAdapter) (displayConotactsFragement.getListAdapter());

            adapter.addAll(listOfContactsToBeAdded);
            adapter.sort(new Contact());
            adapter.notifyDataSetChanged();


        }


    }


}
