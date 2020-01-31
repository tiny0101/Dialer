package com.gidtech.dailingscreen.activity;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.transition.ChangeBounds;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.MediaRouteButton;
import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.telecom.Call;
import android.telecom.CallAudioState;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gidtech.dailingscreen.R;
import com.gidtech.dailingscreen.fragment.DialpadFragment;
import com.gidtech.dailingscreen.model.Contact;
import com.gidtech.dailingscreen.utils.CallCallBack;
import com.gidtech.dailingscreen.utils.CallManager;
import com.gidtech.dailingscreen.utils.StopClock;
import com.gidtech.dailingscreen.utils.Utils;
import com.gidtech.dailingscreen.viewModel.DialViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DailingActivity extends AppCompatActivity implements DialpadFragment.OnKeyDownListener{
//    CallCallBack mCallback;
    Callback mCallback = new Callback();
    AudioManager mAudioManager;
    CallAudioState mCallAudioState;

    // Handler variables
    private static final int TIME_START = 1;
    private static final int TIME_STOP = 0;
    private static final int TIME_UPDATE = 2;
    private static final int REFRESH_RATE = 100;

    // BottomSheet
    BottomSheetBehavior mBottomSheetBehavior;

    // Handlers
    Handler mCallTimeHandler = new CallTimeHandler();

    // PowerManager
    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;
    private int field = 0x00000020;

    StopClock mCallTimer = new StopClock();

    // Text views
    @BindView(R.id.text_status) TextView mStatusText;
    @BindView(R.id.text_caller) TextView mCallerText;
    @BindView(R.id.text_number) TextView mNumberText;
//    @BindView(R.id.text_timer_indicator) TextView mTimerIndicatorText;
    @BindView(R.id.text_stopwatch) TextView mTimeText;

    // Action buttons
    @BindView(R.id.answer_btn) FloatingActionButton mAnswerButton;
    @BindView(R.id.reject_btn) FloatingActionButton mRejectButton;

    // Image Views
    @BindView(R.id.image_placeholder)
    ImageView mPlaceholderImage;
//    @BindView(R.id.image_photo) ImageView mPhotoImage;
    @BindView(R.id.button_hold) ImageView mHoldButton;
    @BindView(R.id.button_mute) ImageView mMuteButton;
    @BindView(R.id.button_keypad) ImageView mKeypadButton;
    @BindView(R.id.button_speaker) ImageView mSpeakerButton;
    @BindView(R.id.button_add_call) ImageView mAddCallButton;
    @BindView(R.id.button_video_call) ImageView mVideoCallButtom;
//    @BindView(R.id.button_send_sms) Button mSendSmsButton;

    // Layouts and overlays
    @BindView(R.id.frame) ViewGroup mRootView;
    @BindView(R.id.dialer_fragment) View mDialerFrame;
    @BindView(R.id.ongoing_call_layout) ConstraintLayout mOngoingCallLayout;
    @Nullable
    ViewGroup mCurrentOverlay = null;
    boolean mIsCallingUI;
    boolean mIsCreatingUI;
    int mState;
    private DialpadFragment mDialpadFragment;
    private DialViewModel mDialViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dailing);
        Utils.setUpLocale(this);

        ButterKnife.bind(this);

        // This activity needs to show even if the screen is off or locked
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            if (km != null) {
                km.requestDismissKeyguard(this, null);
            }
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        // Display caller info
        displayInformation();

        // Initiate PowerManager and WakeLock (turn screen on/off according to distance from face)
        try {
            field = PowerManager.class.getField("PROXIMITY_SCREEN_OFF_WAKE_LOCK").getInt(null);
        } catch (Throwable ignored) {
        }
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(field, getLocalClassName());

//        Context mContext = getApplicationContext();

        // Audio Manager
        mAudioManager = (AudioManager) getApplicationContext().getSystemService(AUDIO_SERVICE);

        // Fragments
        mDialpadFragment = DialpadFragment.newInstance(false);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.dialer_fragment, mDialpadFragment)
                .commit();
        mDialpadFragment.setDigitsCanBeEdited(false);
//        mDialpadFragment.setShowVoicemailButton(false);
        mDialpadFragment.setOnKeyDownListener(this);

        View.OnClickListener rejectListener = v -> endCall();
        View.OnClickListener answerListener = v -> activateCall();
        View.OnClickListener recordListener = v -> recordCall();

        mAnswerButton.setOnClickListener(answerListener);
        mRejectButton.setOnClickListener(rejectListener);


        // Hide
//        hideOverlays();
//        hideButtons();

        // Instantiate ViewModels
        mDialViewModel = ViewModelProviders.of(this).get(DialViewModel.class);
        mDialViewModel.getNumber().observe(this, s -> {
            if (s != null && !s.isEmpty()) {
                char c = s.charAt(s.length() - 1);
                CallManager.keypad(c);
            }
        });

        // Bottom Sheet Behaviour
        mBottomSheetBehavior = BottomSheetBehavior.from(mDialerFrame); // Set the bottom sheet behaviour
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN); // Hide the bottom sheet


    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        //Listen for call state changes
        CallManager.registerCallback(mCallback);
