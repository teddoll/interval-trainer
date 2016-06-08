package com.teddoll.fitness.intervaltrainer.edit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.teddoll.fitness.intervaltrainer.R;
import com.teddoll.fitness.intervaltrainer.data.IntervalContract;
import com.teddoll.fitness.intervaltrainer.util.DBStringParseUtil;

import java.util.ArrayList;
import java.util.List;


public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EDIT_ID_ARG = "edit_id";

    private boolean mEditMode;
    private int mSetId;
    private TextView mTimeView;
    private ListView mListView;
    private EditText mEditLabelView;

    private List<Integer> mSetList;
    private int mTotal;

    private List<Integer> mCopiedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (mEditMode) {
                actionBar.setTitle(R.string.edit_title);
            } else {
                actionBar.setTitle(R.string.add_title);
            }
        }
        if(savedInstanceState != null) {
            mSetId = savedInstanceState.getInt("setId", -1);
        } else {
            mSetId = getIntent().getIntExtra(EDIT_ID_ARG, -1);
        }
        mEditMode = mSetId >= 0;
        mEditLabelView = (EditText) findViewById(R.id.label_input);
        mTimeView = (TextView) findViewById(R.id.time);
        mListView = (ListView) findViewById(R.id.list);
        registerForContextMenu(mListView);

        if(savedInstanceState != null) {
            mTotal = savedInstanceState.getInt("total", 0);
            mSetList = savedInstanceState.getIntegerArrayList("setList");
            mCopiedItems = savedInstanceState.getIntegerArrayList("copyList");
            String label = savedInstanceState.getString("label");
            mListView.setAdapter(new SetItemAdapter(this, mSetList));
            mTimeView.setText(getString(R.string.total_format, mTotal));
            mEditLabelView.setText(label);
        } else {
            mCopiedItems = new ArrayList<>(0);
            if (mEditMode) {
                getSupportLoaderManager().initLoader(0, null, this);
            } else {
                loadPage(null);

            }
        }

        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add(mSetList.size());
            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("setId", mSetId);
        outState.putIntegerArrayList("setList", new ArrayList<>(mSetList));
        outState.putIntegerArrayList("copyList", new ArrayList<>(mCopiedItems));
        outState.putInt("total", mTotal);
        outState.putString("label", mEditLabelView.getText().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                up();
                return true;
            case R.id.action_copy:
                copy();
                return true;
            case R.id.action_paste:
                paste(mSetList.size());
                return true;
            case R.id.action_save:
                save();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_list, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_add:
                add(info.position + 1);
                return true;
            case R.id.action_paste:
                paste(info.position + 1);
                return true;
            case R.id.action_delete:
                delete(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void loadPage(Cursor cursor) {
        String label;
        if (cursor == null || !cursor.moveToFirst()) {
            mTotal = 0;
            mSetList = new ArrayList<>();
            label = "";
        } else {
            mTotal = cursor.getInt(
                    cursor.getColumnIndex(
                            IntervalContract.SetEntry.TOTAL_TIME));
            mSetList = DBStringParseUtil.deserializeSetTimes(
                    cursor.getString(
                            cursor.getColumnIndex(
                                    IntervalContract.SetEntry.TIMES)));
            label = cursor.getString(
                    cursor.getColumnIndex(
                            IntervalContract.SetEntry.LABEL));
        }
        mListView.setAdapter(new SetItemAdapter(this, mSetList));
        mTimeView.setText(getString(R.string.total_format, mTotal));
        mEditLabelView.setText(label);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                IntervalContract.SetEntry.buildSetUri(mSetId),
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        loadPage(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void up() {
        NavUtils.navigateUpFromSameTask(this);
    }

    private void save() {
        if (mEditMode) {
            ContentValues vals = new ContentValues();
            vals.put(IntervalContract.SetEntry.LABEL, mEditLabelView.getText().toString());
            vals.put(IntervalContract.SetEntry.TOTAL_TIME, mTotal);
            vals.put(IntervalContract.SetEntry.TIMES, DBStringParseUtil.serializeSetTimes(mSetList));
            getContentResolver().update(
                    IntervalContract.SetEntry.CONTENT_URI,
                    vals,
                    null,
                    new String[]{String.valueOf(mSetId)});
        } else {
            ContentValues vals = new ContentValues();
            vals.put(IntervalContract.SetEntry.LABEL, mEditLabelView.getText().toString());
            vals.put(IntervalContract.SetEntry.TOTAL_TIME, mTotal);
            vals.put(IntervalContract.SetEntry.TIMES, DBStringParseUtil.serializeSetTimes(mSetList));
            getContentResolver().insert(IntervalContract.SetEntry.CONTENT_URI, vals);

        }
        up();
    }

    private void add(int position) {
        SetIntervalPickerFragment.newInstance(position).show(getSupportFragmentManager(), "ADD");
    }

    private void copy() {
        SetIntervalCopyDialogFragment.newInstance(
                new ArrayList<>(mSetList)).show(getSupportFragmentManager(), "COPY");
    }

    private void delete(int position) {
        mTotal -= mSetList.get(position);
        mSetList.remove(position);
        ((SetItemAdapter) mListView.getAdapter()).notifyDataSetChanged();
        mTimeView.setText(getString(R.string.total_format, mTotal));

    }

    private void paste(int position) {
        List<Integer> pasteItems = new ArrayList<>(mCopiedItems.size());
        if (mCopiedItems != null) {
            for (Integer i : mCopiedItems) {
                Integer time = mSetList.get(i);
                pasteItems.add(time);
                mTotal += time;

            }
            mSetList.addAll(position, pasteItems);
            ((SetItemAdapter) mListView.getAdapter()).notifyDataSetChanged();
            mTimeView.setText(getString(R.string.total_format, mTotal));
        }
    }

    public void onAdd(int position, int value) {
        mTotal += value;
        mSetList.add(position, value);
        ((SetItemAdapter) mListView.getAdapter()).notifyDataSetChanged();
        mTimeView.setText(getString(R.string.total_format, mTotal));
    }

    public void onCopy(@NonNull List<Integer> selected) {
        mCopiedItems = selected;
    }


    private static class SetItemAdapter extends BaseAdapter {

        private Context mContext;
        private LayoutInflater mInflater;
        private List<Integer> mItems;

        public SetItemAdapter(@NonNull Context context, @NonNull List<Integer> items) {
            mContext = context;
            mInflater = LayoutInflater.from(context);
            mItems = items;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int i) {
            return mItems.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = mInflater.inflate(R.layout.item_set_item, viewGroup, false);
                ViewHolder vh = new ViewHolder();
                vh.position = (TextView) view.findViewById(R.id.number);
                vh.time = (TextView) view.findViewById(R.id.time);
                view.setTag(vh);
            }

            ViewHolder vh = (ViewHolder) view.getTag();
            vh.position.setText(String.valueOf(i+1));
            vh.time.setText(mContext.getString(R.string.item_time_format, mItems.get(i)));
            return view;
        }


        class ViewHolder {
            TextView position;
            TextView time;
        }
    }


}
