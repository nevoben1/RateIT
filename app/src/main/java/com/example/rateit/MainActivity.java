package com.example.rateit;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.charset.StandardCharsets;
import android.util.Base64;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //init firebase authentication
        mAuth = FirebaseAuth.getInstance();
    }

    public void login(){
        //get the text of both fields email and password
        String email = ((EditText)findViewById(R.id.emailTextView)).getText().toString();
        String password = ((EditText)findViewById(R.id.paswwordTextView)).getText().toString();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //show a toast on success , LENGTH_LONG sets the duration of the toast
                            Toast.makeText(MainActivity.this , "login ok" , Toast.LENGTH_LONG).show();
                            //get the nav controller
                            //The nav container should be inside the ActivityMain.xml
                            NavController navController = Navigation.findNavController(MainActivity.this , R.id.fragmentContainerView);
                            navController.navigate(R.id.action_loginFragment_to_gamesListFragment);
                        } else {
                            Toast.makeText(MainActivity.this , "login failed" , Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }

    public void register(){
        String email = ((EditText)findViewById(R.id.registerEmailText)).getText().toString();
        String password = ((EditText)findViewById(R.id.registerPassView)).getText().toString();
        String phone = ((EditText)findViewById(R.id.registerPhoneView)).getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Get the UID of the newly created user
                            String uid = mAuth.getCurrentUser().getUid();

                            // Write user data to database using UID
                            writeUserToDb(uid, email, phone);
                        }
                        else {
                            String errorMessage = task.getException() != null ?
                                    task.getException().getMessage() : "register failed";
                            Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void addFavByUser(String gameId, String gameName, String gameImageUrl)
    {
        if (mAuth.getCurrentUser() != null) {
            String uid = mAuth.getCurrentUser().getUid();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference favRef = database.getReference("users").child(uid).child("favorites").child(gameId);

            // Create a simple favorite object with game info
            java.util.HashMap<String, String> favorite = new java.util.HashMap<>();
            favorite.put("gameId", gameId);
            favorite.put("gameName", gameName);
            favorite.put("gameImageUrl", gameImageUrl);
            favorite.put("timestamp", String.valueOf(System.currentTimeMillis()));

            favRef.setValue(favorite).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Added to favorites", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to add favorite", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void removeFavByUser(String gameId) {
        if (mAuth.getCurrentUser() != null) {
            String uid = mAuth.getCurrentUser().getUid();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference favRef = database.getReference("users").child(uid).child("favorites").child(gameId);

            favRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to remove favorite", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void loadUserFavorites(OnFavoritesLoadedListener listener) {
        if (mAuth.getCurrentUser() != null) {
            String uid = mAuth.getCurrentUser().getUid();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference favRef = database.getReference("users").child(uid).child("favorites");

            favRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    java.util.Set<String> favoriteIds = new java.util.HashSet<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        favoriteIds.add(snapshot.getKey());
                    }
                    if (listener != null) {
                        listener.onFavoritesLoaded(favoriteIds);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    if (listener != null) {
                        listener.onFavoritesLoaded(new java.util.HashSet<>());
                    }
                }
            });
        } else {
            if (listener != null) {
                listener.onFavoritesLoaded(new java.util.HashSet<>());
            }
        }
    }

    public interface OnFavoritesLoadedListener {
        void onFavoritesLoaded(java.util.Set<String> favoriteIds);
    }

    private void writeUserToDb(String uid, String email, String phone){
        // Write user data to the database using Firebase Auth UID
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //go to the directory listed below if doesnt exist it will be created
        DatabaseReference myRef = database.getReference("users").child(uid);
        User user = new User(uid , phone , email);
        //pass the object to the function it will be added to the db
        myRef.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(MainActivity.this , "register ok" , Toast.LENGTH_SHORT).show();
                    NavController navController = Navigation.findNavController(MainActivity.this , R.id.fragmentContainerView);
                    navController.navigate(R.id.action_registerFragment_to_loginFragment);
                }
                else{
                    Toast.makeText(MainActivity.this , "failed to create user" , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void readDB(){
        // Get the UID of the currently authenticated user
        if (mAuth.getCurrentUser() != null) {
            String uid = mAuth.getCurrentUser().getUid();

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            // Read from the database using UID
            DatabaseReference myRef = database.getReference("users").child(uid);

            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    //WE READ USER VALUE , THEREFORE GET USER
                    User value = dataSnapshot.getValue(User.class);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                }
            });
        }
    }


}



