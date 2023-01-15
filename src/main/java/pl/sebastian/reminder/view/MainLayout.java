package pl.sebastian.reminder.view;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.RouterLink;

public  class MainLayout extends AppLayout {


    public MainLayout() {
        H1 title = new H1("MyApp");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("left", "var(--lumo-space-l)").set("margin", "0")
                .set("position", "absolute");

        FlexLayout centeredLayout = new FlexLayout();

        Tab studentsTab = new Tab(VaadinIcon.USER.create(), new Span("Students"));

        studentsTab.add(new RouterLink(StudentsView.class));

        Tab booksTab = new Tab(VaadinIcon.BOOK.create(), new Span("Books"));

        booksTab.add(new RouterLink(BooksView.class));

        Tab reservationsTab = new Tab(VaadinIcon.CHECK_CIRCLE.create(), new Span("Reservations"));

        reservationsTab.add(new RouterLink(ReservationsView.class));

        Tabs tabs = new Tabs(studentsTab, booksTab, reservationsTab);

        centeredLayout.setSizeFull();

        centeredLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        centeredLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        centeredLayout.add(tabs);

        for (Tab tab : new Tab[] { studentsTab, booksTab, reservationsTab }) {
            tab.addThemeVariants(TabVariant.LUMO_ICON_ON_TOP);
        }
        tabs.addThemeVariants(TabsVariant.LUMO_CENTERED);

        addToNavbar(false, centeredLayout);

    }
}
