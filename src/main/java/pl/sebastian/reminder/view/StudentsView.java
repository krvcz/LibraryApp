package pl.sebastian.reminder.view;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;

import com.vaadin.flow.router.Route;
import org.vaadin.crudui.crud.CrudOperation;
import org.vaadin.crudui.crud.impl.GridCrud;

import pl.sebastian.reminder.model.Student;
import pl.sebastian.reminder.repository.StudentRepository;



@Route(value = "/students", layout = MainLayout.class)
public class StudentsView extends GridAbstractView {

    private StudentRepository studentRepository;


    private GridCrud<Student> crud;
    private TextField filterText;


    public StudentsView() {
        studentRepository = StudentRepository.getExistingInstance();
        crud = createGridCrud();

        add(crud);

    }

    private GridCrud<Student> createGridCrud() {


        GridCrud<Student> crud = new GridCrud<>(Student.class);
        filterText = createFilter();
        crud.getCrudLayout().addToolbarComponent(filterText);
        crud.getGrid().removeAllColumns();

        crud.getGrid().addColumn(Student::getId).setHeader("ID").setId("ID");

        crud.getGrid().addColumn(Student::getFirstName).setHeader("First Name").setId("First Name");

        crud.getGrid().addColumn(Student::getLastName).setHeader("Last Name").setId("Last Name");

        crud.getGrid().addColumn(Student::getEmail).setHeader("Email").setId("Email");

        crud.setFindAllOperation(() -> studentRepository.getListOfStudents());
        crud.setAddOperation(studentRepository::addStudent);
        crud.setDeleteOperation(studentRepository::deleteStudent);
        crud.getCrudFormFactory().setUseBeanValidation(true);


//        crud.getCrudFormFactory().setErrorListener(e -> {
//                    Notification notification = new Notification("All Fields are required!");
//                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
//                    try {
//                        notification.open();
//                    }
//                    notification.open();
//                });




        return crud;
    }

    public void updateGrid() {
        crud.getGrid().setItems(studentRepository.findByAttributes(filterText.getValue()));
    }
}
