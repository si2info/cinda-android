package info.si2.iista.volunteernetworks;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.Toast;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 7/9/15
 * Project: Virde
 */
public class DialogFragmentAddServer extends DialogFragment {

    DialogFragmentAddServerListener mListener;

    public interface DialogFragmentAddServerListener {
        void onDialogPositiveClick(String name);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (DialogFragmentAddServerListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement DeleteServerDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_server, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        AlertDialog d = (AlertDialog)getDialog();
        if (d != null) {

            // Views
            final TextInputLayout serverName = (TextInputLayout)d.findViewById(R.id.server_name);
            Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);

            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (serverName.getEditText() != null) {
                        String name = serverName.getEditText().getText().toString();
                        if (!name.equals("")) {

                            if (!name.startsWith("http://")) // Añadir sufijo de url si el usuario no lo añade
                                name = "http://" + name;

                            if (URLUtil.isValidUrl(name)) {
                                mListener.onDialogPositiveClick(name);
                                dismiss();
                            } else {
                                Toast.makeText(getActivity(), getString(R.string.invalidUrl), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.completeAll), Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });

        }

    }
}