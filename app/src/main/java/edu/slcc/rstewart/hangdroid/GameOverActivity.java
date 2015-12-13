package edu.slcc.rstewart.hangdroid;

import android.content.Intent;
import android.content.res.Resources;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

public class GameOverActivity extends AppCompatActivity {
  private String pts;


  @Override
  protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    setContentView(R.layout.game_over_activity);
    Intent I = getIntent();

    // set point count
    int p = I.getIntExtra("points",0);
    pts = Integer.toString(p);
    TextView TV=(TextView)findViewById(R.id.gotv2);
    TV.setText(pts);

    // get the win status
    boolean winTF = I.getBooleanExtra("win", false);

    // set the win/lose game over image
    ImageView IV = (ImageView)findViewById(R.id.goiv1);
    if(!winTF){IV.setImageResource(R.drawable.hangdroid_6);}
    else {IV.setImageResource(R.drawable.hangdroid_v);}
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.hangdroid_menu,menu);
    return true;
  }


  public void saveScore(View v){
    JSONObject JSO;

    // return without doing anything if score is zero
    TextView TV = (TextView)findViewById(R.id.gotv2);
    pts = TV.getText().toString();
    int i = Integer.parseInt(pts);
    if(i <= 0){
      HDUtils.alert(this, "Your pitiful score cannot be saved.");

      // show the scores page
      Intent I = new Intent(this, ScoresActivity.class);
      startActivity(I);
      return;}

    // get the name entered
    EditText ET = (EditText)findViewById(R.id.goet1);
    String s = ET.getText().toString();
    String na = s.trim();
    if(na.length() == 0 ){return;}

    // get the score string from SharedPreferences
    SharedPreferences SP = getSharedPreferences("Saved_Scores.dat", MODE_PRIVATE);
    String savedScores = SP.getString("SavedScores","{}");

    // convert the saved scores string to a JSONObject
    try {JSO = new JSONObject(savedScores);}
    catch(JSONException e){
      HDUtils.alert(this,"Score could not be saved.");
      return;}

    try {
      // get the current score for player na
      // if the new score is higher, save it
      int j = 0;

      // see if the player is already registered in preferences
      boolean tf = JSO.has(na);

      // get the players old score (or keep it 0 if there is no old score)
      if(tf){j = JSO.getInt(na);}

      // store the new score only if it is greater than the old score.
      if(i > j){JSO.put(na, i);}
    } catch(JSONException e){
      HDUtils.alert(this,"Score could not be saved.");
    }

    // convert the JSONObject back to a JSON string
    // and save to file
    String json = JSO.toString();
    SharedPreferences.Editor sped = SP.edit();
    sped.putString("SavedScores", json);
    sped.commit();

    // show the scores page
    Intent I = new Intent(this, ScoresActivity.class);
    startActivity(I);
  }  // end method saveScore


  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    Intent I = null;

    //noinspection SimplifiableIfStatement
    if (id == R.id.menu_single) {I = new Intent(this, GameActivity.class);}
    else if(id == R.id.menu_two){I = new Intent(this, EnterWordActivity.class);}
    else if(id == R.id.menu_sms){I = new Intent(this, GetSmsActivity.class);}
      else if(id == R.id.menu_scores){I = new Intent(this, ScoresActivity.class);}
        else if(id == R.id.menu_exit){System.exit(0);}

    // goto next sreen
    startActivity(I);
    return super.onOptionsItemSelected(item);
  }
}
