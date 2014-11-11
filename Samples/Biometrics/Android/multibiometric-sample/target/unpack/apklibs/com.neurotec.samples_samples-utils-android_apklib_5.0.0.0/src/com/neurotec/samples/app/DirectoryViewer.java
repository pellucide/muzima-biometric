package com.neurotec.samples.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.neurotec.samples.R;
import com.neurotec.samples.widget.BrowseListItem;
import com.neurotec.samples.widget.CustomItemAdapter;
import com.neurotec.samples.widget.FileListItem;
import com.neurotec.samples.widget.HeaderItem;
import com.neurotec.samples.widget.ListItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class DirectoryViewer extends BaseListActivity {

	// ===========================================================
	// Private static fields
	// ===========================================================

	private static final String TAG = DirectoryViewer.class.getSimpleName();
	private static final int BROWSE_REQUEST_CODE = 1;
	private static final String EXTRA_APPLICATION_LOCK = "application_lock";
	private static final String FAILED_FILE_OPEN_STATUS_MESSAGE = "failed_file_open_status_message";

	// ===========================================================
	// Public static fields
	// ===========================================================

	public static final String DIRECTORY_LOCATION = DirectoryViewer.class.getPackage().getName() + ".extra_directory";
	public static final String EXTRA_RETURNED_BUNDLE_DATA = "extra_bundle_data";
	public static final String FILE_TYPE = "file/*";
	public static final String IMAGE_TYPE = "image/*";

	// ===========================================================
	// Private fields
	// ===========================================================

	private String mLock;

	// ===========================================================
	// Protected methods
	// ===========================================================

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null && savedInstanceState.getSerializable(EXTRA_APPLICATION_LOCK) != null) {
			mLock = (String) savedInstanceState.getSerializable(EXTRA_APPLICATION_LOCK);
		} else {
			mLock = new String(this.toString());
		}

		String dirName = getIntent().getStringExtra(DIRECTORY_LOCATION);
		List<ListItem> items = new ArrayList<ListItem>();

		items.add(new BrowseListItem(getString(R.string.title_directory_view_browse)));
		items.add(new HeaderItem(getString(R.string.title_item_from_sample_directory)));

		File dir = new File(dirName);
		if (dir.exists()) {
			File[] files = dir.listFiles();
			Arrays.sort(files);
			if (files.length > 0) {
				for (File file : files) {
					if (file.isFile()) {
						items.add(new FileListItem(file.getName(), Uri.fromFile(file)));
					}
				}
			} else {
				items.add(new ListItem(getString(R.string.title_directory_view_no_items), null, false));
			}
		} else {
			items.add(new ListItem(getString(R.string.title_directory_view_cannot_open), null, false));
		}

		setTitle(getString(R.string.title_directory_view_activity) + dir.getAbsolutePath());
		setListAdapter(new CustomItemAdapter(this, items));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(EXTRA_APPLICATION_LOCK, mLock);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent();
		ListItem selectedItem = (ListItem) l.getAdapter().getItem(position);
		if (selectedItem instanceof BrowseListItem) {
			if (getIntent().getType() != null) {
				intent.setType(getIntent().getType());
			} else {
				intent.setType(FILE_TYPE);
			}
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(Intent.createChooser(intent, getString(R.string.title_input_source_activity)), BROWSE_REQUEST_CODE);
		} else if (selectedItem instanceof FileListItem) {
			intent.setData(((FileListItem) selectedItem).getmUri());
			intent.putExtra(EXTRA_RETURNED_BUNDLE_DATA, getIntent().getBundleExtra(EXTRA_RETURNED_BUNDLE_DATA));
			setResult(RESULT_OK, intent);
			finish();
		} else {
			Bundle bundle = new Bundle();
			bundle.putString(FAILED_FILE_OPEN_STATUS_MESSAGE, getString(R.string.msg_not_recognized_file_loading_type));
			showDialog(R.id.directory_viewer_error_dialog, bundle);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Intent result = new Intent();
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case BROWSE_REQUEST_CODE:
				result.setData(data.getData());
				result.putExtra(EXTRA_RETURNED_BUNDLE_DATA, getIntent().getBundleExtra(EXTRA_RETURNED_BUNDLE_DATA));
				setResult(RESULT_OK, result);
				finish();
				break;
			default:
				throw new AssertionError("Unreachable");
			}
		} else {
			Bundle bundle = new Bundle();
			bundle.putString(FAILED_FILE_OPEN_STATUS_MESSAGE, getString(R.string.msg_third_party_file_loading_app_failed));
			showDialog(R.id.directory_viewer_error_dialog, bundle);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle bundle) {
		if (id == R.id.directory_viewer_error_dialog) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			String failedLoagindMessage = "";
			if (bundle != null) {
				failedLoagindMessage = bundle.getString(FAILED_FILE_OPEN_STATUS_MESSAGE) != null ? bundle.getString(FAILED_FILE_OPEN_STATUS_MESSAGE) : "";
			}

			builder.setTitle(R.string.msg_failed_to_open_file)
			.setMessage(failedLoagindMessage + " " + getString(R.string.msg_dialog_retry_to_open_file))
			.setPositiveButton(R.string.msg_yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			})
			.setNegativeButton(R.string.msg_no, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					setResult(RESULT_CANCELED);
					finish();
				}
			});
			return builder.create();
		} else {
			return super.onCreateDialog(id, bundle);
		}
	}

}
