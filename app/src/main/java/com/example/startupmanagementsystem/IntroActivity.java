package com.example.startupmanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class IntroActivity extends AppCompatActivity {

    private Button loginButton;
    private TextView projectTitle, introText, featuresHeader, featuresList,
            objectiveHeader, objectiveText, methodologyHeader, methodologyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        initializeViews();
        setupLoginButton();
        displayIntroductoryContent();
    }

    private void initializeViews() {
        loginButton = findViewById(R.id.loginButton);
        projectTitle = findViewById(R.id.projectTitle);
        introText = findViewById(R.id.introText);
        featuresHeader = findViewById(R.id.featuresHeader);
        featuresList = findViewById(R.id.featuresList);
        objectiveHeader = findViewById(R.id.objectiveHeader);
        objectiveText = findViewById(R.id.objectiveText);
        methodologyHeader = findViewById(R.id.methodologyHeader);
        methodologyText = findViewById(R.id.methodologyText);
    }

    private void setupLoginButton() {
        loginButton.setOnClickListener(v -> {
            Intent loginIntent = new Intent(IntroActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish(); // Close IntroActivity after navigating
        });
    }

    private void displayIntroductoryContent() {
        // You can dynamically set the text here if needed, or leave it to the XML layout.
        // Example:
        // projectTitle.setText(getString(R.string.intro_title));
        // introText.setText(getString(R.string.intro_description));
    }
}