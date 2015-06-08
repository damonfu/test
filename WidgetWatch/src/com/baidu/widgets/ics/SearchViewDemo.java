package com.baidu.widgets.ics;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.baidu.widgets.R;
import com.baidu.widgets.Util;

public class SearchViewDemo extends Activity {
	SearchView mSearchView;
	EditText mHintEditText;
	EditText mSetQueryEditText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Util.setDeviceDefaultLightTheme(this);
		setContentView(R.layout.searchview_demo);
		
		mSearchView = (SearchView) findViewById(R.id.searchView1);
		
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		
		ToggleButton iconifiedButton = (ToggleButton) findViewById(R.id.iconified);
		iconifiedButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mSearchView.setIconifiedByDefault(isChecked);
			}
		});
		
		ToggleButton submitEnableButton = (ToggleButton) findViewById(R.id.submitEnableButton);
		submitEnableButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mSearchView.setSubmitButtonEnabled(isChecked);
			}
		});
		
		ToggleButton refineButton = (ToggleButton) findViewById(R.id.refineEnableButton);
		refineButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mSearchView.setQueryRefinementEnabled(isChecked);
			}
		});
		
		mHintEditText = (EditText) findViewById(R.id.hintText);
		Button setHintButton = (Button) findViewById(R.id.setHintButton);
		setHintButton.setText("Set Hint");
		setHintButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSearchView.setQueryHint(mHintEditText.getText().toString());
			}
		});
		
		mSetQueryEditText = (EditText) findViewById(R.id.queryText);
		Button setQueryButton = (Button) findViewById(R.id.setQueryButton);
		setQueryButton.setText("Set Query");
		setQueryButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSearchView.setQuery(mSetQueryEditText.getText().toString(), false);
			}
		});
		
		
		mSearchView.setQueryHint("Please enter to query");
		mSearchView.setIconifiedByDefault(false);
		mSearchView.setOnCloseListener(new OnCloseListener() {
			@Override
			public boolean onClose() {
				Toast.makeText(getApplication(), "Search View Closed!", Toast.LENGTH_SHORT).show();
				return false;
			}
		});
		mSearchView.setOnQueryTextListener(new OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				Toast.makeText(getApplication(), "Query text"+query+" submit", Toast.LENGTH_SHORT).show();
				return false;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				return false;
			}
		});
	}
}
