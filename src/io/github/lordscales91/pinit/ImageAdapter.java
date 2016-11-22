package io.github.lordscales91.pinit;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class ImageAdapter extends BaseAdapter {
	private Context mContext;
	private String[] urls;
	private DisplayImageOptions options;

	static class ViewHolder {
		ImageView imageView;
		ProgressBar progressBar;
	}

	public ImageAdapter(Context c, String... urls) {
		mContext = c;
		this.urls = urls;
		options = new DisplayImageOptions.Builder()
					.cacheInMemory(true)
					.cacheOnDisk(true)
					.showImageOnLoading(R.drawable.ic_stub)
					.showImageForEmptyUri(R.drawable.ic_image)
					.showImageOnFail(R.drawable.ic_error)
					.considerExifParams(true).build();

	}

	public int getCount() {
		return urls.length;
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	// create a new ImageView for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(mContext);

		final ViewHolder holder;
		View view = convertView;
		if (view == null) {
			view = inflater.inflate(R.layout.image_grid_item, parent, false);
			holder = new ViewHolder();
			assert view != null;
			holder.imageView = (ImageView) view.findViewById(R.id.image);
			holder.progressBar = (ProgressBar) view.findViewById(R.id.progress);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		ImageLoader.getInstance().displayImage(urls[position],
				holder.imageView, options, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						holder.progressBar.setProgress(0);
						holder.progressBar.setVisibility(View.VISIBLE);
						holder.imageView.setScaleType(ScaleType.FIT_XY);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						holder.progressBar.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						holder.progressBar.setVisibility(View.GONE);
						holder.imageView.setScaleType(ScaleType.CENTER_INSIDE);
					}
				}, new ImageLoadingProgressListener() {
					@Override
					public void onProgressUpdate(String imageUri, View view,
							int current, int total) {
						holder.progressBar.setProgress(Math.round(100.0f
								* current / total));
					}
				});

		// imageView.setIma
		return view;
	}

}