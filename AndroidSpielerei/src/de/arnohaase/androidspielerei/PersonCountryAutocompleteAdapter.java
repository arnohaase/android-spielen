package de.arnohaase.androidspielerei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.TextView;
import de.arnohaase.androidspielerei.provider.PersonProvider;


//TODO replace with a generic cursor adapter? --> how to do this with 'good' threading (i.e. not use SimpleCursorAdapter in onCreate)?
//TODO extract more generically server-bound adapter?
/**
 * This class serves as an asynchronous, remote-service-based auto-complete adapter for 
 *  persons' countries.<br>
 * 
 * This class 'lives' in the UI thread - all modifications must be made in that thread.
 * 
 * @author arno
 */
public final class PersonCountryAutocompleteAdapter implements ListAdapter, Filterable {
    private final LayoutInflater layoutInflater;
    private final int viewId;
    private final ContentResolver contentResolver;
    
    private final List<String> currentData = Collections.synchronizedList(new ArrayList<String>()); // Arrays.asList("Germany", "Georgia", "Great Britain", "France");
    private final List<DataSetObserver> observers = new ArrayList<DataSetObserver>();
    
    public PersonCountryAutocompleteAdapter(Context context) {
        this(context, android.R.layout.simple_dropdown_item_1line);
    }
    
    public PersonCountryAutocompleteAdapter(Context context, int viewId) {
        this.contentResolver = context.getContentResolver();
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.viewId = viewId;
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraintRaw) {
                final String constraint = String.valueOf(constraintRaw).toLowerCase();
                
                final FilterResults result = new FilterResults();
                currentData.clear();
                final Cursor countryCursor = contentResolver.query(PersonProvider.URI_PERSON_COUNTRIES, null, null, null, null);
                while(countryCursor.moveToNext()) {
                    final String candidate = countryCursor.getString(0);
                    if (candidate.toLowerCase().startsWith(constraint)) {
                        currentData.add(candidate);
                    }
                }
                result.values = currentData;
                return result;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                for (DataSetObserver observer: observers) {
                    observer.onChanged();
                }
            }
        };
    }

    public void registerDataSetObserver(DataSetObserver observer) {
        this.observers.add(observer);
    }
    public void unregisterDataSetObserver(DataSetObserver observer) {
        this.observers.remove(observer);
    }

    
    public int getCount() {
        return currentData.size();
    }

    public Object getItem(int position) {
        return currentData.get(position);
    }

    public int getItemViewType(int position) {
        return viewId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final TextView result = (TextView) layoutInflater.inflate(getItemViewType(position), parent, false);
        result.setText(String.valueOf(getItem(position)));
        return result;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public boolean isEmpty() {
        return getCount() == 0;
    }

    
    public long getItemId(int position) {
        return position;
    }
    public boolean hasStableIds() {
        return false;
    }

    public boolean areAllItemsEnabled() {
        return true;
    }
    public boolean isEnabled(int position) {
        return true;
    }
}
