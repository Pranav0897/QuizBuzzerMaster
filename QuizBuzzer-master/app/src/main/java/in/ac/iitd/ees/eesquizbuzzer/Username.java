package in.ac.iitd.ees.eesquizbuzzer;

/**
 * Created by pranav97 on 05/02/17.
 */
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.client.ServerValue;



public class Username extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();
    TextView indicatorText;
    Button answerButton;
    Firebase firebase;
    Firebase isQuestionActiveRef, isQuestionAnsweredRef;
    Firebase username;
    long time;
    Firebase prevtime;
    com.firebase.client.Firebase db;
    boolean isQuestionActive, isQuestionAnswered, isAnsweredByMe;
    long myclicktime=Long.MAX_VALUE;
    String un;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        un = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        isQuestionActive = false;
        isQuestionAnswered = false;
        isAnsweredByMe = false;
        firebase = new Firebase("https://buzzer-570e3.firebaseio.com");
        isQuestionActiveRef = firebase.child("isQuestionActive");
        isQuestionAnsweredRef = firebase.child("isQuestionAnswered");
        username=firebase.child("user").child("username");
        prevtime=firebase.child("user").child("presstime");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        TextView editText=(TextView)findViewById(R.id.textView1);
        editText.setText(un);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onAnswerButtonClick(View view) {
        Log.d(TAG, "-->button clicked");
        //if (!isQuestionAnswered||(isQuestionAnswered&&(myclicktime>time&&time==0||(myclicktime<time&&time!=0)))) {
            isAnsweredByMe = true;
            isQuestionAnsweredRef.setValue(true);
        isQuestionAnswered=true;
            myclicktime=System.currentTimeMillis();
        Log.d(TAG," "+ isQuestionActive+" "+isAnsweredByMe+" "+username+" "+time+" "+System.currentTimeMillis()+" button pressed ");

        //if(time!=0&&time<clicktime)
        //}
    }

    @Override
    protected void onStart() {
        super.onStart();
        indicatorText = (TextView) findViewById(R.id.indicatorTextView);
        answerButton = (Button) findViewById(R.id.answerButton);
        firebase.addChildEventListener(new com.firebase.client.ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot==null)
                {
                    return;
                }
                if("isQuestionActive".equals(dataSnapshot.getKey()))
                {
                    isQuestionActive=dataSnapshot.getValue(boolean.class);
                }
                else if("isQuestionAnswered".equals(dataSnapshot.getKey())) {
                    isQuestionAnswered = dataSnapshot.getValue(boolean.class);
                }
                else if("user".equals(dataSnapshot.getKey())) {
                    time = dataSnapshot.child("presstime").getValue(long.class);
                }

                Log.d(TAG,dataSnapshot.hasChild("isQuestionActive")+" "+dataSnapshot.hasChildren()+" "+ isQuestionActive+" "+isQuestionAnswered+" "+username+" "+time+" "+System.currentTimeMillis()+" added ");
                Log.d(TAG,"dataSnapshot"+dataSnapshot);
                if(!isQuestionActive)
                {
                    indicatorText.setText("Wait For Question");
                    answerButton.setText("Wait!");
                    answerButton.setEnabled(false);
                    answerButton.setBackgroundColor(Color.parseColor("#cccccc"));

                        prevtime.setValue(0);
                        isQuestionAnsweredRef.setValue(false);
                        isAnsweredByMe=false;
                        isQuestionAnswered=false;
                    myclicktime=0;
                    time=0;

                }
                else if(isQuestionActive&&!isQuestionAnswered)
                {
                    indicatorText.setText("Press Button to Answer!!");
                    answerButton.setText("Answer");
                    answerButton.setEnabled(true);
                    answerButton.setBackgroundColor(Color.parseColor("#00ffff"));
                }
                else if(isQuestionAnswered&&isQuestionActive)
                {
                    if((myclicktime<=time&&myclicktime!=0&&time!=0)||(time==0&&myclicktime!=0))
                    {
                            answerButton.setEnabled(false);
                            indicatorText.setText("You pressed buzzer first!!");
                            answerButton.setText("Fast :)");
                            answerButton.setEnabled(false);
                            answerButton.setBackgroundColor(Color.parseColor("#00ff00"));
                            username.setValue(un);
                            prevtime.setValue(myclicktime);
                    }
                    else
                    {
                        indicatorText.setText(" Someone already pressed the buzzer!! ");
                        answerButton.setText("Slow :(");
                        answerButton.setEnabled(false);
                        answerButton.setBackgroundColor(Color.parseColor("#ff0000"));
                    }
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot==null)
                {
                    return;
                }
                if("isQuestionActive".equals(dataSnapshot.getKey()))
                {
                    isQuestionActive=dataSnapshot.getValue(boolean.class);
                }
                else if("isQuestionAnswered".equals(dataSnapshot.getKey())) {
                    isQuestionAnswered = dataSnapshot.getValue(boolean.class);
                }
                else if("user".equals(dataSnapshot.getKey())) {
                    time = dataSnapshot.child("presstime").getValue(long.class);
                }

                    Log.d(TAG,dataSnapshot.hasChild("isQuestionActive")+" "+dataSnapshot.hasChildren()+" "+ isQuestionActive+" "+isQuestionAnswered+" "+username+" "+time+" "+System.currentTimeMillis()+" changed ");
                Log.d(TAG,"dataSnapshot"+dataSnapshot);
                if(!isQuestionActive)
                {
                    indicatorText.setText("Wait For Question");
                    answerButton.setText("Wait!");
                    answerButton.setEnabled(false);
                    answerButton.setBackgroundColor(Color.parseColor("#cccccc"));
                    prevtime.setValue(0);
                    isQuestionAnsweredRef.setValue(false);
                    isAnsweredByMe=false;
                    isQuestionAnswered=false;
                    myclicktime=0;
                    time=0;
                }
                else if(isQuestionActive&&!isQuestionAnswered)
                {
                    indicatorText.setText("Press Button to Answer!!");
                    answerButton.setText("Answer");
                    answerButton.setEnabled(true);
                    answerButton.setBackgroundColor(Color.parseColor("#00ffff"));
                }
                else if(isQuestionAnswered&&isQuestionActive)
                {
                    answerButton.setEnabled(false);
                    if((myclicktime<=time&&myclicktime!=0&&time!=0)||(time==0&&myclicktime!=0))
                    {
                            indicatorText.setText("You pressed buzzer first!!");
                            answerButton.setText("Fast :)");
                            answerButton.setEnabled(false);
                            answerButton.setBackgroundColor(Color.parseColor("#00ff00"));
                            //firebase.child("user").removeValue();
                            username.setValue(un);
                            prevtime.setValue(myclicktime);
                    }
                    else
                    {
                        indicatorText.setText(" Someone already pressed the buzzer!!");
                        answerButton.setText("Slow :(");
                        answerButton.setEnabled(false);
                        answerButton.setBackgroundColor(Color.parseColor("#ff0000"));
                    }
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if(dataSnapshot==null)
                {
                    return;
                }
                if("isQuestionActive".equals(dataSnapshot.getKey()))
                {
                    isQuestionActive=dataSnapshot.getValue(boolean.class);
                }
                else if("isQuestionAnswered".equals(dataSnapshot.getKey())) {
                    isQuestionAnswered = dataSnapshot.getValue(boolean.class);
                }
                else if("user".equals(dataSnapshot.getKey())) {
                    time = dataSnapshot.child("presstime").getValue(long.class);
                }

                Log.d(TAG,dataSnapshot.hasChild("isQuestionActive")+" "+dataSnapshot.hasChildren()+" "+ isQuestionActive+" "+isQuestionAnswered+" "+username+" "+time+" "+System.currentTimeMillis()+" removed ");
                Log.d(TAG,"dataSnapshot"+dataSnapshot);
                if(!isQuestionActive)
                {
                    indicatorText.setText("Wait For Question");
                    answerButton.setText("Wait!");
                    answerButton.setEnabled(false);
                    answerButton.setBackgroundColor(Color.parseColor("#cccccc"));
                    prevtime.setValue(0);
                    isQuestionAnsweredRef.setValue(false);
                    isAnsweredByMe=false;
                    isQuestionAnswered=false;
                    myclicktime=0;
                    time=0;
                }
                else if(isQuestionActive&&!isQuestionAnswered)
                {
                    indicatorText.setText("Press Button to Answer!!");
                    answerButton.setText("Answer");
                    answerButton.setEnabled(true);
                    answerButton.setBackgroundColor(Color.parseColor("#00ffff"));
                }
                else if(isQuestionAnswered&&isQuestionActive)
                {
                    if((myclicktime<=time&&myclicktime!=0&&time!=0)||(time==0&&myclicktime!=0))
                    {
                        answerButton.setEnabled(false);
                            indicatorText.setText("You pressed buzzer first!!");
                            answerButton.setText("Fast :)");
                            answerButton.setEnabled(false);
                            answerButton.setBackgroundColor(Color.parseColor("#00ff00"));
                            username.setValue(un);
                            prevtime.setValue(myclicktime);
                    }
                    else
                    {
                        indicatorText.setText(" Someone already pressed the buzzer!!");
                        answerButton.setText("Slow :(");
                        answerButton.setEnabled(false);
                        answerButton.setBackgroundColor(Color.parseColor("#ff0000"));
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot==null)
                {
                    return;
                }
                if("isQuestionActive".equals(dataSnapshot.getKey()))
                {
                    isQuestionActive=dataSnapshot.getValue(boolean.class);
                }
                else if("isQuestionAnswered".equals(dataSnapshot.getKey())) {
                    isQuestionAnswered = dataSnapshot.getValue(boolean.class);
                }
                else if("user".equals(dataSnapshot.getKey())) {
                    time = dataSnapshot.child("presstime").getValue(long.class);
                }

                Log.d(TAG,dataSnapshot.hasChild("isQuestionActive")+" "+dataSnapshot.hasChildren()+" "+ isQuestionActive+" "+isQuestionAnswered+" "+username+" "+time+" "+System.currentTimeMillis()+" moved ");
                Log.d(TAG,"dataSnapshot"+dataSnapshot);
                if(!isQuestionActive)
                {
                    indicatorText.setText("Wait For Question");
                    answerButton.setText("Wait!");
                    answerButton.setEnabled(false);
                    answerButton.setBackgroundColor(Color.parseColor("#cccccc"));
                    prevtime.setValue(0);
                    isQuestionAnsweredRef.setValue(false);
                    isAnsweredByMe=false;
                    isQuestionAnswered=false;
                    myclicktime=0;
                    time=0;
                }
                else if(isQuestionActive&&!isQuestionAnswered)
                {
                    indicatorText.setText("Press Button to Answer!!");
                    answerButton.setText("Answer");
                    answerButton.setEnabled(true);
                    answerButton.setBackgroundColor(Color.parseColor("#00ffff"));
                }
                else if(isQuestionAnswered&&isQuestionActive)
                {
                    if((myclicktime<=time&&myclicktime!=0&&time!=0)||(time==0&&myclicktime!=0))
                    {
                        answerButton.setEnabled(false);
                            indicatorText.setText("You pressed buzzer first!!");
                            answerButton.setText("Fast :)");
                            answerButton.setEnabled(false);
                            answerButton.setBackgroundColor(Color.parseColor("#00ff00"));
                            username.setValue(un);
                            prevtime.setValue(myclicktime);
                    }
                    else
                    {
                        indicatorText.setText(" Someone already pressed the buzzer!!");
                        answerButton.setText("Slow :(");
                        answerButton.setEnabled(false);
                        answerButton.setBackgroundColor(Color.parseColor("#ff0000"));
                    }
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d(TAG,"cancelled");
            }
        });
    }

}