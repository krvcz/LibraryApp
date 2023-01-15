package pl.sebastian.reminder.repository;

import pl.sebastian.reminder.model.Book;
import pl.sebastian.reminder.model.ReservationDetail;
import pl.sebastian.reminder.model.Student;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

public class ReservationDetailRepository {

    private  static EntityManager em;



    private static ReservationDetailRepository ReservationDetailRepositoryInstance;


    private ReservationDetailRepository(EntityManager em) {
        this.em = em;


    }

    public static ReservationDetailRepository getInstance(EntityManager em) {

        ReservationDetailRepository result = ReservationDetailRepositoryInstance;
        if (result != null) {
            return result;
        }
        synchronized(ReservationDetailRepository.class) {
            if (ReservationDetailRepositoryInstance == null) {
                ReservationDetailRepositoryInstance = new ReservationDetailRepository(em);
            }
            return ReservationDetailRepositoryInstance;
        }
    }


    public static ReservationDetailRepository getExistingInstance() {

        ReservationDetailRepository result = ReservationDetailRepositoryInstance;
        if (result != null) {
            return result;
        }
        synchronized(BookRepository.class) {
            if (ReservationDetailRepositoryInstance == null) {
                throw new ExceptionInInitializerError("Instance does not exist! Initialize object first ");
            }
            return ReservationDetailRepositoryInstance;
        }
    }


    public List<ReservationDetail> getTerminatedReservations() {
        return em.createQuery("FROM ReservationDetail a WHERE dateOfReturn < :currentDate", ReservationDetail.class)
                .setParameter("currentDate", LocalDate.now()).getResultList();

    }

    public List<ReservationDetail> getCurrentReservations() {
        return em.createQuery("FROM ReservationDetail a", ReservationDetail.class).getResultList();
    }


    public List<ReservationDetail> getReservationsWithSpecifyTimeToTerminated(long numberDays) {
        return em.createQuery("FROM ReservationDetail a WHERE dateOfReturn = :currentDate", ReservationDetail.class)
                .setParameter("currentDate", LocalDate.now().plusDays(numberDays)).getResultList();

    }


    public void deleteReservation(ReservationDetail reservationDetail) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.remove(reservationDetail);
        transaction.commit();
    }


    public List<ReservationDetail> findByAttributes(String filterText){
        return em.createQuery("SELECT a FROM ReservationDetail a, Student b, Book c " +
                                "WHERE a.student = b.id AND " +
                                "a.book = c.id AND " + "(" +
                                "lower(a.dateOfReturn) like lower(concat('%', :searchTerm, '%')) OR " +
                                "lower(b.firstName) like lower(concat('%', :searchTerm, '%')) OR " +
                                "lower(b.lastName) like  lower(concat('%', :searchTerm, '%')) OR " +
                                "lower(b.email) like lower(concat('%', :searchTerm, '%')) OR " +
                                "lower(c.name) like lower(concat('%', :searchTerm, '%')) OR " +
                                "lower(c.author) like  lower(concat('%', :searchTerm, '%')) OR " +
                                 "lower(c.number) like lower(concat('%', :searchTerm, '%')))",
                         ReservationDetail.class)
                .setParameter("searchTerm", filterText)
                .getResultList();

    }


    public ReservationDetail addReservation(ReservationDetail reservationDetail) {

        EntityTransaction transaction = em.getTransaction();
        ReservationDetail newReservation = reservationDetail;
        transaction.begin();
        em.persist(newReservation);
        transaction.commit();
        em.getEntityManagerFactory().getCache().evictAll();
        Student refreshedStudent = em.find(reservationDetail.getStudent().getClass(), reservationDetail.getStudent().getId());
        Book refreshedBook = em.find(reservationDetail.getBook().getClass(),reservationDetail.getBook().getId());
        em.refresh(refreshedBook);
        em.refresh(refreshedStudent);
        return reservationDetail;
    }


    public void updateReservation(ReservationDetail reservationDetail) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.merge(reservationDetail);
        transaction.commit();
    }

    public void refreshEnitities(ReservationDetail reservationDetail) {
        em.getEntityManagerFactory().getCache().evictAll();
        Student refreshedStudent = em.find(reservationDetail.getStudent().getClass(), reservationDetail.getStudent().getId());
        Book refreshedBook = em.find(reservationDetail.getBook().getClass(),reservationDetail.getBook().getId());
        em.refresh(refreshedBook);
        em.refresh(refreshedStudent);
    }
}
