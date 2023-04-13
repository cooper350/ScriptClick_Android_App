package com.example.mainscreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.mainscreen.SecondFragment;
import androidx.fragment.app.Fragment;
import android.widget.ImageView;



public class MessageFragment extends AppCompatActivity {

    private ListView mListView;
    private SearchView mSearchView;
    private ArrayAdapter<String> mAdapter;
    private ArrayList<String> mTroupes;
    private int mCurrentTroupeIndex = 0;
    private ImageView[] mImageViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_message);

        mListView = findViewById(R.id.list_view);
        mSearchView = findViewById(R.id.search_view);
        mTroupes = new ArrayList<>();
        // Initialize the array of ImageViews
        mImageViews = new ImageView[2];
        mImageViews[0] = findViewById(R.id.icon);
        mImageViews[1] = findViewById(R.id.user_icon);

// Add dummy data to the ArrayList
        mTroupes.add("Troupe 1: Romeo");
        mTroupes.add("Troupe 2: Juliet");
        mTroupes.add("Troupe 3: Tybalt");
        mTroupes.add("Troupe 4: Hamlet");
        mTroupes.add("Troupe 5: Ophelia");
        mTroupes.add("Troupe 6: Macbeth");
        mTroupes.add("Troupe 7: Lady Macbeth");
        mTroupes.add("Troupe 8: King Lear");
        mTroupes.add("Troupe 9: Cordelia");
        mTroupes.add("Troupe 10: Brutus");
        mTroupes.add("Troupe 11: Portia");
        mTroupes.add("Troupe 12: Shylock");
        mTroupes.add("Troupe 13: Rosalind");
        mTroupes.add("Troupe 14: Orlando");
        mTroupes.add("Troupe 15: Iago");
        mTroupes.add("Troupe 16: Desdemona");
        mTroupes.add("Troupe 17: Puck");
        mTroupes.add("Troupe 18: Bottom");
        mTroupes.add("Troupe 19: Viola");
        mTroupes.add("Troupe 20: Malvolio");
        mTroupes.add("Troupe 21: Prospero");
        mTroupes.add("Troupe 22: Miranda");
        mTroupes.add("Troupe 23: Caliban");
        mTroupes.add("Troupe 24: Duke Orsino");
        mTroupes.add("Troupe 25: Olivia");
        mTroupes.add("Troupe 26: Falstaff");
        mTroupes.add("Troupe 27: Hotspur");
        mTroupes.add("Troupe 28: Hal");
        mTroupes.add("Troupe 29: Richard III");
        mTroupes.add("Troupe 30: Lady Anne");


// Create the adapter and set it on the ListView
        mAdapter = new TroupeAdapter(this, mTroupes);
        mListView.setAdapter(mAdapter);

// Set a listener to handle clicks on list items
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item
                String selectedItem = (String) parent.getItemAtPosition(position);

                // Start the SecondFragment with the selected item as a parameter
                Fragment fragment = new SecondFragment();
                Bundle bundle = new Bundle();
                bundle.putString("selectedItem", selectedItem);
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

// Create a new ArrayList to store the filtered items
        final ArrayList<String> filteredTroupes = new ArrayList<>();

// Add all the items from the original ArrayList to the filtered ArrayList
        filteredTroupes.addAll(mTroupes);

// Create the adapter and set it on the ListView
        mAdapter = new TroupeAdapter(this, mTroupes);
        mListView.setAdapter(mAdapter);

// Set a listener to handle clicks on list items
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item
                String selectedItem = (String) parent.getItemAtPosition(position);

                // Start the SecondFragment with the selected item as a parameter
                Fragment fragment = new SecondFragment();
                Bundle bundle = new Bundle();
                bundle.putString("selectedItem", selectedItem);
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

// Create a new list to hold the filtered data
        ArrayList<String> filtTroupes = new ArrayList<>(mTroupes);

// Create the adapter and set it on the ListView with the filtered data
        final TroupeAdapter filteredAdapter = new TroupeAdapter(this, filteredTroupes);
        mListView.setAdapter(filteredAdapter);

// Set a listener to handle clicks on list items
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item
                String selectedItem = (String) parent.getItemAtPosition(position);

                // Start the SecondFragment with the selected item as a parameter
                Fragment fragment = new SecondFragment();
                Bundle bundle = new Bundle();
                bundle.putString("selectedItem", selectedItem);
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

// Set up the search functionality

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filteredTroupes.clear();
                for (String troupe : mTroupes) {
                    if (troupe.toLowerCase().contains(newText.toLowerCase())) {
                        filteredTroupes.add(troupe);
                    }
                }
                filteredAdapter.notifyDataSetChanged();

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


        // Set up the item click listener
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedTroupe = mTroupes.get(position);
                launchSecondFragment(clickedTroupe);
            }
        });
    }

    public void launchSecondFragment(String clickedTroupe) {
        // Hide the ListView
        mListView.setVisibility(View.GONE);
        mSearchView.setVisibility(View.GONE);

        for (ImageView imageView : mImageViews) {
            imageView.setVisibility(View.GONE);
        }

        SecondFragment fragment = new SecondFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // Handles clicks on the buttons
    public void onButtonClick(View view) {
        Button button = (Button) view;
        String buttonText = button.getText().toString();

        // Show a toast with the button text
        Toast.makeText(this, buttonText, Toast.LENGTH_SHORT).show();
    }

    // Handles clicks on the "Next" button
    public void onNextButtonClick(View view) {
        if (mCurrentTroupeIndex < mTroupes.size() - 1) {
            mCurrentTroupeIndex++;
            String nextTroupe = mTroupes.get(mCurrentTroupeIndex);
        } else {
            Toast.makeText(this, "No more troupes to display", Toast.LENGTH_SHORT).show();
        }

        // Get the selected troupe and navigate to Second_Fragment
        ListView listView = findViewById(R.id.list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedTroupe = mAdapter.getItem(position);
                Toast.makeText(getApplicationContext(), clickedTroupe + " clicked ", Toast.LENGTH_SHORT).show();

                // Navigate to Second_Fragment
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new SecondFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}




