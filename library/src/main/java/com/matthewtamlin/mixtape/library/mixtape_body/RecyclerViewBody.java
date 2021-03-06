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

package com.matthewtamlin.mixtape.library.mixtape_body;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.matthewtamlin.android_utilities.library.helpers.ThemeColorHelper;
import com.matthewtamlin.java_utilities.checkers.NullChecker;
import com.matthewtamlin.java_utilities.testing.Tested;
import com.matthewtamlin.mixtape.library.R;
import com.matthewtamlin.mixtape.library.mixtape_body.BodyContract.Presenter;
import com.matthewtamlin.mixtape.library.data.LibraryItem;
import com.matthewtamlin.mixtape.library.databinders.DataBinder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A RecyclerView backed implementation of the BodyContract.View interface.
 */
@Tested(testMethod = "manual")
public abstract class RecyclerViewBody extends FrameLayout implements BodyContract.View {
	/**
	 * Identifies this class during logging.
	 */
	private static final String TAG = "[RecyclerViewBody]";

	/**
	 * The TopReachedListeners which are currently registered to this view.
	 */
	private final Set<TopReachedListener> topReachedListeners = new HashSet<>();

	/**
	 * The items to display in the recycler view.
	 */
	private List<? extends LibraryItem> data; // Default empty list avoids NPEs

	/**
	 * Contains supporting business logic and handles user interaction.
	 */
	private Presenter presenter;

	/**
	 * The menu resource to display when the contextual menu buttons in the recycler view are
	 * clicked.
	 */
	private int contextualMenuResourceId = -1;

	/**
	 * Binds title data to the view holders in the recycler view.
	 */
	private DataBinder<LibraryItem, TextView> titleDataBinder;

	/**
	 * Binds subtitle data to the view holders in the recycler view.
	 */
	private DataBinder<LibraryItem, TextView> subtitleDataBinder;

	/**
	 * Binds artwork data to the view holders in the recycler view.
	 */
	private DataBinder<LibraryItem, ImageView> artworkDataBinder;

	/**
	 * Displays the content to the user.
	 */
	private RecyclerView recyclerView;

	/**
	 * An indefinite progress bar.
	 */
	private ProgressBar loadingIndicator;

	/**
	 * Adapts the data to the recycler view.
	 */
	private Adapter<BodyViewHolder> adapter;

	/**
	 * Constructs a new RecyclerViewBody.
	 *
	 * @param context
	 * 		the Context the body is attached to, not null
	 */
	public RecyclerViewBody(final Context context) {
		super(context);
		init();
	}

