package ninja.symmetry.spider;

import com.erz.joysticklibrary.JoyStick;

/**
 * Created by koz on 21/5/17.
 */

//JoyStickListener Interface
public interface JoyStickListener {
    void onMove(JoyStick joyStick, double angle, double power, int direction);
    void onTap();
    void onDoubleTap();
}