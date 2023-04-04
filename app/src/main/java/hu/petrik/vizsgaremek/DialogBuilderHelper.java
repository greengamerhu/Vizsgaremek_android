package hu.petrik.vizsgaremek;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

public class DialogBuilderHelper {
    ErrorFromServer error ;
    Activity currentActivity;

    public DialogBuilderHelper(ErrorFromServer error, Activity currentActivity) {
        this.error = error;
        this.currentActivity = currentActivity;
    }

    public Dialog createDialog() {
        Dialog dialog = new Dialog(currentActivity);
        dialog.setContentView(R.layout.customdialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView title = dialog.findViewById(R.id.textViewDialogTitle);
        TextView message = dialog.findViewById(R.id.textViewDialogMessage);
        TextView statusCode = dialog.findViewById(R.id.textViewDialogStatusCode);
        MaterialButton btnOk = dialog.findViewById(R.id.buttonDialogOK);

        StringBuilder errormsg = new StringBuilder();
        for (String item : error.getMessage()) {
            errormsg.append(item + "\n");
        }
        dialog.setCancelable(false);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        title.setText("Hiba");
        String errorjson = error.getError().isEmpty() ?  "" : error.getError();
        statusCode.setText("Kód: " + error.getStatusCode() + "\n" + errorjson);
        message.setText(errormsg);

        dialog.create();
        return dialog;

    }

    public Dialog createServerErrorDialog() {
        Dialog dialog = new Dialog(currentActivity);
        dialog.setContentView(R.layout.customdialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView title = dialog.findViewById(R.id.textViewDialogTitle);
        TextView message = dialog.findViewById(R.id.textViewDialogMessage);
        TextView statusCode = dialog.findViewById(R.id.textViewDialogStatusCode);
        MaterialButton btnOk = dialog.findViewById(R.id.buttonDialogOK);
        dialog.setCancelable(false);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(0);
            }
        });
        title.setText("hiba");
        statusCode.setText("");
        message.setText("Probléma lépett fel a szerverrel Vagy ellenőrizd az inernetkapocsolatodat\n");

        dialog.create();
        return dialog;

    }
}
