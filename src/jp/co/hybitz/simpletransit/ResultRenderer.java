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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import jp.co.hybitz.android.DateUtils;
import jp.co.hybitz.simpletransit.model.TransitItem;
import jp.co.hybitz.transit.model.Station;
import jp.co.hybitz.transit.model.TimeType;
import jp.co.hybitz.transit.model.Transit;
import jp.co.hybitz.transit.model.TransitResult;
import android.app.Activity;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 検索結果を描画するクラス
 * 
 * @author ichy <ichylinux@gmail.com>
 */
public class ResultRenderer implements SimpleTransitConst {
    
    private Activity activity;
    
    /**
     * コンストラクタ
     * 
     * @param activity
     */
    ResultRenderer(Activity activity) {
        this.activity = activity;
    }

    void render(TransitResult result) {
        TextView summary = (TextView) activity.findViewById(R.id.tv_summary);
        summary.setTextSize(Preferences.getTextSize(activity));
        summary.setText(createSummary(result));

        ListView results = (ListView) activity.findViewById(R.id.results);
        List<TransitItem> items = new ArrayList<TransitItem>();
        for (int i = 0; i < result.getTransitCount(); i ++) {
            Transit transit = result.getTransits().get(i);
            items.add(new TransitItem(result, transit));
        }
        results.setAdapter(new ResultArrayAdapter(activity, items));
    }
    
    void renderStations(TransitResult result) {
        TextView summary = (TextView) activity.findViewById(R.id.tv_summary);
        summary.setTextSize(Preferences.getTextSize(activity));
        summary.setText("駅候補");

        ListView results = (ListView) activity.findViewById(R.id.results);
        List<StationItem> items = new ArrayList<StationItem>();
        for (Station from : result.getFromStations()) {
            items.add(new StationItem(StationItem.STATION_TYPE_FROM, from));
        }
        
        if (items.size() > 0 && ! result.getToStations().isEmpty()) {
            items.add(new StationItem());
        }

        for (Station from : result.getToStations()) {
            items.add(new StationItem(StationItem.STATION_TYPE_TO, from));
        }
        
        if (items.size() > 0 && ! result.getStopOverStations().isEmpty()) {
            items.add(new StationItem());
        }
        
        for (Station stopOver : result.getStopOverStations()) {
            items.add(new StationItem(StationItem.STATION_TYPE_STOPOVER, stopOver));
        }

        results.setAdapter(new StationArrayAdapter(activity, items));
    }

    public static String createTitle(TransitResult result, boolean withTime) {
        String prefecture = result.getPrefecture() == null ? "" : "（" + result.getPrefecture() + "）";

        StringBuilder sb = new StringBuilder();
        sb.append(result.getFrom().replaceAll(prefecture, ""));
        sb.append(" ～ ");
        sb.append(result.getTo().replaceAll(prefecture, ""));
        
        if (withTime) {
            sb.append("　");
            sb.append(createTime(result));
        }

        return sb.toString();
    }
    
    public static String createTitleWithDate(TransitResult result) {
        String prefecture = result.getPrefecture() == null ? "" : "（" + result.getPrefecture() + "）";

        StringBuilder sb = new StringBuilder();
        sb.append(result.getFrom().replaceAll(prefecture, ""));
        sb.append(" ～ ");
        sb.append(result.getTo().replaceAll(prefecture, ""));
        sb.append("　");
        sb.append(createDate(result));

        return sb.toString();
    }

    public static String createTime(TransitResult result) {
        StringBuilder sb = new StringBuilder();

        if (result.getTimeType() == TimeType.DEPARTURE) {
            sb.append(result.getTime() + "発");
            
        }
        else if (result.getTimeType() == TimeType.ARRIVAL) {
            sb.append(result.getTime() + "着");
            
        }
        else if (result.getTimeType() == TimeType.FIRST) {
            if (result.getQueryDate() != null) {
                sb.append(new SimpleDateFormat("yyyy/MM/dd").format(result.getQueryDate()) + "始発");
            }
            else {
                sb.append("始発");
            }
        }
        else if (result.getTimeType() == TimeType.LAST) {
            if (result.getQueryDate() != null) {
                sb.append(new SimpleDateFormat("yyyy/MM/dd").format(result.getQueryDate()) + "終電");
            }
            else {
                sb.append("終電");
            }
        }
        else {
            throw new IllegalStateException("予期していない時刻タイプです。timeType=" + result.getTimeType());
        }
        
        return sb.toString();
    }
    
    public static String createDate(TransitResult result) {
        StringBuilder sb = new StringBuilder();

        
        if (result.getTimeType() == TimeType.DEPARTURE) {
            sb.append(getDateOrTime(result) + "発");
            
        }
        else if (result.getTimeType() == TimeType.ARRIVAL) {
            sb.append(getDateOrTime(result) + "着");
            
        }
        else if (result.getTimeType() == TimeType.FIRST) {
            if (result.getQueryDate() != null) {
                sb.append(new SimpleDateFormat("yyyy/MM/dd").format(result.getQueryDate()) + "始発");
            }
            else {
                sb.append("始発");
            }
        }
        else if (result.getTimeType() == TimeType.LAST) {
            if (result.getQueryDate() != null) {
                sb.append(new SimpleDateFormat("yyyy/MM/dd").format(result.getQueryDate()) + "終電");
            }
            else {
                sb.append("終電");
            }
        }
        else {
            throw new IllegalStateException("予期していない時刻タイプです。timeType=" + result.getTimeType());
        }
        
        return sb.toString();
    }
    
    private static String getDateOrTime(TransitResult result) {
        String date = result.getTime().toString();
        if (result.getQueryDate() != null) {
            date = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(result.getQueryDate());
        }
        return date;
    }

    private String createSummary(TransitResult result) {
        StringBuilder sb = new StringBuilder();
        if (result.getTransitCount() > 0) {
            if (DateUtils.isToday(result.getQueryDate())) {
                sb.append(createTitle(result, true));
            }
            else {
                sb.append(createTitleWithDate(result));
            }
        }
        else {
            sb.append(activity.getString(R.string.no_route_found));
        }
        return sb.toString();
    }
}
