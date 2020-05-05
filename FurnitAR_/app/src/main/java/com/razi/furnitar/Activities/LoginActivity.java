package com.razi.furnitar.Activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.razi.furnitar.API.LoginService;
import com.razi.furnitar.R;
import com.razi.furnitar.Utils.Constants;
import com.razi.furnitar.Utils.UserPreference;
import com.razi.furnitar.InternetConnectivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 69;
    SignInButton sBtn;
    InternetConnectivity it;
    EditText userT, pass;
    Button signin, signUp;
    protected void onDestroy() {
        unregisterReceiver(it);
        super.onDestroy();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userT = findViewById(R.id.username);
        pass = findViewById(R.id.password);
        signin = findViewById(R.id.signin);
        signUp = findViewById(R.id.signup_2);
        signin.setOnClickListener(v -> signIn(userT.getText().toString(), pass.getText().toString()));
        signUp.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignupActivity.class)));
        String saved_email = UserPreference.getInstance().get("saved_email", "");
        if (saved_email.isEmpty()) {

        } else {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
        IntentFilter in = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        it = new InternetConnectivity();
        registerReceiver(it, in);
    }

    public void signIn(String email, String password) {

        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = userT.getText().toString();

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if (!matcher.matches()) {
            userT.setError("Invalid Email");
            return;
        }

        String passe = pass.getText().toString();
        if (TextUtils.isEmpty(passe) || pass.length() < 6) {
            pass.setError("Invalid Password");
            return;
        }

        Map<String, Object> body = new HashMap<>();
        body.put("user_email", email);
        body.put("user_password", password);

        new LoginService(this, Constants.LOGIN, body, result -> {
            Log.d("LoginResult", result);
            try {
                JSONObject resObject = new JSONObject(result);
                int status = resObject.getInt("code");
                if (status == 400) {
                    String message = resObject.getString("failed");
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
//                    Common.showAlert(LoginActivity.this, getString(R.string.error), message);
                } else if (status == 204) {
                    String message = resObject.getString("success");
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                } else if (status == 206) {
                    String message = resObject.getString("success");
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                } else if (status == 200) {
                    JSONObject user_data = new JSONObject(resObject.getString("data"));
                    int user_id = user_data.getInt("id");
                    String user_email = user_data.getString("user_email");
                    String user_password = user_data.getString("user_password");
                    UserPreference.getInstance().set("saved_email", user_email);
                    UserPreference.getInstance().set("saved_password", user_password);
                    UserPreference.getInstance().set("user_id", user_id);
//                    GlobalData.user_api_hash = user_api_hash;
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("Yens", "Google sign in failed", e);
                // ...
            }
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            //updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("OK", "signInResult:failed code=" + e.getStatusCode());
            // updateUI(null);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("WOT", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
//        gAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d("One", "signInWithCredential:success");
//                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                            String name = user.getDisplayName();
//                            String email = user.getEmail();
//                            Uri photoUrl = user.getPhotoUrl();
//                            String uid = user.getUid();
//                            common.currentUser = new user(name, uid, email, photoUrl);
//                            Log.i("Yes", common.currentUser.getName());
//                            //updateUI(user);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w("OK", "signInWithCredential:failure", task.getException());
//                            //updateUI(null);
//                        }
//
//                        // ...
//                    }
//                });
    }

    @Override
    public void onStart() {
        super.onStart();
//        gAuth.addAuthStateListener(aL);
    }
}
