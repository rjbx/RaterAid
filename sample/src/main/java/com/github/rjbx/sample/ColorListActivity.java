package com.github.rjbx.sample;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.github.rjbx.rateraid.Rateraid;
import com.github.rjbx.rateraid.Rateraid.RatedObject;
import com.github.rjbx.calibrater.Calibrater;
import com.github.rjbx.sample.data.ColorData.*;
import com.github.rjbx.sample.data.ColorData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * An activity representing a list of ColorData. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ColorDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ColorListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private ColorListAdapter mListAdapter;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(clickedView -> {
            ColorData.setSavedItems(ColorData.getOriginalItems());
            mListAdapter.swapItems(new ArrayList<>(ColorData.getSavedItems().values()));
            Snackbar.make(
                    clickedView,
                    "The list has been repopulated with the original dataset.",
                    Snackbar.LENGTH_LONG
            ).setAction("Action", null).show();
        });

        if (ColorData.getSavedItems().isEmpty()) ColorData.setSavedItems(ColorData.getOriginalItems());
        if (findViewById(R.id.color_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.color_list);
        assert recyclerView != null;
        mListAdapter = new ColorListAdapter(this, new ArrayList<>(ColorData.getSavedItems().values()), mTwoPane);
        recyclerView.setAdapter(mListAdapter);
    }

    public static class ColorListAdapter
            extends RecyclerView.Adapter<ColorListAdapter.ViewHolder> {

        private Rateraid.Objects mRateraid;
        private List<RatedObject<ColorItem>> mItems;
        private final ColorListActivity mParentActivity;
        private final boolean mTwoPane;

        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override public void onClick(View view) {
                ColorItem item = (ColorItem) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(ColorDetailFragment.ARG_ITEM_ID, item.getId());
                    ColorDetailFragment fragment = new ColorDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.color_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, ColorDetailActivity.class);
                    intent.putExtra(ColorDetailFragment.ARG_ITEM_ID, item.getId());
                    context.startActivity(intent);
                }
            }
        };

        ColorListAdapter(ColorListActivity parent, List<RatedObject<ColorItem>> items, boolean twoPane) {
            mItems = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
            mRateraid = Rateraid.with(
                    mItems,
                    sMagnitude,
                    Calibrater.STANDARD_PRECISION,
                    clickedView -> notifyDataSetChanged());
        }

        @Override public @NonNull ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.color_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

            ColorItem item = mItems.get(position).getObject();

            holder.mIdView.setText(item.getId());
            holder.mContentView.setText(item.colorResToString(mParentActivity));
            holder.mPercentText.setText(NumberFormat.getPercentInstance().format(item.getPercent()));

            holder.itemView.setTag(item);
            holder.itemView.setOnClickListener(mOnClickListener);
            holder.itemView.setBackgroundColor(
                    holder.itemView.getResources().getColor(item.getColorRes())
            );

            mRateraid.addShifters(holder.mIncrementButton, holder.mDecrementButton, position)
                    .addRemover(holder.mRemoveButton, position)
                    .addEditor(holder.mPercentText, position);
        }

        @Override public int getItemCount() {
            return mItems.size();
        }

        private void swapItems(List<RatedObject<ColorItem>> items) {
            mItems = items;
            mRateraid = Rateraid.with(
                    mItems,
                    sMagnitude,
                    Calibrater.STANDARD_PRECISION,
                    clickedView -> notifyDataSetChanged());
            notifyDataSetChanged();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            final TextView mIdView;
            final TextView mContentView;
            final Button mRemoveButton;
            final Button mIncrementButton;
            final Button mDecrementButton;
            final EditText mPercentText;

            ViewHolder(View view) {
                super(view);
                mIdView = view.findViewById(R.id.id_text);
                mContentView = view.findViewById(R.id.content);
                mRemoveButton = view.findViewById(R.id.remove);
                mIncrementButton = view.findViewById(R.id.increment);
                mDecrementButton = view.findViewById(R.id.decrement);
                mPercentText = view.findViewById(R.id.percent);
            }
        }
    }

    private static double sMagnitude = Calibrater.STANDARD_MAGNITUDE;

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.color_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_adjust) {
            double startingMagnitude = sMagnitude;
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            View view = getLayoutInflater().inflate(R.layout.dialog_color_list, new LinearLayout(this));
            EditText readout = view.findViewById(R.id.main_readout);
            SeekBar seekbar = view.findViewById(R.id.main_seekbar);
            readout.setText(String.format(
                    Locale.getDefault(), "%,2f", sMagnitude)
                    .substring(0, 5));
            readout.setOnEditorActionListener( (textView, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    double decimal = Double.parseDouble(textView.getText().toString());
                    if (decimal < 0 || decimal > .1) return false;
                    sMagnitude = decimal;
                    seekbar.setProgress((int) (sMagnitude * 1000d));
                    return true;
                } return false;
            });
            seekbar.setProgress((int) (startingMagnitude * 1000d));
            seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    sMagnitude = progress / 1000d;
                    readout.setText(String.format(
                            Locale.getDefault(), "%,2f", sMagnitude)
                            .substring(0, 5));
                }
                @Override public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override public void onStopTrackingTouch(SeekBar seekBar) {}
            });
            alertDialog.setView(view);
            alertDialog.setMessage(String.format(Locale.getDefault(),
                    "Rateraid is a library for automatically calibrating as well as " +
                            "defining behaviors for views controlling percent values.\n\n" +
                            "You can adjust how much each percent value is raised or lowered" +
                            "when pressing any shifter button.\n\n" +
                            "The adjustment is currently saved at %s. To change it, " +
                            "move the slider and press Save,",
                    String.format(
                            Locale.getDefault(), "%,2f", sMagnitude)
                            .substring(0, 5)));
            DialogInterface.OnClickListener listener = (dialog, button) -> {
                if (dialog == alertDialog) {
                    switch (button) {
                        case AlertDialog.BUTTON_NEUTRAL:
                            sMagnitude = startingMagnitude;
                            dialog.dismiss();
                            break;
                        case AlertDialog.BUTTON_POSITIVE:
                            setupRecyclerView();
                            dialog.dismiss();
                            break;
                    }
                }
            };
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Dismiss", listener);
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Save", listener);
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.GRAY);
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GRAY);
        } return super.onOptionsItemSelected(item);
    }
}