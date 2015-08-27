package ca.uwaterloo.speakingprep;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;


public class ListViewActivity extends Activity {

    ExpandableListView expListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        expListView = (ExpandableListView) findViewById(R.id.question_list);
        final ExpandableListAdapter expListAdapter = new ExpandableListAdapter(this);
        expListView.setAdapter(expListAdapter);
    }

}
