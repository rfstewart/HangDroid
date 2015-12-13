package edu.slcc.rstewart.hangdroid;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.splash_screen);

    // start new activity after 3 sed delayed
    Runnable w3s = new Runnable(){
    public void run(){nextActivity();}
    };
    Handler H = new Handler();
    H.postDelayed(w3s, 3000);
  }


  public void nextActivity(){
  Intent I = new Intent(this, MainActivity.class);
  startActivity(I);
  }
}
