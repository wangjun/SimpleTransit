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

/**
 * @author ichy <ichylinux@gmail.com>
 */
public interface SimpleTransitConst {

    public static final int ALARM_STATUS_NONE = 0;
    public static final int ALARM_STATUS_SET = 1;
    public static final int ALARM_STATUS_FINISHED = 2;

    public static final String EXTRA_KEY_START_ALARM = "startAlarm";
    public static final String EXTRA_KEY_TRANSIT = "transit";
    public static final String EXTRA_KEY_TRANSIT_QUERY = "transit_query";
    
    public static final int REQUEST_CODE_SELECT_TRANSIT_QUERY = 1;
}
