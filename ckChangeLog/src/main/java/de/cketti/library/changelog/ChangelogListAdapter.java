package de.cketti.library.changelog;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class ChangelogListAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private Context        context;
    private LayoutInflater inflater;
//        private List<ChangeLog.ReleaseItem> releases;
    private List<Change> changes = new ArrayList<>();

    private class Change {

        private Change(int versionCode, String versionName, String changeText) {
            this.versionCode = versionCode;
            this.versionName = versionName;
            this.changeText = changeText;
        }

        int    versionCode;
        String versionName, changeText;
    }

    public ChangelogListAdapter(Context context, List<ChangeLog.ReleaseItem> releases) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
//        this.releases = releases;
        for (ChangeLog.ReleaseItem release : releases) {
            for (String change : release.changes) {
                changes.add(new Change(release.versionCode, release.versionName, change));
            }
        }
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.change_header_item, parent, false);
        }
        TextView headerText = (TextView) convertView.findViewById(R.id.headerText);
        headerText.setText("Version " + changes.get(position).versionName);
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return changes.get(position).versionCode;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return changes.size();
    }

    @Override
    public Object getItem(int position) {
        return changes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        return null;
//        ViewHolder holder;

        TextView changeText;

        if (convertView == null) {
//            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.change_list_item, parent, false);
//            holder.text = (TextView) convertView.findViewById(R.id.text);
//            convertView.setTag(holder);
        }

        changeText = (TextView) convertView.findViewById(R.id.changeText);
//        else {
//            holder = (ViewHolder) convertView.getTag();
//        }

        changeText.setText(changes.get(position).changeText);

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return changes.isEmpty();
    }
}
