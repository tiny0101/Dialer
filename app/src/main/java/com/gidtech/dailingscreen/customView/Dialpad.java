package com.gidtech.dailingscreen.customView;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gidtech.dailingscreen.R;

import butterknife.BindView;

public class Dialpad extends LinearLayout {
    private static final String TAG = Dialpad.class.getSimpleName();

    @BindView(R.id.digits_edit_text) EditText mDigits;
    @BindView(R.id.button_delete) ImageButton mDelete;

    public Dialpad(Context context) {
        this(context, null);
    }

    public Dialpad(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Dialpad(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        setupKeypad();
        super.onFinishInflate();
    }

    private void setupKeypad() {

        final int[] buttonIds = new int[]{R.id.key_0, R.id.key_1, R.id.key_2, R.id.key_3, R.id.key_4,
                R.id.key_5, R.id.key_6, R.id.key_7, R.id.key_8, R.id.key_9, R.id.key_star, R.id.key_hex};

        final int[] numberIds = new int[]{R.string.dialpad_0_number, R.string.dialpad_1_number,
                R.string.dialpad_2_number, R.string.dialpad_3_number, R.string.dialpad_4_number,
                R.string.dialpad_5_number, R.string.dialpad_6_number, R.string.dialpad_7_number,
                R.string.dialpad_8_number, R.string.dialpad_9_number, R.string.dialpad_star_number,
                R.string.dialpad_pound_number};

        final int[] letterIds = new int[]{R.string.dialpad_0_letters, R.string.dialpad_1_letters,
                R.string.dialpad_2_letters, R.string.dialpad_3_letters, R.string.dialpad_4_letters,
                R.string.dialpad_5_letters, R.string.dialpad_6_letters, R.string.dialpad_7_letters,
                R.string.dialpad_8_letters, R.string.dialpad_9_letters,
                R.string.dialpad_star_letters, R.string.dialpad_pound_letters};

        final Resources resources = getContext().getResources();
        DialpadKeyButton dialpadKey;
        TextView numberView;
        TextView lettersView;

        for (int i = 0; i < buttonIds.length; i++) {
            dialpadKey = findViewById(buttonIds[i]);
            numberView = dialpadKey.findViewById(R.id.dialpad_key_number);
            lettersView = dialpadKey.findViewById(R.id.dialpad_key_letters);
            final String numberString = resources.getString(numberIds[i]);
            numberView.setText(numberString);
            numberView.setElegantTextHeight(false);
            if (lettersView != null) {
                lettersView.setText(resources.getString(letterIds[i]));
            }
        }

        setDigitsCanBeEdited(true);
    }

    public void setShowVoicemailButton(boolean show) {
        View view = findViewById(R.id.dialpad_key_voicemail);
        if (view != null) {
            view.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        }
    }

    /**
     * Whether or not the digits above the dialer can be edited.
     *
     * @param canBeEdited If true, the backspace button will be shown and the digits EditText
     *                    will be configured to allow text manipulation.
     */
    public void setDigitsCanBeEdited(boolean canBeEdited) {
        View deleteButton = findViewById(R.id.button_delete);
        deleteButton.setVisibility(canBeEdited ? View.VISIBLE : View.GONE);
        View callButton = findViewById(R.id.button_call);
        callButton.setVisibility(canBeEdited ? View.VISIBLE : View.GONE);
        EditText digits = (DigitsEditText) findViewById(R.id.digits_edit_text);
        digits.setClickable(canBeEdited);
        digits.setLongClickable(canBeEdited);
        digits.setFocusableInTouchMode(canBeEdited);
        digits.setCursorVisible(canBeEdited);
    }

    /**
     * Always returns true for onHoverEvent callbacks, to fix problems with accessibility due to
     * the dialpad overlaying other fragments.
     */
    @Override
    public boolean onHoverEvent(MotionEvent event) {
        return true;
    }
}
