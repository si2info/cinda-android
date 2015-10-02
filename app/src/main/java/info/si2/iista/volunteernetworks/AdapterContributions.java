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
import java.util.Date;

import info.si2.iista.volunteernetworks.apiclient.ItemContribution;
import info.si2.iista.volunteernetworks.util.Util;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 24/9/15
 * Project: Virde
 */
public class AdapterContributions extends RecyclerView.Adapter<AdapterContributions.ViewHolder> {

    // Adapter
    private Context context;
    private ArrayList<ItemContribution> items;

    // Listener
    private ClickListener clickListener;

    ArrayList<Drawable> circles;

    AdapterContributions(Context context, ArrayList<ItemContribution> items){
        this.context = context;
        this.items = items;
        this.circles = new ArrayList<>();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView image;
        TextView user;
        TextView date;
        TextView description;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.imgUser);
            user = (TextView) itemView.findViewById(R.id.user);
            date = (TextView) itemView.findViewById(R.id.date);
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

        ItemContribution item = items.get(position);

        // Date
        Date date = Util.parseDateHourStringToDate(item.getDate());
        String dateSt = Util.parseDateHourToString(date);

        // User
        if (item.isMine()) {
            holder.user.setText(dateSt);
            holder.date.setVisibility(View.GONE);
        } else {
            holder.user.setText(item.getUser());
            holder.date.setText(dateSt);
        }

        // Description
        holder.description.setText(item.getDescription());

    }

    @Override
    public int getItemCount() {

        return items.size();

    }

}
