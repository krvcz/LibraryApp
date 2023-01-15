package pl.sebastian.reminder.repository;

import pl.sebastian.reminder.model.Book;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import java.util.List;

public class BookRepository {

    private static BookRepository bookRepositoryInstance;


   private  static EntityManager em;




    private BookRepository(EntityManager em) {
    this.em = em;

    }


    public static BookRepository getInstance(EntityManager em) {

        BookRepository result = bookRepositoryInstance;
        if (result != null) {
            return result;
        }
        synchronized(BookRepository.class) {
            if (bookRepositoryInstance == null) {
                bookRepositoryInstance = new BookRepository(em);
            }
            return bookRepositoryInstance;
        }
    }

    public static BookRepository getExistingInstance() {

        BookRepository result = bookRepositoryInstance;
        if (result != null) {
            return result;
        }
        synchronized(BookRepository.class) {
            if (bookRepositoryInstance == null) {
                throw new ExceptionInInitializerError("Instance does not exist! Initialize object first ");
            }
            return bookRepositoryInstance;
        }
    }

    public List<Book> findAll() {
        return em.createQuery("from Book m", Book.class).getResultList();
    }

    public Book addBook(Book book) {

            EntityTransaction transaction = em.getTransaction();
            Book newBook = book;
            transaction.begin();
            em.persist(newBook);
            transaction.commit();
        return newBook;
    }

    public List<Book> findByAttributes(String filterText) {
        return em.createQuery("FROM Book a " +
                        "WHERE lower(a.name) like lower(concat('%', :searchTerm, '%')) OR " +
                        "lower(a.author) like  lower(concat('%', :searchTerm, '%')) OR " +
                        "lower(a.number) like lower(concat('%', :searchTerm, '%'))", Book.class)
                .setParameter("searchTerm", filterText)
                .getResultList();
    }

    public void deleteBook(Book book) {

        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.remove(book);
        transaction.commit();
    }

    public void refreshEntities(Book book) {
            em.getEntityManagerFactory().getCache().evictAll();
            Book refreshedBook = em.find(book.getClass(), book.getId());
            em.refresh(refreshedBook);
        }

    public List<Book> findAvailable() {
        return em.createQuery("from Book m WHERE NOT EXISTS (SELECT 1 FROM ReservationDetail rd WHERE rd.book = m)", Book.class).getResultList();
    }
    }

