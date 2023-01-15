package pl.sebastian.reminder.view;



import com.vaadin.flow.router.Route;
import org.vaadin.crudui.crud.impl.GridCrud;
import pl.sebastian.reminder.model.Book;
import pl.sebastian.reminder.repository.BookRepository;




@Route(value = "/books", layout = MainLayout.class)
public class BooksView extends GridAbstractView {




    private BookRepository bookRepository;


    private GridCrud<Book> crud;

    public BooksView() {
        bookRepository = BookRepository.getExistingInstance();

        crud = createGridCrud();

        add(crud);
    }

    private GridCrud<Book> createGridCrud() {



        GridCrud<Book> crud = new GridCrud<>(Book.class);

        filterText = createFilter();

        crud.getCrudLayout().addToolbarComponent(filterText);

        crud.getGrid().removeAllColumns();

        crud.getGrid().addColumn(Book::getId).setHeader("ID").setId("ID");

        crud.getGrid().addColumn(Book::getAuthor).setHeader("Author").setId("Author");

        crud.getGrid().addColumn(Book::getName).setHeader("Name").setId("Name");

        crud.getGrid().addColumn(Book::getNumber).setHeader("Identity").setId("Identity");

        crud.setFindAllOperation(() -> bookRepository.findAll());

        crud.setAddOperation(bookRepository::addBook);

        crud.setDeleteOperation(bookRepository::deleteBook);
        crud.getCrudFormFactory().setUseBeanValidation(true);

        return crud;

    }


    public void updateGrid() {
        crud.getGrid().setItems(bookRepository.findByAttributes(filterText.getValue()));
        crud.getGrid().getDataProvider().refreshAll();
    }
}