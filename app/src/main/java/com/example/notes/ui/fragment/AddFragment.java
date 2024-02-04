package com.example.notes.ui.fragment;


import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.notes.R;
import com.example.notes.database.MyDatabaseHelper;
import com.example.notes.utils.Toaster;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class AddFragment extends Fragment {

    // declaring widgets
    private EditText titleET, contentEt;
    private Button add_button;
    private ImageView backBtn;
    private String date_time;
    private Toaster toaster = new Toaster();

    public AddFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        // initialising all the widgets
        titleET = view.findViewById(R.id.title);
        contentEt = view.findViewById(R.id.content);
        backBtn = view.findViewById(R.id.backBtn);
        add_button = view.findViewById(R.id.saveBtn);

        // getting current data to store in database
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        date_time = dateFormat.format(date);

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDatabaseHelper myDB = new MyDatabaseHelper(getContext(), getUserId());
                String userId = getUserId();

                //validation of data
                if (titleET.getText().toString().trim().isEmpty()) {
                    toaster.showToast(getContext(), "Please give title ");
                    return;
                }

                // adding data in database
                myDB.addNote(userId, titleET.getText().toString().trim(),
                        contentEt.getText().toString().trim(),
                        date_time);

                HomeFragment homeFragment = HomeFragment.newInstance(userId);
                FragmentTransaction fragmentTransaction = requireFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, homeFragment);
                fragmentTransaction.commit();

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();

            }
        });

        return view;
    }

    //getting userid from shared preference
    private String getUserId() {
        SharedPreferences preferences = requireActivity().getSharedPreferences("userDetail", MODE_PRIVATE);
        return preferences.getString("userId", "");
    }
}