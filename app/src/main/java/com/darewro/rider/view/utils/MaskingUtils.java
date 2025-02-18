package com.darewro.rider.view.utils;

import com.redmadrobot.inputmask.helper.Mask;
import com.redmadrobot.inputmask.model.CaretString;

public class MaskingUtils {
    public static final String DEFAULT_CODE = "+92 3";
    public static final String MASK_PHONE = "{+92}{ 3}[00]{ }[0000000]";
    public static final String MASK_PHONE_2 = "+[0000000000000]";
    public static final String MASK_CNIC = "[00000]{-}[0000000]{-}[0]";

    public static String getMaskedText(String maskFormat, String input) {
        // Create the Mask instance
        final Mask mask = new Mask(maskFormat);

        // Apply the mask with the updated CaretString constructor
        final Mask.Result result = mask.apply(
                new CaretString(
                        input,
                        input.length(), // Set caret position at the end of the string
                        new CaretString.CaretGravity.FORWARD(true) // Instantiate FORWARD with autocomplete = true
                )
                // Enable autocompletion
        );

        // Return the formatted text
        return result.getFormattedText().getString();
    }
}
