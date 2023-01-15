package pl.sebastian.reminder;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import org.quartz.SchedulerException;
import pl.sebastian.reminder.model.Book;
import pl.sebastian.reminder.model.ReservationDetail;
import pl.sebastian.reminder.model.Student;
import pl.sebastian.reminder.repository.BookRepository;
import pl.sebastian.reminder.repository.ReservationDetailRepository;
import pl.sebastian.reminder.repository.StudentRepository;
import pl.sebastian.reminder.scheduler.MailScheduler;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.text.ParseException;
import java.time.LocalDate;

/**
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 */
@PWA(name = "Project Base for Vaadin", shortName = "Project Base")
@Theme("my-theme")
public class AppShell implements AppShellConfigurator {


    protected static EntityManagerFactory factory = Persistence.createEntityManagerFactory("sample");
    protected  static EntityManager em = factory.createEntityManager();

    public AppShell() throws SchedulerException, ParseException {

        StudentRepository.getInstance(em);
        BookRepository.getInstance(em);
        ReservationDetailRepository.getInstance(em);

        MailScheduler.getInstance("0/30 * * * * ?");

    }
}
