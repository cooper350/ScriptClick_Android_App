package com.example.mainscreen;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.content.Intent;
import android.graphics.Color;
import android.widget.Toast;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.FragmentManager;
import android.view.MenuItem;
import com.example.mainscreen.RecordLines;
import androidx.annotation.NonNull;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;

import android.util.TypedValue;
import android.content.DialogInterface;




public interface OnButtonClickListener {
    void onButtonClick(@NonNull Fragment fragment);

    public static class Script_Page extends Fragment {
        String troupeName = "School Troupe";

        String troupeShort;

        TextView loggedInUserTextView;
        int troupeId = 04521;

        private void loadScriptFromDatabase() {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("troupes");
            databaseReference.orderByKey().equalTo(troupeShort).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // The troupe exists in the database
                        for (DataSnapshot troupeSnapshot : dataSnapshot.getChildren()) {
                            String script = troupeSnapshot.child("script").getValue(String.class);

                            // Check for double spaces at the end of each line and add a blank line after each one
                            StringBuilder spacedOutScriptBuilder = new StringBuilder();
                            String[] lines = script.split("\n");
                            for (int i = 0; i < lines.length - 1; i++) {
                                if (lines[i].endsWith("  ")) {
                                    spacedOutScriptBuilder.append(lines[i].trim()).append("\n\n");
                                } else {
                                    spacedOutScriptBuilder.append(lines[i]).append("\n");
                                }
                            }
                            // Add the last line without checking for double spaces
                            spacedOutScriptBuilder.append(lines[lines.length - 1]);
                            String spacedOutScript = spacedOutScriptBuilder.toString().trim();

                            // Set the spaced-out script to the text view
                            TextView scriptTextView = getView().findViewById(R.id.script);
                            scriptTextView.setText(spacedOutScript);
                        }
                    } else {
                        // The troupe does not exist in the database
                        System.out.println("Troupe not found.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle possible errors.
                }
            });
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.activity_script_page, container, false);


            loggedInUserTextView = view.findViewById(R.id.menu_username);
            LoggedInUser loggedInUser = LoginRepository.getInstance(new LoginDataSource()).getLoggedInUser();
            loggedInUserTextView.setText(loggedInUser.getDisplayName());

            Toolbar toolbar = view.findViewById(R.id.toolbarScriptPage);
            TextView toolbarTitle = view.findViewById(R.id.titleTroupe);

            Bundle arguments = getArguments();
            if (arguments != null && arguments.containsKey("selectedItem")) {
                String clickedTroupe = arguments.getString("selectedItem");
                String[] troupeInfo = clickedTroupe.split(":");
                troupeName = troupeInfo[0].trim();
            }


            ImageView personImageView = view.findViewById(R.id.person);
            personImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Show a popup menu with user information and logout option
                    androidx.appcompat.widget.PopupMenu popupMenu = new androidx.appcompat.widget.PopupMenu(getContext(), v);
                    popupMenu.getMenuInflater().inflate(R.menu.user_menu, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            if (id == R.id.menu_username) {
                                // Show the user's username in a Toast
                                String username = "Username: " + loggedInUser.getDisplayName();
                                return true;
                            } else if (id == R.id.menu_logout) {
                                // Logout the user and navigate back to the Login screen
                                LoginDataSource dataSource = new LoginDataSource();
                                dataSource.logout();
                                getParentFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                transaction.replace(R.id.container, new Login());
                                transaction.commit();
                                return true;
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });

            toolbarTitle.setText(troupeName);


            int index2 = troupeName.indexOf(" -");
            if (index2 != -1 && index2 < troupeName.length()) {
                troupeShort = troupeName.substring(0, index2).trim();
                loadScriptFromDatabase(); // Load the script from Firebase.
            } else {
                Toast.makeText(view.getContext(), "Invalid troupe name", Toast.LENGTH_SHORT).show();
            }

            ImageView imageTS = view.findViewById(R.id.imageTS);
            imageTS.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View script_page) {

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("troupes");
                    databaseReference.orderByKey().equalTo(troupeShort).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // The troupe exists in the database
                                for (DataSnapshot troupeSnapshot : dataSnapshot.getChildren()) {
                                    int troupeId = troupeSnapshot.child("id").getValue(Integer.class);
                                    Intent intent2 = new Intent(getActivity(), troupe_settings.class);
                                    intent2.putExtra("troupeId", troupeId);
                                    startActivity(intent2);
                                }
                            } else {
                                // The troupe does not exist in the database
                                System.out.println("Troupe not found.");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle possible errors.
                        }
                    });
                }
            });

            ImageView scriptMenu = view.findViewById(R.id.script_menu);
            scriptMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showScriptMenuPopupWindow(v.getContext(), v);
                }
            });

            ImageView backArrow = view.findViewById(R.id.arrow);
            backArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, new MessageFragment());
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });


            ImageView soundButton = view.findViewById(R.id.sound_button);
            soundButton.setOnClickListener(v -> {
                RecordLines recordLines = new RecordLines();
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, recordLines)
                        .addToBackStack(null)
                        .commit();
            });

            return view;

        }

        private void showScriptMenuPopupWindow(Context context, View anchorView) {
            // inflate the layout of the popup window
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.script_menu_popup_window, null);

            // create the popup window
            int width = ViewGroup.LayoutParams.WRAP_CONTENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

            // show the popup window
            popupWindow.setBackgroundDrawable(new BitmapDrawable()); // hide default background
            popupWindow.setOutsideTouchable(true);
            int offsetY = -20; // adjust this value as needed
            popupWindow.showAtLocation(anchorView, Gravity.START | Gravity.BOTTOM, 0, 250);

            // handle popup item clicks
            TextView annotationsOption = popupView.findViewById(R.id.annotations_option);
            TextView actOption = popupView.findViewById(R.id.act_option);
            TextView sceneOption = popupView.findViewById(R.id.scene_option);
            TextView annotationColorOption = popupView.findViewById(R.id.annotation_color_option);

            annotationsOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle Annotations Click
                    popupWindow.dismiss(); // close the popup window

                    // Get the script example piece TextView
                    TextView scriptExamplePiece = getActivity().findViewById(R.id.script);

                    // Enable text selection and highlight annotations
                    scriptExamplePiece.setTextIsSelectable(true);
                    scriptExamplePiece.setHighlightColor(getResources().getColor(R.color.scriptClickBlue));

                    // Apply color change to highlighted text
                    scriptExamplePiece.addTextChangedListener(new TextWatcher() {
                        // Keep track of the previously selected text
                        int previousStart = -1;
                        int previousEnd = -1;

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {}

                        @Override
                        public void afterTextChanged(Editable s) {
                            int selectionStart = scriptExamplePiece.getSelectionStart();
                            int selectionEnd = scriptExamplePiece.getSelectionEnd();

                            if (selectionStart != -1 && selectionEnd != -1) {
                                // Get the spannable text and spans
                                Spannable text = (Spannable) scriptExamplePiece.getText();
                                ForegroundColorSpan[] highlightSpans = text.getSpans(selectionStart, selectionEnd, ForegroundColorSpan.class);

                                if (selectionStart == previousStart && selectionEnd == previousEnd && highlightSpans.length > 0) {
                                    // User highlighted the same text again, so remove the highlight spans
                                    for (ForegroundColorSpan span : highlightSpans) {
                                        text.removeSpan(span);
                                    }
                                } else {
                                    // User highlighted new text, so add the highlight spans
                                    ForegroundColorSpan newSpan = new ForegroundColorSpan(Color.BLUE);
                                    text.setSpan(newSpan, selectionStart, selectionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                }

                                // Remember the current selection
                                previousStart = selectionStart;
                                previousEnd = selectionEnd;
                            }
                        }
                    });
                }
            });




            actOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle Act Click
                    popupWindow.dismiss(); // close the popup window
                }
            });

            sceneOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle Scene Click
                    popupWindow.dismiss(); // close the popup window
                }
            });

            SeekBar fontSizeSeekBar = popupView.findViewById(R.id.font_size_seekbar);
            fontSizeSeekBar.setMax(2);
            fontSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    int fontSize;
                    switch (progress) {
                        case 0:
                            fontSize = 12; // small font size
                            break;
                        case 1:
                            fontSize = 20; // normal font size
                            break;
                        case 2:
                            fontSize = 36; // large font size
                            break;
                        default:
                            fontSize = 20; // default to normal font size
                            break;
                    }
                    // update the font size on the scriptExamplePiece TextView
                    TextView scriptExamplePiece = (TextView) getActivity().findViewById(R.id.script);
                    scriptExamplePiece.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });


            annotationColorOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle Annotation Color Click
                    popupWindow.dismiss(); // close the popup window

                    TextView scriptExamplePiece = (TextView) getActivity().findViewById(R.id.script);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Choose a color");

                    // Add the color options to the dialog
                    final String[] colors = {"Black", "Red", "Blue", "Green"};
                    builder.setItems(colors, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Handle the color selection
                            switch (which) {
                                case 0: // Black
                                    scriptExamplePiece.setTextColor(Color.BLACK);
                                    break;
                                case 1: // Red
                                    scriptExamplePiece.setTextColor(Color.RED);
                                    break;
                                case 2: // Blue
                                    scriptExamplePiece.setTextColor(Color.BLUE);
                                    break;
                                case 3: // Green
                                    scriptExamplePiece.setTextColor(Color.GREEN);
                                    break;
                            }
                        }
                    });

                    // Show the dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });

        }
    }
}