package nl.mvdv.internetradio.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.widget.Button;

import nl.mvdv.internetradio.Constants;
import nl.mvdv.internetradio.R;

/**
 * Created by voorenmi on 18-11-2015.
 */
public class MoreInfoFragment extends AppCompatDialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        // I ignored the warning because it works, but what to provide instead of null?
        builder.setView(inflater.inflate(R.layout.more_info, null))
                // Add action buttons
                .setPositiveButton(R.string.visit_website, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Send a browser intent and visit the website
                        gotoWebsite();
                    }
                })
                .setNegativeButton(R.string.not_now, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Cancel and go back to the MainActivity
                        MoreInfoFragment.this.getDialog().cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                AlertDialog d = (AlertDialog)dialog;
                Button pos = d.getButton(DialogInterface.BUTTON_POSITIVE);
                Button neg = d.getButton(DialogInterface.BUTTON_NEGATIVE);
            }
        });
        return dialog;
    }

    private void gotoWebsite() {
        Intent startBrowserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.Urls.WEB_URL));
        startActivity(startBrowserIntent);
    }
}
