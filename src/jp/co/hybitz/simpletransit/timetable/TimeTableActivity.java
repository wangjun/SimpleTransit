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
package jp.co.hybitz.simpletransit.timetable;

import java.util.ArrayList;
import java.util.List;

import jp.co.hybitz.simpletransit.Preferences;
import jp.co.hybitz.simpletransit.R;
import jp.co.hybitz.simpletransit.SimpleTransitConst;
import jp.co.hybitz.simpletransit.action.OptionMenuHandler;
import jp.co.hybitz.simpletransit.db.TimeTableResultDao;
import jp.co.hybitz.simpletransit.timetable.model.AreaEx;
import jp.co.hybitz.simpletransit.timetable.model.LineEx;
import jp.co.hybitz.simpletransit.timetable.model.PrefectureEx;
import jp.co.hybitz.simpletransit.timetable.model.StationEx;
import jp.co.hybitz.simpletransit.timetable.model.TimeLineEx;
import jp.co.hybitz.simpletransit.timetable.model.TimeTableEx;
import jp.co.hybitz.simpletransit.timetable.model.TimeTableResultEx;
import jp.co.hybitz.timetable.model.Area;
import jp.co.hybitz.timetable.model.Line;
import jp.co.hybitz.timetable.model.Prefecture;
import jp.co.hybitz.timetable.model.Station;
import jp.co.hybitz.timetable.model.TimeTableQuery;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class TimeTableActivity extends Activity implements SimpleTransitConst {
    private OptionMenuHandler optionMenuHandler = new OptionMenuHandler(this);
    private ParentBackItem backItem;
    private TimeTableResultEx result;
    private TextView title;
    private ListView list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initAction();
        showAreas();
    }
    
    private void initView() {
        Preferences.initTheme(this);
        setContentView(R.layout.time_table);
        setTitle(getTitle() + "　Yahoo時刻表");
        result = (TimeTableResultEx) getIntent().getExtras().getSerializable(EXTRA_KEY_TIME_TABLE_RESULT);
        title = (TextView) findViewById(R.id.time_table_title);
        list = (ListView) findViewById(R.id.time_table_list);
    }
    
    private void initAction() {
        list.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TimeTableItem item = (TimeTableItem) parent.getItemAtPosition(position);
                updateList(item);
            }
        });
        registerForContextMenu(list);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (backItem != null) {
                handleBack(backItem);
                return true;
            }
        }
        
        return super.onKeyDown(keyCode, event);
    }

    /**
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_ITEM_PREFERENCES, 1, "設定");
        menu.add(0, MENU_ITEM_QUIT, 2, "終了");
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
     */
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (optionMenuHandler.onMenuItemSelected(featureId, item)) {
            return true;
        }
        
        return super.onMenuItemSelected(featureId, item);
    }
    
    private void handleBack(TimeTableItem item) {
        if (item.getArea() == null) {
            showAreas();
        }
        else if (item.getPrefecture() == null) {
            showPrefectures(item.getArea());
        }
        else if (item.getLine() == null) {
            showLines(item.getArea(), item.getPrefecture());
        }
        else if (item.getStation() == null) {
            showStations(item.getArea(), item.getPrefecture(), item.getLine());
        }
        else if (item.getTimeTable() == null) {
            showTimeTables(item.getArea(), item.getPrefecture(), item.getLine(), item.getStation());
        }
        else if (item.getTimeLine() == null) {
            showTimeLines(item.getArea(), item.getPrefecture(), item.getLine(), item.getStation(), item.getTimeTable());
        }
    }
    
    private void updateList(TimeTableItem item) {
        if (item instanceof ParentBackItem) {
            handleBack(item);
        }
        else if (item.getPrefecture() == null) {
            showPrefectures(item.getArea());
        }
        else if (item.getLine() == null) {
            TimeTableResultDao dao = new TimeTableResultDao(this);
            List<LineEx> lines = dao.getLines(item.getPrefecture().getId());
            if (lines.isEmpty()) {
                TimeTableQuery query = new TimeTableQuery();
                query.setArea(new Area());
                query.getArea().setName(item.getArea().getName());
                query.getArea().setUrl(item.getArea().getUrl());
                query.setPrefecture(new Prefecture());
                query.getPrefecture().setName(item.getPrefecture().getName());
                query.getPrefecture().setUrl(item.getPrefecture().getUrl());
                new TimeTableTask(this, item).execute(query);
            }
            else {
                item.getPrefecture().setLines(lines);
                showLines(item.getArea(), item.getPrefecture());
            }
        }
        else if (item.getStation() == null) {
            TimeTableResultDao dao = new TimeTableResultDao(this);
            List<StationEx> stations = dao.getStations(item.getLine().getId());
            if (stations.isEmpty()) {
                TimeTableQuery query = new TimeTableQuery();
                query.setArea(new Area());
                query.getArea().setName(item.getArea().getName());
                query.getArea().setUrl(item.getArea().getUrl());
                query.setPrefecture(new Prefecture());
                query.getPrefecture().setName(item.getPrefecture().getName());
                query.getPrefecture().setUrl(item.getPrefecture().getUrl());
                query.setLine(new Line());
                query.getLine().setName(item.getLine().getName());
                query.getLine().setUrl(item.getLine().getUrl());
                new TimeTableTask(this, item).execute(query);
            }
            else {
                item.getLine().setStations(stations);
                showStations(item.getArea(), item.getPrefecture(), item.getLine());
            }
        }
        else if (item.getTimeTable() == null) {
            TimeTableResultDao dao = new TimeTableResultDao(this);
            List<TimeTableEx> timeTables = dao.getTimeTables(item.getStation().getId());
            if (timeTables.isEmpty()) {
                TimeTableQuery query = new TimeTableQuery();
                query.setArea(new Area());
                query.getArea().setName(item.getArea().getName());
                query.getArea().setUrl(item.getArea().getUrl());
                query.setPrefecture(new Prefecture());
                query.getPrefecture().setName(item.getPrefecture().getName());
                query.getPrefecture().setUrl(item.getPrefecture().getUrl());
                query.setLine(new Line());
                query.getLine().setName(item.getLine().getName());
                query.getLine().setUrl(item.getLine().getUrl());
                query.setStation(new Station());
                query.getStation().setName(item.getStation().getName());
                query.getStation().setUrl(item.getStation().getUrl());
                new TimeTableTask(this, item).execute(query);
            }
            else {
                item.getStation().setTimeTables(timeTables);
                showTimeTables(item.getArea(), item.getPrefecture(), item.getLine(), item.getStation());
            }
        }
        else {
            showTimeLines(item.getArea(), item.getPrefecture(), item.getLine(), item.getStation(), item.getTimeTable());
        }
    }
    
    public void showAreas() {
        TimeTableArrayAdapter adapter = new TimeTableArrayAdapter(this, R.layout.time_table_list, new ArrayList<TimeTableItem>());
        backItem = null;

        for (AreaEx a : result.getAreas()) {
            TimeTableItem item = new TimeTableItem(a);
            adapter.add(item);
        }

        title.setText(Preferences.getText(this, "地域"));
        list.setAdapter(adapter);
    }

    public void showPrefectures(AreaEx a) {
        TimeTableArrayAdapter adapter = new TimeTableArrayAdapter(this, R.layout.time_table_list, new ArrayList<TimeTableItem>());
        backItem = new ParentBackItem();

        for (int i = 0; i < a.getPrefectures().size(); i ++) {
            PrefectureEx p = a.getPrefectures().get(i);
            TimeTableItem item = new TimeTableItem(a, p);
            adapter.insert(item, i);
        }

        title.setText(Preferences.getText(this, a.getName()));
        list.setAdapter(adapter);
    }
    
    public void showLines(AreaEx a, PrefectureEx p) {
        TimeTableArrayAdapter adapter = new TimeTableArrayAdapter(this, R.layout.time_table_list, new ArrayList<TimeTableItem>());
        backItem = new ParentBackItem(a);

        for (int i = 0; i < p.getLines().size(); i ++) {
            LineEx l = p.getLines().get(i);
            TimeTableItem item = new TimeTableItem(a, p, l);
            adapter.insert(item, i);
        }

        title.setText(Preferences.getText(this, p.getName()));
        list.setAdapter(adapter);
    }
    
    public void showStations(AreaEx a, PrefectureEx p, LineEx l) {
        TimeTableArrayAdapter adapter = new TimeTableArrayAdapter(this, R.layout.time_table_list, new ArrayList<TimeTableItem>());
        backItem = new ParentBackItem(a, p);

        for (int i = 0; i < l.getStations().size(); i ++) {
            StationEx s = l.getStations().get(i);
            TimeTableItem item = new TimeTableItem(a, p, l, s);
            adapter.insert(item, i);
        }

        title.setText(Preferences.getText(this, l.getCompany() + "　" + l.getName()));
        list.setAdapter(adapter);
    }
    
    public void showTimeTables(AreaEx a, PrefectureEx p, LineEx l, StationEx s) {
        TimeTableArrayAdapter adapter = new TimeTableArrayAdapter(this, R.layout.time_table_list, new ArrayList<TimeTableItem>());
        backItem = new ParentBackItem(a, p, l);

        for (int i = 0; i < s.getTimeTables().size(); i ++) {
            TimeTableEx tt = s.getTimeTables().get(i);
            TimeTableItem item = new TimeTableItem(a, p, l, s, tt);
            adapter.insert(item, i);
        }

        title.setText(Preferences.getText(this, l.getCompany() + "　" + l.getName() + "　" + s.getName()));
        list.setAdapter(adapter);
    }
    
    private void showTimeLines(AreaEx a, PrefectureEx p, LineEx l, StationEx s, TimeTableEx tt) {
        TimeTableArrayAdapter adapter = new TimeTableArrayAdapter(this, R.layout.time_table_list, new ArrayList<TimeTableItem>());
        backItem = new ParentBackItem(a, p, l, s);

        for (int i = 0; i < tt.getTimeLines().size(); i ++) {
            TimeLineEx tl = tt.getTimeLines().get(i);
            TimeTableItem item = new TimeTableItem(a, p, l, s, tt, tl);
            adapter.insert(item, i);
        }

        title.setText(Preferences.getText(this, l.getCompany() + "　" + l.getName() + "　" + s.getName() + "　" + tt.getDirection() + "　" + tt.getTypeString()));
        list.setAdapter(adapter);
    }
}