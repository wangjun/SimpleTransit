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
package jp.co.hybitz.simpletransit.db;

import java.util.ArrayList;
import java.util.List;

import jp.co.hybitz.android.CursorEx;
import jp.co.hybitz.simpletransit.SimpleTransitConst;
import jp.co.hybitz.simpletransit.timetable.model.AreaEx;
import jp.co.hybitz.simpletransit.timetable.model.LineEx;
import jp.co.hybitz.simpletransit.timetable.model.PrefectureEx;
import jp.co.hybitz.simpletransit.timetable.model.StationEx;
import jp.co.hybitz.simpletransit.timetable.model.TimeLineEx;
import jp.co.hybitz.simpletransit.timetable.model.TimeTableEx;
import jp.co.hybitz.simpletransit.timetable.model.TransitTimeEx;
import jp.co.hybitz.timetable.model.TransitTime;
import jp.co.hybitz.timetable.model.TimeTable.Type;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class TimeTableResultDao extends AbstractDao implements SimpleTransitConst {

    public TimeTableResultDao(Context context) {
        super(context);
    }

    public List<AreaEx> getAreas() {
        List<AreaEx> ret = new ArrayList<AreaEx>();
        
        SQLiteDatabase db = getReadableDatabase();
        try {
            CursorEx c = (CursorEx) db.query("area", null, null, null, null, null, "_id asc");
            while (c.moveToNext()) {
                AreaEx a = loadArea(c);
                a.setPrefectures(getPrefectures(db, a.getId()));
                ret.add(a);
            }
            c.close();
        }
        finally {
            db.close();
        }
        
        return ret;
    }
    
    private List<PrefectureEx> getPrefectures(SQLiteDatabase db, long areaId) {
        List<PrefectureEx> ret = new ArrayList<PrefectureEx>();
        
        CursorEx c = (CursorEx) db.query("prefecture", null, "area_id=?", new String[]{String.valueOf(areaId)}, null, null, "_id asc");
        while (c.moveToNext()) {
            PrefectureEx p = loadPrefecture(c); 
            ret.add(p);
        }
        c.close();
        
        return ret;
    }

    public List<LineEx> getLines(long prefectureId) {
        List<LineEx> ret = new ArrayList<LineEx>();
        
        SQLiteDatabase db = getReadableDatabase();
        try {
            CursorEx c = (CursorEx) db.query("line", null, "prefecture_id=?", new String[]{String.valueOf(prefectureId)}, null, null, "_id asc");
            while (c.moveToNext()) {
                ret.add(loadLine(c));
            }
            c.close();
        }
        finally {
            db.close();
        }
        
        return ret;
    }

    public List<StationEx> getStations(long lineId) {
        List<StationEx> ret = new ArrayList<StationEx>();
        
        SQLiteDatabase db = getReadableDatabase();
        try {
            CursorEx c = (CursorEx) db.query("station", null, "line_id=?", new String[]{String.valueOf(lineId)}, null, null, "_id asc");
            while (c.moveToNext()) {
                ret.add(loadStation(c));
            }
            c.close();
        }
        finally {
            db.close();
        }
        
        return ret;
    }

    public List<TimeTableEx> getTimeTables(long stationId) {
        List<TimeTableEx> ret = new ArrayList<TimeTableEx>();
        
        SQLiteDatabase db = getReadableDatabase();
        try {
            CursorEx c = (CursorEx) db.query("time_table", null, "station_id=?", new String[]{String.valueOf(stationId)}, null, null, "_id asc");
            while (c.moveToNext()) {
                TimeTableEx tt = loadTimeTable(c);
                tt.setTimeLines(getTimeLines(db, tt.getId()));
                ret.add(tt);
            }
            c.close();
        }
        finally {
            db.close();
        }
        
        return ret;
    }

    private List<TimeLineEx> getTimeLines(SQLiteDatabase db, long timeTableId) {
        List<TimeLineEx> ret = new ArrayList<TimeLineEx>();
        
        CursorEx c = (CursorEx) db.query("time_line", null, "time_table_id=?", new String[]{String.valueOf(timeTableId)}, null, null, "_id asc");
        while (c.moveToNext()) {
            TimeLineEx tl = loadTimeLine(c);
            tl.setTimes(getTransitTimes(db, tl.getId()));
            ret.add(tl);
        }
        c.close();
        
        return ret;
    }

    private List<TransitTimeEx> getTransitTimes(SQLiteDatabase db, long timeLineId) {
        List<TransitTimeEx> ret = new ArrayList<TransitTimeEx>();
        
        CursorEx c = (CursorEx) db.query("transit_time", null, "time_line_id=?", new String[]{String.valueOf(timeLineId)}, null, null, "_id asc");
        while (c.moveToNext()) {
            TransitTimeEx tt = loadTransitTime(c);
            ret.add(tt);
        }
        c.close();
        
        return ret;
    }

    private AreaEx loadArea(CursorEx c) {
        AreaEx a = new AreaEx();
        a.setId(c.getLong("_id"));
        a.setName(c.getString("name"));
        a.setUrl(c.getString("url"));
        return a;
    }
    
    private PrefectureEx loadPrefecture(CursorEx c) {
        PrefectureEx p = new PrefectureEx();
        p.setId(c.getLong("_id"));
        p.setAreaId(c.getLong("area_id"));
        p.setName(c.getString("name"));
        p.setUrl(c.getString("url"));
        return p;
    }

    private LineEx loadLine(CursorEx c) {
        LineEx p = new LineEx();
        p.setId(c.getLong("_id"));
        p.setPrefectureId(c.getLong("prefecture_id"));
        p.setName(c.getString("name"));
        p.setCompany(c.getString("company"));
        p.setUrl(c.getString("url"));
        return p;
    }

    private StationEx loadStation(CursorEx c) {
        StationEx s = new StationEx();
        s.setId(c.getLong("_id"));
        s.setLineId(c.getLong("line_id"));
        s.setName(c.getString("name"));
        s.setUrl(c.getString("url"));
        return s;
    }

    private TimeTableEx loadTimeTable(CursorEx c) {
        TimeTableEx tt = new TimeTableEx();
        tt.setId(c.getLong("_id"));
        tt.setStationId(c.getLong("station_id"));
        tt.setDirection(c.getString("direction"));
        tt.setType(toType(c.getInt("type")));
        return tt;
    }
    
    private TimeLineEx loadTimeLine(CursorEx c) {
        TimeLineEx tl = new TimeLineEx();
        tl.setId(c.getLong("_id"));
        tl.setTimeTableId(c.getLong("time_table_id"));
        tl.setHour(c.getInt("hour"));
        return tl;
    }

    private TransitTimeEx loadTransitTime(CursorEx c) {
        TransitTime t = new TransitTime(c.getInt("hour"), c.getInt("minute"));
        TransitTimeEx tt = new TransitTimeEx(t);
        tt.setId(c.getLong("_id"));
        tt.setTimeLineId(c.getLong("time_line_id"));
        tt.setTransitClass(c.getString("transit_class"));
        tt.setBoundFor(c.getString("bound_for"));
        return tt;
    }

    private Type toType(int type) {
        switch (type) {
        case 1 :
            return Type.WEEKDAY;
        case 2 :
            return Type.SATURDAY;
        case 4 :
            return Type.SUNDAY;
        default :
            throw new IllegalStateException("予期していないタイプです。type=" + type);
        }
    }

    private int toInt(Type type) {
        switch (type) {
        case WEEKDAY :
            return 1;
        case SATURDAY :
            return 2;
        case SUNDAY :
            return 4;
        default :
            throw new IllegalStateException("予期していないタイプです。type=" + type);
        }
    }

    public List<Long> insertAreas(List<AreaEx> areas) {
        long now = getCurrentDateTime();
        List<Long> areaIdList = new ArrayList<Long>();
        
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            for (AreaEx a : areas) {
                // area
                ContentValues cv = new ContentValues();
                cv.put("name", a.getName());
                cv.put("url", a.getUrl());
                cv.put("created_at", now);
                cv.put("updated_at", now);
                
                long areaId = db.insertOrThrow("area", null, cv);
                if (areaId < 0) {
                    return new ArrayList<Long>();
                }
                
                // prefecture
                for (PrefectureEx p : a.getPrefectures()) {
                    ContentValues pcv = new ContentValues();
                    pcv.put("area_id", areaId);
                    pcv.put("name", p.getName());
                    pcv.put("url", p.getUrl());
                    pcv.put("created_at", now);
                    pcv.put("updated_at", now);
                    
                    long prefId = db.insertOrThrow("prefecture", null, pcv);
                    if (prefId < 0) {
                        return new ArrayList<Long>();
                    }
                }
                
                areaIdList.add(areaId);
            }
            
            db.setTransactionSuccessful();
            return areaIdList;
        }
        finally {
            db.endTransaction();
            db.close();
        }
    }
    
    public List<Long> insertLines(long prefectureId, List<LineEx> lines) {
        long now = getCurrentDateTime();
        List<Long> lineIdList = new ArrayList<Long>();
        
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            for (LineEx l : lines) {
                ContentValues cv = new ContentValues();
                cv.put("prefecture_id", prefectureId);
                cv.put("name", l.getName());
                cv.put("company", l.getCompany());
                cv.put("url", l.getUrl());
                cv.put("created_at", now);
                cv.put("updated_at", now);
                
                long lineId = db.insertOrThrow("line", null, cv);
                if (lineId < 0) {
                    return new ArrayList<Long>();
                }
                
                lineIdList.add(lineId);
            }
            
            db.setTransactionSuccessful();
            return lineIdList;
        }
        finally {
            db.endTransaction();
            db.close();
        }
    }
    
    public List<Long> insertStations(long lineId, List<StationEx> stations) {
        long now = getCurrentDateTime();
        List<Long> stationIdList = new ArrayList<Long>();
        
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            for (StationEx l : stations) {
                ContentValues cv = new ContentValues();
                cv.put("line_id", lineId);
                cv.put("name", l.getName());
                cv.put("url", l.getUrl());
                cv.put("created_at", now);
                cv.put("updated_at", now);
                
                long stationId = db.insertOrThrow("station", null, cv);
                if (stationId < 0) {
                    return new ArrayList<Long>();
                }
                
                stationIdList.add(stationId);
            }
            
            db.setTransactionSuccessful();
            return stationIdList;
        }
        finally {
            db.endTransaction();
            db.close();
        }
    }
    
    public List<Long> insertTimeTables(long stationId, List<TimeTableEx> timeTables) {
        long now = getCurrentDateTime();
        List<Long> timeTableIdList = new ArrayList<Long>();
        
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            for (TimeTableEx tt : timeTables) {
                // time_table
                ContentValues cv = new ContentValues();
                cv.put("station_id", stationId);
                cv.put("direction", tt.getDirection());
                cv.put("type", toInt(tt.getType()));
                cv.put("created_at", now);
                cv.put("updated_at", now);
                
                long timeTableId = db.insertOrThrow("time_table", null, cv);
                if (timeTableId < 0) {
                    return new ArrayList<Long>();
                }
                
                // time_line
                for (TimeLineEx tl : tt.getTimeLines()) {
                    ContentValues lcv = new ContentValues();
                    lcv.put("time_table_id", timeTableId);
                    lcv.put("hour", tl.getHour());
                    lcv.put("created_at", now);
                    lcv.put("updated_at", now);
                    
                    long timeLineId = db.insertOrThrow("time_line", null, lcv);
                    if (timeLineId < 0) {
                        return new ArrayList<Long>();
                    }
                    
                    // transit_time
                    for (TransitTimeEx t : tl.getTimes()) {
                        ContentValues tcv = new ContentValues();
                        tcv.put("time_line_id", timeLineId);
                        tcv.put("hour", t.getHour());
                        tcv.put("minute", t.getMinute());
                        tcv.put("transit_class", t.getTransitClass());
                        tcv.put("bound_for", t.getBoundFor());
                        tcv.put("created_at", now);
                        tcv.put("updated_at", now);
                        
                        long transitTimeId = db.insertOrThrow("transit_time", null, tcv);
                        if (transitTimeId < 0) {
                            return new ArrayList<Long>();
                        }
                    }
                }
                
                timeTableIdList.add(timeTableId);
            }
            
            db.setTransactionSuccessful();
            return timeTableIdList;
        }
        finally {
            db.endTransaction();
            db.close();
        }
    }
}