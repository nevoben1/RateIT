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
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            readDB();
                            writeToDb();
                            Toast.makeText(MainActivity.this , "register ok" , Toast.LENGTH_LONG).show();

                            NavController navController = Navigation.findNavController(MainActivity.this , R.id.fragmentContainerView);
                            navController.navigate(R.id.action_registerFragment_to_loginFragment);
                        } else {

                        }
                    }
                });
    }

    public void writeToDb(){
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //get id from register page
        String id = ((EditText)findViewById(R.id.registerIDView)).getText().toString();
        String email = ((EditText)findViewById(R.id.registerEmailText)).getText().toString();
        String phone = ((EditText)findViewById(R.id.registerPhoneView)).getText().toString();
        //go to the directory listed below if doesnt exist it will be created
        DatabaseReference myRef = database.getReference("users").child(id);
        User user = new User(id , phone , email);
        //pass the object to the function it will be added to the db
        myRef.setValue(user);
    }

    public void readDB(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // Read from the database
        DatabaseReference myRef = database.getReference("users").child("2222");

        myRef.addValueEventListener(new ValueEventListener() {
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



