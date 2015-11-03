package mc_sg.translocapp.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import mc_sg.translocapp.R;

public class ProgressCard extends RelativeLayout {

    String text;
    TextView tvText;

    public ProgressCard(Context context) {
        this(context, null);
    }

    public ProgressCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        inflater.inflate(R.layout.view_progress_card, this, true);

        // Get a searchable array of our custom style attributes
        TypedArray attributes = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ViewProgressCard,
                defStyleAttr,
                0);

        try {
            // Read our custom attributes
            text = attributes.getString(R.styleable.ViewProgressCard_text);
        }
        finally {
            // Free up shared resource
            attributes.recycle();
        }

        // Get our inner views
        tvText = (TextView) findViewById(R.id.routes_list_progress_text);
        tvText.setText(text);
    }

    public void setText(String text) {
        this.text = text;
        tvText.setText(text);
    }

    public String getText() {
        return text;
    }

}
