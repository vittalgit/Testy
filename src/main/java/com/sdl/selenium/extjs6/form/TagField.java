package com.sdl.selenium.extjs6.form;

import com.sdl.selenium.web.SearchType;
import com.sdl.selenium.web.WebLocator;
import com.sdl.selenium.web.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Keys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@Slf4j
public class TagField extends Tag {

    public TagField() {
        setClassName("TagField");
        setBaseCls("x-tagfield-input-field");
        setTag("input");
    }

    public TagField(WebLocator container) {
        this();
        setContainer(container);
    }

    public TagField(WebLocator container, String label, SearchType... searchTypes) {
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

    public boolean select(String... values) {
        return select(SearchType.EQUALS, values);
    }

    public boolean select(boolean holdOpen, String... values) {
        return select(SearchType.EQUALS, holdOpen, values);
    }

    public boolean select(SearchType searchType, String... values) {
        boolean selected = doSelect(searchType, 300, true, values);
        assertThat("Could not selected value on : " + this, selected, is(true));
        return selected;
    }

    public boolean select(SearchType searchType, boolean holdOpen, String... values) {
        boolean selected = doSelect(searchType, 300, holdOpen, values);
        assertThat("Could not selected value on : " + this, selected, is(true));
        return selected;
    }

    /**
     * @param searchType         use {@link SearchType}
     * @param optionRenderMillis eg. 300ms
     * @param holdOpen           true | false
     * @param values             values[]
     * @return true if value was selected
     */
    public boolean doSelect(SearchType searchType, long optionRenderMillis, boolean holdOpen, String... values) {
        boolean selected = true;
        String info = toString();
        if (holdOpen) {
            if (expand()) {
                for (String value : values) {
                    WebLocator option = getComboEl(value, optionRenderMillis, searchType);
                    selected = selected && option.doClick();
                    if (selected) {
                        log.info("Set value(" + info + "): " + value);
                    } else {
                        log.debug("(" + info + ") The option '" + value + "' could not be located. " + option.getXPath());
                    }
                }
                collapse(); // to close combo
            }
        } else {
            for (String value : values) {
                expand();
                WebLocator option = getComboEl(value, optionRenderMillis, searchType);
                selected = selected && option.doClick();
                if (selected) {
                    log.info("Set value(" + info + "): " + value);
                } else {
                    log.debug("(" + info + ") The option '" + value + "' could not be located. " + option.getXPath());
                    collapse();
                }
            }
        }
        return selected;
    }

    public boolean setValue(String value) {
        assertReady(value);
        boolean setValue = executor.setValue(this, value);
        Utils.sleep(25);
        return setValue && sendKeys(Keys.ENTER) != null;
    }
}