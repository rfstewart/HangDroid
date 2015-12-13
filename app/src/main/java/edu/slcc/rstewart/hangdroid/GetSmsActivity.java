package edu.slcc.rstewart.hangdroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class GetSmsActivity extends AppCompatActivity{

  @Override
  protected void onCreate(Bundle savedInstanceState){

    // inflate XML
    super.onCreate(savedInstanceState);
    setContentView(R.layout.get_sms_activity);

    // load text message
    loadSms();
  }


  public void refreshSms(View v){loadSms();}


  private void loadSms(){
    // get the text message and from phono stored in shared pref
    String[] ss = HDUtils.getSms(this);

    // load the text message into the display
    EditText ET = (EditText)findViewById(R.id.etsms);
    ET.setText(ss[0]);

    // show non-sms sign
    showIfSmsPresent(ss[1]);
  } // end method loadSms


  public void playSms(View v){
    // get the word that was entered
    EditText ET = (EditText)findViewById(R.id.etsms);
    String w = ET.getText().toString();

    // validate the word that was entered
    // this should not be needed, but left in JIC
    boolean tf = HDUtils.validateWord(w, true, this);
    if(!tf){return;}

    // trim and capitalize word before sending it
    w = HDUtils.cleanWord(w);

    // now that the word is being used, remove word and phono from shared pref
    HDUtils.saveSms("", "", this);

    // create an activity to start the game
    // load the word that the user entered into the Intent
    // then send it
    Intent I = new Intent(this, GameActivity.class);
    I.putExtra("secretWord", w);
    startActivity(I);
  }  // end method playSms


  private void showIfSmsPresent(String phono){
    // reference the sms present sign
    TextView TV = (TextView)findViewById(R.id.tvgone);
    int i = HDUtils.isSmsValid(this);

    // show error if text message is not present
    if(i == 0){
      TV.setText(R.string.smsnotpresent);
      TV.setVisibility(TV.VISIBLE);
      return;
    }

    // show error if text message is not valid
    if(i == 1) {
      TV.setText(R.string.smsnotpresent);
      TV.setVisibility(TV.VISIBLE);
      return;
    }

    // if a contact phone was NOT sent in the intent, then show any sms available
    // and hide the error text view
    Intent I = getIntent();
    String contactPhono = I.getStringExtra("contactPhone");
    if(contactPhono == null){
      TV.setVisibility(TV.GONE);
      return;
    }

    // if you get here, a contact phone number WAS sent in the intent
    // if it does not match the phone number saved with the text message
    // show contact mismatch error
    if(!contactPhono.equals(phono)){
      TV.setText(R.string.smsnotmatched);
      TV.setVisibility(TV.VISIBLE);
      return;
    }

    // if you get to this point, the texted word is present, valid,
    // and from the selected contact.  So use it and hide the error nessage
    TV.setVisibility(TV.GONE);
  }  // end method showIfSmsPresent


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
