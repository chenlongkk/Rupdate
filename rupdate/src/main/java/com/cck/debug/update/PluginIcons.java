package com.cck.debug.update;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public class PluginIcons {

    public static final Icon ActionIcon = load("/icon/icon.png");
    public static final Icon UpdateIcon = load("/icon/update_icon.png");
    public static final Icon BuildIcon = load("/icon/build.png");



    private static Icon load(String path) {
        try {
            return IconLoader.getIcon(path, PluginIcons.class);
        } catch (IllegalStateException e) {
            return null;
        }
    }

}
