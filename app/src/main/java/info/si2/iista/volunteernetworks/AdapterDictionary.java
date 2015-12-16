package info.si2.iista.volunteernetworks;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import info.si2.iista.volunteernetworks.apiclient.Item;
import info.si2.iista.volunteernetworks.apiclient.ItemDictionary;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 15/12/15
 * Project: Shiari
 */
public class AdapterDictionary extends RecyclerView.Adapter<AdapterDictionary.ViewHolder> {

    // Adapter
    private Context context;
    private ArrayList<ItemDictionary> items;

    // Listener
    private OnItemClickListener clickListener;

    AdapterDictionary(Context context, ArrayList<ItemDictionary> items){
        this.context = context;
        this.items = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;
        TextView description;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);

            switch (viewType) {
                case Item.DICTIONARY:
                    name = (TextView)itemView.findViewById(R.id.name);
                    description = (TextView)itemView.findViewById(R.id.description);
                    itemView.setOnClickListener(this);
                    break;
            }

        }

        @Override
        public void onClick(View v) {

            if (clickListener != null) {
                clickListener.onItemClickListener(v, getAdapterPosition());
            }

        }

    }

    // ItemClickListener
    public interface OnItemClickListener {
        void onItemClickListener(View view, int position);
    }

    public void setOnItemClickListener (OnItemClickListener onClickListener) {
        this.clickListener = onClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

        switch (viewType) {
            case Item.DICTIONARY:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dictionary, parent, false);
                return new ViewHolder(v, viewType);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(final AdapterDictionary.ViewHolder holder, final int position) {

        ItemDictionary item = items.get(position);

        switch (item.getType()) {
            case Item.DICTIONARY:

                holder.name.setText(item.getName());
                holder.description.setText(item.getDescription());

                break;
        }

    }

    @Override
    public int getItemCount() {

        return items.size();

    }

    @Override
    public int getItemViewType(int position) {

        return items.get(position).getType();

    }

}
