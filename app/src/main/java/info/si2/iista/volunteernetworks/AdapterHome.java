package info.si2.iista.volunteernetworks;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joooonho.SelectableRoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import info.si2.iista.volunteernetworks.apiclient.Item;
import info.si2.iista.volunteernetworks.apiclient.ItemCampaign;
import info.si2.iista.volunteernetworks.util.CircleTransform;
import info.si2.iista.volunteernetworks.util.Util;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 3/9/15
 * Project: Virde
 */
public class AdapterHome extends RecyclerView.Adapter<AdapterHome.ViewHolder> {

    // Adapter
    private Context context;
    private ArrayList<ItemCampaign> items;

    // Listener
    private ClickListener clickListener;

    ArrayList<Drawable> circles;

    AdapterHome(Context context, ArrayList<ItemCampaign> items){
        this.context = context;
        this.items = items;
        this.circles = new ArrayList<>();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imgCampaign;
        ImageView backGoToCampaign;
        ImageView goToCampaign;
        TextView title;
        TextView description;
        LinearLayout topUsers;
        Button suscribe;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);

            switch (viewType) {
                case Item.CAMPAIGN:
                    imgCampaign = (ImageView)itemView.findViewById(R.id.imgCampaign);
                    backGoToCampaign = (ImageView)itemView.findViewById(R.id.backGoToCampaign);
                    goToCampaign = (ImageView)itemView.findViewById(R.id.goToCampaign);
                    title = (TextView)itemView.findViewById(R.id.title);
                    description = (TextView)itemView.findViewById(R.id.description);
                    topUsers = (LinearLayout)itemView.findViewById(R.id.topUsers);
                    suscribe = (Button)itemView.findViewById(R.id.suscriptionButton);
                    itemView.setOnClickListener(this);
                    break;
            }

        }

        @Override
        public void onClick(View v) {

            if (clickListener != null) {
                clickListener.onHomeItemClick(v, getAdapterPosition());
            }

        }

    }

    // ItemClickListener
    public interface ClickListener {
        void onHomeItemClick(View view, int position);
    }

    public void setClickListener (ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public AdapterHome.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v;

        switch (viewType) {
            case Item.CAMPAIGN:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_campaign, parent, false);
                return new ViewHolder(v, viewType);
        }

        return null;

    }

    @Override
    public void onBindViewHolder(final AdapterHome.ViewHolder holder, int position) {

        final ItemCampaign item = items.get(position);

        switch (item.getType()) {
            case Item.CAMPAIGN:

                // Header image
                Picasso.with(context)
                        .load(item.getImage())
                        .into(holder.imgCampaign);

                int color = Color.parseColor(item.getHeaderColor());
                holder.backGoToCampaign.getDrawable().mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);

                Picasso.with(context)
                        .load(R.drawable.ic_arrow_right_white_24dp)
                        .transform(new CircleTransform())
                        .into(holder.goToCampaign);

                // Images Top Users
                holder.topUsers.post(new Runnable() {
                    @Override
                    public void run() {
                        int topUsers = holder.topUsers.getChildCount();
                        for (int i = 0; i < topUsers; i++) {

                            SelectableRoundedImageView image = (SelectableRoundedImageView) holder.topUsers.getChildAt(i);

                            Picasso.with(context)
                                    .load(R.drawable.test_logo_si2)
                                    .transform(new CircleTransform())
                                    .resize(200, 200)
                                    .into(image);

                        }
                    }
                });

                // Text
                holder.title.setText(item.getTitle());
                holder.description.setText(Html.fromHtml(item.getShortDescription()));

                // Suscribe button style
                holder.suscribe.post(new Runnable() {
                    @Override
                    public void run() {
                        setStyleButton(item.isSuscribe(), holder.suscribe);
                    }
                });

                final int pos = position;
                holder.suscribe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((MainActivity)context).suscription(items.get(pos).getId(), !items.get(pos).isSuscribe());
                    }
                });

                break;
        }

    }

    public void setStyleButton (boolean isSuscribe, Button view) {

        if (isSuscribe) {
            view.setBackgroundResource(R.drawable.button_unsuscribe);
            view.setTextColor(Util.getStatesUnsuscribe(context));
            view.setText(context.getString(R.string.unsuscribe));
        } else {
            view.setBackgroundResource(R.drawable.button_suscribe);
            view.setTextColor(Util.getStatesSuscribe(context));
            view.setText(context.getString(R.string.suscribe));
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
