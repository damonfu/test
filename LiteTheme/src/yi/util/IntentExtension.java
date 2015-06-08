/*
 * Copyright (C) 2010 Baidu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package yi.util;




/*
 @hide
*/
public class IntentExtension {  
     /**
     * Activity action: Perform an ip call to any number (emergency or not)
     * specified by the data.
     * <p>Input: {@link #getData} is URI of a phone number to be dialed or a
     * tel: URI of an explicit phone number.
     * <p>Output: nothing.
     * @hide
     */
    public static final String ACTION_IP_CALL_PRIVILEGED = "yi.intent.action.IP_CALL_PRIVILEGED";


    /**                                                                                              
     * Broadcast Action:  A sticky broadcast that indicates low ext media                             
     * condition on the device                                                                        
     *                                                                                                
     * <p class="note">This is a protected intent that can only be sent                               
     * by the system.                                                                                 
     *
     * {@hide}
     */
    public static final String ACTION_EXT_MEDIA_LOW = "yi.intent.action.EXT_MEDIA_LOW";


    /**                                                                                               
     * Broadcast Action:  Indicates low ext media condition on the device no longer exists            
     *                                                                                                
     * <p class="note">This is a protected intent that can only be sent                               
     * by the system.                                                                                 
     *
     * {@hide}
     */                                                                          
    public static final String ACTION_EXT_MEDIA_OK = "yi.intent.action.EXT_MEDIA_OK";


    /**                                                                                               
     * Broadcast Action:  A sticky broadcast that indicates a ext media full                          
     * condition on the device.                                                                       
     * <p class="note">This is a protected intent that can only be sent                             
     * by the system.                                                                                 
     *                                                                                                
     * {@hide}                                                                                        
     */                                                                          
    public static final String ACTION_EXT_MEDIA_FULL = "yi.intent.action.EXT_MEDIA_FULL";


    /**                                                                                               
     * Broadcast Action:  Indicates ext media full condition on the device                            
     * no longer exists.                                                                              
     *                                                                                                
     * <p class="note">This is a protected intent that can only be sent                               
     * by the system.                                                                                 
     *                                                                                                
     * {@hide}                                                                                        
     */                                                                          
     public static final String ACTION_EXT_MEDIA_NOT_FULL = "yi.intent.action.EXT_MEDIA_NOT_FULL";



     /**
     * String array of package names which should be excluded from ResolveActivity
     * @hide
     */
    public static final String EXTRA_EXCLUDE_PACKAGES = "yi.intent.extra.EXCLUDE_PACKAGES";


     //====voip====
     /**
     * Activity action: Perform an audio SIP call to any number (emergency or not)
     * specified by the data.
     * <p>Input: {@link #getData} is URI of a phone number to be dialed or a
     * tel: URI of an explicit phone number.
     * <p>Output: nothing.
     * @hide
     */
    public static final String ACTION_AUDIO_VOIP_CALL = "yi.intent.action.AUDIO_VOIP_CALL";


     /**
     * Activity action: Perform a video SIP call to any number (emergency or not)
     * specified by the data.
     * <p>Input: {@link #getData} is URI of a phone number to be dialed or a
     * tel: URI of an explicit phone number.
     * <p>Output: nothing.
     * @hide
     */
    public static final String ACTION_VIDEO_VOIP_CALL = "yi.intent.action.VIDEO_VOIP_CALL";
    //====voip end====
}
