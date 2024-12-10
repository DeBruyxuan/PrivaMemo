package net.micode.notes.ui;
import android.app.Activity;
import android.app.SearchManager;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import net.micode.notes.R;
import net.micode.notes.data.Notes;
import net.micode.notes.data.NotesDatabaseHelper;
import net.micode.notes.data.Notes.NoteColumns;

public class SearchableActivity extends Activity {
    private ListView msearchListView;
    private NotesListAdapter mSearchListAdapter;
    private NotesDatabaseHelper mDatabaseHelper;
    private ContentResolver mContentResolver;
    private BackgroundQueryHandler mBackgroundQueryHandler;
    private long mCurrentFolderId;
    private TextView mTitleBar;

    private enum ListEditState {
        NOTE_LIST, SUB_FOLDER, CALL_RECORD_FOLDER
    };
    private static final String ROOT_FOLDER_SELECTION = "(" + NoteColumns.TYPE + "<>"
            + Notes.TYPE_SYSTEM + " AND " + NoteColumns.PARENT_ID + "=?)" + " OR ("
            + NoteColumns.ID + "=" + Notes.ID_CALL_RECORD_FOLDER + " AND "
            + NoteColumns.NOTES_COUNT + ">0)";
    private static final String NORMAL_SELECTION = NoteColumns.PARENT_ID + "=?";

    private ListEditState mState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mCurrentFolderId = Notes.ID_ROOT_FOLDER;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_list);
        mContentResolver = this.getContentResolver();
        mBackgroundQueryHandler = new BackgroundQueryHandler(this.getContentResolver());
        msearchListView = (ListView)findViewById(R.id.search_list);
        msearchListView.setOnItemClickListener(new OnListItemClickListener());
        mSearchListAdapter = new NotesListAdapter(this);
        mDatabaseHelper = new NotesDatabaseHelper(this);
        msearchListView.setAdapter(mSearchListAdapter);
        mTitleBar = (TextView) findViewById(R.id.tv_title_bar);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
            // 获取 SearchView 并设置查询文本监听器
            MenuItem searchItem = menu.findItem(R.id.search);
            SearchView searchView = (SearchView) searchItem.getActionView();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if (query != null && !query.trim().isEmpty()){
                        startAsyncNotesListQuery(query);
                    }else{
                        Log.e("sadas","asdasdasd");
                    }
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    // 查询文本发生变化
                    // 在这里处理文本变化
                    return true;
            }
        });
        return true;
    }


    private void startAsyncNotesListQuery(String query) {
        mBackgroundQueryHandler.cancelOperation(0);
        String selection=null;
        String[] selectionArgs=null;
        selection = NoteColumns.SNIPPET + " LIKE ?";
        selectionArgs = new String[]{"%" + query + "%"};
        mBackgroundQueryHandler.startQuery(0, null,
                Notes.CONTENT_NOTE_URI, NoteItemData.PROJECTION, selection, selectionArgs, NoteColumns.TYPE + " DESC," + NoteColumns.MODIFIED_DATE + " DESC");
    }
    private void startAsyncNotesListQuery() {
        String selection = (mCurrentFolderId == Notes.ID_ROOT_FOLDER) ? ROOT_FOLDER_SELECTION
                : NORMAL_SELECTION;
        mBackgroundQueryHandler.startQuery(0, null,
                Notes.CONTENT_NOTE_URI, NoteItemData.PROJECTION, selection, new String[] {
                        String.valueOf(mCurrentFolderId)
                }, NoteColumns.TYPE + " DESC," + NoteColumns.MODIFIED_DATE + " DESC");
    }

    private final class BackgroundQueryHandler extends AsyncQueryHandler {
        public BackgroundQueryHandler(ContentResolver contentResolver) {
            super(contentResolver);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            Cursor oldCursor = mSearchListAdapter.getCursor();
            if (oldCursor != null && !oldCursor.isClosed()) {
                oldCursor.close();
            }

            mSearchListAdapter.changeCursor(cursor);
            Log.e("SEARCH", "Change Cursor");
        }
    }

    private class OnListItemClickListener implements OnItemClickListener {

        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (view instanceof NotesListItem) {
                NoteItemData item = ((NotesListItem) view).getItemData();
                //openNode(item);
                if (item.getType() == Notes.TYPE_FOLDER
                        || item.getType() == Notes.TYPE_SYSTEM) {
                    openFolder(item);
                } else if (item.getType() == Notes.TYPE_NOTE) {
                    openNode(item);
                } else {
                    Log.e("Search", "Wrong note type in NOTE_LIST");
                }
            }
        }
    }
    private void openNode(NoteItemData data) {
        Intent intent = new Intent(this, NoteEditActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.putExtra(Intent.EXTRA_UID, data.getId());
        Log.e("Search", String.valueOf(data.getId()));
        this.startActivityForResult(intent, 102);
    }

    private void openFolder(NoteItemData data) {
        mCurrentFolderId = data.getId();
        startAsyncNotesListQuery();
        if (data.getId() == Notes.ID_CALL_RECORD_FOLDER) {
            mState = ListEditState.CALL_RECORD_FOLDER;
        } else {
            mState = ListEditState.SUB_FOLDER;
        }
        if (data.getId() == Notes.ID_CALL_RECORD_FOLDER) {
            mTitleBar.setText(R.string.call_record_folder_name);
        } else {
            mTitleBar.setText(data.getSnippet());
        }
        mTitleBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Cursor cursor = mSearchListAdapter.getCursor();
        if (cursor != null) {
            cursor.close();
        }
        mDatabaseHelper.close();
    }
}
