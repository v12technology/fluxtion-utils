/*
 * Copyright (C) 2017 Greg Higgins (greg.higgins@V12technology.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.fluxtion.runtime.plugin.logging;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.yaml.snakeyaml.Yaml;

/**
 * 
 * @author greg
 */
public class YamlLogRecordListener implements LogRecordListener {

    private List<StructuredLogRecord> eventList = new ArrayList<>();
    private final Yaml yaml;

    public List<StructuredLogRecord> getEventList() {
        return eventList;
    }

    public YamlLogRecordListener() {
        yaml = new Yaml();

    }

    public void loadFromFile(Reader reader) {
        yaml.loadAll(reader)
                .forEach(new Consumer<Object>() {
                    @Override
                    public void accept(Object m) {
                        if (m != null) {
                            Map e = (Map) ((Map) m).get("eventLogRecord");
                            if (e != null) {
                                eventList.add(new StructuredLogRecord(e));
                            }
                        }
                    }
                });
    }

    @Override
    public void processCalculationRecord(LogRecord logRecord) {
        yaml.loadAll(logRecord.asCharSequence().toString())
                .forEach(m -> {
                    Map e = (Map) ((Map) m).get("eventLogRecord");
                    eventList.add(new StructuredLogRecord(e));
                });
    }

    @Override
    public String toString() {
        return "MarshallingLogRecordListener{" + "eventList=" + eventList + '}';
    }

}
