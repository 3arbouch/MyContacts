package io.interact.mohamedbenarbia.benmycontacts.Contacts;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
        final TextView phoneNumberView = (TextView)rowView.findViewById(R.id.phoneNumber) ;



        JSONObject phoneNumberJSON = phoneNumbers.get(position) ;
        try {
            final String phoneNumber = phoneNumberJSON.getString("number") ;
           String details =  phoneNumberJSON.getString("number")+ "<br>"+
                   "<small>"+phoneNumberJSON.getString("type")+"</small>";
            phoneNumberView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = "tel:"+phoneNumber;
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
                    getContext().startActivity(intent);
                }
            });

            phoneNumberView.setText(Html.fromHtml(details));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rowView ;
    }

    }
