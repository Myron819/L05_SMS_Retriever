package com.example.l05smsretriever;

import android.Manifest;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentFirst extends Fragment {
    Button btnRetrieveNum;
    EditText etNum;
    TextView tvNumResult;

    public FragmentFirst() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        etNum = view.findViewById(R.id.etNum);
        btnRetrieveNum = view.findViewById(R.id.btnRetrieveNum);
        tvNumResult = view.findViewById(R.id.tvNumResult);


        btnRetrieveNum.setOnClickListener(view1 -> {
            int permissionCheck = PermissionChecker.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS);

            if (permissionCheck != PermissionChecker.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_SMS}, 0);
                return;
            }
            Uri uri = Uri.parse("content://sms");

            String[] reqCols = new String[]{"date", "address", "body", "type"};

            ContentResolver cr = getActivity().getContentResolver();


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
            tvNumResult.setText(smsBody);
        });

        return view;
    }
}