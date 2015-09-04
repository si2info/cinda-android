package info.si2.iista.volunteernetworks;

import android.os.Bundle;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import info.si2.iista.bolunteernetworks.apiclient.Item;
import info.si2.iista.bolunteernetworks.apiclient.ItemIssue;
import info.si2.iista.volunteernetworks.util.Util;

public class MainActivity extends AppCompatActivity implements AdapterHome.ClickListener {

    // RecyclerView
    private RecyclerView recyclerView;
    private ArrayList<ItemIssue> items;

    // Animate server info
    private RelativeLayout serverInfo;
    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    private int sizeInfoServer = 60;
    private int sizeDescServer = -40;
    private int sizeInfoDescServer = 300;
    private long duration = 400L;
    private boolean isAnimatingInfoOut;   // Info
    private boolean isAnimatingInfoIn;    // Info
    private boolean isInfoShowing = true; // Info
    private boolean isAnimatingDescOut;   // Description
    private boolean isAnimatingDescIn;    // Description
    private boolean isDescShowing;        // Description


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Views
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        serverInfo = (RelativeLayout)findViewById(R.id.serverInfo);


        /** Test Data **/

        items = new ArrayList<>();
        items.add(new ItemIssue(Item.ISSUE, "#c53929", "title", "description", true));
        items.add(new ItemIssue(Item.ISSUE, "#29b6f6", "title", "description", false));
        items.add(new ItemIssue(Item.ISSUE, "#ffcd40", "title", "description", false));
        items.add(new ItemIssue(Item.ISSUE, "#68c594", "title", "description", true));
        items.add(new ItemIssue(Item.ISSUE, "#8665e4", "title", "description", true));

        /** End Test Data **/

        // RecyclerView
        recyclerView.setHasFixedSize(true);

        AdapterHome homeAdapter = new AdapterHome(getApplicationContext(), items);
        homeAdapter.setClickListener(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(homeAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 3 && !isAnimatingInfoOut && isInfoShowing) {
                    if (isDescShowing) { // Descripción visible, ocultar el bloque completo
                        animateInfoOut(serverInfo, Util.convertPixelsToDp(getApplicationContext(), sizeInfoDescServer));
                    } else
                        animateInfoOut(serverInfo, sizeInfoServer);
                } else if (dy < -3 && !isAnimatingInfoIn && !isInfoShowing) {
                    animateInfoIn(serverInfo);
                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onHomeItemClick(View view, int position) {

    }

    public void showInfoServer (View view) {

        if (!isAnimatingDescOut && isDescShowing) {
            animateDescOut(view);
        } else if (!isAnimatingDescIn && !isDescShowing) {
            animateDescIn(view);
        }

    }

    public void animateInfoOut(View view, float size) {

        TranslateAnimation anim = new TranslateAnimation(0, 0, Animation.RELATIVE_TO_SELF, Util.convertDpToPixel(getApplicationContext(), size));

        anim.setDuration(duration);
        anim.setFillEnabled(false);
        anim.setInterpolator(INTERPOLATOR);

        anim.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                isAnimatingInfoOut = true;
            }

            public void onAnimationEnd(Animation animation) {
                isAnimatingInfoOut = false;
                isInfoShowing = false;

                // Clear animation to prevent flicker
                serverInfo.clearAnimation();

                // Set new params
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                params.bottomMargin = Util.convertDpToPixel(getApplicationContext(), -100);
                serverInfo.setLayoutParams(params);

                isDescShowing = false;

            }

            @Override
            public void onAnimationRepeat(final Animation animation) {
            }
        });

        view.startAnimation(anim);

    }

    public void animateInfoIn(View view) {

        TranslateAnimation anim = new TranslateAnimation(0, 0, Animation.RELATIVE_TO_SELF, -Util.convertDpToPixel(getApplicationContext(), sizeInfoServer));

        anim.setDuration(duration);
        anim.setFillEnabled(false);
        anim.setInterpolator(INTERPOLATOR);

        anim.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                isAnimatingInfoIn = true;
            }

            public void onAnimationEnd(Animation animation) {
                isAnimatingInfoIn = false;
                isInfoShowing = true;

                // Clear animation to prevent flicker
                serverInfo.clearAnimation();

                // Set new params
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                params.bottomMargin = Util.convertDpToPixel(getApplicationContext(), -40);
                serverInfo.setLayoutParams(params);

            }

            @Override
            public void onAnimationRepeat(final Animation animation) {
            }
        });

        view.startAnimation(anim);

    }

    public void animateDescOut(final View view) {

        TranslateAnimation anim = new TranslateAnimation(0, 0, Animation.RELATIVE_TO_SELF, -Util.convertDpToPixel(getApplicationContext(), sizeDescServer));

        anim.setDuration(duration);
        anim.setFillEnabled(false);
        anim.setInterpolator(INTERPOLATOR);

        anim.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                isAnimatingDescOut = true;
            }

            public void onAnimationEnd(Animation animation) {
                isAnimatingDescOut = false;
                isDescShowing = false;

                // Clear animation to prevent flicker
                serverInfo.clearAnimation();

                // Set new params
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                params.bottomMargin = Util.convertDpToPixel(getApplicationContext(), -40);
                serverInfo.setLayoutParams(params);

            }

            @Override
            public void onAnimationRepeat(final Animation animation) {
            }
        });

        view.startAnimation(anim);

    }

    public void animateDescIn(View view) {

        TranslateAnimation anim = new TranslateAnimation(0, 0, Animation.RELATIVE_TO_SELF, Util.convertDpToPixel(getApplicationContext(), sizeDescServer));

        anim.setDuration(duration);
        anim.setFillEnabled(false);
        anim.setInterpolator(INTERPOLATOR);

        anim.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                isAnimatingDescIn = true;
            }

            public void onAnimationEnd(Animation animation) {
                isAnimatingDescIn = false;
                isDescShowing = true;

                // Clear animation to prevent flicker
                serverInfo.clearAnimation();

                // Set new params
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                serverInfo.setLayoutParams(params);

            }

            @Override
            public void onAnimationRepeat(final Animation animation) {
            }
        });

        view.startAnimation(anim);

    }

}
