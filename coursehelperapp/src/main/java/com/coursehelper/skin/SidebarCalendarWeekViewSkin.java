package com.coursehelper.skin;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Locale;

import com.calendarfx.view.DetailedWeekView;
import com.calendarfx.view.WeekDayHeaderView;

import impl.com.calendarfx.view.DetailedWeekViewSkin;
import javafx.application.Platform;



// NOTE: This skin depends on CalendarFX internal API (impl.*)
// Tested with CalendarFX 11.12.7

public class SidebarCalendarWeekViewSkin 
        extends DetailedWeekViewSkin {

            private int visibleHours = 6;
            
            
    public SidebarCalendarWeekViewSkin(DetailedWeekView view){
            super(view);
            init();
            

    }

    private void init(){
        //set set short label
        setShortLabelHeader();

        //hide time scale and scroll
        getSkinnable().setShowTimeScaleView(false);
        getSkinnable().setShowScrollBar(false);

        //css custom
        removeHourLines();

        //set color?
    }


    protected void removeHourLines() {

        Runnable removeHourLines = () -> {
            // getSkinnable().lookupAll(".full-hour-line").forEach(node -> node.setVisible(false));
            // getSkinnable().lookupAll(".half-hour-line").forEach(node -> node.setVisible(false));

            getSkinnable().getStylesheets().add(getClass().getResource("/stylesheets/LightMode/sideCalendar.css").toExternalForm());

        };
        Platform.runLater(removeHourLines);
        
    }


    private void setShortLabelHeader(){

        WeekDayHeaderView header = getSkinnable().getWeekDayHeaderView();

        header.setCellFactory(view -> {
            WeekDayHeaderView.WeekDayHeaderCell cell =
                    new WeekDayHeaderView.WeekDayHeaderCell(view);


            Runnable updateText = () -> {
                LocalDate date = cell.getDate();
                if (date != null) {
                    String letter =
                            date.getDayOfWeek()
                                .getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                                .substring(0, 1);
                    cell.setText(letter);
                }
            };


            // Date changes
            cell.dateProperty().addListener((obs, o, n) -> updateText.run());

            // Today changes (midnight rollover)
            view.todayProperty().addListener(obs -> updateText.run());

            // Show-today toggle
            view.showTodayProperty().addListener(obs -> updateText.run());

            Platform.runLater(updateText);
            
            return cell;
        });


    }


    private void updateVisibleTimeRange(){
        DetailedWeekView view = getSkinnable();

        LocalTime start = LocalTime.now();
        LocalTime end = start.plusHours(visibleHours);

        view.setStartTime(start);
        view.setEndTime(end);
    }

    

    @Override
    public void dispose() {
        // If you later store listeners as fields, detach them here
        super.dispose();
    }

}