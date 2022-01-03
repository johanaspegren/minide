package com.aspegrenide.minide;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.aspegrenide.minide.data.Challenge;
import com.aspegrenide.minide.data.Idea;
import com.aspegrenide.minide.data.Problem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyListActivity extends AppCompatActivity {

    public static final String ITEM_UID = "ITEM_UID";
    // Use FireBase database to handle ideas, problems and needs
    private DatabaseReference fbDatabaseReference;
    private DatabaseReference challengeCloudEndPoint;
    private DatabaseReference problemCloudEndPoint;
    private DatabaseReference ideaCloudEndPoint;

    ArrayList<Challenge> challenges;
    ArrayList<String> challengesTitles;
    ArrayList<Problem> problems;
    ArrayList<String> problemsTitles;
    ArrayList<Idea> ideas;
    ArrayList<String> ideasTitles;

    private static final String LOG_TAG = "MIN_LISTA";
    // to handle list in the MyList tab

    RecyclerView recViewChallenges;
    RecyclerView recViewProblems;
    RecyclerView recViewIdeas;

    RecyclerView.LayoutManager recViewLayoutManagerChallenges;
    RecyclerView.Adapter recViewAdapterChallenges;

    RecyclerView.LayoutManager recViewLayoutManagerProblems;
    RecyclerView.Adapter recViewAdapterProblems;

    RecyclerView.LayoutManager recViewLayoutManagerIdeas;
    RecyclerView.Adapter recViewAdapterIdeas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);

        fbDatabaseReference =  FirebaseDatabase.getInstance("https://minide-1d8d6-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        challengeCloudEndPoint = fbDatabaseReference.child("challenges");
        challengeCloudEndPoint.addValueEventListener(challengeListener);

        problemCloudEndPoint = fbDatabaseReference.child("problems");
        problemCloudEndPoint.addValueEventListener(problemListener);

        ideaCloudEndPoint = fbDatabaseReference.child("ideas");
        ideaCloudEndPoint.addValueEventListener(ideaListener);

        recViewChallenges = findViewById(R.id.recViewChallenge);
        recViewProblems = findViewById(R.id.recViewProblem);
        recViewIdeas = findViewById(R.id.recViewIdea);

        challenges = new ArrayList<>();
        challengesTitles = new ArrayList<>();
        problems = new ArrayList<>();
        problemsTitles = new ArrayList<>();
        ideas = new ArrayList<>();
        ideasTitles = new ArrayList<>();

        recViewChallenges.setHasFixedSize(true);
        recViewProblems.setHasFixedSize(true);
        recViewIdeas.setHasFixedSize(true);

        recViewLayoutManagerChallenges = new LinearLayoutManager(this);
        recViewLayoutManagerProblems = new LinearLayoutManager(this);
        recViewLayoutManagerIdeas = new LinearLayoutManager(this);

        recViewAdapterChallenges = new MainAdapter(challengesTitles);
        recViewChallenges.setLayoutManager(recViewLayoutManagerChallenges);
        recViewChallenges.setAdapter(recViewAdapterChallenges);

        recViewAdapterProblems = new MainAdapter(problemsTitles);
        recViewProblems.setLayoutManager(recViewLayoutManagerProblems);
        recViewProblems.setAdapter(recViewAdapterProblems);

        recViewProblems.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recViewProblems, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                String title = problemsTitles.get(position);
                Log.d(LOG_TAG, "click from main activity with problem: " + title);
                // open this problem in the tabactivity
                String uid = problems.get(position).getUid();
                startProblem(uid);
            }

            @Override
            public void onLongClick(View view, int position) {
                Log.d(LOG_TAG, "long click from main activity");

            }
        }));

        recViewAdapterIdeas = new MainAdapter(ideasTitles);
        recViewIdeas.setLayoutManager(recViewLayoutManagerIdeas);
        recViewIdeas.setAdapter(recViewAdapterIdeas);

        recViewIdeas.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recViewIdeas, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                String title = ideasTitles.get(position);
                Log.d(LOG_TAG, "click from main activity with idea: " + title);

                // open this idea in the tabactivity
                String uid = ideas.get(position).getUid();
                startIdea(uid);
            }

            @Override
            public void onLongClick(View view, int position) {
                Log.d(LOG_TAG, "long click from main activity");

            }
        }));
    }

    ValueEventListener challengeListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // Get Post object and use the values to update the UI
            Iterable<DataSnapshot> stuff = dataSnapshot.getChildren();
            challenges.clear();
            challengesTitles.clear();
            for (DataSnapshot s : stuff){
                Challenge c = s.getValue(Challenge.class);
                Log.d(LOG_TAG, "read c as " + c.toString());
                challenges.add(c);
                challengesTitles.add(c.getTitle());
            }
            recViewAdapterChallenges.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w(LOG_TAG, "loadPost:onCancelled", databaseError.toException());
        }
    };


    ValueEventListener problemListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // Get Post object and use the values to update the UI
            Iterable<DataSnapshot> stuff = dataSnapshot.getChildren();
            problems.clear();
            problemsTitles.clear();
            for (DataSnapshot s : stuff){
                Problem p = s.getValue(Problem.class);
                Log.d(LOG_TAG, "read p as " + p.toString());
                problems.add(p);
                problemsTitles.add(p.getTitle());
            }
            recViewAdapterProblems.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w(LOG_TAG, "loadPost:onCancelled", databaseError.toException());
        }
    };

    ValueEventListener ideaListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // Get Post object and use the values to update the UI
            Iterable<DataSnapshot> stuff = dataSnapshot.getChildren();
            ideas.clear();
            ideasTitles.clear();
            for (DataSnapshot s : stuff){
                Idea i = s.getValue(Idea.class);
                Log.d(LOG_TAG, "read i as " + i.toString());
                ideas.add(i);
                ideasTitles.add(i.getTitle());
            }
            recViewAdapterIdeas.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w(LOG_TAG, "loadPost:onCancelled", databaseError.toException());
        }
    };

    public void onClickButtonNewChallenge(View view) {
    }

    public void onClickButtonNewProblem(View view) {
        startProblem(null);
    }

    public void startProblem(String uid) {
        Intent i = new Intent(this, MainActivityProblem.class);
        if(uid != null) {
            Log.d(LOG_TAG, "send intent with uid as "+ ITEM_UID);
            i.putExtra(ITEM_UID, uid);
        }
        startActivity(i);
    }

    public void onClickButtonNewIdea(View view) {
        startIdea(null);
    }

    public void startIdea(String uid) {
        Intent i = new Intent(this, MainActivityIdea.class);
        if(uid != null) {
            Log.d(LOG_TAG, "send intent with uid as "+ ITEM_UID);
            i.putExtra(ITEM_UID, uid);
        }
        startActivity(i);
    }

}