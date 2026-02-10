package com.beyondbinary.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class EventCreatedActivity extends AppCompatActivity {

    private RecyclerView communityRecyclerView;
    private CommunityMemberAdapter adapter;
    private List<CommunityMember> communityMembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_created);

        communityRecyclerView = findViewById(R.id.community_recycler_view);
        View doneButton = findViewById(R.id.done_button);
        View closeButton = findViewById(R.id.btn_close_community);

        communityRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Hardcoded community members
        communityMembers = new ArrayList<>();
        communityMembers.add(new CommunityMember("Niko Castillo", "Low Social", "", ""));
        communityMembers.add(new CommunityMember("Jasper Flores", "", "Low Physical", ""));
        communityMembers.add(new CommunityMember("Jeremiah Pennington", "Low Social", "Low Physical", ""));
        communityMembers.add(new CommunityMember("Eileen Bridges", "", "Low Physical", ""));
        communityMembers.add(new CommunityMember("Sarah James", "Low Social", "", ""));
        communityMembers.add(new CommunityMember("Patrick Calhoun", "Low Social", "Low Physical", ""));
        communityMembers.add(new CommunityMember("Melissa Casey", "Low Social", "", ""));
        communityMembers.add(new CommunityMember("Celeste Floyd", "Low Social", "", ""));

        adapter = new CommunityMemberAdapter(communityMembers, member -> {
            // Invite click handled in adapter
        });

        communityRecyclerView.setAdapter(adapter);

        // Done button → go home
        doneButton.setOnClickListener(v -> {
            Intent intent = new Intent(EventCreatedActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        // Close button → also go home
        closeButton.setOnClickListener(v -> {
            Intent intent = new Intent(EventCreatedActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }
}
