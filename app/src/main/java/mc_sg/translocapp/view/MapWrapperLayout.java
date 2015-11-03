package mc_sg.translocapp.view;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

public class MapWrapperLayout extends RelativeLayout {
    /**
     * Reference to a GoogleMap object 
     */
    private GoogleMap map;

    /**
     * Vertical offset in pixels between the bottom edge of our InfoWindow 
     * and the marker position (by default it's bottom edge too).
     * It's a good idea to use custom markers and also the InfoWindow frame, 
     * because we probably can't rely on the sizes of the default marker and frame. 
     */
    private int bottomOffsetPixels;

    /**
     * A currently selected marker 
     */
    private Marker marker;

    /**
     * Our custom view which is returned from either the InfoWindowAdapter.getInfoContents 
     * or InfoWindowAdapter.getInfoWindow
     */
    private View infoWindow;

    public MapWrapperLayout(Context context) {
        this(context, null);
    }

    public MapWrapperLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapWrapperLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Must be called before we can route the touch events
     */
    public void init(GoogleMap map, int bottomOffsetPixels) {
        this.map = map;
        this.bottomOffsetPixels = bottomOffsetPixels;
    }

    /**
     * Best to be called from either the InfoWindowAdapter.getInfoContents 
     * or InfoWindowAdapter.getInfoWindow. 
     */
    public void setMarkerWithInfoWindow(Marker marker, View infoWindow) {
        this.marker = marker;
        this.infoWindow = infoWindow;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean ret = false;
        // Make sure that the infoWindow is shown and we have all the needed references
        if (marker != null && marker.isInfoWindowShown() && map != null && infoWindow != null) {
            // Get a marker position on the screen
            Point point = map.getProjection().toScreenLocation(marker.getPosition());



            // Make a copy of the MotionEvent and adjust it's location
            // so it is relative to the infoWindow left top corner
            MotionEvent copyEv = MotionEvent.obtain(ev);
            copyEv.offsetLocation(
                    -point.x + (infoWindow.getWidth() / 2),
                    -point.y + infoWindow.getHeight() + bottomOffsetPixels);

            // Dispatch the adjusted MotionEvent to the infoWindow
            ret = infoWindow.dispatchTouchEvent(copyEv);
            Log.d(null, "info window touch event consumed: " + ret);
        }
        // If the infoWindow consumed the touch event, then just return true.
        // Otherwise pass this event to the super class and return it's result
        return ret || super.dispatchTouchEvent(ev);
    }

    public static abstract class OnInfoWindowElemTouchListener implements OnTouchListener {
        private final View view;
        private final Handler handler = new Handler();
        private boolean pressed = false;
        private Marker marker;

        public OnInfoWindowElemTouchListener(View view) {
            this.view = view;
        }

        public void setMarker(Marker marker) {
            this.marker = marker;
        }

        @Override
        public boolean onTouch(View vv, MotionEvent event) {
            if (0 <= event.getX() && event.getX() <= view.getWidth() &&
                    0 <= event.getY() && event.getY() <= view.getHeight())
            {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN: startPress(); break;

                    // We need to delay releasing of the view a little so it shows the pressed state on the screen
                    case MotionEvent.ACTION_UP: handler.postDelayed(confirmClickRunnable, 150); break;

                    case MotionEvent.ACTION_CANCEL: endPress(); break;
                    default: break;
                }
            }
            else {
                // If the touch goes outside of the view's area
                // (like when moving finger out of the pressed button)
                // just release the press
                endPress();
            }
            return false;
        }

        private void startPress() {
            if (!pressed) {
                pressed = true;
                handler.removeCallbacks(confirmClickRunnable);
            }
        }

        private boolean endPress() {
            if (pressed) {
                this.pressed = false;
                handler.removeCallbacks(confirmClickRunnable);
                return true;
            }
            else
                return false;
        }

        private final Runnable confirmClickRunnable = new Runnable() {
            public void run() {
                if (endPress()) {
                    onClickConfirmed(view, marker);
                }
            }
        };

        /**
         * This is called after a successful click
         */
        protected abstract void onClickConfirmed(View v, Marker marker);
    }
}