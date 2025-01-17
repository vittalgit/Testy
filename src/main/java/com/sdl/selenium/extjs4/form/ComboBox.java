package com.sdl.selenium.extjs4.form;

import com.sdl.selenium.extjs6.form.Combo;
import com.sdl.selenium.web.SearchType;
import com.sdl.selenium.web.WebLocator;
import com.sdl.selenium.web.utils.Utils;
import lombok.extern.slf4j.Slf4j;

import static org.hamcrest.MatcherAssert.assertThat;

@Slf4j
public class ComboBox extends Combo {

    public ComboBox() {
        setClassName("ComboBox");
    }

    public ComboBox(WebLocator container) {
        this();
        setContainer(container);
    }

    public ComboBox(WebLocator container, String label) {
        this(container);
        setLabel(label, SearchType.DEEP_CHILD_NODE_OR_SELF);
    }

    /**
     * @param value              value
     * @param searchType         true or false
     * @param optionRenderMillis 300ms
     * @return true if value was selected
     * @deprecated use {@link #doSelect(String, SearchType, long)}
     */
    public boolean select(String value, SearchType searchType, long optionRenderMillis) {
        return doSelect(value, searchType, optionRenderMillis);
    }

    /**
     * @param value              value
     * @param searchType         true or false
     * @param optionRenderMillis 300ms
     * @return true if value was selected
     */
    public boolean doSelect(String value, SearchType searchType, long optionRenderMillis) {
        boolean selected;
        String info = toString();
        WebLocator option = getComboEl(value, searchType, optionRenderMillis);

        if (expand()) {
            selected = option.click();
            if (selected) {
                log.info("Set value(" + info + "): " + value);
                Utils.sleep(20);
                return true;
            } else {
                collapse(); // to close combo
            }
            log.debug("(" + info + ") The option '" + value + "' could not be located. " + option.getXPath());
        } else {
            log.debug("(" + info + ") The combo or arrow could not be located.");
        }
        return false;
    }

    private WebLocator getComboEl(String value, SearchType searchType, long optionRenderMillis) {
        String classList = "x-boundlist";
        WebLocator comboListElement = new WebLocator(classList).setInfoMessage(this + " -> " + classList);
        return new WebLocator(comboListElement).setText(value, searchType).setRenderMillis(optionRenderMillis).setInfoMessage(value);
    }

    public boolean select(String value, SearchType searchType) {
        boolean selected = doSelect(value, searchType, 500);
        assertThat("Could not selected value on : " + this, selected);
        return selected;
    }

    @Override
    public boolean select(String value) {
        return select(value, SearchType.EQUALS);
    }

    @Override
    public String getValue() {
        String value = null;
        if (expand()) {
            WebLocator option = getComboEl(null, SearchType.CONTAINS, 300).setClasses("x-boundlist-selected");
            value = option.getText();
            collapse(); // to close combo
        } else {
            log.debug("(" + this + ") The combo or arrow could not be located.");
        }
        return value;
    }

    @Override
    public boolean expand() {
        return clickIcon("arrow");
    }

    @Override
    public boolean collapse() {
        return clickIcon("arrow");
    }
}