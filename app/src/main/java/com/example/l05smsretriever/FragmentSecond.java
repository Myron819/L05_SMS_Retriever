package com.example.l05smsretriever;

import android.Manifest;
import android.content.ContentResolver;
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

public class FragmentSecond extends AppCompatActivity {
    EditText etWord;
    TextView tvWordResult;
    Button btnRetrieveWord;

    public FragmentSecond() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_second);

        // Binding UI variables
        etWord = findViewById(R.id.etWord);
        tvWordResult = findViewById(R.id.tvWordResult);
        btnRetrieveWord = findViewById(R.id.btnRetrieveWord);


        btnRetrieveWord.setOnClickListener(view -> {

            // todo: 9.	To include the runtime check in the app
            int permissionCheck = PermissionChecker.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS);

            if (permissionCheck != PermissionChecker.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS}, 0);
                return;
            }

            // Create all messages URI
            Uri uri = Uri.parse("content://sms");

            // The columns we want
            //  date is when the message took place
            //  address is the word of the other party
            //  body is the message content
            //  type 1 is received, type 2 sent
            String[] reqCols = new String[]{"numb", "address", "body", "type"};

            // Get Content Resolver object from which to
            //  query the content provider
            ContentResolver cr = getContentResolver();


            // Todo: 3.	When user enters a word under Only SMS containing this word,
            //  it will retrieve only SMS that are received from the entered word
            String filter = "address LIKE ?";
            String word = etWord.getText().toString();
            String[] filterArgs = {"%" + word + "%"};


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
            tvWordResult.setText(smsBody);
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
                    btnRetrieveWord.performClick();

                } else {
                    // permission denied... notify user
                    Toast.makeText(MainActivity.this, "Permission not granted",
                            Toast.LENGTH_SHORT).show();
                }

            }
        }
    }
}