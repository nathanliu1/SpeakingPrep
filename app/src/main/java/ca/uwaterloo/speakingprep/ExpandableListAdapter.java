package ca.uwaterloo.speakingprep;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ernestwong on 15-08-27.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Activity context;
    private Map<String, List<Question>> questionMap;
    private List<String> category = new ArrayList<String>();

    public ExpandableListAdapter(Activity context) {
        this.context = context;
        questionMap = QuestionCategory.getMap();
        if (QuestionCategory.getCategory() != null) {
            for (int i = 0; i < QuestionCategory.getCategory().getCount(); i++) {
                if (!QuestionCategory.getCategory().getItem(i).equals("<Add A Category>"))
                    category.add(QuestionCategory.getCategory().getItem(i));
            }
        } else {
            category.add("Uncategorised");
        }
    }

    @Override
    public int getGroupCount() {
        return category.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (questionMap.get(category.get(groupPosition)) != null)
            return questionMap.get(category.get(groupPosition)).size();
        else
            return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return category.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if (questionMap.get(category.get(groupPosition)) != null) {
            Log.e("HI", category.get(groupPosition));
            return questionMap.get(category.get(groupPosition)).get(childPosition);
        } else {
            return new Question("Can you tell me a little bit about yourself?");
        }
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String laptopName = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.group_item,
                    null);
        }
        TextView item = (TextView) convertView.findViewById(R.id.category);
        item.setText(laptopName);
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final String question =  getChild(groupPosition, childPosition).toString();
        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.child_item, null);
        }

        TextView item = (TextView) convertView.findViewById(R.id.question);

        ImageView delete = (ImageView) convertView.findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Do you want to remove?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (groupPosition == 0 && childPosition == 0) {
                                    Toast.makeText(context, "You cannot delete this question", Toast.LENGTH_SHORT).show();
                                } else {
                                    List<Question> child =
                                            questionMap.get(category.get(groupPosition));
                                    child.remove(childPosition);
                                    notifyDataSetChanged();
                                    QuestionCategory.deleteQuestion((String) getGroup(groupPosition), question);
                                    MainActivityFragment.previousQuestion();
                                }
                            }
                        });
                builder.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        item.setText(question);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
