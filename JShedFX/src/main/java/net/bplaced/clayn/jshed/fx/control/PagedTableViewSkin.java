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

import java.util.List;
import java.util.concurrent.Callable;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Pagination;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

/**
 *
 * @author Clayn <clayn_osmato@gmx.de>
 */
class PagedTableViewSkin<S> extends SkinBase<PagedTableView<S>>
{

    private final TableView<S> table = new TableView<>();
    private final Pagination page = new Pagination();

    public PagedTableViewSkin(PagedTableView<S> control)
    {
        super(control);
        VBox box = new VBox();
        page.pageCountProperty().bind(Bindings.createIntegerBinding(
                new Callable<Integer>()
        {
            @Override
            public Integer call() throws Exception
            {
                return control.getEntriesPerPage() <= 0 ? 1 : Math.max(1,
                        table.getItems().size() / control.getEntriesPerPage());
            }
        },
                table.getItems(), control.entriesPerPageProperty()));
        page.currentPageIndexProperty().addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(
                    ObservableValue<? extends Number> observable,
                    Number oldValue, Number newValue)
            {
                int val = newValue.intValue();
                if (val >= 0)
                {
                    updateTable();
                }
            }
        });
        getChildren().add(box);
    }

    private void updateTable()
    {
        int val = page.getCurrentPageIndex();
        table.getItems().clear();
        int start = val * getSkinnable().getEntriesPerPage();
        List<S> items = getSkinnable().getItems().subList(start,
                start + getSkinnable().getEntriesPerPage());
        table.getItems().addAll(items);
    }

}
