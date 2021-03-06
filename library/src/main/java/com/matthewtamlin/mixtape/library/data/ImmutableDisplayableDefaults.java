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

import android.graphics.Bitmap;

import com.matthewtamlin.java_utilities.testing.Tested;

/**
 * An immutable implementation of the DisplayableDefaults interface.
 */
@Tested(testMethod = "unit")
public final class ImmutableDisplayableDefaults implements DisplayableDefaults {
	/**
	 * The default title.
	 */
	private final CharSequence title;

	/**
	 * The default subtitle.
	 */
	private final CharSequence subtitle;

	/**
	 * The default artwork.
	 */
	private final Bitmap artwork;

	/**
	 * Constructs a new ImmutableDisplayableDefaults instance. The supplied values are used for the
	 * defaults and cannot be changed after instantiation.
	 *
	 * @param title
	 * 		the default title
	 * @param subtitle
	 * 		the default subtitle
	 * @param artwork
	 * 		the default artwork
	 */
	public ImmutableDisplayableDefaults(final CharSequence title, final CharSequence subtitle,
			final Bitmap artwork) {
		this.title = title;
		this.subtitle = subtitle;
		this.artwork = artwork;
	}

	@Override
	public final CharSequence getTitle() {
		return title;
	}

	@Override
	public final CharSequence getSubtitle() {
		return subtitle;
	}

	@Override
	public final Bitmap getArtwork() {
		return artwork;
	}
}