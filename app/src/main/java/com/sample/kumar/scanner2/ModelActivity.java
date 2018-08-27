package com.sample.kumar.scanner2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by kumar on 4/2/18.
 */

public class ModelActivity extends AppCompatActivity {
   // private ArrayList<String> arrayList;
    private ArrayAdapter<String> adapter;
    Button b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_layout);
        b = (Button) findViewById(R.id.bt1);
        ListView listView = (ListView) findViewById(R.id.list2);
       // String[] items = {};

       ArrayList<String> arrayList =getIntent().getExtras().getStringArrayList("array list");
        adapter = new ArrayAdapter<String>(this, R.layout.list_items,R.id.txtbarcode, arrayList);
        listView.setAdapter(adapter);
        Iterator itr=arrayList.iterator();
        while (itr.hasNext()){
            Log.d("Next",itr.next().toString());
        }
        adapter.notifyDataSetChanged();



        /*Bundle bd = getIntent().getExtras();
        final String scan = bd.getString("com.sample.kumar.scanner2");
        Log.d("Data", scan);
        if (null != bd) {
            ArrayList<String> arr = bd.getStringArrayList("res");
            Log.i("List", "ArrayList" + arr);
            arrayList.add(scan);
            adapter.notifyDataSetChanged();
        }*/


        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ModelActivity.this, MainActivity.class);
              /*  i.putExtra("scan", scan);
                setResult(2, i);
                finish();*/
                startActivity(i);
                //adapter.notifyDataSetChanged();
            }
        });
    }
}
