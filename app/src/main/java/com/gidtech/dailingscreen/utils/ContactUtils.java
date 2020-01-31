package com.gidtech.dailingscreen.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.gidtech.dailingscreen.model.Contact;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class ContactUtils {

    /**
     * Returns a contact by a given phone number
     *
     * @param context
     * @param phoneNumber
     * @return Contact
     */
    public static Contact getContactByPhoneNumber(@NonNull Context context, @NonNull String phoneNumber) {

        if (phoneNumber.isEmpty()) return null;

        //Check for permission to read contacts
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            //Don't prompt the user now, they are getting a call
            return null;
        }

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup.PHOTO_URI};
        Contact contact;

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        if (cursor.moveToFirst()) {
            contact = new Contact(cursor.getString(0), phoneNumber);
        } else {
            contact = new Contact(null, phoneNumber);
            return contact;
        }
        cursor.close();

        return contact;
    }
}
