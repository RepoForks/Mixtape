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

package com.matthewtamlin.mixtape.library_tests.mixtape_coordinator;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.matthewtamlin.mixtape.library.caching.LibraryItemCache;
import com.matthewtamlin.mixtape.library.caching.LruLibraryItemCache;
import com.matthewtamlin.mixtape.library.data.DisplayableDefaults;
import com.matthewtamlin.mixtape.library.data.ImmutableDisplayableDefaults;
import com.matthewtamlin.mixtape.library.data.LibraryItem;
import com.matthewtamlin.mixtape.library.databinders.ArtworkBinder;
import com.matthewtamlin.mixtape.library.databinders.SubtitleBinder;
import com.matthewtamlin.mixtape.library.databinders.TitleBinder;
import com.matthewtamlin.mixtape.library.mixtape_body.GridBody;
import com.matthewtamlin.mixtape.library.mixtape_body.ListBody;
import com.matthewtamlin.mixtape.library.mixtape_body.RecyclerViewBody;
import com.matthewtamlin.mixtape.library.mixtape_coordinator.CoordinatedMixtapeContainer;
import com.matthewtamlin.mixtape.library.mixtape_header.SmallHeader;
import com.matthewtamlin.mixtape.library_tests.R;
import com.matthewtamlin.mixtape.library_tests.stubs.InaccessibleLibraryItem;
import com.matthewtamlin.mixtape.library_tests.stubs.ReadOnlyLibraryItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Test harness for testing the {@link CoordinatedMixtapeContainer} class.
 */
