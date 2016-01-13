package info.si2.iista.volunteernetworks;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.format.Time;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.WindowInsets;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import info.si2.iista.volunteernetworks.Objects.Campaigns;
import info.si2.iista.volunteernetworks.Objects.Timeline;
import info.si2.iista.volunteernetworks.Objects.Volunteers;

/**
 * Digital watch face with seconds. In ambient mode, the seconds aren't displayed. On devices with
 * low-bit ambient mode, the text is drawn without anti-aliasing in ambient mode.
 */
public class MainActivity extends CanvasWatchFaceService {

    // Arrays for campaigns, volunteers and timeline
    List<Campaigns> campaigns;
    List<Volunteers> volunteers;
    List<Timeline> timeline;

    float startAngle;
    String server;
    int canvasH;
    int canvasW;

    private static final Typeface NORMAL_TYPEFACE =
            Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);

    /**
     * Update rate in milliseconds for interactive mode. We update once a second since seconds are
     * displayed in interactive mode.
     */
    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);

    /**
     * Handler message id for updating the time periodically in interactive mode.
     */
    private static final int MSG_UPDATE_TIME = 0;

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private class Engine extends CanvasWatchFaceService.Engine {
        final Handler mUpdateTimeHandler = new EngineHandler(this);

        final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mTime.clear(intent.getStringExtra("time-zone"));
                mTime.setToNow();
            }
        };
        boolean mRegisteredTimeZoneReceiver = false;

        Paint mBackgroundPaint;
        Paint mBackgroundTextPaint;

        Paint mTextPaint;
        Paint mHourTextPaint;
        Paint mDateTextPaint;
        Paint mTouchCirclePaint;
        Paint mServerTextPaint;
        Paint mImageLogo;

        Paint mBackgroundArcPaint;
        Paint mGreenArcPaint;
        Paint mHourArcPaint;
        Paint mShadowArcPaint;

        Paint mLineAmbientPaint;
        Paint mTriangle;
        Paint mPoint;
        Paint mPointS;

        boolean mAmbient;
        boolean mTouch;

        Time mTime;
        int mTouchX, mTouchY;

        float mXOffset;
        float mYOffset;

        /**
         * Whether the display supports fewer bits for each color in ambient mode. When true, we
         * disable anti-aliasing in ambient mode.
         */
        boolean mLowBitAmbient;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            // Register the local broadcast receiver, defined in step 3.
            IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
            MessageReceiver messageReceiver = new MessageReceiver();
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(messageReceiver, messageFilter);

            setWatchFaceStyle(new WatchFaceStyle.Builder(MainActivity.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_VARIABLE)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .setAcceptsTapEvents(true)
                    .build());
            Resources resources = MainActivity.this.getResources();

            server = "";

            campaigns = new ArrayList<>();
            volunteers = new ArrayList<>();
            timeline = new ArrayList<>();

            mYOffset = resources.getDimension(R.dimen.digital_y_offset);

            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(resources.getColor(R.color.background));

            mBackgroundTextPaint = new Paint();
            mBackgroundTextPaint.setColor(resources.getColor(R.color.white));
            mBackgroundTextPaint.setAntiAlias(true);

            mTextPaint = new Paint();
            mTextPaint = createTextPaint(resources.getColor(R.color.background));
            mTextPaint.setTextSize(21);

            mHourTextPaint = new Paint();
            mHourTextPaint = createTextPaint(resources.getColor(R.color.digital_text));
            mHourTextPaint.setTextAlign(Paint.Align.CENTER);
            mHourTextPaint.setFakeBoldText(true);
            mHourTextPaint.setTextSize(23);

            mDateTextPaint = new Paint();
            mDateTextPaint = createTextPaint(resources.getColor(R.color.digital_text));
            mDateTextPaint.setTextAlign(Paint.Align.CENTER);
            mDateTextPaint.setTextSize(20);

            mServerTextPaint = new Paint();
            mServerTextPaint = createTextPaint(resources.getColor(R.color.digital_text));
            mServerTextPaint.setTextSize(10);
            mServerTextPaint.setTextAlign(Paint.Align.CENTER);

            mImageLogo = new Paint();
            mImageLogo.setAntiAlias(true);

            mTouch = false;
            mTouchCirclePaint = new Paint();
            mTouchCirclePaint.setColor(resources.getColor(R.color.accent));
            mTouchCirclePaint.setAlpha(180);

            mBackgroundArcPaint = new Paint();
            mBackgroundArcPaint.setColor(resources.getColor(R.color.white));
            mBackgroundArcPaint.setAntiAlias(true);
            mBackgroundArcPaint.setStyle(Paint.Style.STROKE);
            mBackgroundArcPaint.setStrokeWidth(20);

            mGreenArcPaint = new Paint();
            mGreenArcPaint.setColor(resources.getColor(R.color.dark_green));
            mGreenArcPaint.setAntiAlias(true);
            mGreenArcPaint.setStyle(Paint.Style.STROKE);
            mGreenArcPaint.setStrokeWidth(30);

            mHourArcPaint = new Paint();
            mHourArcPaint.setColor(resources.getColor(R.color.green));
            mHourArcPaint.setAntiAlias(true);
            mHourArcPaint.setShadowLayer(2.5f, 0.0f, 0.0f, resources.getColor(R.color.semitransparent_grey));

            mShadowArcPaint = new Paint();
            mShadowArcPaint.setColor(resources.getColor(R.color.shadow_green));
            mShadowArcPaint.setAntiAlias(true);

            mLineAmbientPaint = new Paint();
            mLineAmbientPaint.setColor(resources.getColor(R.color.dark_green));
            mLineAmbientPaint.setStrokeWidth(2);

            mTriangle = new Paint();
            mTriangle.setStrokeWidth(1);
            mTriangle.setAntiAlias(true);
            mTriangle.setStrokeCap(Paint.Cap.SQUARE);
            mTriangle.setStyle(Paint.Style.FILL);
            mTriangle.setColor(getResources().getColor(R.color.shadow_green));

            mPoint = new Paint();
            mPoint.setStyle(Paint.Style.FILL);
            mPoint.setAntiAlias(true);
            mPoint.setColor(getResources().getColor(R.color.white));

            mPointS = new Paint();
            mPointS.setStyle(Paint.Style.STROKE);
            mPointS.setStrokeWidth(2);
            mPointS.setAntiAlias(true);
            mPointS.setColor(getResources().getColor(R.color.dark_green));

            mTime = new Time();
        }

        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            super.onDestroy();
        }

        private Paint createTextPaint(int textColor) {
            Paint paint = new Paint();
            paint.setColor(textColor);
            paint.setTypeface(NORMAL_TYPEFACE);
            paint.setAntiAlias(true);
            return paint;
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                registerReceiver();

                // Update time zone in case it changed while we weren't visible.
                mTime.clear(TimeZone.getDefault().getID());
                mTime.setToNow();
            } else {
                unregisterReceiver();
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            MainActivity.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            MainActivity.this.unregisterReceiver(mTimeZoneReceiver);
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            super.onApplyWindowInsets(insets);

            // Load resources that have alternate values for round watches.
            Resources resources = MainActivity.this.getResources();
            boolean isRound = insets.isRound();

            mXOffset = resources.getDimension(R.dimen.digital_x_offset);

            float textSize = resources.getDimension(isRound
                    ? R.dimen.digital_text_size_round : R.dimen.digital_text_size);

            mHourTextPaint.setTextSize(textSize);
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            mAmbient = isInAmbientMode();

            if (mAmbient != inAmbientMode) {
                if (mLowBitAmbient) {
                    // mHourTextPaint.setAntiAlias(!inAmbientMode);
                    mBackgroundPaint.setAlpha(250);
                    mHourTextPaint.setAntiAlias(true);
                    mDateTextPaint.setAntiAlias(true);
                }
                invalidate();
            }
            else {
                mBackgroundPaint.setAlpha(0);

            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        /**
         * Captures tap event (and tap type) and toggles the background color if the user finishes
         * a tap.
         */
        @Override
        public void onTapCommand(int tapType, int x, int y, long eventTime) {
            switch (tapType) {
                case TAP_TYPE_TOUCH:
                    // The user has started touching the screen.
                    mTouch = true;
                    mTouchX = x;
                    mTouchY = y;

                    break;
                case TAP_TYPE_TOUCH_CANCEL:
                    // The user has started a different gesture or otherwise cancelled the tap.
                    mTouch = false;
                    break;
                case TAP_TYPE_TAP:
                    // The user has completed the tap gesture.
                    mTouch = false;

                    break;
            }
            invalidate();
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            // Obtain height and width of canvas.
            canvasH = canvas.getHeight();
            canvasW = canvas.getWidth();

            // Draw the background.
            if (isInAmbientMode()) {
                canvas.drawColor(Color.BLACK);
            } else {
                canvas.drawColor(Color.BLACK);
            }

            // Draw Campaigns
            canvas.drawArc(16,16,304,304,0,360,true, mBackgroundArcPaint);

            startAngle = 270;
            for (Campaigns c : campaigns) {
                if (startAngle > 360)
                    startAngle -= 360;
                Paint p = new Paint();
                p.setColor(Color.parseColor(c.getColor()));
                p.setStyle(Paint.Style.FILL);
                p.setAntiAlias(true);
                p.setShadowLayer(8.5f, 1.0f, 0.0f, Color.BLACK);

                canvas.drawArc(0,0,320,320, startAngle, c.getAngle(), true, p);
                startAngle += c.getAngle();
            }

            // Draw center arcs
            canvas.drawArc(54, 54, 266, 266, 0, 360, true, mHourArcPaint);
            canvas.drawArc(55, canvasH / 2, canvasW - 65, canvasH -40, 0, 180, false, mShadowArcPaint);

            // Draw Volunteers
            canvas.drawArc(40, 40, 280, 280, 300, 360, true, mGreenArcPaint);

            startAngle = 270;
            for (int i = 0; i< volunteers.size(); i++) {
                if (startAngle > 360)
                    startAngle -= 360;
                Paint p = new Paint();
                p.setColor(Color.rgb(10,i*20,10));
                p.setAntiAlias(true);
                p.setAlpha(200-((i+5) * 16));

                canvas.drawArc(24, 25, 295, 295, startAngle, volunteers.get(i).getAngle(), true, p);
                startAngle += volunteers.get(i).getAngle();
            }

            // Draw Timeline
            Boolean notEmpty = false;
            List<Integer> timeline_contribution = new ArrayList<>();
            List<Float> contributionTimeLine0_20;
            int max = 0;

            for (Timeline t : timeline) {

                timeline_contribution.add(t.getContributions());
                if (t.getContributions() > 0) {
                    notEmpty = true;
                }

                if (t.getContributions() > max)
                    max = t.getContributions();
            }

            contributionTimeLine0_20 = normalize0_20(timeline_contribution, max);

            if (notEmpty) {
                canvas.drawArc(55, 55, 265, 265, 0, 360, true, mHourArcPaint);
                canvas.drawArc(83, canvasH / 2 + 14, canvasW - 83, canvasH - 56, 0, 180, false, mShadowArcPaint);

                canvas.drawArc(76, canvasH / 2 + 33, canvasW/2+23, canvasH - 77, 0, 180, false, mShadowArcPaint);
                canvas.drawArc(canvasW/2, canvasH / 2 + 33   , canvasW-76, canvasH - 77, 0, 180, false, mShadowArcPaint);


                for (int i=0 ; i< contributionTimeLine0_20.size(); i++) {

                    Path path = new Path();
                    float xstart = (((canvasW / 8 - 5) + 55+i*26) + ((canvasW / 8 + 5) + 55+i*26)) / 2;
                    float ystart = (((canvasH - ((canvasH / 3) - 5)) - 5) - contributionTimeLine0_20.get(i) + ((canvasH - ((canvasH / 3) - 5)) + 5) - contributionTimeLine0_20.get(i)) / 2;
                    float xend;
                    float yend;

                    if (i == 0) {
                        // Initial point
                        xend = (((canvasW / 8 - 5) + 55+(i)*26) + ((canvasW / 8 + 5) + 55+(i)*26)) / 2;
                        yend = (((canvasH - ((canvasH / 3) - 5)) - 5) - contributionTimeLine0_20.get(i) + ((canvasH - ((canvasH / 3) - 5)) + 5) - contributionTimeLine0_20.get(i)) / 2;

                        path.moveTo(73, canvasH - (canvasH / 3) + 5);
                        path.lineTo(73, canvasH - (canvasH / 3) + 5);
                        path.lineTo(xend, yend);
                        path.lineTo(xend, canvasH - (canvasH / 3) + 5);
                        path.close();
                        canvas.drawPath(path,mTriangle);

                        path.reset();
                        path.moveTo(xstart, ystart);
                        path.lineTo(xstart, ystart);
                        path.lineTo(xend, yend);
                        path.lineTo(xend, canvasH - (canvasH / 3) + 5);
                        path.close();
                        canvas.drawPath(path,mTriangle);

                        canvas.drawLine(73, canvasH - (canvasH / 3) + 5, xend, yend, mLineAmbientPaint);
                        canvas.drawLine(xstart, ystart, xend, yend, mLineAmbientPaint);
                    }
                    if (i == contributionTimeLine0_20.size()-1) {
                        // Final point
                        xend = canvasW - 73;
                        yend = canvasH - (canvasH / 3) + 5;

                        path.moveTo(xstart, ystart);
                        path.lineTo(xstart, ystart);
                        path.lineTo(xend, yend);
                        path.lineTo(xend, canvasH - (canvasH / 3) + 5);
                        path.close();
                        canvas.drawPath(path,mTriangle);

                        canvas.drawLine(xstart, ystart, xend, yend, mLineAmbientPaint);
                    }
                    else {
                        // Middle points
                        xend = (((canvasW / 8 - 5) + 55+(i+1)*26) + ((canvasW / 8 + 5) + 55+(i+1)*26)) / 2;
                        yend = (((canvasH - ((canvasH / 3) - 5)) - 5) - contributionTimeLine0_20.get(i+1) + ((canvasH - ((canvasH / 3) - 5)) + 5) - contributionTimeLine0_20.get(i+1)) / 2;

                        path.moveTo(xstart, ystart);
                        path.lineTo(xstart, ystart);
                        path.lineTo(xend, yend);
                        path.lineTo(xend, canvasH - (canvasH / 3) + 5);
                        path.close();
                        canvas.drawPath(path,mTriangle);

                        path.reset();
                        path.moveTo(xstart, ystart);
                        path.lineTo(xstart, ystart);
                        path.lineTo(xend, yend);
                        path.lineTo(xstart, canvasH - (canvasH / 3) + 5);
                        path.close();
                        canvas.drawPath(path,mTriangle);

                        canvas.drawLine(xstart, ystart, xend, yend, mLineAmbientPaint);
                    }

                    canvas.drawArc((canvasW / 8 - 5) + 55+i*26, ((canvasH - ((canvasH / 3) - 5)) - 5) - contributionTimeLine0_20.get(i),
                            (canvasW / 8 + 5) + 55+i*26, ((canvasH - ((canvasH / 3) - 5)) + 5) - contributionTimeLine0_20.get(i),
                            0, 360, true, mPoint);

                    canvas.drawArc((canvasW / 8 - 5) + 55+i*26, ((canvasH - ((canvasH / 3) - 5)) - 5) - contributionTimeLine0_20.get(i),
                            (canvasW / 8 + 5) + 55+i*26, ((canvasH - ((canvasH / 3) - 5)) + 5) - contributionTimeLine0_20.get(i),
                            0, 360, true, mPointS);
                }

            }
            else {
                // If all contributions are 0 -> draw one line and a point.
                canvas.drawLine(50, canvasH - (canvasH / 3) + 5, canvasW - 50,
                        canvasH - (canvasH / 3) + 5, mLineAmbientPaint);

                canvas.drawArc(canvasW / 2 - 5, ((canvasH - ((canvasH / 3) - 5)) - 5),
                        canvasW / 2 + 5, ((canvasH - ((canvasH / 3) - 5)) + 5),
                        0, 360, true, mPoint);

                canvas.drawArc(canvasW / 2 - 5, ((canvasH - ((canvasH / 3) - 5)) - 5),
                        canvasW / 2 + 5, ((canvasH - ((canvasH / 3) - 5)) + 5),
                        0, 360, true, mPointS);
            }


            // Logo, Hour, Date and Server
            Resources res = getResources();
            Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.logo);
            Bitmap scaledBitmap = scaleDown(bitmap, 48, true);
            canvas.drawBitmap(scaledBitmap, canvasW/2-20, 65, mImageLogo);

            if (!mAmbient)
                mBackgroundPaint.setAlpha(0);
            else
                mBackgroundPaint.setAlpha(250);

            canvas.drawRect(0,0,canvasW, canvasH, mBackgroundPaint);

            mTime.setToNow();
            String hour = String.format("%d:%02d", mTime.hour, mTime.minute);
            canvas.drawText(hour, canvasW/2, canvasH/2, mHourTextPaint);

            String date = String.format("%s %d, %d", getDay(mTime.weekDay), mTime.monthDay, mTime.year);
            canvas.drawText(date, canvasW/2, canvasH/2 + 25, mDateTextPaint);

            canvas.drawText(server, canvasW/2, canvasH-75, mServerTextPaint);

            // Touch arc draw
            if (mTouch)
                canvas.drawCircle(mTouchX,mTouchY, 30, mTouchCirclePaint);

        }

        /**
         * Starts the {@link #mUpdateTimeHandler} timer if it should be running and isn't currently
         * or stops it if it shouldn't be running but currently is.
         */
        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        /**
         * Returns whether the {@link #mUpdateTimeHandler} timer should be running. The timer should
         * only run when we're visible and in interactive mode.
         */
        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        /**
         * Handle updating the time periodically in interactive mode.
         */
        private void handleUpdateTimeMessage() {
            invalidate();
            if (shouldTimerBeRunning()) {
                long timeMs = System.currentTimeMillis();
                long delayMs = INTERACTIVE_UPDATE_RATE_MS
                        - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
            }
        }
    }

    private static class EngineHandler extends Handler {
        private final WeakReference<MainActivity.Engine> mWeakReference;

        public EngineHandler(MainActivity.Engine reference) {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity.Engine engine = mWeakReference.get();
            if (engine != null) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        engine.handleUpdateTimeMessage();
                        break;
                }
            }
        }
    }

    public String getDay(int d) {
        String day = " ";

        switch (d) {
            case 1:
                day = getResources().getString(R.string.monday);
                break;
            case 2:
                day = getResources().getString(R.string.tuesday);
                break;
            case 3:
                day = getResources().getString(R.string.wednesday);
                break;
            case 4:
                day = getResources().getString(R.string.thursday);
                break;
            case 5:
                day = getResources().getString(R.string.friday);
                break;
            case 6:
                day = getResources().getString(R.string.saturday);
                break;
            case 0:
                day = getResources().getString(R.string.sunday);
                break;
        }

        return day;
    }

    private List<Float> normalize0_20(List<Integer> timeline_contribution, int max) {

        List<Float> contribution = new ArrayList<>();
        List<Float> contribution_final = new ArrayList<>();

        // Normalize [0,1]
        for (float i : timeline_contribution) {
            contribution.add(i/max);
        }

        // Scale [0,20]
        for (int i=0; i<contribution.size(); i++) {
            contribution_final.add(contribution.get(i) * 20);
        }

        return contribution_final;
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle data = intent.getBundleExtra("datamap");

            server = data.getString("serverName");
            String json = data.getString("json");

            try {
                JSONObject object = new JSONObject(json);

                JSONArray json_campaigns = new JSONArray(object.getString("campaigns"));
                JSONArray json_volunteers = new JSONArray(object.getString("volunteers"));
                JSONArray json_timeline = new JSONArray(object.getString("timeline"));

                // Campaigns
                campaigns.clear();
                for (int i=0; i<json_campaigns.length(); i++) {
                    JSONObject obj = json_campaigns.getJSONObject(i);

                    campaigns.add(new Campaigns(obj.getString("id"), obj.getString("name"),
                            obj.getInt("suscriptions"), obj.getInt("angle"),
                            obj.getString("color")));
                }

                // Volunteers
                volunteers.clear();
                for (int i=0; i<json_volunteers.length(); i++) {
                    JSONObject obj = json_volunteers.getJSONObject(i);

                    volunteers.add(new Volunteers(obj.getString("id"), obj.getString("name"),
                            obj.getInt("contributions"), obj.getInt("angle")));
                }

                // Timeline
                timeline.clear();
                for (int i=0; i<json_timeline.length(); i++) {
                    JSONObject obj = json_timeline.getJSONObject(i);

                    timeline.add(new Timeline(obj.getString("month"), obj.getInt("contributions")));
                }

            } catch (JSONException e) {
                Log.e("Json parse", e.toString());
                e.printStackTrace();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    // Scale bitmap image
    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }
}
