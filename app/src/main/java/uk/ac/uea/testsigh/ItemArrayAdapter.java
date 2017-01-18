package uk.ac.uea.testsigh;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mradl on 14/12/2016.
 */


public class ItemArrayAdapter extends ArrayAdapter<String[]>{

    public List<String[]> scoreList = new ArrayList<String[]>();
    public String filterList = "academic_dept";
    private String descArray[] = new String[177];
    public String nameArray[] = new String[177];
    private float latArray[] = new float[177];
    private float longArray[] = new float[177];

    int amount = 0;

    static class ItemViewHolder {
        TextView name;
        TextView desc;
        TextView home;
    }

    public ItemArrayAdapter(Context context, int resource) {
        super(context, resource);
    }
    public void add(String[] object) {
        scoreList.add(object);
        super.add(object);
    }

    public void setFilter(String filter) {

        filterList = filter;
    }
    @NonNull
    public String getFilters() {

        return filterList;
    }
    @Override
    public int getCount() {
        return super.getCount();
    }

    public String[] getItem(int position) {
        return this.scoreList.get(position);
    }

    public float getlong(int value) {
        return longArray[value];
    }
    public float getLat(int value) {
        return latArray[value];
    }
    public String getName(int value) { return nameArray[value];}
    public int getAmount() {
        return amount;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ItemViewHolder viewHolder = new ItemViewHolder();



        if (row == null) {

            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.simple_list_item, parent, false);
            viewHolder = new ItemViewHolder();
            viewHolder.name = (TextView) row.findViewById(R.id.name);
            viewHolder.desc = (TextView) row.findViewById(R.id.desc);
            viewHolder.home = (TextView) row.findViewById(R.id.home);
            row.setTag(viewHolder);
        } else {
            viewHolder = (ItemViewHolder) row.getTag();

        }
        String[] stat = getItem(position);


        /* Fixes random 'out of bounds' crash when scrolling list */
        try{

          //  if (stat[0].equalsIgnoreCase(filterList)) {
                amount++;
                viewHolder.name.setText(stat[1]);//name
                viewHolder.desc.setText(stat[6]);//description


                nameArray[position] = stat[1];
                descArray[position] = stat[6];
                latArray[position] = Float.parseFloat(stat[2]);
                longArray[position] = Float.parseFloat(stat[3]);

                System.out.println(amount);


        } catch (Exception oubEx) {
            //return row;
            System.out.println("Caught: "+oubEx);
        }
        return row;
    }
}
