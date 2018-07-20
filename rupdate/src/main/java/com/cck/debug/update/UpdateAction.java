package com.cck.debug.update;

import com.cck.debug.common.MessageType;
import com.cck.debug.request.DeviceClient;
import com.cck.debug.request.Request;
import com.cck.debug.request.ResponseCallback;
import com.cck.debug.request.impl.ToastRequest;
import com.cck.debug.request.impl.UpdateRequest;
import com.cck.debug.utils.IOUtils;
import com.cck.debug.utils.Log;
import com.intellij.build.BuildProgressListener;
import com.intellij.build.events.BuildEvent;
import com.intellij.execution.ProgramRunnerUtil;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.impl.ComponentManagerImpl;
import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskId;
import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskNotificationEvent;
import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskNotificationListener;
import com.intellij.openapi.externalSystem.service.execution.ExternalSystemRunConfiguration;
import com.intellij.openapi.externalSystem.service.notification.ExternalSystemProgressNotificationManager;
import com.intellij.openapi.externalSystem.service.remote.ExternalSystemProgressNotificationManagerImpl;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.UserDataHolderBase;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class UpdateAction extends AnAction implements BuildProgressListener {
    private String basePath;
    private String serverName;
    private String pluginPath;
    private String pluginName;
    public UpdateAction() {
        super("update","push update plugin",PluginIcons.UpdateIcon);
    }

    protected UpdateAction(String text, String desc, Icon icon) {
        super(text,desc,icon);
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        updatePlugin(event.getProject());
    }

    protected void runAssembleDebug(Project project) {
        RunnerAndConfigurationSettings runnerAndConfigurationSettings = RunManager.getInstance(project).findConfigurationByName("buildPlugin");
        if(runnerAndConfigurationSettings != null){
            ExternalSystemProgressNotificationManagerImpl progressNotificationManager =
                    (ExternalSystemProgressNotificationManagerImpl)ServiceManager.getService(ExternalSystemProgressNotificationManager.class);
            progressNotificationManager.addNotificationListener(new ExternalSystemTaskNotificationListener() {
                @Override
                public void onQueued(@NotNull ExternalSystemTaskId id, String workingDir) {

                }

                @Override
                public void onStart(@NotNull ExternalSystemTaskId id) {

                }

                @Override
                public void onStatusChange(@NotNull ExternalSystemTaskNotificationEvent event) {

                }

                @Override
                public void onTaskOutput(@NotNull ExternalSystemTaskId id, @NotNull String text, boolean stdOut) {

                }

                @Override
                public void onEnd(@NotNull ExternalSystemTaskId id) {

                }

                @Override
                public void onSuccess(@NotNull ExternalSystemTaskId id) {
                    progressNotificationManager.removeNotificationListener(this);
                    NotificationUtils.showNotification("更新插件",NotificationType.INFORMATION);
                    updatePlugin(project);
                }

                @Override
                public void onFailure(@NotNull ExternalSystemTaskId id, @NotNull Exception e) {

                }

                @Override
                public void beforeCancel(@NotNull ExternalSystemTaskId id) {

                }

                @Override
                public void onCancel(@NotNull ExternalSystemTaskId id) {

                }
            });
            ProgramRunnerUtil.executeConfiguration(project,runnerAndConfigurationSettings, DefaultRunExecutor.getRunExecutorInstance());
        }
    }

    protected void updatePlugin(Project project) {
        initConfig(project);
        DeviceClient.INSTANCE.init((device,status) -> {
            String name = device == null ? "":device.getName();
            String msg = status == DeviceClient.DeviceStatusCallback.STATUS_CONNECT ? "已连接":"断开连接";
            NotificationUtils.showNotification(name+msg,NotificationType.INFORMATION);
        });
        File pluginFullPath = new File(basePath,pluginPath);
//        Request request = ToastRequest.obtain("test!!!");
        Request update = UpdateRequest.obtain(pluginName,0,pluginFullPath.getAbsolutePath());
        DeviceClient.INSTANCE.connect(update,serverName,new ResponseCallback(){
            @Override
            public void onResponse(int i) {
                if(i == MessageType.MSG_RESULT_SUCCESS) {
                    NotificationUtils.showNotification("更新成功", NotificationType.INFORMATION);
                } else {
                    NotificationUtils.showNotification("更新失败", NotificationType.INFORMATION);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                NotificationUtils.showNotification("更新失败："+throwable.getClass().getName(), NotificationType.INFORMATION);
            }
        });
    }



    private boolean isEmpty(CharSequence str) {
        return str == null||str.length() == 0;
    }

    private void initConfig(Project project) {
        if(project != null) {
            basePath = project.getBasePath();
        }

        BufferedReader br = null;
        try{
            br = new BufferedReader(new FileReader(new File(basePath,"config.cfg")));
            serverName = br.readLine();
            pluginName = br.readLine();
            pluginPath = br.readLine();
        }catch (IOException e) {
            Log.e(e);
        }finally {
            IOUtils.closeQuiet(br);
        }
        Log.i("se:"+serverName+",pl:"+pluginName+",pp:"+pluginPath);
    }

    @Override
    public void onEvent(BuildEvent event) {

    }
}
