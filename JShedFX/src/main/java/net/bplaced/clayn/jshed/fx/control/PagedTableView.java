/*
 * The MIT License
 *
 * Copyright 2019 Your Organisation.
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
package net.bplaced.clayn.jshed.fx.control;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Skin;
import javafx.scene.control.TableView;

/**
 *
 * @author Clayn <clayn_osmato@gmx.de>
 */
public class PagedTableView<S> extends TableView<S>
{

    private final IntegerProperty entriesPerPage = new SimpleIntegerProperty(20);
    
    {
        entriesPerPage.addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(
                    ObservableValue<? extends Number> observable,
                    Number oldValue, Number newValue)
            {
                if(newValue==null||newValue.intValue()<=0) {
                    entriesPerPage.set(20);
                }
            }
        });
    }
    public int getEntriesPerPage()
    {
        return entriesPerPage.get();
    }

    public void setEntriesPerPage(int value)
    {
        entriesPerPage.set(value);
    }

    public IntegerProperty entriesPerPageProperty()
    {
        return entriesPerPage;
    }
    
    @Override
    protected Skin<?> createDefaultSkin()
    {
        return new PagedTableViewSkin<>(this);
    }
 
}
