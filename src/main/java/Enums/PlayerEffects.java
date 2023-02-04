package Enums;

import java.util.ArrayList;

public class PlayerEffects {
    public final int value;
    public static final int AFTERBURNER = 1;
    public static final int ASTEROID_FIELD = 2;
    public static final int GAS_CLOUD = 4;
    public static final int SUPERFOOD = 8;
    public static final int SHIELD = 16;

    public PlayerEffects(int value) {
        this.value = value;
    }

    public boolean getState(int effect) {
        return (this.value & effect) != 0;
    }

    public ArrayList<Integer> getAllStates() {
        var res = new ArrayList<Integer>(5);
        for (int i = 1; i <= 16; i *= 2) {
            if (getState(i)) res.add(i);
        }
        return res;
    }
}
