package com.example.notes.utils;

import android.content.Context;
import android.widget.Toast;

public class Toaster {

    public void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
