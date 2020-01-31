package com.gidtech.dailingscreen.model;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Contact {

//    long contactId;
//    private long listId;
    private String name;
    private String phoneNumbers;

    /**
     * Contact constructor
     * Accepts a name and a  numbers (without an image)
     *
     * @param name
     * @param phoneNumbers
     */
    public Contact(String name, @NonNull String phoneNumbers) {
        this.name = name;
        this.phoneNumbers = phoneNumbers;
    }

//    public Contact(Cursor cursor) {
//        this.name = cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME_PRIMARY));
//        this.phoneNumbers = new ArrayList<>();
//        this.phoneNumbers.add(cursor.getString(cursor.getColumnIndex(Phone.NUMBER)));
//    }

    /**
     * Returns the contact's name
     *
     * @return String of the name
     */
    @NonNull
    public String getName() {
        return name;
    }

    /**
     * Sets the contact's name by a given String
     *
     * @param name
     */
    public void setName(@NonNull String name) {
        this.name = name;
    }

    /**
     * Returns all the phone numbers of a contact
     *
     * @return List<String>
     */
    @NonNull
    public String getPhoneNumbers() {
        return phoneNumbers;
    }

    /**
     * Sets the contact's phone numbers by a given list of strings
     *
     * @param phoneNumbers
     */
    public void setPhoneNumbers(@NonNull String phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }



    @Override
    public boolean equals(@Nullable Object obj) {
        if (super.equals(obj)) return true;
        if (obj instanceof Contact) {
            Contact c = (Contact) obj;
            return (name.equals(c.getName()) &&
                    phoneNumbers.equals(c.getPhoneNumbers()));
        }
        return false;
    }

    /**
     * Returns the contact's details in a string
     *
     * @return a string representing the contact
     */
    @NonNull
    @Override
    public String toString() {
        return "Contact{" +
                "name='" + name + '\'' +
                ", phoneNumbers=" + phoneNumbers +
                '}';
    }
}
