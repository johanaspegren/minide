package com.aspegrenide.minide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.aspegrenide.minide.data.Idea;
import com.aspegrenide.minide.data.Problem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivityProblem extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    FragmentAdapterProblem fragmentAdapterProblem;

    EditText etTitle;

    // Use FireBase database to handle ideas, problems and needs
    private DatabaseReference fbDatabaseReference;
    private DatabaseReference problemCloudEndPoint;

    private ArrayList<Problem> problems;
    private Problem currentProblem;
    private String problemUid;

    private String LOG_TAG = "min_ide";

    // show linked ideas
    RecyclerView recViewIdeas;
    RecyclerView recViewLinkedIdeas;
    RecyclerView.LayoutManager recViewLayoutManagerIdeas;
    RecyclerView.LayoutManager recViewLayoutManagerLinkedIdeas;
    RecyclerView.Adapter recViewAdapterIdeas;
    RecyclerView.Adapter recViewAdapterLinkedIdeas;

    ArrayList<Idea> ideas;
    ArrayList<Idea> ideasLinked;
    ArrayList<String> ideasTitles;
    ArrayList<String> ideasLinkedTitles;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_problem);
        TextView tabIntro = findViewById(R.id.tvTabIntro);
        tabIntro.setText("Beskriv problemet");

        etTitle = findViewById(R.id.textBoxTitle);


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                Log.d(LOG_TAG, "receive intent with NO uid as "+ MyListActivity.ITEM_UID);
                problemUid = null; // we are here to create a new problem
            } else {
                problemUid = extras.getString(MyListActivity.ITEM_UID);
                Log.d(LOG_TAG, "receive intent with uid as "+ MyListActivity.ITEM_UID + " " + problemUid);
            }
        } else {
            problemUid = (String) savedInstanceState.getSerializable(MyListActivity.ITEM_UID);
            Log.d(LOG_TAG, "receive intent (2) with uid as "+ MyListActivity.ITEM_UID);
        }
        Log.d(LOG_TAG, "uid received as = " + problemUid);

        currentProblem = new Problem();
        problems = new ArrayList<>();

        fbDatabaseReference =  FirebaseDatabase.getInstance("https://minide-1d8d6-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        problemCloudEndPoint = fbDatabaseReference.child("problems");
        problemCloudEndPoint.addValueEventListener(problemListener);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager2);

        FragmentManager fm = getSupportFragmentManager();
        fragmentAdapterProblem = new FragmentAdapterProblem(fm, getLifecycle());
        viewPager2.setAdapter(fragmentAdapterProblem);

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
                    tabIntro.setText("Beskriv problemet");
                    // we can not access views from the fragment here
                    // they have not been created, so hold on :-)
                }
                if(tab.getPosition() == 1) {
                    tabIntro.setText("Ta en bild eller hämta från albumet");
                }
                if(tab.getPosition() == 2) {
                    tabIntro.setText("Lite om hur problemet hänger ihop");
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
        if (currentProblem == null) {
            return;
        }
        EditText etContact = findViewById(R.id.textBoxKontakt);
        if(etContact == null) { return; }
        String k = currentProblem.getContact();
        etContact.setText(k);

        Switch openItem = findViewById(R.id.switchOpen);
        openItem.setChecked(currentProblem.isOpen());

        fbDatabaseReference =  FirebaseDatabase.getInstance("https://minide-1d8d6-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        DatabaseReference ideaCloudEndPoint = fbDatabaseReference.child("ideas");
        ideaCloudEndPoint.addValueEventListener(ideaListener);

        ideas = new ArrayList<>();
        ideasLinked = new ArrayList<>();
        ideasTitles = new ArrayList<>();
        ideasLinkedTitles = new ArrayList<>();


        recViewIdeas = findViewById(R.id.recViewAllIdeas);
        recViewLinkedIdeas = findViewById(R.id.recViewLinkedIdeas);
        recViewLayoutManagerIdeas = new LinearLayoutManager(this);
        recViewLayoutManagerLinkedIdeas = new LinearLayoutManager(this);

        recViewAdapterIdeas = new MainAdapter(ideasTitles);
        recViewAdapterLinkedIdeas = new MainAdapter(ideasLinkedTitles);

        recViewIdeas.setLayoutManager(recViewLayoutManagerIdeas);
        recViewIdeas.setAdapter(recViewAdapterIdeas);

        recViewLinkedIdeas.setLayoutManager(recViewLayoutManagerLinkedIdeas);
        recViewLinkedIdeas.setAdapter(recViewAdapterLinkedIdeas);

        recViewIdeas.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recViewIdeas, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                String title = ideasTitles.get(position);
                Log.d(LOG_TAG, "click from main activity with idea: " + title);
                // move item from all to linked
                updateLinkedLists(position);

            }

            @Override
            public void onLongClick(View view, int position) {
                Log.d(LOG_TAG, "long click from main activity");
            }
        }));
    }

    private void updateLinkedLists(int position) {
        // update the main lists
        Idea temp = ideas.get(position);
        ideas.remove(position);
        ideasLinked.add(temp);

        // also update the titles that go into the recyclerviews
        String t = ideasTitles.get(position);
        ideasTitles.remove(position);
        ideasLinkedTitles.add(t);

        recViewAdapterIdeas.notifyDataSetChanged();
        recViewAdapterLinkedIdeas.notifyDataSetChanged();

    }

    ValueEventListener ideaListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // Get Post object and use the values to update the UI
            Iterable<DataSnapshot> stuff = dataSnapshot.getChildren();
            ideas.clear();
            ideasTitles.clear();
            for (DataSnapshot s : stuff) {
                Idea i = s.getValue(Idea.class);
                Log.d(LOG_TAG, "read i as " + i.toString());
                // now check which ones are already selected
                if(currentProblem.getLinkedIdeas().contains(i.getUid())) {
                    // already listed
                    ideasLinked.add(i);
                    ideasLinkedTitles.add(i.getTitle());
                } else {
                    ideas.add(i);
                    ideasTitles.add(i.getTitle());
                }
            }
            recViewAdapterIdeas.notifyDataSetChanged();
            recViewAdapterLinkedIdeas.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };


    private void fillInTheBoxesDescription() {
        if (currentProblem == null) {
            return;
        }
        etTitle.setText(currentProblem.getTitle());

        EditText etWhat = findViewById(R.id.textBoxApproach);
        EditText etWhen = findViewById(R.id.textboxBenefit);
        EditText etWhere = findViewById(R.id.textboxVar);
        EditText etWho = findViewById(R.id.textboxCompetition);

        if(etWhat == null) { return;}
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
                if(p.getUid().equals(problemUid)) {
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



    private void writeToCloud() {

        if(problemUid == null) {
            Toast.makeText(this, "Du måste skapa ett problem först", Toast.LENGTH_SHORT).show();
            return;
        }

        // description tab
        EditText etWhat = findViewById(R.id.textBoxApproach);
        EditText etWhen = findViewById(R.id.textboxBenefit);
        EditText etWhere = findViewById(R.id.textboxVar);
        EditText etWho = findViewById(R.id.textboxCompetition);

        if (etWhat != null) {
            problemCloudEndPoint.child(problemUid).child("what").
                    setValue(etWhat.getText().toString());
        }
        if (etWhere != null) {
            problemCloudEndPoint.child(problemUid).child("where").
                    setValue(etWhere.getText().toString());
        }

        if (etWho != null) {
            problemCloudEndPoint.child(problemUid).child("who").
                    setValue(etWho.getText().toString());
        }
        if (etWhen != null) {
            problemCloudEndPoint.child(problemUid).child("when").
                    setValue(etWhen.getText().toString());
        }

        // details tab
        EditText etContact = findViewById(R.id.textBoxKontakt);
        if(etContact != null) {
            problemCloudEndPoint.child(problemUid).child("contact").
                    setValue(etContact.getText().toString());
        }
        Switch swtOpen = findViewById(R.id.switchOpen);
        if(swtOpen != null) {
            problemCloudEndPoint.child(problemUid).child("open").
                    setValue(swtOpen.isChecked());
        }
        //linked ideas
        RecyclerView rc = findViewById(R.id.recViewLinkedIdeas);
        if(rc != null) {
            ArrayList<String> linkedUids = new ArrayList<>();
            for (Idea i : ideasLinked) {
                linkedUids.add(i.getUid());
            }
            problemCloudEndPoint.child(problemUid).child("linkedIdeas").
                    setValue(linkedUids);
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

    public void onClickButtonSkapaProblem(View view) {
        // create a new problem

        EditText etTitle = findViewById(R.id.textBoxTitle);
        String proposedTitle = etTitle.getText().toString().trim();

        if (proposedTitle.equals("")) {
            Toast.makeText(this, "Ange en kort titel på ditt problem för att skapa det", Toast.LENGTH_SHORT).show();
            return;
        }


        // check if the title is taken
        for (Problem p : problems) {
            if(p.getTitle().equals(proposedTitle)) {
                Toast.makeText(this, "Problemet finns redan registrerat", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        problemUid = problemCloudEndPoint.push().getKey();
        currentProblem = new Problem();
        currentProblem.setUid(problemUid);
        currentProblem.setTitle(proposedTitle);

        problemCloudEndPoint.child(problemUid).setValue(currentProblem);

        Log.d(LOG_TAG, "uid created as : " + problemUid);
    }
}