package io.interact.mohamedbenarbia.benmycontacts.Contacts;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.interact.mohamedbenarbia.benmycontacts.R;

/**
 * Adapter that links phone Number view.
 */
public class PhoneNumberAdapter extends ArrayAdapter<JSONObject>{

private List<JSONObject> phoneNumbers ;
private Context context  ;

    public PhoneNumberAdapter(Context context, List<JSONObject> phoneNumbers) {
        super(context, -1, phoneNumbers);
        this.phoneNumbers = phoneNumbers;
        this.context = context ;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.phone_number_element, parent, false);
        TextView phoneNumber = (TextView)rowView.findViewById(R.id.phoneNumber) ;


        JSONObject phoneNumberJSON = phoneNumbers.get(position) ;
        try {
           String details =  phoneNumberJSON.getString("number")+ "<br>"+
                   "<small>"+phoneNumberJSON.getString("type")+"</small>";
            phoneNumber.setText(Html.fromHtml(details));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rowView ;
    }

    }
