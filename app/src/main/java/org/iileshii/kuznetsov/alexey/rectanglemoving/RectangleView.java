package org.iileshii.kuznetsov.alexey.rectanglemoving;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;


/**
 * @class creates Custom View
 * Created by Alexey on 22.07.2015.
 */
public class RectangleView extends View {
    private static final int X_POINT_DP = 0;
    private static final int Y_POINT_DP = 0;

    private static final int WIDTH_DP = 400;
    private static final int HEIGHT_DP = 200;

    private static final String RECTSAGLEVIEW_TAG = "My view";

    private Paint paint;
    private Rect rectangle;

    private int currentX;
    private int currentY;


    public RectangleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setTag(RECTSAGLEVIEW_TAG);

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

    public void setRect(int dX, int dY) {
        int left = X_POINT_DP + dX;
        int right = X_POINT_DP + WIDTH_DP + dX;
        int top = Y_POINT_DP + dY;
        int bottom = Y_POINT_DP + HEIGHT_DP + dY;

        if (left < 0 || top < 0 || right > 720 || bottom > 1100) return;

        currentX = left;
        currentY = top;

        rectangle = new Rect(left, top, right, bottom);
    }

    public void setColor(int color) {
        paint.setColor(color);
    }

    public boolean isRectangle(int dX, int dY) {
        int right = currentX + WIDTH_DP;
        int bottom = currentY + HEIGHT_DP;
        boolean result;
        result = (dX >= currentX) && (dX <= right) && (dY >= currentY) && (dY <= bottom);
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(rectangle, paint);
    }

    private class CustomDragListener implements OnDragListener {
        int firstX, firstY;
        FrameLayout.LayoutParams layoutParams;

        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    firstX = (int) event.getX()
                            - ((RectangleView) v).getCurrentX() + layoutParams.leftMargin;
                    firstY = (int) event.getY()
                            - ((RectangleView) v).getCurrentY() + layoutParams.topMargin;
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:
                    ((RectangleView) v)
                            .setRect((int) event.getX() - firstX, (int) event.getY() - firstY);
                    setColor(Color.LTGRAY);
                    invalidate();
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    break;
                case DragEvent.ACTION_DROP:
                    ((RectangleView) v)
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
        Point fingerPosition0;
        Point fingerPosition1;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    fingerPosition0 = new Point((int) event.getX(), (int) event.getY());
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (isRectangle((int) event.getX(), (int) event.getY())) {
                        fingerPosition1 = new Point((int) event.getX(), (int) event.getY());
                        int dX = fingerPosition1.x - fingerPosition0.x;
                        int dY = fingerPosition1.y - fingerPosition0.y;
                        double distPosition = Math.sqrt(dX * dX + dY * dY);
                        if (distPosition > 1f) {
                            ClipData data = ClipData.newPlainText("", "");
                            DragShadowBuilder shadowBuilder = new DragShadowBuilder();
                            v.startDrag(data, shadowBuilder, v, 0);
                            v.setVisibility(VISIBLE);
                        }
                    }
                    break;
            }
            return true;
        }
    }
}
