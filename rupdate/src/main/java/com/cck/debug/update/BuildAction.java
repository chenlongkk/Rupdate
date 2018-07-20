package com.cck.debug.update;

import com.intellij.openapi.actionSystem.AnActionEvent;

public class BuildAction extends UpdateAction{
    public BuildAction() {
        super("build","build and update plugin",PluginIcons.BuildIcon);
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        runAssembleDebug(event.getProject());
    }
}
