package org.iileshii.kuznetsov.alexey.rectanglemoving;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;


/**
 * Custom View
 * Created by Alexey on 22.07.2015.
 */
public class RectangleView extends View {
    // Set Rectangle Coordinates
    private static final int X_POINT_DP = 0;
    private static final int Y_POINT_DP = 0;

    private static final int WIDTH_DP = 400;
    private static final int HEIGHT_DP = 200;

    // View border params
    private static final int LEFT_VIEW_BORDER = 0;
    private static final int TOP_VIEW_BORDER = 0;
    private static int RIGHT_VIEW_BORDER = WIDTH_DP;
    private static int BOTTOM_VIEW_BORDER = HEIGHT_DP;

    // Drawing and its params
    private Paint paint;
    private Rect rectangle;

    //Start point of rectangle
    private int currentX;
    private int currentY;

    //Just constructor
    public RectangleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    //Set drawing params and Listeners to view
    private void init() {
        paint = new Paint();
        setColor(Color.GREEN);
        rectangle = new Rect();

        setRect(0, 0);

        setOnDragListener(new CustomDragListener());
        setOnTouchListener(new CustomTouchListener());
    }

    public int getCurrentX() {
        return currentX;
    }

    public int getCurrentY() {
        return currentY;
    }

    //Don't work if the rectangle is out of border
    public void setRect(int dX, int dY) {
        int left = X_POINT_DP + dX;
        int right = X_POINT_DP + WIDTH_DP + dX;
        int top = Y_POINT_DP + dY;
        int bottom = Y_POINT_DP + HEIGHT_DP + dY;

        if (left < LEFT_VIEW_BORDER || top < TOP_VIEW_BORDER || right > RIGHT_VIEW_BORDER || bottom > BOTTOM_VIEW_BORDER)
            return;

        currentX = left;
        currentY = top;

        rectangle = new Rect(left, top, right, bottom);
    }

    public void setColor(int color) {
        paint.setColor(color);
    }

    //Check point if it in the rectangle
    public boolean isRectangle(int dX, int dY) {
        int right = currentX + WIDTH_DP;
        int bottom = currentY + HEIGHT_DP;

        return (dX >= currentX) && (dX <= right) && (dY >= currentY) && (dY <= bottom);
    }

    //Determine borders of screen
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        RIGHT_VIEW_BORDER = getRight();
        BOTTOM_VIEW_BORDER = getBottom();
    }

    //Let's draw it!
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(rectangle, paint);
    }

    private class CustomDragListener implements OnDragListener {
        int firstX;
        int firstY;
        FrameLayout.LayoutParams layoutParams;

        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    firstX = (int) event.getX() //Check position of finger
                            - ((RectangleView) v).getCurrentX() + layoutParams.leftMargin;
                    firstY = (int) event.getY()
                            - ((RectangleView) v).getCurrentY() + layoutParams.topMargin;
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    setColor(Color.GREEN);
                    invalidate();
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:
                    ((RectangleView) v) //Draw grey rectangle follows finger
                            .setRect((int) event.getX() - firstX, (int) event.getY() - firstY);
                    setColor(Color.LTGRAY);
                    invalidate();
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    break;
                case DragEvent.ACTION_DROP:
                    ((RectangleView) v) //Settle rectangle here
                            .setRect((int) event.getX() - firstX, (int) event.getY() - firstY);
                    setColor(Color.GREEN);
                    invalidate();
                    break;
                default:
                    break;
            }
            return true;
        }
    }

    private class CustomTouchListener implements OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    // If a finger touches - do nothing
                    break;
                case MotionEvent.ACTION_MOVE:
                    // If a finger moving and it's in rectangle do start drag!
                    if (isRectangle((int) event.getX(), (int) event.getY())) {
                        ClipData data = ClipData.newPlainText("", "");
                        DragShadowBuilder shadowBuilder = new DragShadowBuilder();
                        v.startDrag(data, shadowBuilder, v, 0);
                        v.setVisibility(VISIBLE);
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    setColor(Color.GREEN);
                    break;
            }
            return true;
        }
    }
}
