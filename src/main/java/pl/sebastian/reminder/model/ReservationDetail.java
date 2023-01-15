package pl.sebastian.reminder.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

@Entity
public class ReservationDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    private LocalDate dateOfReturn;


    @OneToOne(cascade = CascadeType.REFRESH, fetch=FetchType.EAGER)
    @JoinColumn(name = "book_id", referencedColumnName = "id", unique=true)
    @NotNull
    private Book book;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch=FetchType.EAGER)
    @JoinColumn(name="student_id", referencedColumnName = "id")
    @NotNull
    private Student student;



    public void setBook(Book book) {
        this.book = book;
    }

    public void setStudent(Student student) {
        this.student = student;
    }



    public ReservationDetail(LocalDate dateOfReturn, Book book, Student student) {
        this.dateOfReturn = dateOfReturn;
        this.book = book;
        this.student = student;
    }

    public ReservationDetail() {
    }


    @Override
    public String toString() {
        return "ReservationDetail{" +
                "id=" + id +
                '}';
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public LocalDate getDateOfReturn() {
        return dateOfReturn;
    }

    public void setDateOfReturn(LocalDate dateOfReturn) {
        this.dateOfReturn = dateOfReturn;
    }

    public Book getBook() {
        return book;
    }

    public Student getStudent() {
        return student;
    }




}