@SuppressLint("SetTextI18n") // Not important during testing
public class CoordinatedMixtapeContainerTestHarness extends
		MixtapeContainerTestHarness<SmallHeader, RecyclerViewBody> {
	/**
	 * The number of library items to display in the view.
	 */
	private static final int NUMBER_OF_ITEMS = 100;

	/**
	 * The view under test.
	 */
	private CoordinatedMixtapeContainer testView;

	/**
	 * The defaults to use when binding data to the test view.
	 */
	private DisplayableDefaults defaults;

	/**
	 * The cache to use when binding data to the test view.
	 */
	private LibraryItemCache cache;

	private LibraryItem headerItem;

	private List<LibraryItem> bodyItems;

	@Override
	protected void onCreate(final @Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Bitmap artwork = BitmapFactory.decodeResource(getResources(), R.raw.default_artwork);
		defaults = new ImmutableDisplayableDefaults("Default title", "Default subtitle", artwork);

		cache = new LruLibraryItemCache(10000, 10000, 10000);

		headerItem = new ReadOnlyLibraryItem(getResources(), "Header title", "Header subtitle",
				R.raw.real_artwork);
		bodyItems = generateBodyItems();

		getControlsContainer().addView(createUseSmallHeaderButton());
		getControlsContainer().addView(createUseNullHeaderButton());
		getControlsContainer().addView(createUseGridBodyButton());
		getControlsContainer().addView(createUseListBodyButton());
		getControlsContainer().addView(createUseNullBodyButton());
		getControlsContainer().addView(createShowHeaderAlwaysButton());
		getControlsContainer().addView(createHideHeaderAlwaysButton());
		getControlsContainer().addView(createShowHeaderAtTopOnly());
		getControlsContainer().addView(createShowHeaderOnDownwardsScrollOnly());
	}

	@Override
	public CoordinatedMixtapeContainer getTestView() {
		if (testView == null) {
			testView = new CoordinatedMixtapeContainer(this);
		}

		return testView;
	}

	private List<LibraryItem> generateBodyItems() {
		final List<LibraryItem> items = new ArrayList<>();

		for (int i = 0; i < NUMBER_OF_ITEMS; i++) {
			if (new Random().nextBoolean()) {
				items.add(new ReadOnlyLibraryItem(getResources(),
						"Title " + i,
						"Subtitle " + i,
						R.raw.real_artwork));
			} else {
				items.add(new InaccessibleLibraryItem());
			}
		}

		return items;
	}

	/**
	 * Creates a button which sets the header of the test view to a new {@link SmallHeader}.
	 *
	 * @return the button, not null
	 */
	private Button createUseSmallHeaderButton() {
		final Button b = new Button(this);
		b.setText("Use small header");
		b.setAllCaps(false);

		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final SmallHeader smallHeader =
						new SmallHeader(CoordinatedMixtapeContainerTestHarness.this);

				smallHeader.setItem(headerItem);
				smallHeader.setTitleDataBinder(new TitleBinder(cache, defaults));
				smallHeader.setSubtitleDataBinder(new SubtitleBinder(cache, defaults));
				smallHeader.setArtworkDataBinder(new ArtworkBinder(cache, defaults, 100));

				testView.setHeader(smallHeader);
			}
		});

		return b;
	}

	/**
	 * Creates a button which sets the test view to use no header.
	 *
	 * @return the button, not null
	 */
	private Button createUseNullHeaderButton() {
		final Button b = new Button(this);
		b.setText("Use no header");
		b.setAllCaps(false);

		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				testView.setHeader(null);
			}
		});

		return b;
	}

	/**
	 * Creates a button which sets the body of the test view to a new {@link GridBody}.
	 *
	 * @return the button, not null
	 */
	private Button createUseGridBodyButton() {
		final Button b = new Button(this);
		b.setText("Use grid body");
		b.setAllCaps(false);

		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final GridBody body = new GridBody(CoordinatedMixtapeContainerTestHarness.this);

				body.setItems(bodyItems);
				body.setTitleDataBinder(new TitleBinder(cache, defaults));
				body.setSubtitleDataBinder(new SubtitleBinder(cache, defaults));
				body.setArtworkDataBinder(new ArtworkBinder(cache, defaults, 100));

				testView.setBody(body);
			}
		});

		return b;
	}

	/**
	 * Creates a button which sets the body of the test view to a new {@link ListBody}.
	 *
	 * @return the button, not null
	 */
	private Button createUseListBodyButton() {
		final Button b = new Button(this);
		b.setText("Use list body");
		b.setAllCaps(false);

		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final ListBody body = new ListBody(CoordinatedMixtapeContainerTestHarness.this);

				body.setItems(bodyItems);
				body.setTitleDataBinder(new TitleBinder(cache, defaults));
				body.setSubtitleDataBinder(new SubtitleBinder(cache, defaults));
				body.setArtworkDataBinder(new ArtworkBinder(cache, defaults, 100));

				testView.setBody(body);
			}
		});

		return b;
	}

	/**
	 * Creates a button which sets the test view to use no body.
	 *
	 * @return the button, not null
	 */
	private Button createUseNullBodyButton() {
		final Button b = new Button(this);
		b.setText("Use no body");
		b.setAllCaps(false);

		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				testView.setBody(null);
			}
		});

		return b;
	}

	/**
	 * Creates a button which sets the test view to always show the header when clicked.
	 *
	 * @return the button, not null
	 */
	private Button createShowHeaderAlwaysButton() {
		final Button b = new Button(this);
		b.setText("Show header always");
		b.setAllCaps(false);

		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				testView.showHeaderAlways();
			}
		});

		return b;
	}

	/**
	 * Creates a button which sets the test view to always hide the header when clicked.
	 *
	 * @return the button, not null
	 */
	private Button createHideHeaderAlwaysButton() {
		final Button b = new Button(this);
		b.setText("Hide header always");
		b.setAllCaps(false);

		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				testView.hideHeaderAlways();
			}
		});

		return b;
	}

	/**
	 * Creates a button which sets the test view to only show the header at the top of the view when
	 * clicked.
	 *
	 * @return the button, not null
	 */
	private Button createShowHeaderAtTopOnly() {
		final Button b = new Button(this);
		b.setText("Show header at top only");
		b.setAllCaps(false);

		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				testView.showHeaderAtTopOnly();
			}
		});

		return b;
	}

	/**
	 * Creates a button which sets the test view to only show the header on downwards scrolls when
	 * clicked.
	 *
	 * @return the button, not null
	 */
	private Button createShowHeaderOnDownwardsScrollOnly() {
		final Button b = new Button(this);
		b.setText("Show header on downwards scroll only");
		b.setAllCaps(false);

		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				testView.showHeaderOnDownwardScrollOnly();
			}
		});

		return b;
	}
}