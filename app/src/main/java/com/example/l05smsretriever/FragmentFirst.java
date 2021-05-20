package com.example.l05smsretriever;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

public class FragmentFirst extends AppCompatActivity {
    EditText etNum;
    TextView tvResult;
    Button btnRetrieveSMS, btnSendEmail;
    public FragmentFirst() {
        // Required empty public constructor
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_first);

        // Binding UI variables
        etNum = findViewById(R.id.etNum);
        tvResult = findViewById(R.id.tvNumResult);
        // btnSendEmail = findViewById(R.id.btnSendEmail);
        btnRetrieveSMS = findViewById(R.id.btnRetrieveNum);


        btnSendEmail.setOnClickListener(v -> {

            Intent emailIntent = new Intent(Intent.ACTION_SEND);

            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"19039480@rp.edu.sg"});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Testing Email Intent");
            String statement = tvResult.getText().toString();
            emailIntent.putExtra(Intent.EXTRA_TEXT, statement);

            emailIntent.setType("message/rfc822");

            startActivity(Intent.createChooser(emailIntent, "Choose an Email client :"));


        });
        btnRetrieveSMS.setOnClickListener(view -> {

            // todo: 9.	To include the runtime check in the app
            int permissionCheck = PermissionChecker.checkSelfPermission(FragmentFirst.this, Manifest.permission.READ_SMS);

            if(permissionCheck != PermissionChecker.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(FragmentFirst.this, new String[]{Manifest.permission.READ_SMS}, 0);
                return;
            }

            // Create all messages URI
            Uri uri = Uri.parse("content://sms");

            // The columns we want
            //  date is when the message took place
            //  address is the number of the other party
            //  body is the message content
            //  type 1 is received, type 2 sent
            String[] reqCols = new String[]{"numb", "address", "body", "type"};

            // Get Content Resolver object from which to
            //  query the content provider
            ContentResolver cr = getContentResolver();


            // Todo: 3.	When user enters a number under Only SMS containing this number,
            //  it will retrieve only SMS that are received from the entered number
            String filter = "address LIKE ?";
            String number = etNum.getText().toString();
            String[] filterArgs = {"%" + number + "%"};




            Cursor cursor = cr.query(uri, reqCols, filter, filterArgs, null);
            String smsBody = "";

            if (cursor.moveToFirst()) {
                do {
                    long dateInMillies = cursor.getLong(0);
                    String date = (String) DateFormat.format("dd MM yyyy h:mm:ss aa", dateInMillies);
                    String address = cursor.getString(1);
                    String body = cursor.getString(2);
                    String type = cursor.getString(3);
                    if (type.equalsIgnoreCase("1")) {
                        type = "Inbox:";
                    } else {
                        type = "Sent:";
                    }
                    smsBody += type + " " + address + "\n at" + date + "\n\"" + body + "\"\n\n";
                } while (cursor.moveToNext());
            }
            tvResult.setText(smsBody);
        });

    }

    // Todo: 10.	The user will have the option to grant the permission.
    //  In any case, you can choose to react with actions after the user makes his choice on granting the permission.
   /* onRequestPermissionsResult()  will be triggered (it’s a callback method)
   after the request for permission dialog is shown to the user.

   You can choose the actions to be associated with the cases where:
    i)	Permission is granted
    Mostly like, you’ll want to continue where it was left off when the dialog was triggered.
    In this case, the button click event to be triggered.

    ii)	Permission is denied
    In this case, a feedback to the user is necessary and a Toast will be appropriate.
    */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the read SMS
                    //  as if the btnRetrieve is clicked
                    btnRetrieveSMS.performClick();

                } else {
                    // permission denied... notify user
                    Toast.makeText(FragmentFirst.this, "Permission not granted",
                            Toast.LENGTH_SHORT).show();
                }

            }
        }
    }
}