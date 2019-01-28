package com.github.rjbx.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.github.rjbx.calibrater.Calibrater;
import com.github.rjbx.sample.data.ColorData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.rjbx.rateraid.Rateraid;
import com.github.rjbx.sample.data.ColorData.*;

import java.text.NumberFormat;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (findViewById(R.id.color_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        View recyclerView = findViewById(R.id.color_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(new ColorListAdapter(this, ColorData.ITEMS, mTwoPane));
    }

    public static class ColorListAdapter
            extends RecyclerView.Adapter<ColorListAdapter.ViewHolder> {

        private Rateraid.Builder sBuilder;
        private static double[] sPercents;
        private final ColorListActivity mParentActivity;
        private final List<ColorItem> mItems;
        private final boolean mTwoPane;
        private static final NumberFormat PERCENT_FORMATTER = NumberFormat.getPercentInstance();

        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

        ColorListAdapter(ColorListActivity parent,
                         List<ColorItem> items,
                         boolean twoPane) {
            mItems = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
            sPercents = new double[mItems.size()];
            Calibrater.resetRatings(sPercents);
            syncPercentsToItems(mItems, sPercents);
            sBuilder = Rateraid.with(
                    sPercents,
                    Calibrater.STANDARD_MAGNITUDE,
                    Calibrater.STANDARD_PRECISION,
                    clickedView -> {
                        syncPercentsToItems(mItems, sPercents);
                        notifyDataSetChanged();
                    });
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.color_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            ColorItem item = mItems.get(position);

            holder.mIdView.setText(item.getId());
            holder.mContentView.setText(item.getContent());
            holder.mPercentText.setText(PERCENT_FORMATTER.format(item.getPercent()));

            holder.itemView.setTag(item);
            holder.itemView.setOnClickListener(mOnClickListener);
            holder.itemView.setBackgroundColor(
                    holder.itemView.getResources().getColor(item.getColorRes())
            );

            sBuilder.addShifters(holder.mIncrementButton, holder.mDecrementButton, position)
                    .addRemover(holder.mRemoveButton, mItems, position)
                    .addEditor(holder.mPercentText, position);
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        private void syncPercentsToItems(List<ColorItem> items, double[] percents) {
            for (int i = 0; i < items.size(); i++) items.get(i).setPercent(percents[i]);
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
}