package com.sdl.selenium.extjs6.form;

import com.sdl.selenium.extjs6.button.Button;
import com.sdl.selenium.extjs6.slider.Slider;
import com.sdl.selenium.web.SearchType;
import com.sdl.selenium.web.WebLocator;
import com.sdl.selenium.web.link.WebLink;
import com.sdl.selenium.web.utils.RetryUtils;
import com.sdl.selenium.web.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriverException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class DateField extends TextField {

    private WebLocator trigger = new WebLocator(this).setRoot("/../../").setClasses("x-form-date-trigger");
    private WebLocator calendarLayer = new WebLocator().setClasses("x-datepicker", "x-layer").setAttribute("aria-hidden", "false").setVisibility(true);
    private Button monthYearButton = new Button(calendarLayer);
    private WebLocator selectOkButton = new WebLocator(calendarLayer).setText("OK").setVisibility(true).setInfoMessage("Ok");
    private WebLocator yearAndMonth = new WebLocator(calendarLayer).setClasses("x-monthpicker").setVisibility(true);
    private WebLocator nextYears = new WebLocator(yearAndMonth).setClasses("x-monthpicker-yearnav-next").setVisibility(true);
    private WebLocator prevYears = new WebLocator(yearAndMonth).setClasses("x-monthpicker-yearnav-prev").setVisibility(true);
    private WebLocator yearContainer = new WebLocator(yearAndMonth).setClasses("x-monthpicker-years");
    private WebLocator monthContainer = new WebLocator(yearAndMonth).setClasses("x-monthpicker-months");
    private WebLocator dayContainer = new WebLocator(calendarLayer).setClasses("x-datepicker-active");

    private WebLocator hourLayer = new WebLocator().setClasses("x-panel", "x-layer").setVisibility(true);
    private Slider hourSlider = new Slider(hourLayer).setLabel("Hour", SearchType.DEEP_CHILD_NODE_OR_SELF);
    private Slider minuteSlider = new Slider(hourLayer).setLabel("Min", SearchType.DEEP_CHILD_NODE_OR_SELF);

    private WebLocator tooltip = new WebLocator().setClasses("x-tip").setAttribute("aria-hidden", "false");

    public DateField() {
        setClassName("DateField");
    }

    public DateField(WebLocator container) {
        this();
        setContainer(container);
    }

    public DateField(WebLocator container, String label, SearchType... searchTypes) {
        this(container);
        if (searchTypes.length == 0) {
            searchTypes = new SearchType[]{SearchType.DEEP_CHILD_NODE_OR_SELF};
        } else {
            List<SearchType> types = new ArrayList<>(Arrays.asList(searchTypes));
            types.add(SearchType.DEEP_CHILD_NODE_OR_SELF);
            searchTypes = types.toArray(new SearchType[0]);
        }
        setLabel(label, searchTypes);
    }

    /**
     * example new DataField().setDate("19", "05", "2013")
     *
     * @param day   String 'dd'
     * @param month String 'MMM'
     * @param year  String 'yyyy'
     * @return true if is selected date, false when DataField doesn't exist
     */
    private boolean setDate(String day, String month, String year) {
        String fullDate = RetryUtils.retry(4, () -> monthYearButton.getText()).trim();
        if (!fullDate.contains(month) || !fullDate.contains(year)) {
            monthYearButton.click();
            if (!yearAndMonth.ready()) {
                monthYearButton.click();
            }
            goToYear(year, fullDate);
            WebLink monthEl = new WebLink(monthContainer).setText(month, SearchType.EQUALS).setInfoMessage("month " + month);
            monthEl.click();
            selectOkButton.click();
        }
        WebLocator dayEl = new WebLocator(dayContainer).setText(day, SearchType.EQUALS).setVisibility(true).setInfoMessage("day " + day);
        Utils.sleep(50);
        return dayEl.click();
    }

    private boolean setHour(String hour, String minute) {
        return hourSlider.move(Integer.parseInt(hour)) &&
                minuteSlider.move(Integer.parseInt(minute));
    }

    private void goToYear(String year, String fullDate) {
        int currentYear = Integer.parseInt(fullDate.split(" ")[1]);
        int yearInt = Integer.parseInt(year);
        boolean goNext = yearInt > currentYear;
        WebLocator btn = goNext ? nextYears : prevYears;
        WebLink yearEl = new WebLink(yearContainer).setText(year, SearchType.EQUALS).setVisibility(true).setInfoMessage("year " + year);
        boolean found;
        do {
            found = yearEl.waitToRender(150L, false);
            if (!found) {
                btn.click();
            }
        } while (!found && !foundYear(yearInt, goNext));
        try {
            yearEl.click();
        } catch (WebDriverException e) {
            if (tooltip.waitToRender(500L, false)) {
                WebLocator monthEl = new WebLocator(monthContainer).setText("Jan", SearchType.EQUALS).setInfoMessage("month Jan");
                monthEl.mouseOver();
                Utils.sleep(300);
            }
            yearEl.click();
        }
    }

    private boolean foundYear(int yearInt, boolean goNext) {
        WebLink yearEl = new WebLink(yearContainer).setResultIdx(12);
        int actualYear = Integer.parseInt(yearEl.getText());
        return goNext ? yearInt <= actualYear : yearInt >= actualYear;
    }

    /**
     * example new DataField().select("19/05/2013")
     *
     * @param date accept only this format: 'dd/MM/yyyy'
     * @return true if is selected date, false when DataField doesn't exist
     */
    public boolean select(String date) {
        return select(date, "dd/MM/yyyy");
    }

    public boolean select(String date, String format) {
        return select(date, format, Locale.ENGLISH);
    }

    public boolean select(String date, String format, Locale locale) {
        SimpleDateFormat inDateFormat = new SimpleDateFormat(format, locale);
        SimpleDateFormat outDateForm = new SimpleDateFormat("dd/MMM/yyyy H:m", locale);
        Date fromDate;
        try {
            fromDate = inDateFormat.parse(date);
            date = outDateForm.format(fromDate);
        } catch (ParseException e) {
            log.error("ParseException: {}", e);
        }

        log.debug("select: " + date);
        String[] dates = date.split("/");
        trigger.click();
        String[] extraDates = dates[2].split(" ");
        String year = extraDates[0];
        if (format.contains("H")) {
            String[] hours = extraDates[1].split(":");
            String hour = hours[0];
            String minutes = hours[1];
            return setHour(hour, minutes) && setDate(Integer.parseInt(dates[0]) + "", dates[1], year);
        } else {
            return setDate(Integer.parseInt(dates[0]) + "", dates[1], year);
        }
    }

    public boolean select(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/YYYY");
        String dateStr = sdf.format(date);
        return select(dateStr);
    }

    public boolean selectToday() {
        return select(new Date());
    }
}