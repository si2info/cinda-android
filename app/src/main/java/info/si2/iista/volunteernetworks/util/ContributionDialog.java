package info.si2.iista.volunteernetworks.util;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import info.si2.iista.volunteernetworks.R;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 22/9/15
 * Project: Virde
 */
public class ContributionDialog extends DialogFragment {

    ContributionDialogListener listener;

    public interface ContributionDialogListener {
        void onContributionDialogPositiveClick();
        void onContributionDialogAgainClick();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
        builder.setMessage(R.string.contributionNoSend)
                .setTitle("Contribución")
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onContributionDialogPositiveClick();
                    }
                })
                .setNegativeButton(R.string.sendAgain, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onContributionDialogAgainClick();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (ContributionDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement ContributionDialogListener");
        }
    }

}