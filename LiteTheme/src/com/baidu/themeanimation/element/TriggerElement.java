package com.baidu.themeanimation.element;

import com.baidu.themeanimation.manager.ExpressionManager;
import com.baidu.themeanimation.util.Constants;
/**
 * Trigger下包含触发后执行的Command。
   Trigger属性：
        action 触发动作  用于Button: down，up，double
Command类型：
* 系统设置  <Command target=""  value=""/>
   铃声：target="RingMode" value="normal,silent,vibrate"  可以指定其中的几种状态，在指定的多个状态间切换
   WIFI：target="Wifi" value=""   value可指定为toggle/on/off
   数据：target="Data" value=""  同上
   蓝牙：target="Bluetooth" value="" 同上
   USB：target="UsbStorage" value="" 同上
* 属性命令   <Command target="元素.visibility"  value="true/false/toggle" />
            <Command target="元素.animation"  value="play" />
* 变量赋值   <VariableCommand name=""  expression=""  type="" />   同变量定义<Var>
* 变量绑定   <BinderCommand name=""  command="refresh" />   更新VariableBinder
* Intent命令  <IntentCommand  id="" action="" type="" category="" package="" class="" name="" />
* 声音命令(即将支持)<SoundCommand  sound=""  volume=""  keepCur=""  loop="" />   volume 音量，0～1的浮点数； keepCur 是否保持当前正在播放的声音，默认false； loop 是否循环播放，默认false。
    所有的命令都可以额外指定以下属性：
   condition:  触发条件，表达式为真时触发
   delay:  延时触发，单位ms
 * @author luqingyuan
 *
 */
public class TriggerElement extends Element {

    public static String ACTION_DOWN   = "down";
    public static String ACTION_UP     = "up";
    public static String ACTION_DOUBLE = "double";
    public static String ACTION_RESUME = "resume";
    public static String ACTION_PAUSE  = "pause";

    private String mAction;
    private String mTarget; // the target element which the button action will take
                    // effect on
    private String mProperty; // change the property of the target, visibility
    private String mValue; // the action, toggle: toggle the mProperty value, true
                   // <-> false

    @Override
    public boolean matchTag(String tagName) {
        return Constants.TAG_TRIGGER.equals(tagName);
    }

    @Override
    public Element createElement(String tagName) {
        return new TriggerElement();
    }

    public void setAction(String action) {
        mAction = action;
    }

    public void setTarget(String target) {
        mTarget = target;
    }

    public void setProperty(String property) {
        mProperty = property;
    }

    public void setValue(String value) {
        mValue = value;
    }

    public void exec() {
        if (getCommandElements() != null) {
            for (CommandElement cmd : getCommandElements()) {
                switch (cmd.getCommandType()) {
                    case CommandElement.TYPE_COMMAND:
                        cmd.execDelay();
                        break;
                    case CommandElement.TYPE_VARIABLE_COMMAND:
                        cmd.execDelay();
                        break;
                    case CommandElement.TYPE_INTENT_COMMAND:
                        cmd.execDelay();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public void exec(String action){
        if(action != null && action.equals(mAction)) {
            exec();
            ExpressionManager.getInstance().execElement(mTarget, mProperty, mValue);
        }
    }
}
