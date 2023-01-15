package pl.sebastian.reminder.model;


import javax.persistence.*;
import javax.validation.constraints.NotNull;


@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    private String name;

    @NotNull
    private String author;

    @NotNull
    private String number;

    public void setReservationDetail(ReservationDetail reservationDetail) {
        this.reservationDetail = reservationDetail;
    }

    public ReservationDetail getReservationDetail() {
        return reservationDetail;
    }

    @OneToOne(mappedBy = "book")
    private ReservationDetail reservationDetail;


    public Book(String name, String author, String number) {
        this.name = name;
        this.author = author;
        this.number = number;
    }

    public Book() {
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return name + " " + author + " " + "(" + number + ")";
    }


}
