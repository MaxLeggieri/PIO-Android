package com.livelife.pioalert;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SigninActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String tag = SigninActivity.class.getSimpleName();

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    LinearLayout fbButton,gButton,promoCodeView;
    ProgressBar loginProgress;

    EditText code;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_signin);


        fbButton = (LinearLayout) findViewById(R.id.fbButton);
        gButton = (LinearLayout) findViewById(R.id.gButton);
        fbButton.setOnClickListener(signinButtonListener);
        gButton.setOnClickListener(signinButtonListener);
        loginProgress = (ProgressBar) findViewById(R.id.loginProgress);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(tag, "onAuthStateChanged:signed_in:" + user.getUid());
                    PioUser.getInstance().setLogged(true);
                    finish();
                }

            }
        };

        promoCodeView = (LinearLayout) findViewById(R.id.promoCodeView);
        code = (EditText) findViewById(R.id.code);
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (!PioUser.getInstance().promoCode.equals("")) {
            promoCodeView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void hideLoginControls(boolean hide) {

        if (hide) {
            fbButton.setVisibility(View.INVISIBLE);
            gButton.setVisibility(View.INVISIBLE);
            loginProgress.setVisibility(View.VISIBLE);
        } else {
            fbButton.setVisibility(View.VISIBLE);
            gButton.setVisibility(View.VISIBLE);
            loginProgress.setVisibility(View.GONE);
        }

    }

    GoogleApiClient mGoogleApiClient;
    CallbackManager callbackManager;
    Handler signinHandler = new Handler();
    View.OnClickListener signinButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {


            final int buttonId = view.getId();

            Log.e(tag,"Signin clicked...");

            hideLoginControls(true);
            switch (buttonId) {
                case R.id.gButton: {



                    // Configure sign-in to request the user's ID, email address, and basic
                    // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken("920202055350-3t25lumk83h4pq7ql7pqknmnldp5nc13.apps.googleusercontent.com")
                            .requestEmail()
                            .build();

                    mGoogleApiClient = new GoogleApiClient.Builder(SigninActivity.this)
                            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                            .build();


                    signIn();



                    break;
                }
                case R.id.fbButton: {



                    //FacebookSdk.sdkInitialize(SigninActivity.this.getApplicationContext());
                    callbackManager = CallbackManager.Factory.create();

                    LoginManager.getInstance().registerCallback(callbackManager,
                            new FacebookCallback<LoginResult>() {
                                @Override
                                public void onSuccess(LoginResult loginResult) {
                                    // App code
                                    handleFacebookAccessToken(loginResult.getAccessToken());
                                    Log.v(tag,"callbackManager: logged with facebook success...");
                                }

                                @Override
                                public void onCancel() {
                                    // App code
                                    hideLoginControls(false);
                                }

                                @Override
                                public void onError(FacebookException exception) {
                                    // App code
                                    hideLoginControls(false);
                                }
                            });

                    LoginManager.getInstance().logInWithReadPermissions(SigninActivity.this, Arrays.asList("public_profile","email"));

                    break;
                }
            }



        }
    };



    public static int RC_SIGN_IN = 4001;
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);

            } else {
                // Google Sign In failed, update UI appropriately
                Log.e(tag,"Google Sign In failed, update UI appropriately");
                hideLoginControls(false);
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d(tag, "firebaseAuthWithGoogle:" + acct.getId());

        //PioUser.getInstance().setAuthToken(this,acct.getIdToken());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(tag, "signInWithCredential:onComplete:" + task.isSuccessful());

                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    Log.e(tag, "signInWithCredential", task.getException());

                    hideLoginControls(false);
                } else {


                    JSONObject object = WebApi.getInstance().sendGoogleData(acct,code.getText().toString());


                    try {
                        Log.v(tag,object.toString(2));
                        PioUser.getInstance().setUid(object.getInt("uid"));
                        PioUser.getInstance().setPromoCode(code.getText().toString());
                        PioUser.getInstance().setUserEmail(acct.getEmail());
                        PioUser.getInstance().setUserNameAndImage(acct.getGivenName(),acct.getFamilyName(),"https://lh4.googleusercontent.com"+acct.getPhotoUrl().getPath());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        });
    }

    private void handleFacebookAccessToken(final AccessToken token) {
        Log.d(tag, "handleFacebookAccessToken:" + token);


        //PioUser.getInstance().setAuthToken(this,token.getToken());



        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(tag, "signInWithCredential:onComplete:" + task.isSuccessful());

                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    Log.w(tag, "signInWithCredential", task.getException());
                    Toast.makeText(SigninActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                } else {

                    GraphRequest request = GraphRequest.newMeRequest(
                            AccessToken.getCurrentAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(
                                        JSONObject object,
                                        GraphResponse response) {
                                    if(object == null) {
                                        return;
                                    }

                                    WebApi.getInstance().sendFacebookData(object.toString(), SigninActivity.this,code.getText().toString());

                                    try {
                                        Log.v(tag,"graphrequest: "+object.toString(2));

                                        String imgPath = object.getJSONObject("picture").getJSONObject("data").getString("url");
                                        String userName = object.getString("first_name");
                                        String lastName = object.getString("last_name");

                                        PioUser.getInstance().setUserEmail(object.getString("email"));
                                        PioUser.getInstance().setUserNameAndImage(userName,lastName,imgPath);
                                        PioUser.getInstance().setPromoCode(code.getText().toString());


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,email,first_name,last_name,verified,locale,timezone,gender,birthday,location,picture.type(large)");
                    request.setParameters(parameters);
                    request.executeAsync();

                }

                // ...
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                if(e instanceof FirebaseAuthUserCollisionException)
                {

                    if(FirebaseAuth.getInstance().getCurrentUser() == null) {

                        // TODO
                        // Handle users...

                        Log.v(tag,"FirebaseAuthUserCollisionException: No valid firebase user present");


                    }



                }

            }
        });
    }






    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v(tag,connectionResult.toString());
    }
}
