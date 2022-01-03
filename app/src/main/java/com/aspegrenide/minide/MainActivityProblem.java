package com.aspegrenide.minide;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.aspegrenide.minide.data.Problem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivityProblem extends AppCompatActivity {

    public static final String ITEM_TITLE = "ITEM_TITLE";
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    FragmentAdapter fragmentAdapter;

    // Use FireBase database to handle ideas, problems and needs
    private DatabaseReference fbDatabaseReference;
    private DatabaseReference problemCloudEndPoint;
    private DatabaseReference ideasCloudEndPoint;
    private DatabaseReference projectCloudEndPoint;

    private ArrayList<Problem> problems;
    private Problem currentProblem;
    private String problemTitle;

    private String LOG_TAG = "min_ide";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_problem);
        TextView tabIntro = findViewById(R.id.tvTabIntro);
        tabIntro.setText("Beskriv problemet");

        currentProblem = new Problem();
        problems = new ArrayList<>();

        fbDatabaseReference =  FirebaseDatabase.getInstance("https://minide-1d8d6-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        problemCloudEndPoint = fbDatabaseReference.child("problems");
        problemCloudEndPoint.addValueEventListener(problemListener);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager2);

        FragmentManager fm = getSupportFragmentManager();
        fragmentAdapter = new FragmentAdapter(fm, getLifecycle());
        viewPager2.setAdapter(fragmentAdapter);

        tabLayout.addTab(tabLayout.newTab().setText("Beskrivning"));
        tabLayout.addTab(tabLayout.newTab().setText("Bild"));
        tabLayout.addTab(tabLayout.newTab().setText("Detaljer"));
        //tabLayout.addTab(tabLayout.newTab().setText("Min lista"));

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                problemTitle = null;
            } else {
                problemTitle = extras.getString(ITEM_TITLE);
            }
        } else {
            problemTitle = (String) savedInstanceState.getSerializable(ITEM_TITLE);
        }
        Log.d(LOG_TAG, "title send as = " + problemTitle);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
                Log.d(LOG_TAG, "onTabSelected");
                if(tab.getPosition() == 0) {
                    tabIntro.setText("Beskriv problemet");
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
        if (currentProblem == null) {
            return;
        }
        EditText etContact = findViewById(R.id.textBoxKontakt);
        if(etContact == null) { return; }
        String k = currentProblem.getContact();
        etContact.setText(k);

        Switch openItem = findViewById(R.id.switchOpen);
        openItem.setChecked(currentProblem.isOpen());
    }


    private void fillInTheBoxesDescription() {
        if (currentProblem == null) {
            return;
        }

        EditText etTitle = findViewById(R.id.textBoxTitle);
        EditText etWhat = findViewById(R.id.textBoxApproach);
        EditText etWhen = findViewById(R.id.textboxBenefit);
        EditText etWhere = findViewById(R.id.textboxVar);
        EditText etWho = findViewById(R.id.textboxCompetition);

        if(etWhat == null) { return; }
        etTitle.setText(currentProblem.getTitle());
        etWhat.setText(currentProblem.getWhat());
        etWhen.setText(currentProblem.getWhen());
        etWhere.setText(currentProblem.getWhere());
        etWho.setText(currentProblem.getWho());

        Log.w(LOG_TAG, "updated UI");
    }


    ValueEventListener problemListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // Get Post object and use the values to update the UI
            Iterable<DataSnapshot> stuff = dataSnapshot.getChildren();

            for (DataSnapshot s : stuff){
                Problem p = s.getValue(Problem.class);
                Log.d(LOG_TAG, "read p as " + p.toString());
                problems.add(p);
                if(p.getTitle().equals(problemTitle)) {
                    currentProblem = p;
                    Log.d(LOG_TAG, "currentproblem found " + p.getTitle());
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
        currentProblem.setTitle(etTitle.getText().toString());

        // description tab
        EditText etWhat = findViewById(R.id.textBoxApproach);
        EditText etWhen = findViewById(R.id.textboxBenefit);
        EditText etWhere = findViewById(R.id.textboxVar);
        EditText etWho = findViewById(R.id.textboxCompetition);

        if (etWhat != null) {
            problemCloudEndPoint.child(currentProblem.getTitle()).child("what").
                    setValue(etWhat.getText().toString());
        }
        if (etWhere != null) {
            problemCloudEndPoint.child(currentProblem.getTitle()).child("where").
                    setValue(etWhere.getText().toString());
        }

        if (etWho != null) {
            problemCloudEndPoint.child(currentProblem.getTitle()).child("who").
                    setValue(etWho.getText().toString());
        }
        if (etWhen != null) {
            problemCloudEndPoint.child(currentProblem.getTitle()).child("when").
                    setValue(etWhen.getText().toString());
        }

        // details tab
        EditText etContact = findViewById(R.id.textBoxKontakt);
        if(etContact != null) {
            problemCloudEndPoint.child(currentProblem.getTitle()).child("contact").
                    setValue(etContact.getText().toString());
        }
        Switch swtOpen = findViewById(R.id.switchOpen);
        if(swtOpen != null) {
            problemCloudEndPoint.child(currentProblem.getTitle()).child("open").
                    setValue(swtOpen.isChecked());
        }

        Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();

    }

    public void onClickButtonMyList(View view) {
        Intent i = new Intent(this, MyListActivity.class);
        startActivity(i);
    }

}