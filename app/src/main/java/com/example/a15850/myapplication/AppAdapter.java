package com.example.a15850.myapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class AppAdapter extends ArrayAdapter<App> {
    private int resourceId;

    public AppAdapter(Context context, int itemViewResourceId, List<App> apps){
        super(context, itemViewResourceId, apps);
        resourceId = itemViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        App app = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId, null, false);
            viewHolder = new ViewHolder();

            // 缓存
            viewHolder.setAppLabel((TextView) view.findViewById(R.id.appLabel));
            viewHolder.setPkgName((TextView)view.findViewById(R.id.pkgName));
            viewHolder.setVersionName((TextView)view.findViewById(R.id.versionName));
            viewHolder.setAppIcon((ImageView)view.findViewById(R.id.app_icon));
            viewHolder.setType((TextView)view.findViewById(R.id.type));
            viewHolder.setVersionCode((TextView)view.findViewById(R.id.versionCode));
            view.setTag(viewHolder);
        }else {
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }

        viewHolder.getAppIcon().setImageDrawable(app.getAppIcon());
        viewHolder.getAppLabel().setText(String.format("Label: %s", app.getAppLabel()));
        viewHolder.getPkgName().setText(String.format("PkgName: %s", app.getPkgName()));
        viewHolder.getVersionName().setText(String.format("VersionName: %s", app.getVersionName()));
        viewHolder.getType().setText(app.getType());
        viewHolder.getVersionCode().setText(String.format("VersionCode: %d", app.getVersionCode()));

        return view;
    }

    class ViewHolder{
        private TextView appLabel;
        private TextView pkgName;
        private TextView versionName;
        private ImageView appIcon;
        private TextView type;
        private TextView versionCode;

        public TextView getType() {
            return type;
        }

        public void setType(TextView type) {
            this.type = type;
        }

        public TextView getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(TextView versionCode) {
            this.versionCode = versionCode;
        }

        public ImageView getAppIcon() {
            return appIcon;
        }

        public void setAppIcon(ImageView appIcon) {
            this.appIcon = appIcon;
        }

        public TextView getAppLabel() {
            return appLabel;
        }

        public void setAppLabel(TextView appLabel) {
            this.appLabel = appLabel;
        }

        public TextView getPkgName() {
            return pkgName;
        }

        public void setPkgName(TextView pkgName) {
            this.pkgName = pkgName;
        }

        public TextView getVersionName() {
            return versionName;
        }

        public void setVersionName(TextView versionName) {
            this.versionName = versionName;
        }

    }
}
