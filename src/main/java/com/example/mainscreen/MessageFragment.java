package com.example.mainscreen;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.SearchView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.view.MenuItem;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.firebase.database.ChildEventListener;

import android.widget.TextView;
import android.content.Intent;

import java.util.TreeMap;
import java.util.ArrayList;

public class MessageFragment extends Fragment {

    private DatabaseReference mDatabase;
    private ListView mListView;
    private SearchView mSearchView;
    private ArrayAdapter<String> mAdapter;
    private ArrayList<String> mTroupes;
    private int mCurrentTroupeIndex = 0;
    private ImageView[] mImageViews;
    private ImageView mUserIcon;
    TextView loggedInUserTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        loggedInUserTextView = view.findViewById(R.id.menu_username);
        LoggedInUser loggedInUser = LoginRepository.getInstance(new LoginDataSource()).getLoggedInUser();
        loggedInUserTextView.setText(loggedInUser.getDisplayName());
        ImageView scriptClickIcon = getActivity().findViewById(R.id.imageTS);
        scriptClickIcon.setVisibility(View.GONE);

        mDatabase = FirebaseDatabase.getInstance().getReference("troupes");
        fetchTroupeData();

        mListView = view.findViewById(R.id.list_view);
        mSearchView = view.findViewById(R.id.search_view);
        mTroupes = new ArrayList<>();
        // Initialize the array of ImageViews
        mImageViews = new ImageView[2];
        mImageViews[0] = view.findViewById(R.id.icon);
        mImageViews[1] = view.findViewById(R.id.user_icon);


        // Create the adapter and set it on the ListView
        mAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, mTroupes);
        mListView.setAdapter(mAdapter);

        // Set up the search functionality
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);

                if (newText.isEmpty()) { // Check if search bar is empty
                    for (ImageView imageView : mImageViews) {
                        imageView.setVisibility(View.VISIBLE); // set visibility to VISIBLE
                    }
                } else {
                    for (ImageView imageView : mImageViews) {
                        imageView.setVisibility(View.INVISIBLE); // set visibility to INVISIBLE
                    }
                }

                return true;
            }
        });

        mUserIcon = view.findViewById(R.id.user_icon);
        mUserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the user menu popup
                PopupMenu popup = new PopupMenu(getContext(), v);
                popup.getMenuInflater().inflate(R.menu.user_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // Handle user menu item clicks
                        if (item.getItemId() == R.id.menu_logout) {
                            // Restart the app by creating a new intent to launch the main activity
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                            // Kill the current activity to ensure a fresh start
                            getActivity().finish();
                            return true;
                        }

                        return false;
                    }

                });
                popup.show();
            }
        });

        // Set up the user icon click listener
        ImageView userIcon = view.findViewById(R.id.user_icon);

        userIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show a popup menu with user information and logout option
                PopupMenu popupMenu = new PopupMenu(getContext(), v);
                popupMenu.getMenuInflater().inflate(R.menu.user_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.menu_username) {
                            // Show the user's username in a Toast
                            String username = "Username: " + loggedInUser.getDisplayName();
                            Toast.makeText(getActivity(), username, Toast.LENGTH_SHORT).show();
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

        // Set up the item click listener
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedTroupe = mTroupes.get(position);
                launchSecondFragment(clickedTroupe);
            }
        });

        return view;
    }

    public void launchSecondFragment(String clickedTroupe) {
        // Hide the ListView and SearchView
        mListView.setVisibility(View.GONE);
        mSearchView.setVisibility(View.GONE);

        for (ImageView imageView : mImageViews) {
            imageView.setVisibility(View.GONE);
        }

        // Start the SecondFragment with the selected item as a parameter
        Fragment fragment = new OnButtonClickListener.Script_Page();
        Bundle bundle = new Bundle();
        bundle.putString("selectedItem", clickedTroupe);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // Handles clicks on the buttons
    public void onButtonClick(View view) {
        // Show a toast with the button text
        Toast.makeText(getActivity(), view.getTag().toString(), Toast.LENGTH_SHORT).show();
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fetchTroupeData();
    }

    // Handles clicks on the "Next" button
    public void onNextButtonClick(View view) {
        if (mCurrentTroupeIndex < mTroupes.size()
                - 1) {
            mCurrentTroupeIndex++;
            String nextTroupe = mTroupes.get(mCurrentTroupeIndex);
        } else {
            Toast.makeText(getActivity(), view.getTag().toString(), Toast.LENGTH_SHORT).show();
        }

        // Get the selected troupe and navigate to Second_Fragment
        ListView listView = view.findViewById(R.id.list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedTroupe = mAdapter.getItem(position);
                Toast.makeText(getActivity(), clickedTroupe + " clicked ", Toast.LENGTH_SHORT).show();

                // Navigate to Second_Fragment
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.container, new OnButtonClickListener.Script_Page())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void fetchTroupeData() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mTroupes.clear(); // Clear the list to avoid duplicates
                LoggedInUser loggedInUser = LoginRepository.getInstance(new LoginDataSource()).getLoggedInUser();
                TreeMap<Integer, String> sortedTroupes = new TreeMap<>();
                for (DataSnapshot troupeSnapshot : dataSnapshot.getChildren()) {
                    String troupeName = troupeSnapshot.getKey();
                    if (troupeName != null) {
                        // Check if the logged in user is a member of this troupe and get their role
                        String userRole = "";
                        DataSnapshot membersSnapshot = troupeSnapshot.child("members");
                        for (DataSnapshot memberSnapshot : membersSnapshot.getChildren()) {
                            String username = memberSnapshot.child("name").getValue(String.class);
                            if (username != null && username.equals(loggedInUser.getDisplayName())) {
                                userRole = memberSnapshot.child("role").getValue(String.class);
                                break;
                            }
                        }
                        // Add the troupe name and user role to the sorted map
                        int troupeNumber = Integer.parseInt(troupeName.replaceAll("[^0-9]", ""));
                        if (!userRole.isEmpty()) {
                            sortedTroupes.put(troupeNumber, troupeName + " - " + userRole);
                        } else {
                            sortedTroupes.put(troupeNumber, troupeName);
                        }
                    }
                }

                // Add the sorted troupe names to the list
                for (String sortedTroupe : sortedTroupes.values()) {
                    mTroupes.add(sortedTroupe);
                }

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });

    }
}