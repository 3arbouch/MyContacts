package io.interact.mohamedbenarbia.benmycontacts.Contacts;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.interact.mohamedbenarbia.benmycontacts.FetchContactsAsyncTask;
import io.interact.mohamedbenarbia.benmycontacts.R;

/**
 * Fragment that shows Contact details. i.e: name,  phone numbers, emails
 */
public class ContactsDetailsFragment extends Fragment {


    private Contact contact;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Fragment Details", "On create method of Contacts details fragment");


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getActionBar().hide();
        Log.d("Fragment Details", "On activity created method of Contacts details fragment");

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Fragment Details", "On  resume method of Contacts details fragment");
        getActivity().getActionBar().hide();
    }


    @Override
    public void onPause() {
        super.onPause();
        getActivity().getActionBar().show();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View detailsView = inflater.inflate(R.layout.contact_details_layout, container, false);

        // Set the initials text view
        TextView contactInitials = (TextView) detailsView.findViewById(R.id.contactInitials);
        contactInitials.setText(this.contact.getContactInitials());

        // Set the contact Name listView
        TextView contactName = (TextView) detailsView.findViewById(R.id.contactName);
        contactName.setText(this.contact.getDisplayName());

        // Set the phone NumbersList View
        ListView phoneNumbersListView = (ListView) detailsView.findViewById(R.id.phoneNumbersListView);
        List<JSONObject> listOfPhoneNumbers = this.contact.getPhoneNumbersList();
        phoneNumbersListView.setAdapter(new PhoneNumberAdapter(getActivity(), listOfPhoneNumbers));
        if (!listOfPhoneNumbers.isEmpty()) {
            TextView titleListOfContacts = (TextView) detailsView.findViewById(R.id.titlePhoneNumberListView);
            titleListOfContacts.setText("Phone numbers");
            titleListOfContacts.setVisibility(View.VISIBLE);

        }

        //Set the email List View

        // Set the phone NumbersList View
        ListView emailListView = (ListView) detailsView.findViewById(R.id.emailsListView);
        List<JSONObject> listOfEmails = this.contact.getEmailList();
        emailListView.setAdapter(new EmailAdapter(getActivity(), listOfEmails));

        if (!listOfEmails.isEmpty()) {
            TextView titleListOfEmails = (TextView) detailsView.findViewById(R.id.titleEmailListView);
            titleListOfEmails.setText("Emails");
            titleListOfEmails.setVisibility(View.VISIBLE);

        }

        return detailsView;
    }


    public void setContact(Contact contact) {
        this.contact = contact;
    }
}

