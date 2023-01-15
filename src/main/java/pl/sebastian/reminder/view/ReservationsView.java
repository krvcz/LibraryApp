package pl.sebastian.reminder.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.RequiredFieldConfigurator;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;

import org.vaadin.crudui.layout.impl.WindowBasedCrudLayout;
import pl.sebastian.reminder.model.Book;
import pl.sebastian.reminder.model.ReservationDetail;
import pl.sebastian.reminder.model.Student;
import pl.sebastian.reminder.repository.BookRepository;
import pl.sebastian.reminder.repository.ReservationDetailRepository;
import pl.sebastian.reminder.repository.StudentRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.util.List;


@Route(value = "/reservations", layout = MainLayout.class)
public class ReservationsView extends GridAbstractView {

//    private static EntityManagerFactory factory = Persistence.createEntityManagerFactory("sample");
//    private  static EntityManager em = factory.createEntityManager();

    private ReservationDetailRepository reservationDetailRepository;

    private StudentRepository studentRepository;
    private BookRepository bookRepository;

    private Grid<ReservationDetail> grid;

    private Tab current;
    private Tab terminated;
    private Tab wait;
    private Tabs tabs;



    public ReservationsView() {


        reservationDetailRepository = ReservationDetailRepository.getExistingInstance();
        studentRepository = StudentRepository.getExistingInstance();
        bookRepository = BookRepository.getExistingInstance();

        createTabs();
        createGrid();



    }

    private void createTabs() {
        current = createTab("Current", reservationDetailRepository.getCurrentReservations().size());
        terminated = createTab("Terminated", reservationDetailRepository.getTerminatedReservations().size());
        wait = createTab("Wait", 10);
        wait.setEnabled(false);


        tabs = new Tabs(current, terminated, wait);

        tabs.addSelectedChangeListener(selectedChangeEvent -> setContent(tabs.getSelectedTab()));

        add(tabs);

    }

    private void createGrid() {


        Button addButton = new Button("Add reservation", e -> showAddInterface());

        filterText = createFilter();

        HorizontalLayout horizontalLayout = new HorizontalLayout(filterText, addButton);


        grid = new Grid<ReservationDetail>(ReservationDetail.class);

        grid.removeAllColumns();

        grid.addColumn(studentRepo -> {
            return studentRepo.getStudent().getFirstName() + " " + studentRepo.getStudent().getLastName();
        }).setHeader("Student").setId("Student");


        grid.addColumn(bookRepo -> {
            return bookRepo.getBook().getAuthor() + " " + bookRepo.getBook().getName();
        }).setHeader("Book").setId("Book");

        grid.addColumn(ReservationDetail::getDateOfReturn).setHeader("Date Of Return").setId("Date_Of_Return");


        Grid.Column<ReservationDetail> studentColumn = grid.getColumns().get(0);
        Grid.Column<ReservationDetail> bookColumn = grid.getColumns().get(1);
        Grid.Column<ReservationDetail> dateOfReturnColumn = grid.getColumns().get(2);


        Editor<ReservationDetail> editor = grid.getEditor();
        Grid.Column<ReservationDetail> editColumn = grid.addComponentColumn(reservationDetail -> {
            Button editButton = new Button("Edit");
            editButton.addClickListener(e -> {
                if (editor.isOpen())
                    editor.cancel();
                grid.getEditor().editItem(reservationDetail);
            });
            return editButton;
        }).setWidth("150px").setFlexGrow(0);

        Grid.Column<ReservationDetail> reservationDetailColumn = grid.addColumn(
                new ComponentRenderer<>(Button::new, (button, reservation) -> {
                    button.addThemeVariants(ButtonVariant.LUMO_ICON,
                            ButtonVariant.LUMO_ERROR,
                            ButtonVariant.LUMO_TERTIARY);
                    button.addClickListener(e -> removeReservation(reservation));
                    button.setIcon(new Icon(VaadinIcon.TRASH));
                }));

        Binder<ReservationDetail> binder = new Binder<>(ReservationDetail.class);
        editor.setBinder(binder);
        editor.setBuffered(true);

        ComboBox<Student> comboBoxStudentField = new ComboBox<>(
                "Student");
        List<Student> students =  studentRepository.getListOfStudents();
        ListDataProvider<Student> listOfStudents = new ListDataProvider(students);
        comboBoxStudentField.setItems(listOfStudents);


        ComboBox<Book> comboBoxBookField = new ComboBox<>(
                "Book");
        List<Book> books =  bookRepository.findAvailable();
        ListDataProvider<Book> listOfBooks = new ListDataProvider(books);
        comboBoxBookField.setItems(listOfBooks);

        DatePicker dateOfReturnField = new DatePicker("Date Of Return");

        binder.forField(comboBoxStudentField)
                .bind(ReservationDetail::getStudent, ReservationDetail::setStudent);

        binder.forField(comboBoxBookField)
                .bind(ReservationDetail::getBook, ReservationDetail::setBook);

        binder.forField(dateOfReturnField)
                .bind(ReservationDetail::getDateOfReturn, ReservationDetail::setDateOfReturn);


        studentColumn.setEditorComponent(comboBoxStudentField);
        bookColumn.setEditorComponent(comboBoxBookField);
        dateOfReturnColumn.setEditorComponent(dateOfReturnField);

        Button saveButton = new Button("Save", e -> saveEditor(editor));


        Button cancelButton = new Button(VaadinIcon.CLOSE.create(),
                e -> editor.cancel());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ICON,
                ButtonVariant.LUMO_ERROR);
        HorizontalLayout actions = new HorizontalLayout(saveButton,
                cancelButton);
        actions.setPadding(false);
        editColumn.setEditorComponent(actions);


