/*
 * Copyright (c) 2011, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package javafx.beans.property;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.binding.DoubleExpression;

/**
 * Super class for all readonly properties wrapping a {@code double}.
 * 
 * @see javafx.beans.value.ObservableDoubleValue
 * @see javafx.beans.binding.DoubleExpression
 * @see ReadOnlyProperty
 * 
 */
public abstract class ReadOnlyDoubleProperty extends DoubleExpression implements
        ReadOnlyProperty<Number> {

    /**
     * The constructor of {@code ReadOnlyDoubleProperty}.
     */
    public ReadOnlyDoubleProperty() {
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        final Object bean1 = getBean();
        final String name1 = getName();
        if ((bean1 == null) || (name1 == null) || name1.equals("")) {
            return false;
        }
        if (obj instanceof ReadOnlyDoubleProperty) {
            final ReadOnlyDoubleProperty other = (ReadOnlyDoubleProperty) obj;
            final Object bean2 = other.getBean();
            final String name2 = other.getName();
            return (bean1 == bean2) && name1.equals(name2);
        }
        return false;
    }

    /**
     * Returns a hash code for this {@code ReadOnlyDoubleProperty} object.
     * @return a hash code for this {@code ReadOnlyDoubleProperty} object.
     */ 
    @Override
    public int hashCode() {
        final Object bean = getBean();
        final String name = getName();
        if ((bean == null) && ((name == null) || name.equals(""))) {
            return super.hashCode();
        } else {
            int result = 17;
            result = 31 * result + ((bean == null)? 0 : bean.hashCode());
            result = 31 * result + ((name == null)? 0 : name.hashCode());
            return result;
        }
    }

    /**
     * Returns a string representation of this {@code ReadOnlyDoubleProperty} object.
     * @return a string representation of this {@code ReadOnlyDoubleProperty} object.
     */ 
    @Override
    public String toString() {
        final Object bean = getBean();
        final String name = getName();
        final StringBuilder result = new StringBuilder(
                "ReadOnlyDoubleProperty [");
        if (bean != null) {
            result.append("bean: ").append(bean).append(", ");
        }
        if ((name != null) && !name.equals("")) {
            result.append("name: ").append(name).append(", ");
        }
        result.append("value: ").append(get()).append("]");
        return result.toString();
    }
    
    /**
     * Returns a {@code ReadOnlyDoubleProperty} that wraps a
     * {@link javafx.beans.property.ReadOnlyProperty}. If the
     * {@code ReadOnlyProperty} is already a {@code ReadOnlyDoubleProperty}, it
     * will be returned. Otherwise a new
     * {@code ReadOnlyDoubleProperty} is created that is bound to
     * the {@code ReadOnlyProperty}.
     * 
     * Note: null values will be interpreted as 0.0
     * 
     * @param property
     *            The source {@code ReadOnlyProperty}
     * @return A {@code ReadOnlyDoubleProperty} that wraps the
     *         {@code ReadOnlyProperty} if necessary
     * @throws NullPointerException
     *             if {@code value} is {@code null}
     */
    public static <T extends Number> ReadOnlyDoubleProperty readOnlyDoubleProperty(final ReadOnlyProperty<T> property) {
        if (property == null) {
            throw new NullPointerException("Property cannot be null");
        }
        
        return property instanceof ReadOnlyDoubleProperty ? (ReadOnlyDoubleProperty) property:
           new ReadOnlyDoublePropertyBase() {
            private boolean valid = true;
            private final InvalidationListener listener = new InvalidationListener() {
                @Override
                public void invalidated(Observable observable) {
                    if (valid) {
                        valid = false;
                        fireValueChangedEvent();
                    }
                }
            };

            {
                property.addListener(new WeakInvalidationListener(listener));
            }
                    
            @Override
            public double get() {
                valid = true;
                final T value = property.getValue();
                return value == null ? 0.0 : value.doubleValue();
            }

            @Override
            public Object getBean() {
                return null; // Virtual property, no bean
            }

            @Override
            public String getName() {
                return property.getName();
            }
        };
    }

    /**
     * Creates a {@link javafx.beans.property.ReadOnlyObjectProperty} that holds the value
     * of this {@code ReadOnlyDoubleProperty}. If the
     * value of this {@code ReadOnlyDoubleProperty} changes, the value of the
     * {@code ReadOnlyObjectProperty} will be updated automatically.
     * 
     * @return the new {@code ReadOnlyObjectProperty}
     */
    @Override
    public ReadOnlyObjectProperty<Double> asObject() {
        return new ReadOnlyObjectPropertyBase<Double>() {

            private boolean valid = true;
            private final InvalidationListener listener = new InvalidationListener() {
                @Override
                public void invalidated(Observable observable) {
                    if (valid) {
                        valid = false;
                        fireValueChangedEvent();
                    }
                }
            };

            {
                ReadOnlyDoubleProperty.this.addListener(new WeakInvalidationListener(listener));
            }
            
            @Override
            public Object getBean() {
                return null; // Virtual property, does not exist on a bean
            }

            @Override
            public String getName() {
                return ReadOnlyDoubleProperty.this.getName();
            }

            @Override
            public Double get() {
                valid = true;
                return ReadOnlyDoubleProperty.this.getValue();
            }
        };
    };
    
    

}