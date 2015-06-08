package com.baidu.widgets.rice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.LinearLayout;
import android.view.Window;
import com.baidu.widgets.R;
import com.baidu.widgets.Util;

public class ListItemLayouts extends Activity implements OnClickListener{
	private CheckBox chkImg1;
    private CheckBox chkImg2;	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setYiTheme(this);
        setContentView(R.layout.list_item_layouts);

        chkImg1 = (CheckBox)findViewById(R.id.show_img_1);
        chkImg1.setOnClickListener(this);
        
        chkImg2 = (CheckBox)findViewById(R.id.show_img_2);   
        chkImg2.setOnClickListener(this);
        
        SetupAllListItem();
    }
    public void SetupAllListItem() {
        View listitem1 = (View)findViewById(R.id.list_item1);
        SetupListItem(listitem1);  
        
        View listitem2 = (View)findViewById(R.id.list_item2);
        SetupListItem(listitem2);
        
        View listitem3 = (View)findViewById(R.id.list_item3);
        SetupListItem(listitem3);
        
        View listitem4 = (View)findViewById(R.id.list_item4);
        SetupListItem(listitem4);
/*        
        View listitem5 = (View)findViewById(R.id.list_item5);
        SetupListItem(listitem5);
        
        View listitem6 = (View)findViewById(R.id.list_item6);
        SetupListItem(listitem6);
        
        View listitem7 = (View)findViewById(R.id.list_item7);
        SetupListItem(listitem7);

        View listitem8 = (View)findViewById(R.id.list_item8);
        SetupListItem(listitem8);
*/        
        View listitem9 = (View)findViewById(R.id.list_item9);
        SetupListItem(listitem9);

        View listitem10 = (View)findViewById(R.id.list_item10);
        SetupListItem(listitem10);

        View listitem11 = (View)findViewById(R.id.list_item11);
        SetupListItem(listitem11);
        
        View listitem12 = (View)findViewById(R.id.list_item12);
        SetupListItem(listitem12);
        
        View listitem13 = (View)findViewById(R.id.list_item13);
        SetupListItem(listitem13);

        View listitem14 = (View)findViewById(R.id.list_item14);
        SetupListItem(listitem14);
        
        setupHeaderText();
    }
    
    void setupHeaderText() {
    	TextView tv;
        tv = (TextView)findViewById(R.id.list_item1_e);;
    	tv.setText(R.string.yi_list_item_1);
    	
        tv = (TextView)findViewById(R.id.list_item2_e);
    	tv.setText(R.string.yi_list_item_2);
    	
        tv = (TextView)findViewById(R.id.list_item3_e);
    	tv.setText(R.string.yi_list_item_3);
    	
        tv = (TextView)findViewById(R.id.list_item4_e);
    	tv.setText(R.string.yi_list_item_4);
    	
        tv = (TextView)findViewById(R.id.list_item9_e);
    	tv.setText(R.string.yi_list_item_9);
    	
        tv = (TextView)findViewById(R.id.list_item10_e);
    	tv.setText(R.string.yi_list_item_10);
    	
        tv = (TextView)findViewById(R.id.list_item11_e);
    	tv.setText(R.string.yi_list_item_11);
    	
        tv = (TextView)findViewById(R.id.list_item12_e);
    	tv.setText(R.string.yi_list_item_12);
    	
        tv = (TextView)findViewById(R.id.list_item13_e);
    	tv.setText(R.string.yi_list_item_13);

        tv = (TextView)findViewById(R.id.list_item14_e);
    	tv.setText(R.string.yi_list_item_14);
    	    	
    }
    
    public void SetupListItem( View listitem ) {    	

		if(null == listitem) {
			return;
		}
		
		listitem.setClickable(true);
		listitem.setOnClickListener(this);
		
        ImageView img1 = (ImageView)listitem.findViewById(R.id.image1);
        if( null != img1 ) {
            if(chkImg1.isChecked()) {
                img1.setVisibility(View.VISIBLE);
                //img1.setImageResource(R.drawable.yi_indicator_noti);
                //img1.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.yi_indicator_noti));
                img1.setImageResource(R.drawable.ic_appstore_picture);
            } else {
                img1.setVisibility(View.GONE);
            }
        }
        ImageView img2 = (ImageView)listitem.findViewById(R.id.image2);
        if( null != img2 ) {
            if(chkImg2.isChecked()) {
                img2.setVisibility(View.VISIBLE);
                img2.setImageResource(R.drawable.yi_indicator_arrow_right);
                //img2.setBackgroundResource(R.drawable.yi_indicator_arrow_right);
                //img2.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.yi_indicator_arrow_right));
            } else {
                img2.setVisibility(View.GONE);
            }
        }
        TextView txt1 = (TextView)listitem.findViewById(R.id.text1);
        if( null != txt1 ) {
        	txt1.setText(getText(R.string.txt1));
        }
        TextView txt2 = (TextView)listitem.findViewById(R.id.text2);
        if( null != txt2 ) {
        	txt2.setText(getText(R.string.txt2));
        }
        TextView txt3 = (TextView)listitem.findViewById(R.id.text3);
        if( null != txt3 ) {
        	txt3.setText(getText(R.string.txt3));
        }
        TextView txt4 = (TextView)listitem.findViewById(R.id.text4);
		if( null != txt4 ) {
			txt4.setText(getText(R.string.txt4));
		}
    }

    //@Override
    public void onClick(View v) {
        Log.d("WidgetWatch", "layout item click!");
        int layoutId = 0;
        switch(v.getId()) {
            case R.id.list_item1:
                layoutId = R.layout.yi_list_item_1;
                break;
            case R.id.list_item2:
                layoutId = R.layout.yi_list_item_2;
                break;                
            case R.id.list_item3:
                layoutId = R.layout.yi_list_item_3;
                break;                
            case R.id.list_item4:
                layoutId = R.layout.yi_list_item_4;
                break;                
/*
            case R.id.list_item5:
                layoutId = R.layout.yi_list_item_5;
                break;                
            case R.id.list_item6:
                layoutId = R.layout.yi_list_item_6;
                break;                
            case R.id.list_item7:
                layoutId = R.layout.yi_list_item_7;
                break;                
            case R.id.list_item8:
                layoutId = R.layout.yi_list_item_8;
                break;                
*/
            case R.id.list_item9:
                layoutId = R.layout.yi_list_item_9;
                break;   
            case R.id.list_item10:
                layoutId = R.layout.yi_list_item_10;
                break;   
            case R.id.list_item11:
                layoutId = R.layout.yi_list_item_11;
                break;
            case R.id.list_item12:
                layoutId = R.layout.yi_list_item_12;
                break;
            case R.id.list_item13:
                layoutId = R.layout.yi_list_item_13;
                break;
            case R.id.list_item14:
                layoutId = R.layout.yi_list_item_14;
                break;
            case R.id.show_img_1:
            case R.id.show_img_2:
                SetupAllListItem();
                findViewById(R.id.all_content).requestLayout();
                break;                
        }
        
        if(0 != layoutId) {
            Intent intent = new Intent(this, ShowListItem.class);
            intent.putExtra("LAYOUT_ID", layoutId);
            intent.putExtra("SHOW_IMG_1", chkImg1.isChecked());
            intent.putExtra("SHOW_IMG_2", chkImg2.isChecked());
            startActivity(intent);
        }
    }
}