        grid.setItems(reservationDetailRepository.getCurrentReservations());

        add(horizontalLayout, grid);

    }


    private void showAddInterface() {


        BeanValidationBinder<ReservationDetail> binder = new BeanValidationBinder<>(ReservationDetail.class);

        binder.setBean(new ReservationDetail());

        FormLayout formLayout = new FormLayout();

        ComboBox<Student> comboBoxStudent = new ComboBox<>(
                "Student");

        List<Student> students =  studentRepository.getListOfStudents();
        ListDataProvider<Student> listOfStudents = new ListDataProvider(students);
        comboBoxStudent.setItems(listOfStudents);
        comboBoxStudent.setItemLabelGenerator(Student::toString);

        binder.setRequiredConfigurator(
                RequiredFieldConfigurator.NOT_EMPTY
                        .chain(RequiredFieldConfigurator.NOT_NULL));

        binder.forField(comboBoxStudent)
                .bind(
                ReservationDetail::getStudent,
                ReservationDetail::setStudent);

        ComboBox<Book> comboBoxBookField = new ComboBox<>(
                "Book");
        List<Book> books =  bookRepository.findAvailable();;
        ListDataProvider<Book> listOfBooks = new ListDataProvider(books);
        comboBoxBookField.setItems(listOfBooks);

        binder.forField(comboBoxBookField)

                .bind(
                ReservationDetail::getBook,
                ReservationDetail::setBook);

        DatePicker dateOfReturn = new DatePicker("Date Of Return");

        binder.forField(dateOfReturn)
                .bind(
                ReservationDetail::getDateOfReturn,
                ReservationDetail::setDateOfReturn);

        formLayout.add(
                comboBoxStudent, comboBoxBookField, dateOfReturn
        );


        VerticalLayout dialogLayout = new VerticalLayout(formLayout);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        Dialog dialog = new Dialog();


        dialog.setHeaderTitle("New reservation");

        dialog.add(dialogLayout);

        Button saveButton = new Button("Add", e -> {
            reservationDetailRepository.addReservation(binder.getBean());
            grid.setItems(reservationDetailRepository.getCurrentReservations());
            grid.getDataProvider().refreshItem(binder.getBean());
            grid.getDataProvider().refreshAll();
            dialog.close();
        }
        );
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel", e -> dialog.close());
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(saveButton);
        dialog.open();
    }



    private boolean saveEditor(Editor<ReservationDetail> editor) {
        StudentRepository studentRepository = StudentRepository.getExistingInstance();
        BookRepository bookRepository = BookRepository.getExistingInstance();
        ReservationDetail reservationDetail = editor.getItem();
        boolean result = editor.save();
        reservationDetailRepository.updateReservation(reservationDetail);
        reservationDetailRepository.refreshEnitities(reservationDetail);
        studentRepository.refreshEntities(reservationDetail.getStudent());
        bookRepository.refreshEntities(reservationDetail.getBook());

        grid.setItems(reservationDetailRepository.getCurrentReservations());
        grid.getDataProvider().refreshItem(reservationDetail);
        grid.getDataProvider().refreshAll();


        return result;

    }

    private void removeReservation(ReservationDetail reservation) {
        StudentRepository studentRepository = StudentRepository.getExistingInstance();
        BookRepository bookRepository = BookRepository.getExistingInstance();
        reservationDetailRepository.deleteReservation(reservation);
        reservationDetailRepository.refreshEnitities(reservation);
        grid.setItems(reservationDetailRepository.getCurrentReservations());
        grid.getDataProvider().refreshAll();
        studentRepository.refreshEntities(reservation.getStudent());
        bookRepository.refreshEntities(reservation.getBook());

    }


    private void setContent(Tab selectedTab) {
        if (selectedTab.equals(current)) {
            grid.setItems(reservationDetailRepository.getCurrentReservations());
            grid.getDataProvider().refreshAll();

        } else if (selectedTab.equals(terminated)) {
            grid.setItems(reservationDetailRepository.getTerminatedReservations());
            grid.getDataProvider().refreshAll();

        } else {
            grid.setItems(reservationDetailRepository.getCurrentReservations());
            grid.getDataProvider().refreshAll();

        }
    }

    private static Tab createTab(String labelText, int messageCount) {
            Span label = new Span(labelText);
            Span counter = new Span(String.valueOf(messageCount));
            counter.getElement().getThemeList().add("badge pill small contrast");
            counter.getStyle().set("margin-inline-start", "var(--lumo-space-s)");
            // Accessible badge label
            String counterLabel = String.format("%d unread messages", messageCount);
            counter.getElement().setAttribute("aria-label", counterLabel);
            counter.getElement().setAttribute("title", counterLabel);

            return new Tab(label, counter);
        }

    @Override
    public void updateGrid() {
        grid.setItems(reservationDetailRepository.findByAttributes(filterText.getValue()));
    }
}
