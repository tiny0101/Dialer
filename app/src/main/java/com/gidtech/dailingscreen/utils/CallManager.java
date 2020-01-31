package com.gidtech.dailingscreen.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telecom.Call;
import android.telecom.VideoProfile;
import android.util.Log;
import android.widget.Toast;

import com.gidtech.dailingscreen.activity.DailingActivity;
import com.gidtech.dailingscreen.model.Contact;

public class CallManager {
    public static Call call;

    /**
     * Answer incoming call
     * */
    public static void answer(){
        if(call != null){
            call.answer(VideoProfile.STATE_AUDIO_ONLY);
        }
    }

    /**
     * End Call
     * */
    public static void reject(){
        if(call != null) {
            if(call.getState() == Call.STATE_RINGING) {
                call.reject(false,null);
            }else {
                call.disconnect();
            }
        }
    }

    /**
     * Hold Call
     * @param hold Boolean
     * */
    public static void hold(boolean hold) {
        if(call != null) {
            if(hold) call.hold();
            else call.unhold();
        }
    }

    /**
     * Call a given number
     *
     * @param context
     * @param number
     */
    public static void call(Context context,String number) {
//        Timber.i("Trying to call: %s", number);
        String uri;
        try {
            // Set the data for the call
            if (number.contains("#")) uri = "tel: " + Uri.encode(number);
            else uri = "tel: " + number;
            Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(uri));
//            int simCard = getSimSelection(context);
//            if (simCard != -1) {
//                callIntent.putExtra("simSlot", simCard);
////                Timber.i("simCard %s", simCard);
//            }
            context.startActivity(callIntent); // Start the call
        } catch (SecurityException e) {
//            Timber.e(e, "Couldn't call %s", number);
        }
    }


    /**
     * Open keypad
     * @param digit keypad entry
     * */
    public static void keypad(char digit) {
        if(call != null) {
            call.playDtmfTone(digit);
            call.stopDtmfTone();
        }
    }

    /**
     * Add a call for conference
     * @param newCall new call to add
     * */
    public static void addCall(Call newCall) {
        if(call != null) {
            call.conference(newCall);
        }
    }

    /**
     * Registers a Callback object to the current call
     * @param callback the callback to register
     */
    public static void registerCallback(DailingActivity.Callback callback) {
        if (call == null) return;
        call.registerCallback(callback);
    }

    /**
     * Unregisters the Callback from the current call
     * @param callback the callback to unregister
     */
    public static void unregisterCallback(Call.Callback callback) {
        if (call == null) return;
        call.unregisterCallback(callback);
    }

    /**
     * Gets the phone number of the contact from the end side of the current call
     *
     * @return Contact - phone contact. if not recognized, return null.
     */
    public static Contact getDisplayContact(Context context) {

        String uri = null;

        if (call.getState() == Call.STATE_DIALING) {
            Toast.makeText(context, "Dialing", Toast.LENGTH_LONG).show();
        }

        if (call.getDetails().getHandle() != null) {
            //Get caller details
            uri = Uri.decode(call.getDetails().getHandle().toString());
            Log.i("Display Contact: %s", uri);
        }

        if (uri != null && uri.isEmpty()) return new Contact("UNKNOWN","UNKNOWN");

        // If uri contains 'voicemail' this is a... voicemail dah
//        if (uri.contains("voicemail")) return ContactUtils.VOICEMAIL;

        String telephoneNumber = null;

        // If uri contains 'tel' this is a normal number
        if(uri != null){
            if (uri.contains("tel:")) telephoneNumber = uri.replace("tel:", "");
        }


        if (telephoneNumber == null || telephoneNumber.isEmpty())
            return new Contact("UNKNOWN","UNKNOWN");

        if (telephoneNumber.contains(" ")) telephoneNumber = telephoneNumber.replace(" ", "");

        Contact contact = ContactUtils.getContactByPhoneNumber(context, telephoneNumber); // Get the contacts with the number
        Log.d("Contact",""+contact.toString());

        if (contact == null) {
            return new Contact(telephoneNumber, telephoneNumber);
        } else if (contact != null && contact.getName() == null){
            return new Contact(telephoneNumber, telephoneNumber);
        }
//        else if{
//            if(contact.getName().isEmpty()){
//                return new Contact(telephoneNumber, telephoneNumber);
//            }
//        }


        // No known contacts for the number, return the number

        return contact;
    }

    /**
     * Returnes the current state of the call from the Call object (named sCall)
     *
     * @return Call.State
     */
    public static int getState() {
        if (call == null) return Call.STATE_DISCONNECTED;
        return call.getState();
    }
}
