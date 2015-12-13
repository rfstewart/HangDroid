package edu.slcc.rstewart.hangdroid;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ContactsActivity extends ListActivity implements AdapterView.OnItemClickListener {
  ListView LV;
  Cursor rs;


  @Override
  protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_contacts);

    // query for phone contacts
    ContentResolver CR = getContentResolver();
    rs = CR.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
    startManagingCursor(rs);

    // create a string array with texter name and phono
    String Texter[] = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
         ContactsContract.CommonDataKinds.Phone.NUMBER};
    int[] item = {android.R.id.text1, android.R.id.text2};

    // create and set the ListAdapter
    SimpleCursorAdapter LA = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, rs, Texter, item, 0);
    setListAdapter(LA);

    // the list view to populate
    LV = getListView();
    LV.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

    // add onclick listener to LV
    LV.setOnItemClickListener(this);
  }


  public void onItemClick(AdapterView<?> parent, View v, int posn, long id){
    // get the phone number from the contact that was clicked
    TextView TV=(TextView)v.findViewById(android.R.id.text2);
    String phono=TV.getText().toString();

    // create a new intent to start the GetSmsActivity
    Intent I=new Intent(this, GetSmsActivity.class);
    I.putExtra("contactPhone",phono);
    startActivity(I);
  }
}
