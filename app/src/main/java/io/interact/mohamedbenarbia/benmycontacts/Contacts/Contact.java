package io.interact.mohamedbenarbia.benmycontacts.Contacts;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Models a contact which can be a person or a company.
 */
public class Contact implements Comparator<Contact> {

    public static String TYPE_KEY = "contactType";
    public static String FIRST_NAME_KEY = "firstName";
    public static String LAST_NAME_KEY = "lastName";
    public static String COMPANY_NAME_KEY = "companyName";
    public static String PHONE_KEY = "phoneNumbers";
    public static String EMAIL_KEY = "emails";
    public static String DISPLAY_KEY = "displayName";
    public static String ID_KEY = "id";

    private static String DEBUG_TAG = "Contact";


    @Override
    public int compare(Contact lhs, Contact rhs) {
        return lhs.displayName.toLowerCase().compareTo(rhs.displayName.toLowerCase());
    }

    @Override
    public boolean equals(Object other) {
        if (this instanceof Contact && other instanceof Contact) {
            return this.id.equals(((Contact) other).id);
        }
        return false;
    }


    /**
     * Used as a comparator.
     */
    public Contact() {
        super();
    }


    public enum Type {
        COMPANY, PERSON
    }

    /**
     * Contact's first name
     */
    private String firstName;

    /**
     * Contact's last name
     */
    private String lastName;

    /**
     * Company Name in case of a company
     */
    private String companyName;

    /**
     * Contact's emails. Presented by a JSONArray
     */

    private JSONArray emails;


    /**
     * Contact's phone numbers. Same thing as emails.
     */

    private JSONArray phoneNumbers;


    /**
     * The contact type. Person or company.
     */
    private Type type;


    /**
     * The display name of the contact.
     */
    private String displayName;


    /**
     * Id of the contact.
     */
    private String id;


    public Contact(Type type, String firstName, String lastName, JSONArray emails, JSONArray phoneNumbers, String displayName) {

        this.type = type;

        if (firstName != null) {
            this.firstName = firstName;
        } else {
            this.firstName = "";
        }
        if (lastName != null) {
            this.lastName = lastName;
        } else {
            this.lastName = "";
        }

        this.emails = emails;
        this.phoneNumbers = phoneNumbers;

        this.displayName = displayName;

    }


    /**
     * Construct a contact from a JSON Object
     *
     * @param contactJsonObject jsonObject representing a contact
     */
    public Contact(JSONObject contactJsonObject) {

        try {
            // First check if it's a company or a contact;
            this.type = Contact.Type.valueOf(contactJsonObject.getString(Contact.TYPE_KEY));


            //Get the display name of the contact
            this.displayName = contactJsonObject.getString(Contact.DISPLAY_KEY);

            // Get the id of the contact
            this.id = contactJsonObject.getString(Contact.ID_KEY);

            JSONArray emails = null;
            JSONArray phoneNumbers = null;

            boolean emailExist = !contactJsonObject.isNull(Contact.EMAIL_KEY);
            if (emailExist) {
                this.emails = contactJsonObject.getJSONArray(Contact.EMAIL_KEY);
                Log.d(DEBUG_TAG, "Fetchched emails " + emails);
            }

            boolean phoneExist = !contactJsonObject.isNull(Contact.PHONE_KEY);
            if (phoneExist) {
                this.phoneNumbers = contactJsonObject.getJSONArray(Contact.PHONE_KEY);
                Log.d(DEBUG_TAG, "Fetchched phoneNumbers " + phoneNumbers);

            }

            if (type.equals(Contact.Type.PERSON)) {
                String firstName = contactJsonObject.getString(Contact.FIRST_NAME_KEY);
                String lastName = contactJsonObject.getString(Contact.LAST_NAME_KEY);
                if (firstName != null) {
                    this.firstName = firstName;
                } else {
                    this.firstName = "";
                }
                if (lastName != null) {
                    this.lastName = lastName;
                } else {
                    this.lastName = "";
                }
            } else if (type.equals(Contact.Type.COMPANY)) {
                String companyName = contactJsonObject.getString(Contact.COMPANY_NAME_KEY);
                if (companyName != null) {
                    this.companyName = companyName;
                } else {
                    this.companyName = "";
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public String toString() {
        String contactInfo = "";

        contactInfo = "First Name: " + this.firstName + " Last Name: " + this.lastName;

        if (this.emails != null) {

            contactInfo = contactInfo + " emails:" + this.emails;

        }


        if (this.phoneNumbers != null) {

            contactInfo = contactInfo + " emails:" + this.phoneNumbers;

        }

        return contactInfo;
    }


    public String getFirstName() {
        return this.firstName;
    }


    public String getLastName() {
        return this.lastName;
    }


    public String getDisplayName() {


        return this.displayName;

    }

    public String getId() {
        return this.id;
    }


    /**
     * Return a JSON object that represent the contact.
     *
     * @return
     */
    public JSONObject getJson() {
        JSONObject contactJSON = new JSONObject();
        try {
            contactJSON.put(TYPE_KEY, this.type.toString());
            contactJSON.put(FIRST_NAME_KEY, this.firstName);
            contactJSON.put(LAST_NAME_KEY, this.lastName);
            contactJSON.put(COMPANY_NAME_KEY, this.companyName);
            contactJSON.put(EMAIL_KEY, this.emails);
            contactJSON.put(PHONE_KEY, this.phoneNumbers);
            contactJSON.put(DISPLAY_KEY, this.displayName);
            contactJSON.put(ID_KEY, this.id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return contactJSON;

    }


    /**
     * @return Initials of the contact name
     */
    public String getContactInitials() {
        String initials = "";

        if (this.type.equals(Type.COMPANY)) {
            initials = String.valueOf(this.displayName.charAt(0));
        } else if (this.type.equals(Type.PERSON)) {
            if (!this.firstName.isEmpty() && !this.lastName.isEmpty()) {
                initials = String.valueOf(this.firstName.charAt(0)).toUpperCase() + String.valueOf(this.lastName.charAt(0)).toUpperCase();
            } else {
                initials = String.valueOf(this.displayName.charAt(0));
            }
        }

        return initials;
    }


    /**
     * @return List of JsonObjects forming the phone Numbers JSON Array
     */
    public List<JSONObject> getPhoneNumbersList() {
        List<JSONObject> listphoneNumbersJSON = new ArrayList<>();


        if (this.phoneNumbers != null) {
            for (int i = 0; i < this.phoneNumbers.length(); i++) {
                try {
                    listphoneNumbersJSON.add(this.phoneNumbers.getJSONObject(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
        return listphoneNumbersJSON;
    }


    /**
     * @return List of JsonObjects forming the phone Numbers JSON Array
     */
    public List<JSONObject> getEmailList() {
        List<JSONObject> emailsJSON = new ArrayList<>();


        if (this.emails != null) {
            for (int i = 0; i < this.emails.length(); i++) {
                try {
                    emailsJSON.add(this.emails.getJSONObject(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
        return emailsJSON;
    }

}
