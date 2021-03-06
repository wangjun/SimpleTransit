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
package jp.co.hybitz.simpletransit.model;

import java.util.Date;

import jp.co.hybitz.common.Engine;
import jp.co.hybitz.simpletransit.common.model.Favorable;
import jp.co.hybitz.transit.model.TimeType;
import jp.co.hybitz.transit.model.TransitQuery;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class TransitQueryEx extends Favorable {
    private int useCount;
    private TransitQuery transitQuery;
    
    public TransitQueryEx() {
        this(new TransitQuery());
    }

    public TransitQueryEx(TransitQuery transitQuery) {
        this.transitQuery = transitQuery;
    }
    
    public int getUseCount() {
        return useCount;
    }

    public void setUseCount(int useCount) {
        this.useCount = useCount;
    }

    public TransitQuery getTransitQuery() {
        return transitQuery;
    }

    public Engine getEngine() {
        return transitQuery.getEngine();
    }

    public String getFromCode() {
        return transitQuery.getFromCode();
    }

    public String getToCode() {
        return transitQuery.getToCode();
    }

    public void setEngine(Engine engine) {
        transitQuery.setEngine(engine);
    }

    public void setFromCode(String fromCode) {
        transitQuery.setFromCode(fromCode);
    }

    public void setToCode(String toCode) {
        transitQuery.setToCode(toCode);
    }

    public Date getDate() {
        return transitQuery.getDate();
    }

    public String getFrom() {
        return transitQuery.getFrom();
    }

    public TimeType getTimeType() {
        return transitQuery.getTimeType();
    }

    public String getTo() {
        return transitQuery.getTo();
    }

    public String getStopOver() {
        return transitQuery.getStopOver();
    }

    public String getStopOverCode() {
        return transitQuery.getStopOverCode();
    }

    public void setStopOver(String stopOver) {
        transitQuery.setStopOver(stopOver);
    }

    public void setStopOverCode(String stopOverCode) {
        transitQuery.setStopOverCode(stopOverCode);
    }

    public boolean isUseAirline() {
        return transitQuery.isUseAirline();
    }

    public boolean isUseExpress() {
        return transitQuery.isUseExpress();
    }

    public void setDate(Date date) {
        transitQuery.setDate(date);
    }

    public void setFrom(String from) {
        transitQuery.setFrom(from);
    }

    public void setTimeType(TimeType timeType) {
        transitQuery.setTimeType(timeType);
    }

    public void setTo(String to) {
        transitQuery.setTo(to);
    }

    public void setUseAirline(boolean useAirline) {
        transitQuery.setUseAirline(useAirline);
    }

    public void setUseExpress(boolean useExpress) {
        transitQuery.setUseExpress(useExpress);
    }
    
    public String getFromTo() {
        return getFrom() + " ～ " + getTo();
    }

    public String getSort() {
        return transitQuery.getSort();
    }

    public void setSort(String sort) {
        transitQuery.setSort(sort);
    }

}
