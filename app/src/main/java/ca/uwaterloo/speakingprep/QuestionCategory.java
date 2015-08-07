package ca.uwaterloo.speakingprep;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ernestwong on 15-08-06.
 */
public class QuestionCategory {

    private final Context context;
    private static List<String> category = new ArrayList<String>();
    private static List<String> additionalCategory = new ArrayList<String>();
    private static ArrayAdapter<String> mAdapter;
    //Key -> category, Value -> List of questions which belongs to that category
    private static HashMap<String,List<Question>> questionMap = new HashMap<>();

    public QuestionCategory(final Context context) {
        this.context = context;

        // Clear category array before adding
        category.clear();
        category.add("Uncategorised");
        addToMap("Uncategorised");

        for (int i = 0; i < additionalCategory.size(); i++) {
            category.add(additionalCategory.get(i));
            addToMap(additionalCategory.get(i));
        }
        category.add("<Add A Category>");
        mAdapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item, category){
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                Typeface externalFont=Typeface.createFromAsset(context.getAssets(), "Lato-Light.ttf");
                ((TextView) v).setTypeface(externalFont);
                ((TextView) v).setTextSize(18f);
                return v;
            }

            public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                View v =super.getDropDownView(position, convertView, parent);

                Typeface externalFont=Typeface.createFromAsset(context.getAssets(), "Lato-Light.ttf");
                ((TextView) v).setTypeface(externalFont);
                ((TextView) v).setTextSize(18f);

                return v;
            }
        };
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    public ArrayAdapter<String> getCategory() { return mAdapter; }

    public static boolean addCategory (String categoryName) {
        for (String s : category) {
            // Prevent duplicate
            if (categoryName.equalsIgnoreCase(s)) return false;
        }
        category.add(category.size()-1,categoryName);
        additionalCategory.add(categoryName);
        mAdapter.notifyDataSetChanged();
        return true;
    }

    private void addToMap(String key) {
        if (!questionMap.containsKey(key)) {
            questionMap.put(key,new ArrayList<Question>());
        }
    }

    public static void addQuestion(String categoryName, Question question) {
        if (!questionMap.containsKey(categoryName)) {
            questionMap.put(categoryName, new ArrayList<Question>());
            questionMap.get(categoryName).add(question);
        } else {
            questionMap.get(categoryName).add(question);
        }

        for (Question q : questionMap.get(categoryName)) {
            Log.e("HI",categoryName + ": " + q.toString());
        }
    }
}
