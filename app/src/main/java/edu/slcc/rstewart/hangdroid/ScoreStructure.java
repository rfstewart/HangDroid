package edu.slcc.rstewart.hangdroid;

import java.lang.Comparable;

/**
 * Created by rstewart on 11/28/15.
 */
public class ScoreStructure implements Comparable<ScoreStructure> {
  String PlayerName;
  int PlayerScore;

  public int compareTo(ScoreStructure ss){
    int n = (this.PlayerScore - ss.PlayerScore);
    if(n == 0){n = this.PlayerName.compareTo(ss.PlayerName);}
    return n;
  }  // end method compareTo


  public String toString(){
    String s = PlayerName + ": " + PlayerScore;
    return s;
  }  // end method toString
}
