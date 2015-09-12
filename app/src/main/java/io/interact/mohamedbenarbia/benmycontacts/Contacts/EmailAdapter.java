package io.interact.mohamedbenarbia.benmycontacts.Contacts;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.interact.mohamedbenarbia.benmycontacts.R;

/**
 * Adapter that handles email view.
 */
public class EmailAdapter extends ArrayAdapter<JSONObject>{


    private List<JSONObject> emails ;
    private Context context  ;

    public EmailAdapter(Context context, List<JSONObject> emails) {
        super(context, -1, emails);
        this.emails = emails;
        this.context = context ;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.email_element, parent, false);
        TextView phoneNumber = (TextView)rowView.findViewById(R.id.email) ;


        JSONObject phoneNumberJSON = emails.get(position) ;
        try {
            String details =  phoneNumberJSON.getString("email")+ "<br>"+
                    "<small>"+phoneNumberJSON.getString("type")+"</small>";
            phoneNumber.setText(Html.fromHtml(details));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rowView ;
    }

}
