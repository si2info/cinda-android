package info.si2.iista.volunteernetworks.util;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 7/9/15
 * Project: Virde
 */
public class ScrollAwareFABBehavior extends FloatingActionButton.Behavior {
    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    private boolean mIsAnimatingIn = false;
    private boolean mIsAnimatingOut = false;
    private boolean isShowing = true;

    @SuppressWarnings("UnusedParameters")
    public ScrollAwareFABBehavior(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public boolean onStartNestedScroll(final CoordinatorLayout coordinatorLayout, final FloatingActionButton child,
                                       final View directTargetChild, final View target, final int nestedScrollAxes) {
        // Ensure we react to vertical scrolling
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
                || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onNestedScroll(final CoordinatorLayout coordinatorLayout, final FloatingActionButton child,
                               final View target, final int dxConsumed, final int dyConsumed,
                               final int dxUnconsumed, final int dyUnconsumed) {

        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        if (dyConsumed > 3 && !this.mIsAnimatingOut && isShowing) {
            // User scrolled down and the FAB is currently visible -> hide the FAB
            animateOut(child);
        } else if (dyConsumed < -3 && !this.mIsAnimatingIn && !isShowing) {
            // User scrolled up and the FAB is currently not visible -> show the FAB
            animateIn(child);
        }
    }

    // Same animation that FloatingActionButton.Behavior uses to hide the FAB when the AppBarLayout exits
    private void animateOut(final FloatingActionButton button) {

        TranslateAnimation anim = new TranslateAnimation(0, 0, Animation.RELATIVE_TO_SELF, 220.0f);

        anim.setDuration(300L);
        anim.setFillAfter(true);
        anim.setFillEnabled(true);
        anim.setInterpolator(INTERPOLATOR);

        anim.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                mIsAnimatingOut = true;
            }

            public void onAnimationEnd(Animation animation) {
                mIsAnimatingOut = false;
                isShowing = false;
            }

            @Override
            public void onAnimationRepeat(final Animation animation) {
            }
        });

        button.startAnimation(anim);

    }

    // Same animation that FloatingActionButton.Behavior uses to show the FAB when the AppBarLayout enters
    public void animateIn(FloatingActionButton button) {

        TranslateAnimation anim = new TranslateAnimation(0, 0, 220.0f, Animation.RELATIVE_TO_SELF);

        anim.setDuration(300L);
        anim.setFillAfter(true);
        anim.setFillEnabled(true);
        anim.setInterpolator(INTERPOLATOR);

        anim.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                mIsAnimatingIn = true;
            }

            public void onAnimationEnd(Animation animation) {
                mIsAnimatingIn = false;
                isShowing = true;
            }

            @Override
            public void onAnimationRepeat(final Animation animation) {
            }
        });

        button.startAnimation(anim);

    }

    public boolean getIsShowing () {
        return isShowing;
    }

}