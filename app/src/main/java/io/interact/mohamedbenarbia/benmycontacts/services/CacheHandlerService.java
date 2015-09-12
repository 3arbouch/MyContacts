package io.interact.mohamedbenarbia.benmycontacts.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import io.interact.mohamedbenarbia.benmycontacts.Interaction.UserInteraction;
import io.interact.mohamedbenarbia.benmycontacts.Util.FileLogger;
import io.interact.mohamedbenarbia.benmycontacts.Util.SharedAttributes;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * wakes up periodically, checks if the cache is not empty
 * If it is the case for each element in the cache call the NewInteractionhandlerService then clear the cache and ends
 * If not the case do nothing
 */
public class CacheHandlerService extends IntentService {
    String LOG_TAG = CacheHandlerService.class.getName();

    Context context;

    public CacheHandlerService() {
        super(CacheHandlerService.class.getName());
    }


    @Override
    public void onCreate() {
        super.onCreate();
        context=this;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.e(LOG_TAG,"cache handler is woken up");
        //
        try {
            //take the interactions from cache to memory and empty the cache
            ArrayList<UserInteraction> triggeredInteractions = fromCache2InteractionList();

            Iterator<UserInteraction> it = triggeredInteractions.iterator();
            while (it.hasNext()) {
                UserInteraction inter = it.next();
                //for each interaction treat it as if it was freshly captured i.e. call the new interaction handler service
                Intent mServiceIntent = new Intent(context, NewInteractionHandlerService.class);
                mServiceIntent.putExtra("interaction",inter.toString());
                context.startService(mServiceIntent);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }




    /**
     *
     *  constructs a list of users interactions based on the cache of the user
     *  If the cache is empty or does not exist already then the returned list is empty
     * @return  list of UserInteraction that are logged in the cache, the list is empty if the cache is empty
     */
    private ArrayList<UserInteraction> fromCache2InteractionList() throws JSONException {

        ArrayList<UserInteraction> res=new ArrayList<>();
        ArrayList < String > l = (ArrayList < String >) FileLogger.getInstance(SharedAttributes.NAME_FILE_USER_INTERACTIONS_TRIGGERED).fetchStringList();

        if(null != l) { // the cache already contains something
            Iterator<String> it = l.iterator();

            while (it.hasNext()) {
                JSONObject obj = new JSONObject(it.next());
                res.add(new UserInteraction(obj));
            }
        }
        //we empty the triggerd cache since we treat the cached interaction as new captured interactions
        FileLogger.remove(SharedAttributes.NAME_FILE_USER_INTERACTIONS_TRIGGERED);
        return res;
    }



}
