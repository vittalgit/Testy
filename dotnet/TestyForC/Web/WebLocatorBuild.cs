﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace TestyForC.Web
{
    public abstract class WebLocatorBuild <T>  where T : WebLocatorBuild<T>
    {
        private XPathBuilder xPath = new XPathBuilder();


        public T setContainer(WebLocator container)
        {
            xPath.Container = container;
            return (T)this;
        }

        public T setRoot(String root)
        {
            xPath.Root = root;
            return (T)this;
        }

        public T setTag(string tag)
        {
            xPath.Tag = tag;
            return (T)this;
        }

        public T setId(string id)
        {
            xPath.Id = id;
            return (T)this;
        }

        public T setElPath(string elPath)
        {
            xPath.ElPath = elPath;
            return (T)this;
        }

        public T setElCssSelector(string elCssSelector)
        {
            xPath.ElCssSelector = elCssSelector;
            return (T)this;
        }

        public T setBaseCls(string baseCls)
        {
            xPath.BaseCls = baseCls;
            return (T)this;
        }

        public T setCls(string cls)
        {
            xPath.Cls = cls;
            return (T)this;
        }

        public T setClasses(string[] classes)
        {
            xPath.Classes = new List<string>(classes);
            return (T)this;
        }

        public T setExcludeClasses(string[] excludeClasses)
        {
            xPath.ExcludeClasses = new List<string>(excludeClasses);
            return (T)this;
        }

        public T setName(string name)
        {
            xPath.Name = name; ;
            return (T)this;
        }

        public T setText(string text, List<SearchType> searchTypes)
        {
            xPath.Text = text;
            xPath.SearchTextType = searchTypes;
            return (T)this;
        }

        public T setSearchTextType(List<SearchType> searchTypes)
        {
            xPath.SearchTextType = searchTypes;
            return (T)this;
        }

        public T setSearchTitleType(List<SearchType> searchTypes)
        {
            xPath.SearchTitleType = searchTypes;
            return (T)this;
        }

        public T setSearchLabelType(List<SearchType> searchTypes)
        {
            xPath.SearchLabelType = searchTypes;
            return (T)this;
        }

        public T setStyle(String style)
        {
            xPath.Style = style;
            return (T)this;
        }

        public T setTitle(string title, List<SearchType> searchTypes)
        {
            xPath.Title = title;
            xPath.SearchTitleType = searchTypes;
            return (T)this;
        }

        public T setTemplateTitle(WebLocator webLocator)
        {
            xPath.TemplateTitle = new Dictionary<string, WebLocator>() { { "title", webLocator } };
            return (T)this;
        }

        public T setTemplatesValues(String key, String[] values)
        {
            xPath.TemplatesValues = new Dictionary<string, string[]>() { { key, values } };
            return (T)this;
        }

        public T setElPathSuffix(String key, String value)
        {
            xPath.ElPathSuffix = new Dictionary<string, string>() { { key, value } };
            return (T)this;
        }

        public T setElInfoMessage(String infoMessage)
        {
            xPath.InfoMessage = infoMessage;
            return (T)this;
        }

        public String XPath()
        {
            return xPath.XPath();
        }
    }
}
