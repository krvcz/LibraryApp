package pl.sebastian.reminder.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

abstract class GridAbstractView extends VerticalLayout implements FilterGrid {

    protected TextField filterText;


    protected TextField createFilter() {
        filterText = new TextField();
        filterText.setPlaceholder("Filter by name ....");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateGrid());
        return filterText;
    }




}
