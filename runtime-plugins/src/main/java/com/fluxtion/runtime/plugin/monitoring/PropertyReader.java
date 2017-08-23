/* 
 * Copyright (C) 2017 V12 Technology Limited
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
package com.fluxtion.runtime.plugin.monitoring;

import java.lang.reflect.Field;
import java.util.Objects;
import org.apache.commons.lang3.reflect.FieldUtils;

/**
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class PropertyReader implements PropertyRecord {

    private final String instanceName;
    private final String propertyName;
    private final Object instance;
    private final StringBuilder value;
    private final Field field;

    public PropertyReader(String instanceName, String propertyName, Object instance) {
        this.instanceName = instanceName;
        this.propertyName = propertyName;
        this.instance = instance;
        this.value = new StringBuilder("N/A");
        if (instance == null | propertyName == null | instanceName == null) {
            field = null;
            value.setLength(0);
            value.append("error bad config");
        } else {
            //specific test for primitive type to generate zero GC
            field = FieldUtils.getField(instance.getClass(), propertyName, true);
            if (field == null) {
                value.setLength(0);
                value.append("error missing property");
            }
        }
    }

    public void recordValue() {
        if (field != null) {
            value.setLength(0);
            try {
                value.append(FieldUtils.readField(field, instance).toString());
            } catch (IllegalAccessException ex) {
                value.append("error cannot read");
            }
        }
    }

    @Override
    public String getInstanceName() {
        return instanceName;
    }

    @Override
    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public CharSequence getFormattedValue() {
        return value;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + Objects.hashCode(this.instanceName);
        hash = 43 * hash + Objects.hashCode(this.propertyName);
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
        final PropertyReader other = (PropertyReader) obj;
        if (!Objects.equals(this.instanceName, other.instanceName)) {
            return false;
        }
        return Objects.equals(this.propertyName, other.propertyName);
    }

}
