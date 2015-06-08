
package com.baidu.widget.next;

import android.app.AlertDialog;
import yi.support.v1.YiLaf;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.widgets.R;

public class ActionBarTitleActivity extends Activity {
    
    private static final String sSpinnerExampleNames[] = {
        "Title only", "Title and Icon", "Submenu", "Groups",
        "Checkable", "Shortcuts", "Order", "Category and Order",
        "Visible", "Disabled"
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Yi_Theme_Light_ActionbarTabTop);
        TextView text = new TextView(this);
        text.setText("title");
        setContentView(text);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
                R.layout.select_dialog_item_holo, sSpinnerExampleNames); 
        YiLaf.enable(this);
        YiLaf.current().getActionBar().setDisplayHomeAsUpEnabled(true);
        YiLaf.current().getActionBar().setDisplayActionSpinerEnabled(true);
        YiLaf.current().getActionBar().setDisplayActionButtonEnabled(true, this.getResources().getDrawable(R.drawable.cld_icon_example));
        YiLaf.current().getActionBar().setAcitonSpinerAdapter(adapter, new YiLaf.OnSpinerItemListener(){

            @Override
            public boolean onSpinerItemSelected(int itemPosition, long itemId) {
                // TODO Auto-generated method stub
                return false;
            }
            
        });
        YiLaf.current().getActionBar().setDisplayShowCustomEnabled(true);
        YiLaf.current().getActionBar().setTitle("Wahaha");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
        case android.R.id.home:
            // app icon in action bar clicked; go home
            Intent intent = new Intent(this, NextActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
          
        case R.id.action_bar_button:
            new AlertDialog.Builder(this)
            .setTitle("Action Bar Button ")
            .setMessage("Click Action Bar Button")
            .setPositiveButton("ok", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub

                }
            }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub

                }
            }).create().show();
            return true;
            
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    
}
