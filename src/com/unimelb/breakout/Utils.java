package com.unimelb.breakout;

/**
 * COMP90018 Mobile Computing System Programming, Project Breakout Game
 * Semester 2, 2014
 * Group 25
 * Students: (Name, StudentNumber, Email)
 *          Chenchao Ye, 633657, chenchaoy@student.unimelb.edu.au
 *          Fengmin Deng, 659332, dengf@student.unimelb.edu.au
 *          Jiajie Li, 631482, jiajiel@student.unimelb.edu.au
 *          Shuangchao Yin, 612511, shuangchaoy@student.unimelb.edu.au
 */


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public final class Utils {

    private Utils() {}

    public static final List<Brick> extraLevelData(String json, RuntimeData rData) {
        Gson gson = new Gson();
        JsonObject jobj = gson.fromJson(json, JsonObject.class);
        
        rData.setInitballx(gson.fromJson(jobj.get("initballx"), float.class));
        rData.setInitbally(gson.fromJson(jobj.get("initbally"), float.class));
        rData.setInitballXSpeed(gson.fromJson(jobj.get("initballxspeed"), float.class));
        rData.setInitballYSpeed(gson.fromJson(jobj.get("initballyspeed"), float.class));
        rData.setBarLengthFactor(gson.fromJson(jobj.get("barlengthfactor"), float.class));
        
        Type bricksType = new TypeToken<ArrayList<Brick>>() {}.getType();
        return gson.fromJson(jobj.get("bricks"), bricksType);
    }
    
    public static final List<Brick> buildBricks(String json) {
        Gson gson = new Gson();
        JsonObject jobj = gson.fromJson(json, JsonObject.class);
        Type listType = new TypeToken<ArrayList<Brick>>() {}.getType();
        return gson.fromJson(jobj.get("bricks"), listType);
    }
    
    public static final String saveBricks(Bricks brickData) {
        Gson gson = new Gson();
        String json = gson.toJson(brickData);
        return json;
    }
    
    public static final String nameInitials(String name) {
        StringBuilder nameInitials = new StringBuilder("");
        nameInitials.append(name.charAt(0) + ".");
        for(int i = 1; i < name.length(); ++i) {
            if (name.charAt(i) == ' ') {
                nameInitials.append(name.charAt(i + 1) + ".");
            }
        }
        return nameInitials.toString().toUpperCase();
    }
}
