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

package com.matthewtamlin.mixtape.library_tests.caching;
import android.support.test.runner.AndroidJUnit4;


import com.matthewtamlin.mixtape.library.caching.LibraryItemCache;
import com.matthewtamlin.mixtape.library.caching.LruLibraryItemCache;

import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TestLruLibraryItemCache extends TestLibraryItemCache {
	private static final int SMALL_CACHE_SIZE = 10000;
	private static final int LARGE_CACHE_SIZE = 20000000;

	@Override
	protected LibraryItemCache createLibraryItemCache() {
		return new LruLibraryItemCache(SMALL_CACHE_SIZE, SMALL_CACHE_SIZE, LARGE_CACHE_SIZE);
	}
}