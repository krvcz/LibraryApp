package pl.sebastian.reminder.view;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.Query;
import org.junit.Test;
import org.junit.BeforeClass;
import pl.sebastian.reminder.model.Book;
import pl.sebastian.reminder.model.ReservationDetail;
import pl.sebastian.reminder.model.Student;
import pl.sebastian.reminder.repository.BookRepository;
import pl.sebastian.reminder.repository.ReservationDetailRepository;
import pl.sebastian.reminder.repository.StudentRepository;


import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class ReservationsViewTest  {

    private static ReservationDetailRepository reservationDetailRepository;
    private static StudentRepository studentRepository;
    private static BookRepository bookRepository;

    private static List<ReservationDetail> reservationDetailExamplesCurrent;
    private static List<ReservationDetail> reservationDetailExamplesTerminated;




    @BeforeClass
    public static void initTest() {
        reservationDetailRepository = mock(ReservationDetailRepository.class);
        studentRepository = mock(StudentRepository.class);
        bookRepository = mock(BookRepository.class);


        mockStatic(ReservationDetailRepository.class);
        given(ReservationDetailRepository.getExistingInstance())
                .willReturn(reservationDetailRepository);

        mockStatic(StudentRepository.class);
        given(StudentRepository.getExistingInstance())
                .willReturn(studentRepository);

        mockStatic(BookRepository.class);
        given(BookRepository.getExistingInstance())
                .willReturn(bookRepository);

        reservationDetailExamplesCurrent = new ArrayList<>(List.of(new ReservationDetail(LocalDate.now(),
                        new Book("W pustyni i w puszczy", "Henryk Sienkiewicz", "III/PLN"),
                        new Student("Andrzej", "Komosa", "mail@mail.com")),
                new ReservationDetail(LocalDate.now(),
                        new Book("Krzyżacy", "Henryk Sienkiewicz", "III/PLN"),
                        new Student("Sebastian", "Jankowski", "aj@mail.com"))));

        reservationDetailExamplesTerminated = new ArrayList<>(List.of(new ReservationDetail(LocalDate.now(),
                        new Book("Moja Droga", "Robert Kubica", "III/PLN"),
                        new Student("Andrzej", "Komosa", "mail@mail.com")),
                new ReservationDetail(LocalDate.now(),
                        new Book("Krzyżacy", "Henryk Sienkiewicz", "III/PLN"),
                        new Student("Sebastian", "Jankowski", "aj@mail.com")),
                new ReservationDetail(LocalDate.now(),
                        new Book("Książka", "Autor", "III/PLN"),
                        new Student("Sebastian", "Jankowski", "aj@mail.com"))));


        given(reservationDetailRepository.getCurrentReservations()).willReturn(reservationDetailExamplesCurrent);
        given(reservationDetailRepository.getTerminatedReservations()).willReturn(reservationDetailExamplesTerminated);



    }


    @Test
    public void ShouldReturnErrorWhileAttemptInitializeRepositories(){


        //given
        String errorMessage = "Instance does not exist! Initialize object first ";

        //when
        try {
            ReservationsView reservationsView = new ReservationsView();
        } catch (ExceptionInInitializerError e) {
            //then
                assertEquals(errorMessage, e.getMessage());
        }


    }

    @Test
    public void ShouldHaveTabsComponentWithNumberOfElementsInGrid() {
        //given
        String numericLabelFirstTab = "2";
        String numericLabelSecondTab = "3";


        //when
        ReservationsView reservationsView = new ReservationsView();

        Tabs checkedTabs = (Tabs) reservationsView.getComponentAt(0);
        checkedTabs.setSelectedIndex(0);
        Span numberOfCurrentReservationInCurrentTab = (Span) checkedTabs.getSelectedTab().getChildren().collect(Collectors.toList()).get(1);

        checkedTabs.setSelectedIndex(1);

        Span numberOfTerminatedReservationInCurrentTab = (Span) checkedTabs.getSelectedTab().getChildren().collect(Collectors.toList()).get(1);

        checkedTabs.setSelectedIndex(2);
        Span numberOfWaitReservationInCurrentTab = (Span) checkedTabs.getSelectedTab().getChildren().collect(Collectors.toList()).get(1);

        //then
        assertEquals(numericLabelFirstTab, numberOfCurrentReservationInCurrentTab.getElement().getText());
        assertEquals(numericLabelSecondTab, numberOfTerminatedReservationInCurrentTab.getElement().getText());
        assertEquals(numericLabelSecondTab, numberOfWaitReservationInCurrentTab.getElement().getText());

        }

    @Test
    public void ShouldReturn5Columns(){
        //given
        int numberOfColumns = 5;

        //when
        ReservationsView reservationsView = new ReservationsView();
        Grid checkedGrid = (Grid) reservationsView.getComponentAt(2);

        //then
        assertEquals(numberOfColumns, checkedGrid.getColumns().size());
        }
    @Test
    public void ShouldReturn2RowsInGrid(){
        //given
        int numberOfRows = 2;

        //when
        ReservationsView reservationsView = new ReservationsView();
        Grid checkedGrid = (Grid) reservationsView.getComponentAt(2);

        //then
        assertEquals(numberOfRows, checkedGrid.getDataProvider().size(new Query<>()));
    }

    @Test
    public void ShouldReturn3RowsInGrid(){
        //given
        int numberOfRows = 3;

        //when
        ReservationsView reservationsView = new ReservationsView();
        Grid checkedGrid = (Grid) reservationsView.getComponentAt(2);
        Tabs checkedTabs = (Tabs) reservationsView.getComponentAt(0);
        checkedTabs.setSelectedIndex(1);


        //then
        assertEquals(numberOfRows, checkedGrid.getDataProvider().size(new Query<>()));
    }
    @Test
    public void ShouldReturnEmptyGridWhenFilteredAndNotMockedEnityManager(){
        //given
        int numberOfRows = 0;


        //when
        ReservationsView reservationsView = new ReservationsView();
        Grid checkedGrid = (Grid) reservationsView.getComponentAt(2);
        HorizontalLayout checkedLayout = (HorizontalLayout) reservationsView.getComponentAt(1);
        List<Component> components = checkedLayout.getChildren().collect(Collectors.toList());
        TextField filterField = (TextField) components.get(0);
        filterField.setValue("Andrzej");



        //then
        assertEquals(numberOfRows, checkedGrid.getDataProvider().size(new Query<>()));
    }

    @Test
    public void ShouldReturnOneValueInGridWhenFilteredAndMockedEnityManager(){
        //given
        int numberOfRows = 1;
        String filteredString = "Andrzej";
        given(reservationDetailRepository.findByAttributes(filteredString)).willReturn(reservationDetailExamplesTerminated.stream().filter(reservationDetail ->
               reservationDetail.getStudent().getFirstName().equals(filteredString)).collect(Collectors.toList()));

        //when
        ReservationsView reservationsView = new ReservationsView();
        Grid checkedGrid = (Grid) reservationsView.getComponentAt(2);
        HorizontalLayout checkedLayout = (HorizontalLayout) reservationsView.getComponentAt(1);
        List<Component> components = checkedLayout.getChildren().collect(Collectors.toList());
        TextField filterField = (TextField) components.get(0);
        filterField.setValue("Andrzej");



        //then
        assertEquals(numberOfRows, checkedGrid.getDataProvider().size(new Query<>()));
    }

}
