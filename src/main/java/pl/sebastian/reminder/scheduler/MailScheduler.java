package pl.sebastian.reminder.scheduler;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import pl.sebastian.reminder.repository.BookRepository;
import pl.sebastian.reminder.service.MailService;

import javax.persistence.EntityManager;
import java.text.ParseException;

public class MailScheduler {

    private JobDetail job;

    private CronTrigger trigger;

    private Scheduler scheduler;


    public String getCronParameter() {
        return cronParameter;
    }

    public void setCronParameter(String cronParameter) {
        this.cronParameter = cronParameter;
    }

    private String cronParameter;

    private static MailScheduler mailSchedulerInstance;


    private MailScheduler(String cronParameter) throws ParseException, SchedulerException {
//        "0/30 * * * * ?"
        this.cronParameter = cronParameter;

        job = new JobDetail();
        job.setName("MailSender Job");
        job.setJobClass(MailService.class);

        trigger = new CronTrigger();
        trigger.setName("MailSender Trigger");
        trigger.setCronExpression(cronParameter);

        //schedule it
        scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.start();
        scheduler.scheduleJob(job, trigger);
    }

    public static MailScheduler getInstance(String cronParameter) throws SchedulerException, ParseException {

        MailScheduler result = mailSchedulerInstance;
        if (result != null) {
            result.setCronParameter(cronParameter);
            return result;
        }
        synchronized(MailScheduler.class) {
            if (mailSchedulerInstance == null) {
                mailSchedulerInstance = new MailScheduler(cronParameter);
            }
            return mailSchedulerInstance;
        }
    }
}
