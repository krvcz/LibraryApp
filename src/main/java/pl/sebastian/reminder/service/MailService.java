package pl.sebastian.reminder.service;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import pl.sebastian.reminder.model.ReservationDetail;
import pl.sebastian.reminder.repository.ReservationDetailRepository;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MailService implements Job {

    private ReservationDetailRepository reservationDetailRepository;

    private Properties prop;

    private Session session;


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        reservationDetailRepository = ReservationDetailRepository.getExistingInstance();

        prop = new Properties();

        try {
            prop.load(new FileInputStream("src/main/resources/mailconfig.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



        session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(prop.getProperty("user"), prop.getProperty("password"));
            }
        });

        System.out.println("------------------------Scheduler configured !----------------------------------------------");


        List<ReservationDetail> reservationDetailListTerminated = reservationDetailRepository.getTerminatedReservations();

        List<ReservationDetail> reservationDetailList3DaysToTerminated = reservationDetailRepository.getReservationsWithSpecifyTimeToTerminated(3);

        List<ReservationDetail> reservationDetailList1DaysToTerminated = reservationDetailRepository.getReservationsWithSpecifyTimeToTerminated(1);

        List<ReservationDetail> reservationDetailList7DaysToTerminated = reservationDetailRepository.getReservationsWithSpecifyTimeToTerminated(7);


        List<ReservationDetail> taggedReservations = new ArrayList<>();

        taggedReservations.addAll(reservationDetailListTerminated);
        taggedReservations.addAll(reservationDetailList3DaysToTerminated);
        taggedReservations.addAll(reservationDetailList1DaysToTerminated);
        taggedReservations.addAll(reservationDetailList7DaysToTerminated);

        sendMessage(taggedReservations);

    }


    public void sendMessage(List<ReservationDetail> reservationDetailList) {
        if (!reservationDetailList.isEmpty())
        {
            for (ReservationDetail reservationDetail:reservationDetailList) {
                Message message = new MimeMessage(session);

                try {
                    message.setFrom(new InternetAddress("from@gmail.com"));

                    message.setRecipients(
                            Message.RecipientType.TO, InternetAddress.parse(reservationDetail.getStudent().getEmail()));

                    message.setSubject("Mail Subject");

                    long daysElapsed = java.time.temporal.ChronoUnit.DAYS.between( reservationDetail.getDateOfReturn() , LocalDate.now());

                    String msg = "";

                    if (daysElapsed < 0) {
                         msg = "Przypomnienie! Musisz zwrócić książkę: "
                                 + reservationDetail.getBook().getAuthor()
                                 +" "
                                 +  reservationDetail.getBook().getName()
                                 + " za "
                                 + daysElapsed * (-1)
                                 + " "
                                 + "dni";
                    } else if (daysElapsed == 0) {
                        msg = "Przypomnienie! Dziś musisz zwrócić książkę: "
                                + reservationDetail.getBook().getAuthor()
                                + " "
                                +  reservationDetail.getBook().getName();
                    } else {
                        msg = "Upomnienie! Termin na zwrot ksiązki: "
                                + reservationDetail.getBook().getAuthor()
                                + " "
                                +  reservationDetail.getBook().getName() + "minął dnia "
                                + reservationDetail.getDateOfReturn().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG));
                    }


                    MimeBodyPart mimeBodyPart = new MimeBodyPart();

                    mimeBodyPart.setContent(msg, "text/html; charset=utf-8");

                    Multipart multipart = new MimeMultipart();

                    multipart.addBodyPart(mimeBodyPart);

                    message.setContent(multipart);

                    Transport.send(message);
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }

            }


        }


    }
}


