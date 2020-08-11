package com.comeaqui.eduardorodriguez.comeaqui.behaviors;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import static androidx.core.view.ViewCompat.TYPE_NON_TOUCH;

public class SlideHideBehavior extends CoordinatorLayout.Behavior<View> {

    private int mInitialOffset;
    private int cardDragDistance;
    private ObjectAnimator mAnimator;

    private static final int DIRECTION_UP = 1;
    private static final int DIRECTION_DOWN = -1;
    private int mScrollingDirection;


    private static OnBehaviorListener mListener;

    public SlideHideBehavior() {
    }
    public SlideHideBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static void setListener(OnBehaviorListener listener){
        mListener = listener;
    }


    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public boolean onLayoutChild(@NonNull CoordinatorLayout parent, @NonNull final View child, int layoutDirection) {

        child.post(() -> {
            mInitialOffset = (int) child.getY();
            child.setY(parent.getBottom());
            child.setVisibility(View.VISIBLE);
            parent.setVisibility(View.VISIBLE);
            child.animate().y(mInitialOffset).setDuration(child.getHeight() / 4);
        });

        child.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //System.out.println("Card Drag:" + cardDragDistance + " Child Height: " + v.getHeight() / 2 + " Child Y: " + child.getY() + " Offset:" + mInitialOffset);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        if (-cardDragDistance > v.getHeight() / 2) {
                            restartAnimator(v, parent.getHeight(), true, parent);
                        } else {
                            restartAnimator(v, mInitialOffset, false, parent);
                        }
                        break;
                }

                return false;
            }
        });

        return super.onLayoutChild(parent, child, layoutDirection);
    }


    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        if (dy > 0) {
            mScrollingDirection = DIRECTION_UP;
        } else if (dy < 0) {
            mScrollingDirection = DIRECTION_DOWN;
        }

        if (cardDragDistance < 0 && TYPE_NON_TOUCH != type) {
            consumed[1] = scroll(child, dy, mInitialOffset);
            cardDragDistance += consumed[1];
        }
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        if (mScrollingDirection == DIRECTION_DOWN && TYPE_NON_TOUCH != type){
            cardDragDistance += scroll(child, dyUnconsumed, mInitialOffset);
        }
    }

    @Override
    public boolean onNestedFling(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, float velocityX, float velocityY, boolean consumed) {
        if (cardDragDistance < 0 && velocityY <= -3000) {
            restartAnimator(child, coordinatorLayout.getHeight(), true, coordinatorLayout);
        }
        return false;
    }

    private int scroll(View child, int dy, int minOffset) {
        final int initialOffset = (int) child.getY();
        //Clamped new position - initial position = offset change
        int delta = clamp(initialOffset - dy, minOffset) - initialOffset;
        child.offsetTopAndBottom(delta);

        return -delta;
    }

    private int clamp(int value, int min) {
        if (value < min) {
            return min;
        } else {
            return value;
        }
    }


    private void restartAnimator(final View target, final float value, boolean close, CoordinatorLayout parent) {
        if (mAnimator != null) {
            mAnimator.cancel();
            mAnimator = null;
        }
        mAnimator = ObjectAnimator
                .ofFloat(target, View.Y, value)
                .setDuration(250);
        mAnimator.start();

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                cardDragDistance = 0;
                if (close){
                    mListener.onCloseBehavior();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public interface OnBehaviorListener {
        void onCloseBehavior();
    }
}