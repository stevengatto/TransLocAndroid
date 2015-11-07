package mc_sg.translocapp.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import mc_sg.translocapp.R;

public class ArrivalEstimateView extends FrameLayout {

    String text;
    TextView tvName;
    TextView tvRealTime;
    TextView tvTimeLeft;

    public ArrivalEstimateView(Context context) {
        this(context, null);
    }

    public ArrivalEstimateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArrivalEstimateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.view_arrival_estimates, this, true);

        // Get our inner views
        tvName = (TextView) view.findViewById(R.id.arr_est_stop_name);
        tvRealTime = (TextView) view.findViewById(R.id.arr_est_real_time);
        tvTimeLeft = (TextView) view.findViewById(R.id.arr_est_time_left);
    }

    public void setTimeLeft(String timeLeft) {
        tvTimeLeft.setText(timeLeft);
    }

    public void setRealTime(String realTime) {
        tvRealTime.setText(realTime);
    }

    public void setName(String name) {
        tvName.setText(name);
    }


}
