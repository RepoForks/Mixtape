/*
 * Copyright 2017 Matthew Tamlin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.matthewtamlin.mixtape.library.data;

import com.matthewtamlin.mixtape.library.base_mvp.BaseDataSource;

import java.util.HashSet;
import java.util.Set;

/**
 * Abstract implementation of the BaseDataSource interface which handles listener registration.
 * Getters are provided for accessing the registered listeners.
 *
 * @param <D>
 * 		the type of data supplied by the source
 */
public abstract class BaseDataSourceAdapter<D> implements BaseDataSource<D> {
	/**
	 * All data replaced listeners which are currently registered. This set must never contain
	 * null.
	 */
	private final Set<DataReplacedListener<D>> dataReplacedListeners = new HashSet<>();

	/**
	 * All data modified listeners which are currently registered. This set must never contain
	 * null.
	 */
	private final Set<DataModifiedListener<D>> dataModifiedListeners = new HashSet<>();

	/**
	 * All long operation listeners which are currently registered. This set must never contain
	 * null.
	 */
	private final Set<LongOperationListener> longOperationListeners = new HashSet<>();

	@Override
	public void registerDataReplacedListener(final DataReplacedListener<D> listener) {
		if (listener != null) {
			dataReplacedListeners.add(listener);
		}
	}

	@Override
	public void unregisterDataReplacedListener(final DataReplacedListener<D> listener) {
		dataReplacedListeners.remove(listener);
	}

	@Override
	public void registerDataModifiedListener(final DataModifiedListener<D> listener) {
		if (listener != null) {
			dataModifiedListeners.add(listener);
		}
	}

	@Override
	public void unregisterDataModifiedListener(final DataModifiedListener<D> listener) {
		dataModifiedListeners.remove(listener);
	}

	@Override
	public void registerLongOperationListener(final LongOperationListener listener) {
		if (listener != null) {
			longOperationListeners.add(listener);
		}
	}

	@Override
	public void unregisterLongOperationListener(final LongOperationListener listener) {
		longOperationListeners.remove(listener);
	}

	/**
	 * Returns all data replaced listeners which are currently registered for callbacks. This method
	 * may return an empty set, but it will never return null.
	 *
	 * @return the listeners
	 */
	public Set<DataReplacedListener<D>> getDataReplacedListeners() {
		return dataReplacedListeners;
	}

	/**
	 * Returns all data modified listeners which are currently registered for callbacks. This method
	 * may return an empty set, but it will never return null.
	 *
	 * @return the listeners
	 */
	public Set<DataModifiedListener<D>> getDataModifiedListeners() {
		return dataModifiedListeners;
	}

	/**
	 * Returns all long operation listeners which are currently registered for callbacks. This
	 * method may return an empty set, but it will never return null.
	 *
	 * @return the listeners
	 */
	public Set<LongOperationListener> getLongOperationListeners() {
		return longOperationListeners;
	}
}
