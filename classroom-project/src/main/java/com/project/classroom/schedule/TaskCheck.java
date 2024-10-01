package com.project.classroom.schedule;

import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TaskCheck {

   public static void runTask() {
       ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

       long delay = 5000;
       Runnable task = () -> {
           System.out.println("Run schedule");
       };
       scheduler.scheduleAtFixedRate(task, 5,1, TimeUnit.SECONDS);
   }
}
