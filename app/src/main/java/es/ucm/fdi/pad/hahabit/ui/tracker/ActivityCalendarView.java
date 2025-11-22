package es.ucm.fdi.pad.hahabit.ui.tracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.pad.hahabit.data.DayActivity;

public class ActivityCalendarView extends View {

    private List<DayActivity> activities = new ArrayList<>();
    private Paint paint;
    private float cellSize;
    private float cellSpacing;
    private int maxDays = 30;
    private int rows = 3;

    private int[] intensityColors = new int[5];

    public ActivityCalendarView(Context context) {
        super(context);
        init(context);
    }

    public ActivityCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ActivityCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        // Convertir dp a pixels para el spacing
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        cellSpacing = 3 * metrics.density;

        // Colores de intensidad (estilo GitHub)
        intensityColors[0] = 0xFFEBEDF0; // Sin actividad
        intensityColors[1] = 0xFF9BE9A8; // Baja
        intensityColors[2] = 0xFF40C463; // Media-baja
        intensityColors[3] = 0xFF30A14E; // Media-alta
        intensityColors[4] = 0xFF216E39; // Alta
    }

    public void setActivities(List<DayActivity> activities) {
        this.activities = activities != null ? activities : new ArrayList<>();
        requestLayout();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int cols = (int) Math.ceil(maxDays / (float) rows);

        // Calcular cellSize basado en el ancho disponible
        int availableWidth;
        if (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST) {
            availableWidth = widthSize - getPaddingLeft() - getPaddingRight();
        } else {
            // Si no hay restricción, usar un tamaño por defecto
            availableWidth = (int) (300 * getResources().getDisplayMetrics().density);
        }

        // cellSize = (availableWidth - (cols + 1) * spacing) / cols
        cellSize = (availableWidth - (cols + 1) * cellSpacing) / cols;

        // Limitar tamaño mínimo y máximo
        float minCellSize = 8 * getResources().getDisplayMetrics().density;
        float maxCellSize = 20 * getResources().getDisplayMetrics().density;
        cellSize = Math.max(minCellSize, Math.min(maxCellSize, cellSize));

        int width = (int) ((cellSize + cellSpacing) * cols + cellSpacing);
        int height = (int) ((cellSize + cellSpacing) * rows + cellSpacing);

        setMeasuredDimension(
                resolveSize(width, widthMeasureSpec),
                resolveSize(height, heightMeasureSpec)
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int cols = (int) Math.ceil(maxDays / (float) rows);
        int activityIndex = 0;

        // Centrar horizontalmente
        float totalWidth = cols * (cellSize + cellSpacing) + cellSpacing;
        float startX = (getWidth() - totalWidth) / 2f + cellSpacing;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (activityIndex >= maxDays) break;

                float left = startX + col * (cellSize + cellSpacing);
                float top = cellSpacing + row * (cellSize + cellSpacing);
                float right = left + cellSize;
                float bottom = top + cellSize;

                int intensity = 0;
                if (activityIndex < activities.size()) {
                    intensity = activities.get(activityIndex).getIntensityLevel();
                }

                paint.setColor(intensityColors[intensity]);
                float cornerRadius = cellSize * 0.15f;
                RectF rect = new RectF(left, top, right, bottom);
                canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint);

                activityIndex++;
            }
        }
    }
}
