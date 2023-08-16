package com.example.fimsdelivery.global;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;

import com.example.fimsdelivery.LocalityActivity;
import com.example.fimsdelivery.R;
import com.example.fimsdelivery.sharedpreferences.SessionManager;

public class Global {
    public static String orderId = "";
    public static String contact = "";
    public static int otp = 0;

    public static void menuNavigation(MenuItem item, Context context) {

        switch (item.getItemId()) {
            case R.id.nav_locality:
                context.startActivity(new Intent(context, LocalityActivity.class));
                break;
            case R.id.nav_logout:
                AlertDialog.Builder builder2;
                AlertDialog alert2;
                builder2 = new AlertDialog.Builder(context);
                builder2.setTitle("Exit Alert");
                builder2.setMessage("Are you sure you want to logout and exit?");
                builder2.setCancelable(true);
                builder2.setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SessionManager sessionManager = new SessionManager(context);
                                sessionManager.logoutFromSession();
                                System.exit(0);
                            }
                        });
                builder2.setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                alert2 = builder2.create();
                alert2.show();
                break;
        }
    }
}
