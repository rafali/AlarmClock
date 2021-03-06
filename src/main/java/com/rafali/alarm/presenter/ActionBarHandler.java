package com.rafali.alarm.presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.rafali.alarm.R;
import com.rafali.alarm.configuration.EditedAlarm;
import com.rafali.alarm.interfaces.IAlarmsManager;
import com.google.common.base.Preconditions;


import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.functions.Consumer;

/**
 * This class handles options menu and action bar
 *
 * @author Kate
 */
public class ActionBarHandler {

    private static final int JELLY_BEAN_MR1 = 17;
    private final Context mContext;
    private final UiStore store;
    private final IAlarmsManager alarms;
    private Disposable sub = Disposables.disposed();

    //not injected - requires activity to work
    public ActionBarHandler(Activity context, UiStore store, IAlarmsManager alarms) {
        this.mContext = Preconditions.checkNotNull(context);
        this.store = store;
        this.alarms = alarms;
    }

    /**
     * Delegate {@link Activity#onCreateOptionsMenu(Menu)}
     *
     * @param menu
     * @param inflater
     * @param actionBar
     * @return
     */
    public boolean onCreateOptionsMenu(final Menu menu, MenuInflater inflater, final ActionBar actionBar) {
        inflater.inflate(R.menu.settings_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_share);

        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide what to do with
        // it.
        intent.putExtra(Intent.EXTRA_SUBJECT, "https://play.google.com/uiStore/apps/details?id=com.better.alarm");
        intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/uiStore/apps/details?id=com.better.alarm");

        //TODO sp.setShareIntent(intent);

        if (Build.VERSION.SDK_INT < JELLY_BEAN_MR1) {
            MenuItem menuItemDashclock = menu.findItem(R.id.menu_dashclock);
            menuItemDashclock.setVisible(false);
        }

        sub = store.editing().subscribe(new Consumer<EditedAlarm>() {
            @Override
            public void accept(@NonNull EditedAlarm editedAlarm) throws Exception {
                boolean showDelete = editedAlarm.isEdited() && !editedAlarm.isNew();

                menu.findItem(R.id.set_alarm_menu_delete_alarm).setVisible(showDelete);

                actionBar.setDisplayHomeAsUpEnabled(editedAlarm.isEdited());
            }
        });

        return true;
    }


    public void onDestroy() {
        sub.dispose();
    }

    /**
     * Delegate {@link Activity#onOptionsItemSelected(MenuItem)}
     *
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_settings:
                mContext.startActivity(new Intent(mContext, SettingsActivity.class));
                return true;

            case R.id.menu_review:
                showReview();
                return true;


            case R.id.menu_dashclock:
                showDashClock();
                return true;

            case R.id.menu_mp3cutter:
                showMp3();
                return true;

            case R.id.set_alarm_menu_delete_alarm:
                deleteAlarm();
                break;

            case android.R.id.home:
                store.onBackPressed().onNext("ActionBar");
                return true;

            default:
        }
        return false;
    }

    private void deleteAlarm() {
        new AlertDialog.Builder(mContext).setTitle(mContext.getString(R.string.delete_alarm))
                .setMessage(mContext.getString(R.string.delete_alarm_confirm))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int w) {
                        alarms.getAlarm(store.editing().blockingFirst().id()).delete();
                        store.hideDetails();
                    }
                }).setNegativeButton(android.R.string.cancel, null).show();
    }

    private void showReview() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="
                        + mContext.getApplicationContext().getPackageName()));
                mContext.startActivity(intent);
            }
        });
        builder.setTitle(R.string.review);
        builder.setMessage(R.string.review_message);
        builder.setCancelable(true);
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create().show();
    }

    private void showDashClock() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="
                        + "net.nurik.roman.dashclock"));
                mContext.startActivity(intent);
            }
        });
        builder.setTitle(R.string.dashclock);
        builder.setMessage(R.string.dashclock_message);
        builder.setCancelable(true);
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create().show();
    }

    private void showMp3() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=mp3+cutter&c=app"));
                mContext.startActivity(intent);
            }
        });
        builder.setTitle(R.string.mp3cutter);
        builder.setMessage(R.string.mp3cutter_message);
        builder.setCancelable(true);
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create().show();
    }

}
