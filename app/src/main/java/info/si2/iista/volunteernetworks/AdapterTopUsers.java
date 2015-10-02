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

import info.si2.iista.volunteernetworks.apiclient.ItemUser;
import info.si2.iista.volunteernetworks.util.CircleTransform;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 2/10/15
 * Project: Virde
 */
public class AdapterTopUsers extends RecyclerView.Adapter<AdapterTopUsers.ViewHolder> {

    // Adapter
    private Context context;
    private ArrayList<ItemUser> items;

    // Listener
    private ClickListener clickListener;

    ArrayList<Drawable> circles;

    AdapterTopUsers(Context context, ArrayList<ItemUser> items){
        this.context = context;
        this.items = items;
        this.circles = new ArrayList<>();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView nTop;
        TextView user;
        ImageView image;
        TextView nContributions;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            nTop = (TextView)itemView.findViewById(R.id.n_top);
            user = (TextView)itemView.findViewById(R.id.user);
            image = (ImageView)itemView.findViewById(R.id.userImage);
            nContributions = (TextView)itemView.findViewById(R.id.nContributions);
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
    public AdapterTopUsers.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_topuser, parent, false);
        return new ViewHolder(v, viewType);

    }

    @Override
    public void onBindViewHolder(final AdapterTopUsers.ViewHolder holder, int position) {

        ItemUser item = items.get(position);

        holder.nTop.setText(String.valueOf(item.getnTop()));

        Picasso.with(context)
                .load(R.drawable.test_logo_si2)
                .transform(new CircleTransform())
                .resize(200, 200)
                .into(holder.image);

    }

    @Override
    public int getItemCount() {

        return items.size();

    }

}
