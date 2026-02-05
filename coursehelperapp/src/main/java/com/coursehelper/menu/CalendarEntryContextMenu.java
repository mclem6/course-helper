package com.coursehelper.menu;


import java.util.Optional;

import com.calendarfx.model.Entry;
import com.calendarfx.view.DayViewBase;
import com.calendarfx.view.EntryViewBase;
import com.coursehelper.UserSession;
import com.coursehelper.dao.EventDAO;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;

public class CalendarEntryContextMenu {

    public static void install(DayViewBase view, EventDAO eventDAO, UserSession userSession){
        // CUSTOM CONTEXT MENU
        view.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
            Node node = event.getPickResult().getIntersectedNode();

            // Traverse up the node hierarchy to find the EntryViewBase
            while (node != null && !(node instanceof EntryViewBase)) {
                node = node.getParent();
            }

            if (node instanceof EntryViewBase) {
                EntryViewBase<?> entryView = (EntryViewBase<?>) node;
                Entry<?> entry = entryView.getEntry();

                //get event's ID // should be same for recurring events 
                Integer eventId = (Integer)entry.getRecurrenceSourceEntry().getUserObject();

                // Create custom context menu
                ContextMenu contextMenu = new ContextMenu();

                MenuItem editItem = new MenuItem("Edit");
                editItem.setOnAction(e -> {
                    // Replace this with your custom editing logic
                    // showMyEditDialog(entry);
                });
                
                //Delete entry from database and calendar
                MenuItem deleteItem = new MenuItem("Delete");
                deleteItem.setOnAction(e -> {

                    //if reoccuring event, ask if user wants to delete all or just single event
                    if (entry.isRecurring()){

                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Delete Recurring Event");
                        alert.setHeaderText("This is a recurring event.");
                        alert.setContentText("Deleting this event will delete all dates associated with this event.");

                        ButtonType deleteAll = new ButtonType("Confirm Delete");
                        ButtonType cancel = ButtonType.CANCEL;

                        alert.getButtonTypes().setAll(deleteAll, cancel);

                        Optional<ButtonType> result = alert.showAndWait();

                        if(result.isPresent()){

                            // delete event from calendar and delete from events table
                            if (result.get() == deleteAll){
                                //delete event ftom events table 
                                eventDAO.deleteEvent(userSession.getUserId(), eventId);

                                //delete all entries from calendar 
                                entry.getRecurrenceSourceEntry().removeFromCalendar();

                            }
                        } 
                    }
                    //single event
                    else {
                        
                        //delete from events table
                        eventDAO.deleteEvent(userSession.getUserId(), eventId);

                        //remove from calendar
                        entry.removeFromCalendar();
                    }
        
        
                });

                contextMenu.getItems().addAll(editItem, deleteItem);
                contextMenu.show(entryView, event.getScreenX(), event.getScreenY());

                event.consume(); // Prevent default context menu from appearing
            }
        });        

     }


}