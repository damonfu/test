package com.baidu.widgets.ics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.baidu.widgets.R;

public class SearchSuggProvider extends ContentProvider {
	public static String AUTHORITY = "com.baidu.widgets.ics.SearchSuggProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/dictionary");
    
 // MIME types used for searching words or looking up a single definition
    public static final String WORDS_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                                                  "/vnd.example.android.searchabledict";
    public static final String DEFINITION_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                                                       "/vnd.example.android.searchabledict";

    private DictionaryDatabase mDictionary;

    // UriMatcher stuff
    private static final int SEARCH_WORDS = 0;
    private static final int GET_WORD = 1;
    private static final int SEARCH_SUGGEST = 2;
    private static final int REFRESH_SHORTCUT = 3;
    private static final UriMatcher sURIMatcher = buildUriMatcher();
    
    /**
     * Builds up a UriMatcher for search suggestion and shortcut refresh queries.
     */
    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher =  new UriMatcher(UriMatcher.NO_MATCH);
        // to get definitions...
        matcher.addURI(AUTHORITY, "dictionary", SEARCH_WORDS);
        matcher.addURI(AUTHORITY, "dictionary/#", GET_WORD);
        // to get suggestions...
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);

        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT, REFRESH_SHORTCUT);
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT + "/*", REFRESH_SHORTCUT);
        return matcher;
    }

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		switch (sURIMatcher.match(uri)) {
        case SEARCH_WORDS:
            return WORDS_MIME_TYPE;
        case GET_WORD:
            return DEFINITION_MIME_TYPE;
        case SEARCH_SUGGEST:
            return SearchManager.SUGGEST_MIME_TYPE;
        case REFRESH_SHORTCUT:
            return SearchManager.SHORTCUT_MIME_TYPE;
        default:
            throw new IllegalArgumentException("Unknown URL " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public boolean onCreate() {
		mDictionary = new DictionaryDatabase(getContext());
        return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// Use the UriMatcher to see what kind of query we have and format the db query accordingly
        switch (sURIMatcher.match(uri)) {
            case SEARCH_SUGGEST:
                if (selectionArgs == null) {
                  throw new IllegalArgumentException(
                      "selectionArgs must be provided for the Uri: " + uri);
                }
                return getSuggestions(selectionArgs[0]);
            case SEARCH_WORDS:
                if (selectionArgs == null) {
                  throw new IllegalArgumentException(
                      "selectionArgs must be provided for the Uri: " + uri);
                }
                return search(selectionArgs[0]);
            case GET_WORD:
                return getWord(uri);
            case REFRESH_SHORTCUT:
                return refreshShortcut(uri);
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
	}
	
	public Cursor getSuggestions(String query) {
		query = query.toLowerCase();
		String[] columns = new String[] {
				BaseColumns._ID,
				DictionaryDatabase.KEY_WORD,
				DictionaryDatabase.KEY_DEFINITION,
				/* SearchManager.SUGGEST_COLUMN_SHORTCUT_ID,
                        (only if you want to refresh shortcuts) */
				SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID};

		return mDictionary.getWordMatches(query, columns);
    }
	
	private Cursor search(String query) {
		query = query.toLowerCase();
		String[] columns = new String[] {
				BaseColumns._ID,
				DictionaryDatabase.KEY_WORD,
				DictionaryDatabase.KEY_DEFINITION};

		return mDictionary.getWordMatches(query, columns);
    }

    private Cursor getWord(Uri uri) {
    	String rowId = uri.getLastPathSegment();
    	String[] columns = new String[] {
    			DictionaryDatabase.KEY_WORD,
    			DictionaryDatabase.KEY_DEFINITION};

    	return mDictionary.getWord(rowId, columns);
    }
    
    private Cursor refreshShortcut(Uri uri) {
        String rowId = uri.getLastPathSegment();
        String[] columns = new String[] {
            BaseColumns._ID,
            DictionaryDatabase.KEY_WORD,
            DictionaryDatabase.KEY_DEFINITION,
            SearchManager.SUGGEST_COLUMN_SHORTCUT_ID,
            SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID};

        return mDictionary.getWord(rowId, columns);
    }

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}
	
	public static class DictionaryDatabase {
	    //The columns we'll include in the dictionary table
	    public static final String KEY_WORD = SearchManager.SUGGEST_COLUMN_TEXT_1;
	    public static final String KEY_DEFINITION = SearchManager.SUGGEST_COLUMN_TEXT_2;

	    private static final String DATABASE_NAME = "dictionary";
	    private static final String FTS_VIRTUAL_TABLE = "FTSdictionary";
	    private static final int DATABASE_VERSION = 2;

	    private final DictionaryOpenHelper mDatabaseOpenHelper;
	    private static final HashMap<String,String> mColumnMap = buildColumnMap();

	    /**
	     * Constructor
	     * @param context The Context within which to work, used to create the DB
	     */
	    public DictionaryDatabase(Context context) {
	        mDatabaseOpenHelper = new DictionaryOpenHelper(context);
	    }

	    /**
	     * Builds a map for all columns that may be requested, which will be given to the 
	     * SQLiteQueryBuilder. This is a good way to define aliases for column names, but must include 
	     * all columns, even if the value is the key. This allows the ContentProvider to request
	     * columns w/o the need to know real column names and create the alias itself.
	     */
	    private static HashMap<String,String> buildColumnMap() {
	        HashMap<String,String> map = new HashMap<String,String>();
	        map.put(KEY_WORD, KEY_WORD);
	        map.put(KEY_DEFINITION, KEY_DEFINITION);
	        map.put(BaseColumns._ID, "rowid AS " +
	                BaseColumns._ID);
	        map.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, "rowid AS " +
	                SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
	        map.put(SearchManager.SUGGEST_COLUMN_SHORTCUT_ID, "rowid AS " +
	                SearchManager.SUGGEST_COLUMN_SHORTCUT_ID);
	        return map;
	    }

	    /**
	     * Returns a Cursor positioned at the word specified by rowId
	     *
	     * @param rowId id of word to retrieve
	     * @param columns The columns to include, if null then all are included
	     * @return Cursor positioned to matching word, or null if not found.
	     */
	    public Cursor getWord(String rowId, String[] columns) {
	        String selection = "rowid = ?";
	        String[] selectionArgs = new String[] {rowId};

	        return query(selection, selectionArgs, columns);

	        /* This builds a query that looks like:
	         *     SELECT <columns> FROM <table> WHERE rowid = <rowId>
	         */
	    }

	    /**
	     * Returns a Cursor over all words that match the given query
	     *
	     * @param query The string to search for
	     * @param columns The columns to include, if null then all are included
	     * @return Cursor over all words that match, or null if none found.
	     */
	    public Cursor getWordMatches(String query, String[] columns) {
	        String selection = KEY_WORD + " MATCH ?";
	        String[] selectionArgs = new String[] {query+"*"};

	        return query(selection, selectionArgs, columns);

	        /* This builds a query that looks like:
	         *     SELECT <columns> FROM <table> WHERE <KEY_WORD> MATCH 'query*'
	         * which is an FTS3 search for the query text (plus a wildcard) inside the word column.
	         *
	         * - "rowid" is the unique id for all rows but we need this value for the "_id" column in
	         *    order for the Adapters to work, so the columns need to make "_id" an alias for "rowid"
	         * - "rowid" also needs to be used by the SUGGEST_COLUMN_INTENT_DATA alias in order
	         *   for suggestions to carry the proper intent data.
	         *   These aliases are defined in the DictionaryProvider when queries are made.
	         * - This can be revised to also search the definition text with FTS3 by changing
	         *   the selection clause to use FTS_VIRTUAL_TABLE instead of KEY_WORD (to search across
	         *   the entire table, but sorting the relevance could be difficult.
	         */
	    }

	    /**
	     * Performs a database query.
	     * @param selection The selection clause
	     * @param selectionArgs Selection arguments for "?" components in the selection
	     * @param columns The columns to return
	     * @return A Cursor over all rows matching the query
	     */
	    private Cursor query(String selection, String[] selectionArgs, String[] columns) {
	        /* The SQLiteBuilder provides a map for all possible columns requested to
	         * actual columns in the database, creating a simple column alias mechanism
	         * by which the ContentProvider does not need to know the real column names
	         */
	        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
	        builder.setTables(FTS_VIRTUAL_TABLE);
	        builder.setProjectionMap(mColumnMap);

	        Cursor cursor = builder.query(mDatabaseOpenHelper.getReadableDatabase(),
	                columns, selection, selectionArgs, null, null, null);

	        if (cursor == null) {
	            return null;
	        } else if (!cursor.moveToFirst()) {
	            cursor.close();
	            return null;
	        }
	        return cursor;
	    }


	    /**
	     * This creates/opens the database.
	     */
	    private static class DictionaryOpenHelper extends SQLiteOpenHelper {

	        private final Context mHelperContext;
	        private SQLiteDatabase mDatabase;

	        /* Note that FTS3 does not support column constraints and thus, you cannot
	         * declare a primary key. However, "rowid" is automatically used as a unique
	         * identifier, so when making requests, we will use "_id" as an alias for "rowid"
	         */
	        private static final String FTS_TABLE_CREATE =
	                    "CREATE VIRTUAL TABLE " + FTS_VIRTUAL_TABLE +
	                    " USING fts3 (" +
	                    KEY_WORD + ", " +
	                    KEY_DEFINITION + ");";

	        DictionaryOpenHelper(Context context) {
	            super(context, DATABASE_NAME, null, DATABASE_VERSION);
	            mHelperContext = context;
	        }

	        @Override
	        public void onCreate(SQLiteDatabase db) {
	            mDatabase = db;
	            mDatabase.execSQL(FTS_TABLE_CREATE);
	            loadDictionary();
	        }

	        /**
	         * Starts a thread to load the database table with words
	         */
	        private void loadDictionary() {
	            new Thread(new Runnable() {
	                public void run() {
	                    try {
	                        loadWords();
	                    } catch (IOException e) {
	                        throw new RuntimeException(e);
	                    }
	                }
	            }).start();
	        }

	        private void loadWords() throws IOException {
	            final Resources resources = mHelperContext.getResources();
	            InputStream inputStream = resources.openRawResource(R.raw.definitions);
	            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

	            try {
	                String line;
	                while ((line = reader.readLine()) != null) {
	                    String[] strings = TextUtils.split(line, "-");
	                    if (strings.length < 2) continue;
	                    addWord(strings[0].trim(), strings[1].trim());
	                }
	            } finally {
	                reader.close();
	            }
	        }

	        /**
	         * Add a word to the dictionary.
	         * @return rowId or -1 if failed
	         */
	        public long addWord(String word, String definition) {
	            ContentValues initialValues = new ContentValues();
	            initialValues.put(KEY_WORD, word);
	            initialValues.put(KEY_DEFINITION, definition);

	            return mDatabase.insert(FTS_VIRTUAL_TABLE, null, initialValues);
	        }

	        @Override
	        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	            db.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE);
	            onCreate(db);
	        }
	    }

	}

}
