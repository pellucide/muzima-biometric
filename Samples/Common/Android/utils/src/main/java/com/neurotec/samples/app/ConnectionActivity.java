package com.neurotec.samples.app;

import android.os.Bundle;

import com.neurotec.samples.R;
import com.neurotec.samples.net.ConnectivityHelper;
import com.neurotec.samples.widget.CustomItemAdapter;
import com.neurotec.samples.widget.HeaderItem;
import com.neurotec.samples.widget.ListItem;

import java.util.ArrayList;
import java.util.List;

public final class ConnectionActivity extends BaseListActivity {

	// ===========================================================
	// Protected methods
	// ===========================================================

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		List<ListItem> items = new ArrayList<ListItem>();
		items.add(new HeaderItem(getString(R.string.msg_internet_connection)));
		items.add(new ListItem(getString(R.string.msg_internet_connection_status), ConnectivityHelper.getStatus(this)));
		if (ConnectivityHelper.isConnected(this)) {
			items.add(new ListItem(getString(R.string.msg_internet_connection_type), ConnectivityHelper.getType(this)));
		}
		setListAdapter(new CustomItemAdapter(this, items));
	}
}