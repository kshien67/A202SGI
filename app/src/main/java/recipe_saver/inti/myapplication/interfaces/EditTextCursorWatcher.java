package recipe_saver.inti.myapplication.interfaces;

import android.content.Context;
import android.util.AttributeSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class EditTextCursorWatcher extends androidx.appcompat.widget.AppCompatEditText {
    private OnSelectionChangedListener selectionChangedListener;

    public EditTextCursorWatcher(Context context, AttributeSet attrs,
                                 int defStyle) {
        super(context, attrs, defStyle);
    }

    public EditTextCursorWatcher(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextCursorWatcher(Context context) {
        super(context);
    }

    public void setOnSelectionChangedListener(OnSelectionChangedListener listener) {
        this.selectionChangedListener = listener;
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        if (selectionChangedListener != null) {
            String text = getText().toString();
            Pattern pattern = Pattern.compile("\\[\\[.*?\\|.*?\\]\\]");
            Matcher matcher = pattern.matcher(text);

            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                int firstPipeIndex = text.indexOf('|', start);
                int secondPipeIndex = text.indexOf('|', firstPipeIndex + 1);

                if (firstPipeIndex != -1 && secondPipeIndex != -1 && secondPipeIndex < end && selStart > start && selStart < end) {
                    selectionChangedListener.onSelectionChanged(selStart, selEnd, firstPipeIndex, secondPipeIndex, end-1);
                    break;
                }
            }
        }
    }
}

