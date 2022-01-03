package com.aspegrenide.minide;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.aspegrenide.minide.data.Idea;
import com.aspegrenide.minide.data.Problem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivityIdea extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    FragmentAdapterIdea fragmentAdapter;

    EditText etTitle;

    // Use FireBase database to handle ideas, problems and needs
    private DatabaseReference fbDatabaseReference;
    private DatabaseReference ideaCloudEndPoint;

    private ArrayList<Idea> ideas;
    private Idea currentIdea;
    private String ideaUid;

    private String LOG_TAG = "min_ide";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_idea);
        TextView tabIntro = findViewById(R.id.tvTabIntro);
        tabIntro.setText("Beskriv Lösningen");

        etTitle = findViewById(R.id.textBoxTitle);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                Log.d(LOG_TAG, "receive intent with NO uid as "+ MyListActivity.ITEM_UID);
                ideaUid = null; // we are here to create a new idea
            } else {
                ideaUid = extras.getString(MyListActivity.ITEM_UID);
                Log.d(LOG_TAG, "receive intent with uid as "+ MyListActivity.ITEM_UID + " " + ideaUid);
            }
        } else {
            ideaUid = (String) savedInstanceState.getSerializable(MyListActivity.ITEM_UID);
            Log.d(LOG_TAG, "receive intent (2) with uid as "+ MyListActivity.ITEM_UID);
        }
        Log.d(LOG_TAG, "uid received as = " + ideaUid);

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


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
                Log.d(LOG_TAG, "onTabSelected");
                if(tab.getPosition() == 0) {
                    tabIntro.setText("Beskriv din idé");
                    // we can not access views from the fragment here
                    // they have not been created, so hold on :-)
                }
                if(tab.getPosition() == 1) {
                    tabIntro.setText("Ta en bild eller hämta från albumet");
                }
                if(tab.getPosition() == 2) {
                    tabIntro.setText("Hur idén hänger ihop med annat");
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
                // now everything has loaded and we can access the views of the fragment
                fillInTheBoxesDescription();
                fillInTheBoxesDetails();

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
        etTitle.setText(currentIdea.getTitle());

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
                Log.d(LOG_TAG, "snappa idea as " + idea.toString());
                ideas.add(idea);
                if(idea.getUid().equals(ideaUid)) {
                    currentIdea = idea;
                    Log.d(LOG_TAG, "snappa currentidea found " + idea.getTitle());
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



    private void writeToCloud() {

        if(ideaUid == null) {
            Toast.makeText(this, "Du måste skapa en idé först", Toast.LENGTH_SHORT).show();
            return;
        }

        // description tab
        EditText etApproach = findViewById(R.id.textBoxApproach);
        EditText etBenefit = findViewById(R.id.textboxBenefit);
        EditText etVar = findViewById(R.id.textboxVar);
        EditText etCompetition = findViewById(R.id.textboxCompetition);

        if (etApproach != null) {
            ideaCloudEndPoint.child(ideaUid).child("approach").
                    setValue(etApproach.getText().toString());
        }
        if (etBenefit != null) {
            ideaCloudEndPoint.child(ideaUid).child("benefit").
                    setValue(etBenefit.getText().toString());
        }
        if (etCompetition != null) {
            ideaCloudEndPoint.child(ideaUid).child("competition").
                    setValue(etCompetition.getText().toString());
        }

        // details tab
        EditText etContact = findViewById(R.id.textBoxKontakt);
        if(etContact != null) {
            ideaCloudEndPoint.child(ideaUid).child("contact").
                    setValue(etContact.getText().toString());
        }
        Switch swtOpen = findViewById(R.id.switchOpen);
        if(swtOpen != null) {
            ideaCloudEndPoint.child(ideaUid).child("open").
                    setValue(swtOpen.isChecked());
        }

        Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();

    }

    public void onClickButtonUpdate(View view) {
        writeToCloud();
    }


    public void onClickButtonMyList(View view) {
        Intent i = new Intent(this, MyListActivity.class);
        startActivity(i);
    }

    public void onClickButtonSkapaIde(View view) {
        // create a new idea
        EditText etTitle = findViewById(R.id.textBoxTitle);
        String proposedTitle = etTitle.getText().toString().trim();

        if (proposedTitle.equals("")) {
            Toast.makeText(this, "Ange en kort titel på din idé för att skapa det", Toast.LENGTH_SHORT).show();
            return;
        }


        // check if the title is taken
        for (Idea i : ideas) {
            if(i.getTitle().equals(proposedTitle)) {
                Toast.makeText(this, "Idén finns redan registrerad", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        ideaUid = ideaCloudEndPoint.push().getKey();
        currentIdea = new Idea();
        currentIdea.setUid(ideaUid);
        currentIdea.setTitle(proposedTitle);
        ideaCloudEndPoint.child(ideaUid).setValue(currentIdea);

        Log.d(LOG_TAG, "uid created as : " + ideaUid);
    }
}