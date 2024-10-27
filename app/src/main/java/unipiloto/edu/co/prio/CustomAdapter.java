// CustomAdapter.java
package unipiloto.edu.co.prio;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import unipiloto.edu.co.prio.deciderActivities.ProjectStatisticsActivity;

public class CustomAdapter extends ArrayAdapter<Project> {
    private List<Project> originalList;
    public List<Project> filteredList;
    private PrioDatabaseHelper dbHelper;

    public CustomAdapter(Context context, int resource, List<Project> projects, PrioDatabaseHelper dbHelper) {
        super(context, resource, projects);
        this.originalList = new ArrayList<>(projects);
        this.filteredList = new ArrayList<>(projects);
        this.dbHelper = dbHelper;
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public Project getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.card_item, parent, false);
        }

        Project currentItem = getItem(position);

        ImageView logoImageView = convertView.findViewById(R.id.logoImageView);
        TextView titleTextView = convertView.findViewById(R.id.titleTextView);
        TextView descriptionTextView = convertView.findViewById(R.id.descriptionTextView);

        logoImageView.setImageResource(currentItem.getLogoResId());
        titleTextView.setText(currentItem.getTitle());
        List<Integer> votes = dbHelper.getVotes(currentItem.getId());
        int aFavor = 0;

        for (int i : votes) {
            if (i == 1) {
                aFavor++;
            }
        }
        double aprobacion = aFavor / (double) votes.size() * 100;
        descriptionTextView.setText(String.valueOf(dbHelper.getVotes(currentItem.getId()).size() + " votos"+ "\n" + "Rating: " + aprobacion+"%"));

        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ProjectStatisticsActivity.class);
            intent.putExtra("item", currentItem);
            getContext().startActivity(intent);
        });
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    results.values = originalList;
                    results.count = originalList.size();
                } else {
                    List<Project> filteredItems = new ArrayList<>();
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Project item : originalList) {
                        if (filterPattern.startsWith("loc ")) {
                            int localityId = Integer.parseInt(filterPattern.substring(4));
                            if (item.getLocalityId() == localityId) {
                                filteredItems.add(item);
                            }
                        } else if (filterPattern.startsWith("cat ")) {
                            int categoryId = Integer.parseInt(filterPattern.substring(4));
                            if (item.getCategoryId() == categoryId) {
                                filteredItems.add(item);
                            }
                        } else if (item.getTitle().toLowerCase().contains(filterPattern)) {
                            filteredItems.add(item);
                        }
                    }
                    results.values = filteredItems;
                    results.count = filteredItems.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (List<Project>) results.values;
                if (filteredList == null) {
                    filteredList = new ArrayList<>();
                }
                Collections.sort(filteredList, (p1, p2) -> Double.compare(getAprobacion(dbHelper.getVotes(p2.getId())), getAprobacion(dbHelper.getVotes(p1.getId()))));
                notifyDataSetChanged();
            }
        };
    }

    private double getAprobacion(List<Integer> votes) {
        int aFavor = 0;
        for (int i : votes) {
            if (i == 1) {
                aFavor++;
            }
        }
        return aFavor / (double) votes.size() * 100;
    }
}