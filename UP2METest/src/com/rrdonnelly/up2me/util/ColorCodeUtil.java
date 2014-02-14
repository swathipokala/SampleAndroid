package com.rrdonnelly.up2me.util;

import java.util.HashMap;
import java.util.Map;

public class ColorCodeUtil {

	public static final Map<String, String> hexToRgb;
	public static final Map<String, String> rgbToHex;
    static
    {
        hexToRgb = new HashMap<String, String>();
        rgbToHex = new HashMap<String, String>();
        
        hexToRgb.put("#EE1F25", "238,31,37");
        hexToRgb.put("#C268A9", "194,104,169");
        hexToRgb.put("#466DB4", "70,109,180");
        hexToRgb.put("#BBBBBB", "187,187,187");
        hexToRgb.put("#F3EC0C", "243,236,12");
        hexToRgb.put("#4AB747", "74,183,71");
        hexToRgb.put("#F58021", "245,128,33");
        hexToRgb.put("#672A86", "103,42,134");
        
        rgbToHex.put("238,31,37", "#EE1F25");
        rgbToHex.put("194,104,169", "#C268A9");
        rgbToHex.put("70,109,180", "#466DB4");
        rgbToHex.put("187,187,187", "#BBBBBB");
        rgbToHex.put("243,236,12", "#F3EC0C");
        rgbToHex.put("74,183,71", "#4AB747");
        rgbToHex.put("245,128,33", "#F58021");
        rgbToHex.put("103,42,134", "#672A86");
    }
}
