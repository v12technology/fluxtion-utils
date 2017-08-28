/* 
 * Copyright (C) 2017 V12 Technology Limited (greg.higgins@v12technology.com)
 *
 * This file is part of Fluxtion.
 *
 * Fluxtion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.fluxtion.runtime.plugin.nodes;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.runtime.plugin.events.TimingPulseEvent;
import com.fluxtion.runtime.plugin.logging.EventLogSource;
import com.fluxtion.runtime.plugin.logging.EventLogger;
import java.util.Calendar;

/**
 * Filters
 *
 * @author Greg Higgins (greg.higgins@higherfrequencytrading.com)
 */
public class BetweenHoursTest implements EventLogSource{

    private final int openingHour;
    private final int closingHour;
    private boolean open;
    private Calendar calendar;
    private EventLogger log;
    private int currentHour;

    public BetweenHoursTest(int openingHour, int closingHour) {
        this.openingHour = openingHour;
        this.closingHour = closingHour;
    }

    public int getOpeningHour() {
        return openingHour;
    }

    public int getClosingHour() {
        return closingHour;
    }

    @EventHandler
    public boolean timeUpdate(TimingPulseEvent time) {
        calendar.setTimeInMillis(time.currentTimeMillis);
        currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        boolean changed = open != currentHour > openingHour && currentHour < closingHour;
        open = changed ? !open : open;
        log.info("open", open);
        log.info("currentHour", currentHour);
        return changed;
    }

    public int getCurrentHour() {
        return currentHour;
    }

    public void reset() {
        currentHour = 0;
        open = false;
        log.info("reset", true);
    }

    public boolean isOpen() {
        return open;
    }

    @Initialise
    public void init() {
        open = false;
        calendar = Calendar.getInstance();
    }

    @Override
    public void setLogger(EventLogger log) {
        this.log = log;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + this.openingHour;
        hash = 73 * hash + this.closingHour;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BetweenHoursTest other = (BetweenHoursTest) obj;
        if (this.openingHour != other.openingHour) {
            return false;
        }
        if (this.closingHour != other.closingHour) {
            return false;
        }
        return true;
    }

}
