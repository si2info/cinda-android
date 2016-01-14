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

import info.si2.iista.volunteernetworks.apiclient.ItemProfile;
import info.si2.iista.volunteernetworks.util.Util;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 13/1/16
 * Project: Cinda
 */
public class AdapterProfile extends RecyclerView.Adapter<AdapterProfile.ViewHolder> {

    // Adapter
    private Context context;
    private ArrayList<ItemProfile> items;

    // Listener
    private ClickListener clickListener;

    ArrayList<Drawable> circles;

    public AdapterProfile(Context context, ArrayList<ItemProfile> items){
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
    public AdapterProfile.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_campaign_user, parent, false);
        return new ViewHolder(v, viewType);

    }

    @Override
    public void onBindViewHolder(final AdapterProfile.ViewHolder holder, int position) {

        ItemProfile item = items.get(position);

        // Date
        String dateSt = Util.parseDateToString("dd/MM/yyyy 'a las ' HH:mm", item.getCreateDate());

        // User
        holder.user.setText(item.getNameCampaign());
        holder.date.setText(dateSt);

        // Description
        if (!item.getDescription().equals(""))
            holder.description.setText(item.getDescription());
        else
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
