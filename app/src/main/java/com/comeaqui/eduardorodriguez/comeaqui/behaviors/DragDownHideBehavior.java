package com.comeaqui.eduardorodriguez.comeaqui.behaviors;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class DragDownHideBehavior extends CoordinatorLayout.Behavior<View> {

    private int cardDragDistance;
    private ObjectAnimator mAnimator;

    private static OnBehaviorListener mListener;

    float initialY, dY;
    float IMAGE_SCROLL_ZISE = 228;

    public DragDownHideBehavior() {
    }
    public DragDownHideBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static void setListener(OnBehaviorListener listener){
        mListener = listener;
    }

    @Override
    public boolean onLayoutChild(@NonNull CoordinatorLayout parent, @NonNull final View child, int layoutDirection) {

        initialY = child.getTop();

        child.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //System.out.println("Card Drag:" + cardDragDistance + " Child Height: " + v.getHeight() / 2 + " Child Y: " + child.getY() + " Offset:" + mInitialOffset);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        movementLogic(v);
                        break;
                }

                return false;
            }
        });

        return super.onLayoutChild(parent, child, layoutDirection);
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull MotionEvent ev) {
        return touchLogic(child, ev);
    }

    @Override
    public boolean onTouchEvent(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull MotionEvent ev) {
        return touchLogic(child, ev);
    }

    float lastX, lastY;
    private boolean touchLogic(View child, MotionEvent ev){
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dY = child.getY() - ev.getRawY();
                lastX = ev.getX();
                lastY = ev.getY();

                System.out.println(lastY + "," + child.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                if(ev.getRawY() + dY > initialY) {
                    child.animate()
                            .y(ev.getRawY() + dY)
                            .setDuration(0)
                            .start();
                }

                if (curX != lastX){
                    if(child.getY() == initialY){
                        return false;
                    } else {
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                movementLogic(child);
                break;
            default:
                return false;
        }
        return false;
    }

    private void movementLogic(View child){
        if (child.getY() - initialY > child.getHeight() / 3){
            child.animate()
                    .y(child.getBottom() * 2)
                    .setDuration(100).withEndAction((
            ) -> {
                child.setVisibility(View.GONE);
                mListener.onCloseBehavior();
            }).start();
        } else {
            child.animate()
                    .y(initialY)
                    .setDuration(100)
                    .start();
        }
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        return true;
    }

    @Override
    public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int type) {
        movementLogic(child);
    }


    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        System.out.println(dxConsumed + "," + dxUnconsumed);
    }

    @Override
    public boolean onNestedPreFling(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, float velocityX, float velocityY) {
        return true;
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
                    parent.setBackgroundColor(Color.TRANSPARENT);
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