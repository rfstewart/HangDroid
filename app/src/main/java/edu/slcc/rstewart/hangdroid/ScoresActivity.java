package edu.slcc.rstewart.hangdroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Collections;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;

public class ScoresActivity extends AppCompatActivity{

  @Override
  protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_scores);
    JSONObject JSO = null;
    ArrayList<ScoreStructure> PlayerScores = new ArrayList<ScoreStructure>();
    String k = null;
    ScoreStructure scst = null;
    StringBuilder sb = new StringBuilder();
    String s = null;

    // create a JSONObject from the preferences file
    SharedPreferences SP = getSharedPreferences("Saved_Scores.dat", MODE_PRIVATE);
    String savedScores = SP.getString("SavedScores","{}");

    // convert the saved scores string to a JSONObject
    try {JSO = new JSONObject(savedScores);}
    catch(JSONException e){JSO = new JSONObject();}

    // get a JSONObject key iterator
    Iterator<String> it = JSO.keys();

    // move object properties into ScoresArray
    while(it.hasNext()){
      k = it.next().trim();

      // remove any entries without a name
      if(k.length() == 0){
          it.remove();
          continue;}
      else if(k == JSONObject.NULL){
        it.remove();
        continue;
      }

      // remove any entries that have no points
      //. otherwise add them to the array list
      try {
        if(JSO.getInt(k) == 0){it.remove();}
        else {
          ScoreStructure ss = new ScoreStructure();
          ss.PlayerName = k;
          ss.PlayerScore = JSO.getInt(k);
          PlayerScores.add(ss);
        }
      } catch(JSONException e){continue;}
    }

    // sort the array list
    Collections.sort(PlayerScores);

    // scan the array list from high to low score
    int L = PlayerScores.size();
    int n = L - 1;
    for(int i = n; i >= 0; i--){
      scst = PlayerScores.get(i);
      s = scst.toString();
      sb = sb.append(s);
      sb = sb.append("\n");
    }

    // get the complete score text
    if(L > 0){s = sb.toString();}
    else {s = "No scores.";}

    // load score text into the text view
    TextView tv = (TextView)findViewById(R.id.tvscores);
    tv.setText(s);
  }


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
