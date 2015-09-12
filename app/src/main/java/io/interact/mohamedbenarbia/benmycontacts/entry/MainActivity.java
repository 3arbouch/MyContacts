package io.interact.mohamedbenarbia.benmycontacts.entry;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import io.interact.mohamedbenarbia.benmycontacts.Contacts.Contact;
import io.interact.mohamedbenarbia.benmycontacts.Contacts.ContactsDetailsFragment;
import io.interact.mohamedbenarbia.benmycontacts.Contacts.DisplayContactsFragement;
import io.interact.mohamedbenarbia.benmycontacts.Interaction.DisplayInteractionsFragment;
import io.interact.mohamedbenarbia.benmycontacts.LogOutAsyncTask;
import io.interact.mohamedbenarbia.benmycontacts.R;
import io.interact.mohamedbenarbia.benmycontacts.TabListener;
import io.interact.mohamedbenarbia.benmycontacts.Util.SharedAttributes;
import io.interact.mohamedbenarbia.benmycontacts.services.CacheHandlerService;
import io.interact.mohamedbenarbia.benmycontacts.services.SmsService;


/**
 * Main activity that displays user's contacts and interactions.
 */

public class MainActivity extends Activity implements DisplayContactsFragement.OnFragmentInteractionListener {


    private static final String TAG_DEBUG = "MAIN ACTIVITY";

    private ActionBar actionBar;

    private ActionBar.Tab tabContacts;

    AlarmManager mAlarmManager;


    /**
     * Load principle view otherwise lunch LoginActivity if user logged out.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG_DEBUG, "On create: setting action bar");

        // Customise action bar
        actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle(getText(R.string.app_name));


        // Activate the navigation mode of the tabs
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);


        tabContacts = actionBar.newTab()
                .setIcon(R.drawable.contacts_icon_off).setTabListener(new TabListener<DisplayContactsFragement>(
                        this, (String) getText(R.string.contacts_tab_tag), DisplayContactsFragement.class));

        actionBar.addTab(tabContacts);

        ActionBar.Tab tabInteractions = actionBar.newTab()
                .setIcon(R.drawable.interactions_icon_off).setTabListener(new TabListener<DisplayInteractionsFragment>(
                        this, (String) getText(R.string.interactions_tab_tag), DisplayInteractionsFragment.class));
        actionBar.addTab(tabInteractions);


        if (hasAlarmService()) {
            Intent serviceIntent = new Intent(this, CacheHandlerService.class);
            PendingIntent pIntent = PendingIntent.getService(this,
                    456,
                    serviceIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + SharedAttributes.TIME_MILIS_SERVICE_STARTUP_DELAY,
                    SharedAttributes.TIME_MILIS_SERVICE_WAKEUP_INTERVAL,
                    pIntent);
        }
        //start the service that will launch the contentObserver watching the outgoing sms

        Intent intent = new Intent(this, SmsService.class);
        startService(intent);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     *  method called when logout button pressed.
     * @param menuItem
     */

    public void logout(MenuItem menuItem) {

        LogOutAsyncTask logOutAsyncTask = new LogOutAsyncTask(this);
        logOutAsyncTask.execute();

    }

    /**
     * Called when user click on contact view handled by the DisplayContactsFragment
     * @param contact
     */
    @Override
    public void onFragmentInteraction(Contact contact) {

        FragmentTransaction fragmentTransaction = getFragmentManager()
                .beginTransaction();
        ContactsDetailsFragment contactDetailsFragment = new ContactsDetailsFragment();
        contactDetailsFragment.setContact(contact);
        fragmentTransaction
                .replace(android.R.id.content, contactDetailsFragment);

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }


    /**
     * Verifies {@link AlarmManager} is initialized
     *
     * @return {@code true}, if {@link AlarmManager} is properly initialized, {@code false} otherwise
     */
    private boolean hasAlarmService() {
        if (null == mAlarmManager) {
            mAlarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        }

        if (null == mAlarmManager) {
            Log.e(TAG_DEBUG, "Failed to get system alarm service");
        }

        return null != mAlarmManager;
    }
}
