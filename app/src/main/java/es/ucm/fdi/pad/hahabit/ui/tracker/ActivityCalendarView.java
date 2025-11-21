package es.ucm.fdi.pad.hahabit.ui.tracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.pad.hahabit.R;
import es.ucm.fdi.pad.hahabit.data.DayActivity;

public class ActivityCalendarView extends View {

    private List<DayActivity> activities = new ArrayList<>();
    private Paint paint;
    private float cellSize;
    private float cellSpacing;
    private int maxDays = 30;

    private int[] intensityColors = new int[5];

    public ActivityCalendarView(Context context) {
        super(context);
        init();
    }

    public ActivityCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cellSize = 36f;
        cellSpacing = 4f;

        intensityColors[0] = 0xFFEBEDF0;
        intensityColors[1] = 0xFF9BE9A8;
        intensityColors[2] = 0xFF40C463;
        intensityColors[3] = 0xFF30A14E;
        intensityColors[4] = 0xFF216E39;
    }

    public void setActivities(List<DayActivity> activities) {
        this.activities = activities;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int rows = 3;
        int cols = (int) Math.ceil(maxDays / (float) rows);

        int width = (int) ((cellSize + cellSpacing) * cols + cellSpacing);
        int height = (int) ((cellSize + cellSpacing) * rows + cellSpacing);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int rows = 3;
        int cols = (int) Math.ceil(maxDays / (float) rows);
        int activityIndex = 0;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (activityIndex >= maxDays) break;

                float left = cellSpacing + col * (cellSize + cellSpacing);
                float top = cellSpacing + row * (cellSize + cellSpacing);
                float right = left + cellSize;
                float bottom = top + cellSize;

                int intensity = 0;
                if (activityIndex < activities.size()) {
                    intensity = activities.get(activityIndex).getIntensityLevel();
                }

                paint.setColor(intensityColors[intensity]);
                RectF rect = new RectF(left, top, right, bottom);
                canvas.drawRoundRect(rect, 3f, 3f, paint);

                activityIndex++;
            }
        }
    }
}
