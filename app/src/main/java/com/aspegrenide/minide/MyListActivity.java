package com.aspegrenide.minide;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

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

    private DatabaseReference fbDatabaseReference;
    private DatabaseReference problemCloudEndPoint;
    private DatabaseReference ideasCloudEndPoint;
    private DatabaseReference projectsCloudEndPoint;

    ArrayList <Problem> problems = new ArrayList<>();
    ArrayList <Idea> ideas = new ArrayList<>();

    private static final String LOG_TAG = "MIN_LISTA";
    // to handle list in the MyList tab
    ExpandableListView expandableListViewProblems;
    ExpandableListView expandableListViewIdeas;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableTitleListProblem;
    List<String> expandableTitleListIdea;
    HashMap<String, List<String>> expandableDetailListProblems;
    HashMap<String, List<String>> expandableDetailListIdeas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);

        // fetch data
        fbDatabaseReference =  FirebaseDatabase.getInstance("https://minide-1d8d6-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        problemCloudEndPoint = fbDatabaseReference.child("problems");
        ideasCloudEndPoint = fbDatabaseReference.child("ideas");
        projectsCloudEndPoint = fbDatabaseReference.child("projects");
        initProblemDataListener(problemCloudEndPoint);
        initIdeaDataListener(ideasCloudEndPoint);

    }



    private void drawList() {
        expandableListViewProblems = (ExpandableListView) findViewById(R.id.expandableListViewProblems);
        expandableListViewIdeas = (ExpandableListView) findViewById(R.id.expandableListViewIdeas);
        Log.d(LOG_TAG, "exp = " + expandableListViewProblems);
        Log.d(LOG_TAG, "exp = " + expandableListViewIdeas);
        expandableDetailListProblems = ExpandableListDataProblems.getData(problems);
        expandableDetailListIdeas = ExpandableListDataIdeas.getData(ideas);

        expandableTitleListProblem = new ArrayList<String>(expandableDetailListProblems.keySet());
        expandableTitleListIdea = new ArrayList<String>(expandableDetailListIdeas.keySet());

        expandableListViewProblems.setAdapter(new CustomizedExpandableListAdapter(
                this, expandableTitleListProblem,
                expandableDetailListProblems));
        expandableListViewIdeas.setAdapter(new CustomizedExpandableListAdapter(
                this, expandableTitleListIdea,
                expandableDetailListIdeas));


        // This method is called when the group is expanded
        expandableListViewProblems.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(), expandableTitleListProblem.get(groupPosition) + " List Expanded.", Toast.LENGTH_SHORT).show();
            }
        });

        // This method is called when the group is collapsed
        expandableListViewProblems.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(), expandableTitleListProblem.get(groupPosition) + " List Collapsed.", Toast.LENGTH_SHORT).show();
            }
        });

        // This method is called when the child in any group is clicked
        // via a toast method, it is shown to display the selected child item as a sample
        // we may need to add further steps according to the requirements
        expandableListViewProblems.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                String itemStr = expandableDetailListProblems.get(
                        expandableTitleListProblem.get(groupPosition)).get(
                        childPosition);
                String itemHeader = expandableTitleListProblem.get(groupPosition);

                if(itemStr.contains("Klicka mig för att uppdatera")) {
                    openProblemItem(expandableTitleListProblem.get(groupPosition));
                }

                return false;
            }
        });

        expandableListViewIdeas.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                String itemStr = expandableDetailListIdeas.get(
                        expandableTitleListIdea.get(groupPosition)).get(
                        childPosition);
                String itemHeader = expandableTitleListIdea.get(groupPosition);

                if(itemStr.contains("Klicka mig för att uppdatera")) {
                    openIdeaItem(expandableTitleListIdea.get(groupPosition));
                }

                return false;
            }
        });

        expandableListViewProblems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "loong click", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }



    public void initIdeaDataListener(DatabaseReference ref) {
        Log.d(LOG_TAG, "readRoundsData ");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Iterable<DataSnapshot> stuff = dataSnapshot.getChildren();
                for(DataSnapshot s : stuff){
                    Log.d(LOG_TAG, "Stuff is: " + s.toString());
                    Idea idea = s.getValue(Idea.class);
                    Log.d(LOG_TAG, "Idea is: " + idea.toString());
                    ideas.add(idea);
                }
                if(ideas.isEmpty()){
                    Log.d(LOG_TAG, "No Ideas available: " );
                }else{
                    Log.d(LOG_TAG, "Ideas available: " );
                    drawList();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(LOG_TAG, "Failed to read value.", error.toException());
            }
        });
    }
    public void initProblemDataListener(DatabaseReference ref) {
        Log.d(LOG_TAG, "readRoundsData ");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Iterable<DataSnapshot> stuff = dataSnapshot.getChildren();
                for(DataSnapshot s : stuff){
                    Log.d(LOG_TAG, "Stuff is: " + s.toString());
                    Problem problem = s.getValue(Problem.class);
                    Log.d(LOG_TAG, "Problem is: " + problem.toString());
                    problems.add(problem);
                }
                if(problems.isEmpty()){
                    Log.d(LOG_TAG, "No problems available: " );
                }else{
                    Log.d(LOG_TAG, "Problems available: " );
                    drawList();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(LOG_TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public void onClickButtonNewProblem(View view) {
        Intent i = new Intent(this, MainActivityProblem.class);
        startActivity(i);
    }

    public void onClickButtonNewIdea(View view) {
        Intent i = new Intent(this, MainActivityIdea.class);
        startActivity(i);
    }

    private void openProblemItem(String s) {
        Intent i = new Intent(this, MainActivityProblem.class);
        i.putExtra(MainActivityProblem.ITEM_TITLE, s);
        startActivity(i);
    }

    private void openIdeaItem(String s) {
        Intent i = new Intent(this, MainActivityIdea.class);
        i.putExtra(MainActivityIdea.ITEM_TITLE, s);
        startActivity(i);
    }

    public void onClickButtonNewProject(View view) {
        Intent i = new Intent(this, MainActivityIdea.class);
        startActivity(i);
    }
}