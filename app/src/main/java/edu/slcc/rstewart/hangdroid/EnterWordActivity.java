package edu.slcc.rstewart.hangdroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class EnterWordActivity extends AppCompatActivity{

  @Override
  protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_enter_word);
    // Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
    // setSupportActionBar(toolbar);

    /*
    FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View view){
        Snackbar.make(view,"Replace with your own action",Snackbar.LENGTH_LONG).setAction("Action",null).show();
      }
    });
    */
  }


  public void submitSecretWord(View v){
    // create an activity to start the game
    Intent I = new Intent(this, GameActivity.class);

    // get the word that was entered
    EditText ET = (EditText)findViewById(R.id.etsw);
    String w = ET.getText().toString();

    // validate the word that was entered
    boolean tf = HDUtils.validateWord(w, true, this);
    if(!tf){return;}

    // trim and capitalize word before sending it
    w = HDUtils.cleanWord(w);

    // load the word that the user entered into the Intent
    // then send it
    I.putExtra("secretWord", w);
    startActivity(I);
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.hangdroid_menu, menu);
    return true;
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
