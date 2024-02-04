package com.example.notes.ui.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.notes.R;
import com.example.notes.database.MyDatabaseHelper;
import com.example.notes.utils.Toaster;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class UpdateFragment extends Fragment {

    // declaring widgets and variable
    private EditText titleET, authorEt;
    private TextView titleTv, dateTv;
    private Button add_button, deleteBtn;
    private Toaster toaster = new Toaster();

    private String id, title, content, date_time, preDate;

    public UpdateFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update, container, false);

        //initialising all the widgets
        titleET = view.findViewById(R.id.titleED);
        dateTv = view.findViewById(R.id.dateTv);
        authorEt = view.findViewById(R.id.authorED);
        deleteBtn = view.findViewById(R.id.deleteBtn);
        titleTv = view.findViewById(R.id.titleTv);
        add_button = view.findViewById(R.id.editBtn);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        date_time = dateFormat.format(date);

        Bundle args = getArguments();
        if (args != null) {
            id = args.getString("id", "");
            title = args.getString("title", "");
            content = args.getString("content", "");
            preDate = args.getString("date", "");
            titleET.setText(title);
            authorEt.setText(content);
            titleTv.setText(title);
            dateTv.setText(preDate);

        } else {
            Toast.makeText(getContext(), "No data", Toast.LENGTH_SHORT).show();
        }

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //updating data in db
                String userId = getUserId();
                MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(requireContext(), userId);
                if (titleET.getText().toString().isEmpty()) {
                    toaster.showToast(getContext(), "Please give title ");
                } else {
                    myDatabaseHelper.updateNote(id, titleET.getText().toString().trim(), authorEt.getText().toString().trim(), date_time);
                }

                // return to home page
                HomeFragment homeFragment = HomeFragment.newInstance(userId);
                FragmentTransaction fragmentTransaction = requireFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, homeFragment);
                fragmentTransaction.commit();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDialog();
            }
        });


        return view;
    }

    private String getUserId() {
        SharedPreferences preferences = requireActivity().getSharedPreferences("userDetail", MODE_PRIVATE);
        return preferences.getString("userId", "");
    }

    void confirmDialog() {
        // showing dialog to the user for confirmation
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete " + title + " ?");
        builder.setMessage("Are you sure you want to delete " + title + " ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //delete current note from db
                String userId = getUserId();
                MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(requireContext(), userId);
                myDatabaseHelper.deleteOneNote(id);

                //return to home page
                HomeFragment homeFragment = HomeFragment.newInstance(userId);
                FragmentTransaction fragmentTransaction = requireFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, homeFragment);
                fragmentTransaction.commit();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.create().show();

    }

}