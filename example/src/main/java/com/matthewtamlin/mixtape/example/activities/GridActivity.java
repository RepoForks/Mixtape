package com.matthewtamlin.mixtape.example.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.matthewtamlin.mixtape.example.R;
import com.matthewtamlin.mixtape.example.data.Mp3Album;
import com.matthewtamlin.mixtape.example.data.Mp3AlbumDataSource;
import com.matthewtamlin.mixtape.library.caching.LibraryItemCache;
import com.matthewtamlin.mixtape.library.caching.LruLibraryItemCache;
import com.matthewtamlin.mixtape.library.data.DisplayableDefaults;
import com.matthewtamlin.mixtape.library.data.ImmutableDisplayableDefaults;
import com.matthewtamlin.mixtape.library.data.LibraryItem;
import com.matthewtamlin.mixtape.library.databinders.ArtworkBinder;
import com.matthewtamlin.mixtape.library.databinders.SubtitleBinder;
import com.matthewtamlin.mixtape.library.databinders.TitleBinder;
import com.matthewtamlin.mixtape.library.mixtape_body.BodyContract;
import com.matthewtamlin.mixtape.library.mixtape_body.GridBody;
import com.matthewtamlin.mixtape.library.mixtape_body.RecyclerViewBodyPresenter;
import com.matthewtamlin.mixtape.library.mixtape_coordinator.CoordinatedMixtapeContainer;

public class GridActivity extends AppCompatActivity {
	private GridBody body;

	private CoordinatedMixtapeContainer rootView;

	private Mp3AlbumDataSource dataSource;

	private RecyclerViewBodyPresenter<Mp3Album, Mp3AlbumDataSource> presenter;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("Albums");

		setupView();
		setupDataSource();
		setupPresenter();

		presenter.setView(body);
		presenter.setDataSource(dataSource);
		presenter.present(true);
	}

	private void setupView() {
		setContentView(R.layout.example_layout);

		rootView = (CoordinatedMixtapeContainer) findViewById(R.id.example_layout_coordinator);

		body = new GridBody(this);

		body.setContextualMenuResource(R.menu.album_menu);

		rootView.setBody(body);
	}

	private void setupDataSource() {
		dataSource = new Mp3AlbumDataSource();
	}

	private void setupPresenter() {
		final Bitmap defaultArtwork = BitmapFactory.decodeResource(getResources(), R.raw
				.default_artwork);

		final DisplayableDefaults defaults = new ImmutableDisplayableDefaults(
				"Unknown title", "Unknown subtitle", defaultArtwork);

		final LibraryItemCache cache = new LruLibraryItemCache(10000, 10000, 10000000);

		final TitleBinder titleBinder = new TitleBinder(cache, defaults);
		final SubtitleBinder subtitleBinder = new SubtitleBinder(cache, defaults);
		final ArtworkBinder artworkBinder = new ArtworkBinder(cache, defaults, 300);

		presenter = new RecyclerViewBodyPresenter<Mp3Album, Mp3AlbumDataSource>
				(titleBinder, subtitleBinder, artworkBinder) {
			@Override
			public void onContextualMenuItemClicked(final BodyContract.View hostView,
					final LibraryItem libraryItem, final MenuItem menuItem) {
				handleContextualMenuClick(libraryItem, menuItem);
			}
		};
	}

	private void handleContextualMenuClick(final LibraryItem libraryItem, final MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case R.id.album_menu_playNext: {
				Snackbar.make(rootView, "Playing next...", Snackbar.LENGTH_LONG);
				break;
			}

			case R.id.album_menu_addToQueue: {
				Snackbar.make(rootView, "Adding to queue...", Snackbar.LENGTH_LONG);
				break;
			}

			case R.id.album_menu_remove: {
				dataSource.deletedItem((Mp3Album) libraryItem);
			}
		}
	}
}