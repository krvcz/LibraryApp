package pl.sebastian.reminder.repository;


import pl.sebastian.reminder.model.Student;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;


public class StudentRepository {


    private  static EntityManager em;

    private static StudentRepository studentRepositoryInstance;

    private StudentRepository(EntityManager em) {
        this.em = em;


    }

    public static StudentRepository getInstance(EntityManager em) {


        StudentRepository result = studentRepositoryInstance;
        if (result != null) {
            return result;
        }
        synchronized(StudentRepository.class) {
            if (studentRepositoryInstance == null) {
                studentRepositoryInstance =  new StudentRepository(em);
            }
            return studentRepositoryInstance;
        }

    }

    public static StudentRepository getExistingInstance() {

        StudentRepository result = studentRepositoryInstance;
        if (result != null) {
            return result;
        }
        synchronized(BookRepository.class) {
            if (studentRepositoryInstance == null) {
                throw new ExceptionInInitializerError("Instance does not exist! Initialize object first ");
            }
            return studentRepositoryInstance;
        }
    }

    public List<Student> getListOfStudents(){
        return  em.createQuery("FROM Student a", Student.class).getResultList();

    }

    public Student findById(long i ){
        return em.find(Student.class, i);
    }

    public List<Student> findByAttributes(String firstName, String lastName, String email ){
        return em.createQuery("FROM Student a " +
                                        "WHERE firstName = :firstName AND " +
                                            "lastName = :lastName AND " +
                                            "email = :email", Student.class)
                                            .setParameter("firstName", firstName)
                                            .setParameter("lastName", lastName)
                                            .setParameter("email", email)
                                            .getResultList();
    }
    public List<Student> findByAttributes(String filterText){
        return em.createQuery("FROM Student a " +
                                        "WHERE lower(a.firstName) like lower(concat('%', :searchTerm, '%')) OR " +
                                            "lower(lastName) like  lower(concat('%', :searchTerm, '%')) OR " +
                                            "lower(email) like lower(concat('%', :searchTerm, '%'))", Student.class)
                                            .setParameter("searchTerm", filterText)
                                            .getResultList();
    }

    public Student addStudent(Student student) {
        EntityTransaction transaction = em.getTransaction();
        Student newStudent = student;
        transaction.begin();
        em.persist(newStudent);
        transaction.commit();
        return newStudent;
    }

    public void deleteStudent(Student student) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.remove(student);
        transaction.commit();
    }


    public void refreshEntities(Student student) {
        em.getEntityManagerFactory().getCache().evictAll();
        Student refreshedStudent = em.find(student.getClass(), student.getId());
        em.refresh(refreshedStudent);
    }
}
