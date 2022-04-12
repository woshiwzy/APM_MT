package com.common.task;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class MyTask2 extends DefaultTask {

    public MyTask2() {
        this.setGroup("a_sand2");
    }

    @TaskAction
    void run(){
        System.err.println("This is Mytask2 run");
    }
}