//        mCallback.onStateChanged(CallManager.call,CallManager.getState());
        updateUI(CallManager.getState());
//        updateTimeUI();
    }

    /**
     * Update the current call time ui
     */
    private void updateTimeUI() {
        mTimeText.setText(mCallTimer.getStringTime());
    }
    /**
     * Updates the ui given the call state
     *
     * @param state the current call state
     */
    private void updateUI(int state) {
        @StringRes int statusTextRes;
        switch (state) {
            case Call.STATE_ACTIVE: // Ongoing
                statusTextRes = R.string.status_call_active;
                mCallTimeHandler.sendEmptyMessage(TIME_START); // Starts the call timer
                break;
            case Call.STATE_DISCONNECTED: // Ended
                statusTextRes = R.string.status_call_disconnected;
                break;
            case Call.STATE_RINGING: // Incoming
                statusTextRes = R.string.status_call_incoming;
                break;
            case Call.STATE_DIALING: // Outgoing
                statusTextRes = R.string.status_call_dialing;
                break;
            case Call.STATE_CONNECTING: // Connecting (probably outgoing)
                statusTextRes = R.string.status_call_dialing;
                break;
            case Call.STATE_HOLDING: // On Hold
                statusTextRes = R.string.status_call_holding;
                break;
            default:
                statusTextRes = R.string.status_call_active;
                break;
        }
        mStatusText.setText(statusTextRes);
        if (state != Call.STATE_RINGING && state != Call.STATE_DISCONNECTED) switchToCallingUI();
        if (state == Call.STATE_DISCONNECTED) endCall();
        mState = state;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mIsCreatingUI = false;
    }

    @Override
    protected void onDestroy() {

        CallManager.unregisterCallback(mCallback); //The activity is gone, no need to listen to changes
//        mActionTimer.cancel();
        releaseWakeLock();
        super.onDestroy();
    }

    /**
     * Turn on mute according to current state (if already on/off)
     *
     * @param view current view
     */
    @OnClick(R.id.button_mute)
    public void toggleMute(View view) {
        Utils.toggleViewActivation(view);

        if (mAudioManager.isMicrophoneMute()) {
            mMuteButton.setImageResource(R.drawable.ic_mic_off_black_24dp);
            mAudioManager.setMicrophoneMute(false);
            Toast.makeText(this, "Mute on: "+ mAudioManager.isMicrophoneMute(), Toast.LENGTH_SHORT).show();
        } else {
            mMuteButton.setImageResource(R.drawable.ic_mic_black_24dp);
            mAudioManager.setMicrophoneMute(true);
            Toast.makeText(this, "Mute on: "+ mAudioManager.isMicrophoneMute(), Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Turn speaker on/off according to current state
     *
     * @param view current view
     */
    @OnClick(R.id.button_speaker)
    public void toggleSpeaker(View view) {

        Utils.toggleViewActivation(view);
//        mAudioManager.setSpeakerphoneOn(view.isActivated());
//        mAudioManager.setMode(AudioManager.MODE_NORMAL);
        Log.d("Audio Speaker:",""+ mAudioManager.isSpeakerphoneOn());
        if(mAudioManager.isSpeakerphoneOn()){
            mAudioManager.setSpeakerphoneOn(false);
            mAudioManager.setMode(AudioManager.MODE_NORMAL);
            Toast.makeText(this, "Speaker Off", Toast.LENGTH_SHORT).show();
        }else{

            mAudioManager.setMode(AudioManager.MODE_IN_CALL);
//            mCallAudioState.
            mAudioManager.setSpeakerphoneOn(true);
            Log.d("Audio Speaker:","after press"+ mAudioManager.isSpeakerphoneOn());
            Toast.makeText(this, "Speaker On", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Puts the call on hold
     *
     * @param view current view
     */
    @OnClick(R.id.button_hold)
    public void toggleHold(View view) {
        Utils.toggleViewActivation(view);
        CallManager.hold(view.isActivated());
    }

    //TODO add functionality to the Keypad button
    @OnClick(R.id.button_keypad)
    public void toggleKeypad(View view) {
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @OnClick(R.id.button_add_call)
    public void addCall(View view) {
//        CallManager.addCall(// a call);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        mDialpadFragment.setDigitsCanBeEdited(true);
//        CallManager.addCall(new Call(CallManager.call(this,mDialpadFragment.)));
    }

    /**
     * Answers incoming call and change ui accordingly
     */
    private void activateCall() {
        CallManager.answer();
        if(CallManager.getState() == Call.STATE_ACTIVE) switchToCallingUI();
        //switchToCallingUI();
    }

    /**
     * Answers incoming call and change ui accordingly
     */
    private void recordCall() {
        //start and stop record call

    }

    /**
     * Switches the ui to an active call ui.
     */
    private void switchToCallingUI() {
        if (mIsCallingUI) return;
        else mIsCallingUI = true;
        mAudioManager.setMode(AudioManager.MODE_IN_CALL);
        acquireWakeLock();

        // Change the buttons layout
        mAnswerButton.hide();
        mHoldButton.setVisibility(View.VISIBLE);
        mMuteButton.setVisibility(View.VISIBLE);
        mKeypadButton.setVisibility(View.VISIBLE);
        mSpeakerButton.setVisibility(View.VISIBLE);
        mAddCallButton.setVisibility(View.VISIBLE);
        mVideoCallButtom.setVisibility(View.VISIBLE);
        moveRejectButtonToMiddle();
    }

    /**
     * Acquire the wake lock
     */
    private void acquireWakeLock() {
        if (!wakeLock.isHeld()) {
            wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
        }
    }

    /**
     * Moves the reject button to the middle
     */
    private void moveRejectButtonToMiddle() {
        ConstraintSet ongoingSet = new ConstraintSet();
        ongoingSet.clone(mOngoingCallLayout);
        ongoingSet.connect(R.id.reject_btn, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.END);
        ongoingSet.connect(R.id.reject_btn, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.START);
        ongoingSet.setHorizontalBias(R.id.reject_btn, 0.5f);

        ongoingSet.setMargin(R.id.reject_btn, ConstraintSet.END, 0);

        ConstraintSet overlaySet = new ConstraintSet();
//        overlaySet.clone(this, R.layout.correction_overlay_reject_call_options);

        if (!mIsCreatingUI) { //Don't animate if the activity is just being created
            Transition transition = new ChangeBounds();
            transition.setInterpolator(new AccelerateDecelerateInterpolator());
//            transition.addTarget(mRejectCallOverlay);
            transition.addTarget(mRejectButton);
            TransitionManager.beginDelayedTransition(mOngoingCallLayout, transition);
        }

        ongoingSet.applyTo(mOngoingCallLayout);


//        mRootView.removeView(mAnswerCallOverlay);
    }

    /**
     * End current call / Incoming call and change ui accordingly
     */
    private void endCall() {
        mCallTimeHandler.sendEmptyMessage(TIME_STOP);
        CallManager.reject();
        releaseWakeLock();
//        android.os.Process.killProcess(android.os.Process.myPid());
//        System.exit(1);

        mAudioManager.setMode(AudioManager.MODE_NORMAL);
        finish();
        moveTaskToBack(true);
    }

    /**
     * Releases the wake lock
     */
    private void releaseWakeLock() {
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    private void displayInformation() {
        // Display the information about the caller
        Contact callerContact = CallManager.getDisplayContact(this);
        if (callerContact != null) {
            mNumberText.setText(callerContact.getPhoneNumbers());
            mCallerText.setText(callerContact.getName());
            mPlaceholderImage.setVisibility(View.VISIBLE);

        } else {
            mCallerText.setText("UNKOWN NUMBER");
        }
    }

    @Override
    public void onKeyPressed(int keyCode, KeyEvent event) {
        CallManager.keypad((char) event.getUnicodeChar());
    }

    /**
     * Callback class
     * Listens to the call and do stuff when something changes
     */
    public class Callback extends Call.Callback {

        @Override
        public void onStateChanged(Call call, int state) {
            /*
              Call states:

              1   = Call.STATE_DIALING
              2   = Call.STATE_RINGING
              3   = Call.STATE_HOLDING
              4   = Call.STATE_ACTIVE
              7   = Call.STATE_DISCONNECTED
              8   = Call.STATE_SELECT_PHONE_ACCOUNT
              9   = Call.STATE_CONNECTING
              10  = Call.STATE_DISCONNECTING
              11  = Call.STATE_PULLING_CALL
             */
            super.onStateChanged(call, state);
            updateUI(state);
        }

        @Override
        public void onDetailsChanged(Call call, Call.Details details) {
            super.onDetailsChanged(call, details);
        }
    }

    @SuppressLint("HandlerLeak")
    class CallTimeHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TIME_START:
                    mCallTimer.start(); // Starts the timer
                    mCallTimeHandler.sendEmptyMessage(TIME_UPDATE); // Starts the time ui updates
                    break;
                case TIME_STOP:
                    mCallTimeHandler.removeMessages(TIME_UPDATE); // No more updates
                    mCallTimer.stop(); // Stops the timer
                    updateTimeUI(); // Updates the time ui
                    break;
                case TIME_UPDATE:
                    updateTimeUI(); // Updates the time ui
                    mCallTimeHandler.sendEmptyMessageDelayed(TIME_UPDATE, REFRESH_RATE); // Text view updates every milisecond (REFRESH RATE)
                    break;
                default:
                    break;
            }
        }
    }
}
