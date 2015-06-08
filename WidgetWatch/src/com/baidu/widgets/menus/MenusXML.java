package com.baidu.widgets.menus;

import com.baidu.widgets.R;
import com.baidu.widgets.Util;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Window;
//import yi.app.BaiduAppTitle;
/**
 * Demonstrates inflating menus from XML. There are different menu XML resources
 * that the user can choose to inflate. First, select an example resource from
 * the spinner, and then hit the menu button. To choose another, back out of the
 * activity and start over.
 */
public class MenusXML extends Activity {
    /**
     * Different example menu resources.
     */
    private static final int sMenuExampleResources[] = {
        R.menu.title_only, R.menu.title_icon, R.menu.submenu, R.menu.groups,
        R.menu.checkable, R.menu.order, R.menu.category_order,
        R.menu.visible, R.menu.disabled
    };
    
    /**
     * Names corresponding to the different example menu resources.
     */
    private static final String sMenuExampleNames[] = {
        "Title only", "Title and Icon", "Submenu", "Groups",
        "Checkable", "Shortcuts", "Order", "Category and Order",
        "Visible", "Disabled"
    };
   
    /**
     * Lets the user choose a menu resource.
     */
    private Spinner mSpinner;

    /**
     * Shown as instructions.
     */
    private TextView mInstructionsText;
    
    /**
     * Safe to hold on to this.
     */
    private Menu mMenu;

    //BaiduAppTitle mBaiduAppTitle;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setDeviceDefaultLightTheme(this);

        setContentView(R.layout.menu);


        mSpinner = (Spinner) findViewById(R.id.Spinner01);
        /*
        // Create a simple layout
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        */
        // Create the spinner to allow the user to choose a menu XML
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sMenuExampleNames); 

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // When programmatically creating views, make sure to set an ID
        // so it will automatically save its instance state
        mSpinner.setId(R.id.Spinner01);
        mSpinner.setAdapter(adapter);
        
        
        // Create help text
        mInstructionsText = new TextView(this);
        mInstructionsText.setText("Select a menu resource and press the menu key.");
        /*
        // Add the help, make it look decent
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10, 10, 10, 10);
        layout.addView(mInstructionsText, lp);
        
        
        // Add the spinner
        layout.addView(mSpinner,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));


        // Set the layout as our content view
        setContentView(layout);
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Hold on to this
        mMenu = menu;
        
        // Inflate the currently selected menu XML resource.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(sMenuExampleResources[mSpinner.getSelectedItemPosition()], menu);
        
        // Disable the spinner since we've already created the menu and the user
        // can no longer pick a different menu XML.
        //mSpinner.setEnabled(false);
        
        // Change instructions
        mInstructionsText.setText("If you want to choose another menu resource, go back and re-run this activity.");
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // For "Title only": Examples of matching an ID with one assigned in
            //                   the XML
            case R.id.jump:
                Toast.makeText(this, "Jump up in the air!", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.dive:
                Toast.makeText(this, "Dive into the water!", Toast.LENGTH_SHORT).show();
                return true;

            // For "Groups": Toggle visibility of grouped menu items with
            //               nongrouped menu items
            case R.id.browser_visibility:
                // The refresh item is part of the browser group
                final boolean shouldShowBrowser = !mMenu.findItem(R.id.refresh).isVisible();
                mMenu.setGroupVisible(R.id.browser, shouldShowBrowser);
                break;
                
            case R.id.email_visibility:
                // The reply item is part of the email group
                final boolean shouldShowEmail = !mMenu.findItem(R.id.reply).isVisible();
                mMenu.setGroupVisible(R.id.email, shouldShowEmail);
                break;
                
            // Generic catch all for all the other menu resources
            default:
                // Don't toast text when a submenu is clicked
                if (!item.hasSubMenu()) {
                    Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
                    return true;
                }
                break;
        }
        
        return false;
    }
    
    

}
