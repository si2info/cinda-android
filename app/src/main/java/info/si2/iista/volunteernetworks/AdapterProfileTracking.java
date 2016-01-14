package info.si2.iista.volunteernetworks;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import info.si2.iista.volunteernetworks.apiclient.ItemProfileTracking;
import info.si2.iista.volunteernetworks.util.Util;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 14/1/16
 * Project: Cinda
 */
public class AdapterProfileTracking extends RecyclerView.Adapter<AdapterProfileTracking.ViewHolder> {

    // Adapter
    private Context context;
    private ArrayList<ItemProfileTracking> items;

    // Listener
    private ClickListener clickListener;

    ArrayList<Drawable> circles;

    public AdapterProfileTracking(Context context, ArrayList<ItemProfileTracking> items){
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
    public AdapterProfileTracking.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_campaign_user, parent, false);
        return new ViewHolder(v, viewType);

    }

    @Override
    public void onBindViewHolder(final AdapterProfileTracking.ViewHolder holder, int position) {

        ItemProfileTracking item = items.get(position);

        // Date
        String dateSt = Util.parseDateToString("dd/MM/yyyy'\n'HH:mm", item.getCreateDate());

        // User
        holder.user.setText(item.getNameCampaign());
        holder.date.setText(dateSt);

        // Description
        holder.description.setText(context.getString(R.string.no_description));

        if (item.getImageCampaign() != null) {
            if (!item.getImageCampaign().equals("")) {
                Picasso.with(context)
                        .load(item.getImageCampaign())
                        .into(holder.image);
            }
        }

    }

    @Override
    public int getItemCount() {

        return items.size();

    }

}
