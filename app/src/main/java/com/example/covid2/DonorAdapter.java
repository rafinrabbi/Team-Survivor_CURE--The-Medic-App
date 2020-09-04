package com.example.covid2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class DonorAdapter extends ArrayAdapter<DonorInfo> {
    private Context c;
    private static class ViewHolder {
        TextView name;
        TextView phoneNo;
        ImageButton call;
    }

    public DonorAdapter(Context context, ArrayList<DonorInfo> users) {
        super(context, R.layout.donor_info, users);
        c = context;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        // Get the data item for this position
        final DonorInfo user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.donor_info, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.DONOR_NAME);
            viewHolder.phoneNo = (TextView) convertView.findViewById(R.id.DONOR_PHONE_NO);
            viewHolder.call = (ImageButton) convertView.findViewById(R.id.BUTTON_CALL_DONOR);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.name.setText(user.name);
        viewHolder.phoneNo.setText(user.phoneNo);
        viewHolder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(parent.getContext(), "Under development", Toast.LENGTH_LONG).show();
                if(((PlasmaActivity) c).checkPermission(Manifest.permission.CALL_PHONE)){
                    try {
                        c.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:+88"+user.phoneNo)));
                    }catch (Exception e){
                        Toast.makeText(c, e.toString(), Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(parent.getContext(), "Permission Call Phone denied", Toast.LENGTH_SHORT).show();

                }
            }
        });
        // Return the completed view to render on screen
        return convertView;
    }
}
