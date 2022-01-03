package com.aspegrenide.minide;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.aspegrenide.minide.data.Idea;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivityIdea extends AppCompatActivity {

    public static final String ITEM_TITLE = "ITEM_TITLE";
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    FragmentAdapterIdea fragmentAdapter;

    // Use FireBase database to handle ideas, problems and needs
    private DatabaseReference fbDatabaseReference;
    private DatabaseReference ideaCloudEndPoint;
    private DatabaseReference ideasCloudEndPoint;
    private DatabaseReference projectCloudEndPoint;

    private ArrayList<Idea> ideas;
    private Idea currentIdea;
    private String ideaTitle;

    private String LOG_TAG = "min_ide";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_idea);
        TextView tabIntro = findViewById(R.id.tvTabIntro);
        tabIntro.setText("Beskriv Lösningen");

        currentIdea = new Idea();
        ideas = new ArrayList<>();

        fbDatabaseReference =  FirebaseDatabase.getInstance("https://minide-1d8d6-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        ideaCloudEndPoint = fbDatabaseReference.child("ideas");
        ideaCloudEndPoint.addValueEventListener(ideaListener);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager2);

        FragmentManager fm = getSupportFragmentManager();
        fragmentAdapter = new FragmentAdapterIdea(fm, getLifecycle());
        viewPager2.setAdapter(fragmentAdapter);

        tabLayout.addTab(tabLayout.newTab().setText("Beskrivning"));
        tabLayout.addTab(tabLayout.newTab().setText("Bild"));
        tabLayout.addTab(tabLayout.newTab().setText("Detaljer"));
        //tabLayout.addTab(tabLayout.newTab().setText("Min lista"));

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                ideaTitle = null;
            } else {
                ideaTitle = extras.getString(ITEM_TITLE);
            }
        } else {
            ideaTitle = (String) savedInstanceState.getSerializable(ITEM_TITLE);
        }
        Log.d(LOG_TAG, "title send as = " + ideaTitle);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
                Log.d(LOG_TAG, "onTabSelected");
                if(tab.getPosition() == 0) {
                    tabIntro.setText("Beskriv din idé");
                    fillInTheBoxesDescription();
                }
                if(tab.getPosition() == 1) {
                    tabIntro.setText("Ta en bild eller hämta från albumet");
                }
                if(tab.getPosition() == 2) {
                    tabIntro.setText("Välj hur du vill gå vidare");
                    fillInTheBoxesDetails();
                }
                if(tab.getPosition() == 3) {
                    tabIntro.setText("Min lista");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // store updates
                if(tab.getPosition() == 0) {
                    // description being left
                }
                Log.d(LOG_TAG, "onTabUnSelected");
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.d(LOG_TAG, "onTabReSelected");
            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
    }

    private void fillInTheBoxesDetails() {
        if (currentIdea == null) {
            return;
        }
        EditText etContact = findViewById(R.id.textBoxKontakt);
        if(etContact == null) { return; }
        String k = currentIdea.getContact();
        etContact.setText(k);

        Switch openItem = findViewById(R.id.switchOpen);
        openItem.setChecked(currentIdea.isOpen());

    }


    private void fillInTheBoxesDescription() {
        if (currentIdea == null) {
            return;
        }

        EditText etTitle = findViewById(R.id.textBoxTitle);
        EditText etApproach = findViewById(R.id.textBoxApproach);
        EditText etBenefit = findViewById(R.id.textboxBenefit);
        EditText etvar = findViewById(R.id.textboxVar);
        EditText etCompetition = findViewById(R.id.textboxCompetition);

        if(etApproach == null) { return; }
        etTitle.setText(currentIdea.getTitle());
        etApproach.setText(currentIdea.getApproach());
        etBenefit.setText(currentIdea.getBenefit());
        etCompetition.setText(currentIdea.getCompetition());

        Log.w(LOG_TAG, "updated UI");
    }


    ValueEventListener ideaListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // Get Post object and use the values to update the UI
            Iterable<DataSnapshot> stuff = dataSnapshot.getChildren();

            for (DataSnapshot s : stuff){
                Idea idea = s.getValue(Idea.class);
                Log.d(LOG_TAG, "read idea as " + idea.toString());
                ideas.add(idea);
                if(idea.getTitle().equals(ideaTitle)) {
                    currentIdea = idea;
                    Log.d(LOG_TAG, "currentproblem found " + idea.getTitle());
                }
            }
            fillInTheBoxesDescription();
            fillInTheBoxesDetails();

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w(LOG_TAG, "loadPost:onCancelled", databaseError.toException());
        }
    };


    public void onClickButtonUpdate(View view) {
        writeToCloud();
    }

    private void writeToCloud() {

        // title must always be filled in and should always be available
        EditText etTitle = findViewById(R.id.textBoxTitle);
        if (etTitle == null) {
            Toast.makeText(this, "Title is missing, please enter", Toast.LENGTH_SHORT).show();
            return;
        }
        currentIdea.setTitle(etTitle.getText().toString());

        // description tab
        EditText etApproach = findViewById(R.id.textBoxApproach);
        EditText etBenefit = findViewById(R.id.textboxBenefit);
        EditText etVar = findViewById(R.id.textboxVar);
        EditText etCompetition = findViewById(R.id.textboxCompetition);

        ideaCloudEndPoint.child(currentIdea.getTitle()).child("title").
                setValue(currentIdea.getTitle());

        if (etApproach != null) {
            ideaCloudEndPoint.child(currentIdea.getTitle()).child("approach").
                    setValue(etApproach.getText().toString());
        }
        if (etBenefit != null) {
            ideaCloudEndPoint.child(currentIdea.getTitle()).child("benefit").
                    setValue(etBenefit.getText().toString());
        }
        if (etCompetition != null) {
            ideaCloudEndPoint.child(currentIdea.getTitle()).child("competition").
                    setValue(etCompetition.getText().toString());
        }

        // details tab
        EditText etContact = findViewById(R.id.textBoxKontakt);
        if(etContact != null) {
            ideaCloudEndPoint.child(currentIdea.getTitle()).child("contact").
                    setValue(etContact.getText().toString());
        }
        Switch swtOpen = findViewById(R.id.switchOpen);
        if(swtOpen != null) {
            ideaCloudEndPoint.child(currentIdea.getTitle()).child("open").
                    setValue(swtOpen.isChecked());
        }

        Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();

    }

    public void onClickButtonMyList(View view) {
        Intent i = new Intent(this, MyListActivity.class);
        startActivity(i);
    }

}