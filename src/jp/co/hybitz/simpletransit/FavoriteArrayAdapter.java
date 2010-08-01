/**
 * Copyright (C) 2010 Hybitz.co.ltd
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 * 
 */
package jp.co.hybitz.simpletransit;

import java.util.List;

import jp.co.hybitz.android.ArrayAdapterEx;
import jp.co.hybitz.simpletransit.model.SimpleTransitQuery;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * @author ichy <ichylinux@gmail.com>
 */
class FavoriteArrayAdapter extends ArrayAdapterEx<SimpleTransitQuery> {
    private SimpleTransit activity;

    public FavoriteArrayAdapter(SimpleTransit activity, int textViewResourceId, List<SimpleTransitQuery> items) {
        super(activity, textViewResourceId, items);
        this.activity = activity;
    }

    @Override
    protected void updateView(View view, final SimpleTransitQuery item) {
        TextView textView = (TextView) view;
        textView.setText(item.getFromTo());
        textView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                activity.updateQuery(item);
            }
        });
    }
}
