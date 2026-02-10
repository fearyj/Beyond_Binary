package com.beyondbinary.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class EventCreatedActivity extends AppCompatActivity {

    private RecyclerView communityRecyclerView;
    private CommunityMemberAdapter adapter;
    private List<CommunityMember> communityMembers;
    private Button doneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_created);

        communityRecyclerView = findViewById(R.id.community_recycler_view);
        doneButton = findViewById(R.id.done_button);

        communityRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Hardcoded community members for demonstration
        communityMembers = new ArrayList<>();
        communityMembers.add(new CommunityMember("Niko Castillo", "Low Social", "", "https://placeholder.com/user1"));
        communityMembers.add(new CommunityMember("Jasper Flores", "", "Low Physical", "https://placeholder.com/user2"));
        communityMembers.add(new CommunityMember("Jeremiah Pennington", "Low Social", "Low Physical", "https://placeholder.com/user3"));
        communityMembers.add(new CommunityMember("Eileen Bridges", "", "Low Physical", "https://placeholder.com/user4"));
        communityMembers.add(new CommunityMember("Sarah James", "Low Social", "", "https://placeholder.com/user5"));
        communityMembers.add(new CommunityMember("Patrick Calhoun", "Low Social", "Low Physical", "https://placeholder.com/user6"));
        communityMembers.add(new CommunityMember("Melissa Casey", "Low Social", "", "https://placeholder.com/user7"));
        communityMembers.add(new CommunityMember("Celeste Floyd", "", "Low Physical", "https://placeholder.com/user8"));

        adapter = new CommunityMemberAdapter(communityMembers, member -> {
            // Handle invite click
            // TODO: Implement invite functionality
        });

        communityRecyclerView.setAdapter(adapter);

        // Done button click listener
        doneButton.setOnClickListener(v -> {
            Intent intent = new Intent(EventCreatedActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }
}
