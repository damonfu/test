
package yi.preference;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public abstract class PreferenceGroupActivity extends PreferenceActivity {

   /**
    *@hide
    */
   public boolean onPrepareYiFeatures(){
         return true;
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
   } 
}
