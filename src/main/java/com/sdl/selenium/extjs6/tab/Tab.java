package com.sdl.selenium.extjs6.tab;

import com.google.common.base.Strings;
import com.sdl.selenium.conditions.ConditionManager;
import com.sdl.selenium.conditions.RenderSuccessCondition;
import com.sdl.selenium.web.SearchType;
import com.sdl.selenium.web.WebLocator;
import com.sdl.selenium.web.XPathBuilder;
import com.sdl.selenium.web.link.WebLink;
import com.sdl.selenium.web.tab.ITab;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Getter
public class Tab extends WebLocator implements ITab {
    private String iconCls;

    public Tab() {
        setClassName("TabPanel");
        setBaseCls("x-tab-bar");
        WebLink activeTab = new WebLink().setClasses("x-tab-active");
        setTemplateTitle(activeTab);
    }

    public Tab(String title, SearchType... searchTypes) {
        this();
        List<SearchType> types = new LinkedList<>(Arrays.asList(searchTypes));
        types.addAll(Arrays.asList(SearchType.EQUALS, SearchType.DEEP_CHILD_NODE));
        setTitle(title, types.toArray(new SearchType[0]));
    }

    public Tab(WebLocator container) {
        this();
        setContainer(container);
    }

    public Tab(WebLocator container, String title, SearchType... searchTypes) {
        this(title, searchTypes);
        setContainer(container);
    }

    public <T extends Tab> T setIconCls(final String iconCls) {
        this.iconCls = iconCls;
        return (T) this;
    }

    public WebLocator getTitleInactiveEl() {
        WebLocator container = new WebLocator(getPathBuilder().getContainer()).setClasses(getPathBuilder().getBaseCls()).setTag(getPathBuilder().getTag());
        WebLink link = new WebLink(container).setClasses("x-tab");
        if (!Strings.isNullOrEmpty(getPathBuilder().getTitle())) {
            List<SearchType> ts = getPathBuilder().getSearchTitleType();
            link.setText(getPathBuilder().getTitle(), ts.toArray(new SearchType[0]));
        }
        if (getPathBuilder().getChildNodes() != null && !getPathBuilder().getChildNodes().isEmpty()) {
            link.setChildNodes(getPathBuilder().getChildNodes().toArray(new WebLocator[0]));
        }
        return link.setInfoMessage(getPathBuilder().getTitle() + " Tab");
    }

    /**
     * this method return the path of the main Tab (that contains also this Tab/Panel)
     *
     * @return the path of the main TabPanel
     */
    private String getBaseTabPanelPath() {
        String selector = getPathBuilder().getBasePath();
        WebLocator child = new WebLocator().setClasses(iconCls);
        WebLink activeTab = new WebLink().setClasses("x-tab-active").setChildNodes(child);
        selector = selector + (Strings.isNullOrEmpty(iconCls) ? "" : " and count(." + activeTab.getXPath() + ") > 0");
        return getPathBuilder().getRoot() + getPathBuilder().getTag() + "[" + selector + "]";
    }

    protected XPathBuilder createXPathBuilder() {
        return new XPathBuilder() {
            /**
             * this method return the path of only one visible div from the main TabPanel
             * @param disabled disabled
             * @return the path of only one visible div from the main TabPanel
             */
            @Override
            public String getItemPath(boolean disabled) {
                WebLocator body = new WebLocator().setTag("following-sibling::*").setClasses("x-panel-body");
                WebLocator tab = new WebLocator(body).setRoot("/").setExcludeClasses("x-hidden-offsets").setClasses("x-tabpanel-child");
                return getBaseTabPanelPath() + tab.getXPath();
            }

            @Override
            public void addTextInPath(List<String> selector, String text, String pattern, List<SearchType> searchTypes) {

            }
        };
    }

    /**
     * After the tab is set to active
     *
     * @return true or false
     */
    @Override
    public boolean setActive() {
        WebLocator inactiveTab = getTitleInactiveEl().setExcludeClasses("x-tab-active");
        boolean activated = isActive() || inactiveTab.click();
        if (activated) {
            log.info("setActive : " + toString());
        }
        return activated;
    }

    @Override
    public boolean isActive() {
        return new ConditionManager(Duration.ofMillis(200)).add(new RenderSuccessCondition(this)).execute().isSuccess();
    }

    public boolean isTabDisplayed() {
        return getTitleInactiveEl().ready();
    }

    public boolean close() {
        WebLocator titleEl = getTitleInactiveEl().setClasses("x-tab-active");
        WebLocator closeEl = new WebLocator(titleEl).setClasses("x-tab-close-btn");
        return closeEl.click();
    }
}