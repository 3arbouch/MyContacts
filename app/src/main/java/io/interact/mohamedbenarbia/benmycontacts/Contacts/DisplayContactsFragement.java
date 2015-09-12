package io.interact.mohamedbenarbia.benmycontacts.Contacts;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import io.interact.mohamedbenarbia.benmycontacts.Contacts.Contact;
import io.interact.mohamedbenarbia.benmycontacts.FetchContactsAsyncTask;


/**
 * A fragment that displays the contacts.
 */
public class DisplayContactsFragement extends ListFragment {


    /**
     *     Listener that handles the event when a contact view is clicked.
     */
    private OnFragmentInteractionListener mListener;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Initialize the listener
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        Log.d("Fragment", "On Attach method of display contacts") ;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Remove the default deviders
        getListView().setDivider(null);
        getListView().setDividerHeight(0);
        FetchContactsAsyncTask fetchContactsAsyncTask = new FetchContactsAsyncTask(this);
        fetchContactsAsyncTask.execute();
        Log.d("Fragment", "On createActivity method of display contacts") ;

    }


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
    **/
    public DisplayContactsFragement() {
    }




    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("Fragment", "On detach method of display contacts") ;
        mListener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        super.onListItemClick(l, v, position, id);
        Contact contact = ((ContactsAdapter)(l.getAdapter())).getContacts().get(position) ;

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(contact);
        }
    }

    /**
     * Allow the interaction between fragment and parent activity
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Contact contact);
    }

}
