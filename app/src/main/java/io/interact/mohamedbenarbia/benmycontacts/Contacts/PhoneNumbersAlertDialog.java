package io.interact.mohamedbenarbia.benmycontacts.Contacts;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MohamedBenArbia on 15/09/15.
 */
public class PhoneNumbersAlertDialog extends DialogFragment {


    public static PhoneNumbersAlertDialog newInstance(String title, ArrayList<String> phoneNumbers, boolean makeCall) {
        PhoneNumbersAlertDialog frag = new PhoneNumbersAlertDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putStringArrayList("phoneNumbers", phoneNumbers);
        args.putBoolean("call", makeCall);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        final List<String> phoneNumbers = getArguments().getStringArrayList("phoneNumbers");
        final boolean makeCall = getArguments().getBoolean("call");

        return new AlertDialog.Builder(getActivity())
                .setTitle(title).setItems(phoneNumbers.toArray(new CharSequence[phoneNumbers.size()]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String phoneNumber = phoneNumbers.get(which);

                        if (makeCall) {

                            String url = "tel:" + phoneNumber;
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
                            getActivity().startActivity(intent);

                        } else {
                            String url = "sms:" + phoneNumber;
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            getActivity().startActivity(intent);

                        }

                    }
                })

                .create();
    }

}
