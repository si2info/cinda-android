package info.si2.iista.volunteernetworks.util;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 28/12/15
 * Project: Shiari
 */
public class SpannableText extends ClickableSpan {

    private boolean isUnderline = true;

    /**
     * Constructor
     */
    public SpannableText(boolean isUnderline) {
        this.isUnderline = isUnderline;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setUnderlineText(isUnderline);
    }

    @Override
    public void onClick(View widget) {

    }

}
