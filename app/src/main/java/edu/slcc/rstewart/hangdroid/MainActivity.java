package edu.slcc.rstewart.hangdroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.hangdroid_menu,menu);
    return true;
  }


  public void startSinglePlayerGame(View v){
  Intent I = new Intent(this, GameActivity.class);
  startActivity(I);
  }


  public void startTwoPlayerGame(View v){
    Intent I = new Intent(this, EnterWordActivity.class);
    startActivity(I);
  }


  public void showScores(View v){
    Intent I = new Intent(this, ScoresActivity.class);
    startActivity(I);
  }  // end method showScores


  public void openSmsLoad(View v){
    Intent I = new Intent(this, GetSmsActivity.class);
    startActivity(I);
  }  // end method openSmsLoad

  public void showContacts(View v){
    Intent I = new Intent(this, ContactsActivity.class);
    startActivity(I);
  }  // end method openSmsLoad


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
