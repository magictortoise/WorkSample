/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.evi.web.resources.cronreports;

import com.evi.web.resources.EVerifileBean;
import java.util.Date;
import org.apache.log4j.Logger;
import org.quartz.CronExpression;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;


public class CronReporter extends EVerifileBean {

   private static Logger logger = Logger.getLogger(CronReporter.class);
   private static SchedulerFactory schedulerFactory;
   private static Scheduler scheduler,schedulerX;

   //Should not get instanciated except through the StartupFilter

   //To add additional tasks to the scheduler, create a class with job interface, place executing code there.
   //create cron expression and jobdetail and add to scheduler.
   protected CronReporter() {
   }



   public static void beginTask()
    {
        System.out.println("CronReporter, beginning tasks");
       try {
        schedulerFactory = new StdSchedulerFactory();
        scheduler = schedulerFactory.getScheduler();
        scheduler.start();
        prep();
        } catch(Exception ex) {
         logger.fatal("Scheduler Exception!",ex);
        }
    }
       
   public static void endTask() {
       try {
          scheduler.shutdown();
        } catch( SchedulerException ex) {
         logger.fatal("Scheduler Exception!",ex);
        }
        
        
   }

   public static void prep() {
    
        
        try {
          //JobDetail instantiation: Job name ,Job Group ,Job executable class
            JobDetail jobDetail = new JobDetail("UICompliance", "UIComplianceGroup", UIComplianceJob.class);
          //trigger name, trigger group name
            CronTrigger cronTrigger = new CronTrigger("cronTrigger", "triggerGroup");
          //  1. Seconds  2. Minutes 3. Hours 4. Day-of-Month 5. Month 6. Day-of-Week 7. Year (optional field)
            CronExpression cexp = new CronExpression("0 30 9,12,17 * * ?");
            cronTrigger.setCronExpression(cexp);
            scheduler.scheduleJob(jobDetail, cronTrigger);

/* Test case
            jobDetail = new JobDetail("UICompliance4", "UIComplianceGroup4", UIComplianceJob.class);
            cronTrigger = new CronTrigger("cronTrigger4", "triggerGroup4");
            cexp = new CronExpression("0 3 17 * * ?");
            cronTrigger.setCronExpression(cexp);
            scheduler.scheduleJob(jobDetail, cronTrigger);
*/

        } catch (Exception e) {
            logger.fatal("Scheduler Exception!",e);
        }

   }

   

}

