/*
 * The MIT License
 *
 * Copyright 2019 Clayn <clayn_osmato@gmx.de>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.bplaced.clayn.jshed.i18n;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The language pack provides the functionality to combine multiple
 * {@link ResourceBundle} into one. Changing the language of a language pack can
 * be easily done and it will try to reload all used {@link ResourceBundle}.
 *
 * @author Clayn <clayn_osmato@gmx.de>
 */
public class LanguagePack extends ResourceBundle
{

    private final Map<String, ResourceBundle> loadedBundles = new HashMap<>();
    private Locale locale;

    public LanguagePack(Locale locale)
    {
        this.locale = locale;
    }

    public LanguagePack()
    {
        this(Locale.getDefault());
    }

    public void addResourceBundle(String base)
    {
        Objects.requireNonNull(base);
        addResourceBundle(ResourceBundle.getBundle(base, locale));
    }

    public void addResourceBundle(ResourceBundle bundle)
    {
        Objects.requireNonNull(bundle);
        if (!bundle.getLocale().equals(locale))
        {
            addResourceBundle(bundle.getBaseBundleName());
            return;
        }
        loadedBundles.put(bundle.getBaseBundleName(), bundle);
    }

    @Override
    public Locale getLocale()
    {
        return locale;
    }

    /**
     * Sets the locale of this language pack. This method will cause all loaded
     * bundles to be reloaded with the new locale. If any of the bundles can't
     * be loaded an exception gets thrown.
     *
     * @param locale the new locale for the languagepack
     * @throws MissingResourceException if at least one of the loaded bundles
     * can't be found for the new locale
     */
    public void setLocale(Locale locale) throws MissingResourceException
    {
        this.locale = locale;
        rebuildResourceBundles();
    }

    private void rebuildResourceBundles()
    {
        Map<String, ResourceBundle> tmp = new HashMap<>();
        for (String base : loadedBundles.keySet())
        {
            tmp.put(base, ResourceBundle.getBundle(base, locale));
        }
        loadedBundles.clear();
        loadedBundles.putAll(tmp);
    }

    @Override
    protected Object handleGetObject(String key)
    {
        for (ResourceBundle bundle : loadedBundles.values())
        {
            if (bundle.containsKey(key))
            {
                return bundle.getObject(key);
            }
        }
        return null;
    }

    private Set<String> toSet(Enumeration<String> en)
    {
        Set<String> set = new HashSet<>();
        while (en.hasMoreElements())
        {
            set.add(en.nextElement());
        }
        return set;
    }

    @Override
    public Enumeration<String> getKeys()
    {
        Set<String> all = loadedBundles.values()
                .stream()
                .flatMap((bundle) -> toSet(bundle.getKeys()).stream())
                .collect(Collectors.toSet());
        return Collections.enumeration(all);
    }

}
