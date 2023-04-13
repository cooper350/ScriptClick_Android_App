package com.example.mainscreen;

import android.view.View;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import android.graphics.Color;
import android.util.TypedValue;
import android.graphics.Typeface;


public class TroupeAdapter extends ArrayAdapter<String> {

    private List<String> mTroupes;

    public TroupeAdapter(Context context, List<String> troupes) {
        super(context, R.layout.list_item, troupes);
        mTroupes = troupes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        String troupe = mTroupes.get(position);
        String[] troupeInfo = troupe.split(":");
        String name = troupeInfo[0].trim();
        String role = troupeInfo[1].trim();

        TextView nameTextView = convertView.findViewById(R.id.troupe_name);
        TextView roleTextView = convertView.findViewById(R.id.troupe_role);

        // Set the text for the Troupe and Role fields
        nameTextView.setText(name);
        roleTextView.setText(role);

        // Set the text color, size, and style for the Troupe field
        nameTextView.setTextColor(Color.BLACK);
        nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        nameTextView.setTypeface(null, Typeface.BOLD);

        // Set the text color, size, and style for the Role field
        roleTextView.setTextColor(Color.parseColor("#FF03DAC5"));
        roleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        roleTextView.setTypeface(null, Typeface.NORMAL);

        return convertView;
    }
}