	/**
	 * Constructs a new RecyclerViewBody.
	 *
	 * @param context
	 * 		the Context the body is attached to, not null
	 * @param attrs
	 * 		configuration attributes, null allowed
	 */
	public RecyclerViewBody(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	/**
	 * Constructs a new RecyclerViewBody.
	 *
	 * @param context
	 * 		the Context the body is attached to, not null
	 * @param attrs
	 * 		configuration attributes, null allowed
	 * @param defStyleAttr
	 * 		an attribute in the current theme which supplies default attributes, pass 0	to ignore
	 */
	public RecyclerViewBody(final Context context, final AttributeSet attrs,
			final int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	/**
	 * Sets the DataBinder to use when binding titles. Changing the data binder results in a full
	 * rebind of the recycler view. This method must be called on the UI thread.
	 *
	 * @param titleDataBinder
	 * 		the DataBinder to use for titles
	 */
	public void setTitleDataBinder(final DataBinder<LibraryItem, TextView> titleDataBinder) {
		// Use reference equality since equality is hard to define for multithreaded objects
		if (this.titleDataBinder != titleDataBinder) {
			if (this.titleDataBinder != null) {
				this.titleDataBinder.cancelAll();
			}

			this.titleDataBinder = titleDataBinder;
			recyclerView.getAdapter().notifyDataSetChanged(); // Ensures new data binder is used
		}
	}

	/**
	 * Sets the DataBinder to use when binding subtitles. Changing the data binder results in a full
	 * rebind of the recycler view. This method must be called on the UI thread.
	 *
	 * @param subtitleDataBinder
	 * 		the DataBinder to use for subtitles
	 */
	public void setSubtitleDataBinder(final DataBinder<LibraryItem, TextView> subtitleDataBinder) {
		// Use reference equality since equality is hard to define for multithreaded objects
		if (this.subtitleDataBinder != subtitleDataBinder) {
			if (this.subtitleDataBinder != null) {
				this.subtitleDataBinder.cancelAll();
			}

			this.subtitleDataBinder = subtitleDataBinder;
			recyclerView.getAdapter().notifyDataSetChanged(); // Ensures new data binder is used
		}
	}

	/**
	 * Sets the DataBinder to use when binding artwork. Changing the data binder results in a full
	 * rebind of the recycler view. This method must be called on the UI thread.
	 *
	 * @param artworkDataBinder
	 * 		the DataBinder to use for artwork
	 */
	public void setArtworkDataBinder(final DataBinder<LibraryItem, ImageView> artworkDataBinder) {
		// Use reference equality since equality is hard to define for multithreaded objects
		if (this.artworkDataBinder != artworkDataBinder) {
			if (this.artworkDataBinder != null) {
				this.artworkDataBinder.cancelAll();
			}

			this.artworkDataBinder = artworkDataBinder;
			recyclerView.getAdapter().notifyDataSetChanged(); // Ensures new data binder is used
		}
	}

	/**
	 * Sets the color of the loading indicator.
	 *
	 * @param color
	 * 		the color to use, as an ARGB hex code
	 */
	public void setLoadingIndicatorColor(final int color) {
		loadingIndicator.getIndeterminateDrawable().setColorFilter(color, android.graphics
				.PorterDuff.Mode.MULTIPLY);
	}

	/**
	 * @return the RecyclerView component of this view
	 */
	public final RecyclerView getRecyclerView() {
		return recyclerView;
	}

	@Override
	public void setPresenter(final BodyContract.Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public List<? extends LibraryItem> getItems() {
		return data;
	}

	@Override
	public void setItems(final List<? extends LibraryItem> items) {
		NullChecker.checkNotNull(items, "items cannot be null");
		data = items;
		adapter.notifyDataSetChanged();
	}

	@Override
	public int getContextualMenuResource() {
		return contextualMenuResourceId;
	}

	@Override
	public void setContextualMenuResource(final int contextualMenuResourceId) {
		// The resource is not used until a contextual menu button is clicked, so just save it
		this.contextualMenuResourceId = contextualMenuResourceId;
	}

	@Override
	public void showItem(final int itemIndex) {
		recyclerView.smoothScrollToPosition(itemIndex);
	}

	@Override
	public void notifyItemsChanged() {
		adapter.notifyDataSetChanged();
	}

	@Override
	public void notifyItemAdded(final int index) {
		adapter.notifyItemInserted(index);
	}

	@Override
	public void notifyItemRemoved(final int index) {
		adapter.notifyItemRemoved(index);
	}

	@Override
	public void notifyItemModified(final int index) {
		adapter.notifyItemChanged(index);
	}

	@Override
	public void notifyItemMoved(final int initialIndex, final int finalIndex) {
		adapter.notifyItemMoved(initialIndex, finalIndex);
	}

	@Override
	public void showLoadingIndicator(final boolean show) {
		recyclerView.setVisibility(show ? INVISIBLE : VISIBLE);
		loadingIndicator.setVisibility(show ? VISIBLE : GONE);
	}

	@Override
	public boolean loadingIndicatorIsShown() {
		return (loadingIndicator.getVisibility() == VISIBLE);
	}

	/**
	 * Registers a TopReachedListener to this RecyclerViewListener. The listener will be notified
	 * when this view is scrolled to the top. This method exits normally if the supplied listener is
	 * null or is already registered.
	 *
	 * @param listener
	 * 		the listener to register
	 */
	public void registerTopReachedListener(TopReachedListener listener) {
		topReachedListeners.add(listener);
	}

	/**
	 * Unregisters a TopReachedListener from this RecyclerViewListener. The listener will no longer
	 * be notified when this view is scrolled to the top. This method exits normally if the supplied
	 * listener is null or is not already registered.
	 *
	 * @param listener
	 * 		the listener to unregister
	 */
	public void unregisterTopReachedListener(TopReachedListener listener) {
		topReachedListeners.remove(listener);
	}

	/**
	 * Unregisters all TopReachedListener from this RecyclerViewListener. All listener which are
	 * currently registered will no longer be notified when this view is scrolled to the top.
	 */
	public void clearRegisteredTopReachedListeners() {
		topReachedListeners.clear();
	}

	/**
	 * Called when the RecyclerView is created to allow customisation before the adapter is set.
	 *
	 * @param recyclerView
	 * 		the RecyclerView which was created, not null
	 */
	protected void onRecyclerViewCreated(RecyclerView recyclerView) {}

	/**
	 * Called each time data binding completes. The default implementation does nothing.
	 *
	 * @param viewHolder
	 * 		the view holder which data was bound to, not null
	 * @param data
	 * 		the data which was bound to the view holder, not null
	 */
	protected void onViewHolderBound(final BodyViewHolder viewHolder, final LibraryItem data) {}

	/**
	 * Called each time a new BodyViewHolder is required.
	 *
	 * @return a new BodyViewHolder, not null
	 */
	protected abstract BodyViewHolder supplyNewBodyViewHolder(final ViewGroup parent);

	/**
	 * Initialises this view. This method should only be called from a constructor.
	 */
	private void init() {
		// Initialise overall view
		inflate(getContext(), R.layout.reyclerviewbody, this);
		getViewHandles();
		setLoadingIndicatorColor(ThemeColorHelper.getAccentColor(getContext(), Color.GRAY));

		// Configure recycler view
		onRecyclerViewCreated(recyclerView);
		generateAdapter();
		recyclerView.setAdapter(adapter);

		// When the view is scrolled to the top, notify registered listeners
		recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView,
					int newState) {
				if (newState == RecyclerView.SCROLL_STATE_IDLE) {
					final LinearLayoutManager llm = (LinearLayoutManager) recyclerView
							.getLayoutManager();

					if (llm.findFirstCompletelyVisibleItemPosition() == 0) {
						for (TopReachedListener listener : topReachedListeners) {
							listener.onTopReached(RecyclerViewBody.this);
						}
					}
				}
			}
		});
	}

