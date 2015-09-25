package info.si2.iista.volunteernetworks;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import info.si2.iista.volunteernetworks.apiclient.ItemModelValue;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 24/9/15
 * Project: Virde
 */
public class AdapterContributions extends RecyclerView.Adapter<AdapterContributions.ViewHolder> {

    // Adapter
    private Context context;
    private ArrayList<ItemModelValue> items;

    // Listener
    private ClickListener clickListener;

    ArrayList<Drawable> circles;

    AdapterContributions(Context context, ArrayList<ItemModelValue> items){
        this.context = context;
        this.items = items;
        this.circles = new ArrayList<>();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView image;
        TextView title;
        TextView description;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.imgUser);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (clickListener != null) {
                clickListener.onContributionItemClick(v, getAdapterPosition());
            }

        }

    }

    // ItemClickListener
    public interface ClickListener {
        void onContributionItemClick(View view, int position);
    }

    public void setClickListener (ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public AdapterContributions.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_campaign_user, parent, false);
        return new ViewHolder(v, viewType);

    }

    @Override
    public void onBindViewHolder(final AdapterContributions.ViewHolder holder, int position) {

        ItemModelValue item = items.get(position);

    }

    @Override
    public int getItemCount() {

        return items.size();

    }

}
