package de.schiewe.volker.bgjugend2016.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ScrollView;

import de.schiewe.volker.bgjugend2016.helper.AppPersist;
import de.schiewe.volker.bgjugend2016.R;
import de.schiewe.volker.bgjugend2016.activities.MainActivity;
import de.schiewe.volker.bgjugend2016.fragments.EventFragment;
import de.schiewe.volker.bgjugend2016.helper.FirebaseHandler;

/**
 * Scrollview with right & left Swipe
 */
public class SwipeScrollView extends ScrollView {
    private static final String TAG = "SwipeScrollView";
    private GestureDetector gestureDetector;
    private AppPersist app;
    private FirebaseHandler firebaseHandler;
    private FragmentManager fragManager;
    private int position;

    public SwipeScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        AppCompatActivity activity;
        try {
            activity = (AppCompatActivity) context;
        } catch (ClassCastException ex) {
            Log.e(TAG, "SwipeScrollView: Not used AppCompatActivity?", ex);
            return;
        }
        fragManager = activity.getSupportFragmentManager();
        app = AppPersist.getInstance();
        firebaseHandler = FirebaseHandler.getInstance(context);
        position = firebaseHandler.getEvents().indexOf(app.getCurrEvent());
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        return gestureDetector.onTouchEvent(ev);
    }

    private void onSwipeLeft() {
        if (position < firebaseHandler.getEvents().size() - 1) {
            app.setCurrEvent(position + 1);
            EventFragment eventFragment = new EventFragment();

            fragManager.beginTransaction()
                    .setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out)
//                    .addToBackStack(null)
                    .replace(R.id.container, eventFragment, MainActivity.EVENT_FRAGMENT)
                    .commit();
        }
    }

    private void onSwipeRight() {
        if (position > 0) {
            app.setCurrEvent(position - 1);
            EventFragment eventFragment = new EventFragment();
            fragManager.beginTransaction()
                    .setCustomAnimations(R.anim.push_right_in, R.anim.push_right_out)
//                    .addToBackStack(null)
                    .replace(R.id.container, eventFragment, MainActivity.EVENT_FRAGMENT)
                    .commit();
        }
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_DISTANCE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float distanceX = e2.getX() - e1.getX();
            float distanceY = e2.getY() - e1.getY();
            if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (distanceX > 0)
                    onSwipeRight();
                else
                    onSwipeLeft();
                return true;
            }
            return false;
        }
    }

}