	/**
	 * Assigns the necessary views references to member variables.
	 */
	private void getViewHandles() {
		try {
			recyclerView = (RecyclerView) NullChecker.checkNotNull(findViewById(R.id
					.recyclerViewBody_recyclerView), "init failed: recycler view not found");

			loadingIndicator = (ProgressBar) NullChecker.checkNotNull(findViewById(R.id
							.recyclerViewBody_progressIndicator),
					"init failed: progress indicator not found");
		} catch (final IllegalArgumentException e) {
			throw new RuntimeException("layout does not contain all required views");
		}
	}

	/**
	 * Generates a new adapter but does not assign it to the recycler view. The adapter uses the
	 * current data binders to bind data to the view holders.
	 */
	private void generateAdapter() {
		adapter = new Adapter<BodyViewHolder>() {
			@Override
			public BodyViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
				return supplyNewBodyViewHolder(parent);
			}

			@Override
			public void onBindViewHolder(final BodyViewHolder holder, final int position) {
				final LibraryItem displayedDataItem = data.get(holder.getAdapterPosition());

				if (titleDataBinder != null) {
					titleDataBinder.bind(holder.getTitleTextView(), displayedDataItem);
				} else {
					Log.w(TAG, "No title data binder set, could not bind title.");
				}

				if (subtitleDataBinder != null) {
					subtitleDataBinder.bind(holder.getSubtitleTextView(), displayedDataItem);
				} else {
					Log.w(TAG, "No subtitle data binder set, could not bind subtitle.");
				}

				if (artworkDataBinder != null) {
					artworkDataBinder.bind(holder.getArtworkImageView(), displayedDataItem);
				} else {
					Log.w(TAG, "No artwork data binder set, could not bind artwork.");
				}

				holder.getRootView().setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(final View v) {
						if (presenter != null) {
							presenter.onItemClicked(RecyclerViewBody.this, displayedDataItem);
						}
					}
				});

				final View overflowButton = holder.getContextualMenuButton();
				overflowButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(final View v) {
						// If the resource hasn't been set, inflating the menu will fail
						if (contextualMenuResourceId != -1) {
							final PopupMenu menu = new PopupMenu(getContext(), overflowButton);
							menu.inflate(contextualMenuResourceId);
							menu.show();

							// Propagate menu selections back to the presenter
							menu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
								@Override
								public boolean onMenuItemClick(final MenuItem menuItem) {
									if (presenter != null) {
										presenter.onContextualMenuItemClicked(RecyclerViewBody.this,
												displayedDataItem, menuItem);
										return true; // handled
									} else {
										return false; // not handled
									}
								}
							});
						}
					}
				});

				// Allow further customisation by subclasses
				onViewHolderBound(holder, displayedDataItem);
			}

			@Override
			public int getItemCount() {
				return data == null ? 0 : data.size();
			}
		};
	}

	/**
	 * Receives callbacks form a RecyclerViewBody when the view is scrolled to the top.
	 */
	public interface TopReachedListener {
		/**
		 * Invoked to indicate that the supplied RecyclerViewBody has been scrolled to the top.
		 *
		 * @param recyclerViewBody
		 * 		the scrolled RecyclerViewBody, not null
		 */
		void onTopReached(RecyclerViewBody recyclerViewBody);
	}
}