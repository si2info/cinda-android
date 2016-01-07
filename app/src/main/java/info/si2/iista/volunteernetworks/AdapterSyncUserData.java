package info.si2.iista.volunteernetworks;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import info.si2.iista.volunteernetworks.apiclient.Item;
import info.si2.iista.volunteernetworks.apiclient.ItemSync;
import info.si2.iista.volunteernetworks.util.CircleTransform;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 7/9/15
 * Project: Virde
 */
public class AdapterSyncUserData extends RecyclerView.Adapter<AdapterSyncUserData.ViewHolder> {

    // Adapter
    private Context context;
    private ArrayList<ItemSync> items;

    // Listener
    private ClickListener clickListener;

    AdapterSyncUserData(Context context, ArrayList<ItemSync> items){
        this.context = context;
        this.items = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imgUser;
        TextView title;
        TextView url;
        TextView date;
        ImageView isSync;
        ProgressBar syncProgress;
        View separator;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);

            switch (viewType) {
                case Item.SYNC:
                    imgUser = (ImageView)itemView.findViewById(R.id.imgUser);
                    title = (TextView)itemView.findViewById(R.id.title);
                    url = (TextView)itemView.findViewById(R.id.urlServer);
                    date = (TextView)itemView.findViewById(R.id.date);
                    isSync = (ImageView)itemView.findViewById(R.id.isSync);
                    syncProgress = (ProgressBar)itemView.findViewById(R.id.syncProgressBar);
                    separator = itemView.findViewById(R.id.separator);
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
    public AdapterSyncUserData.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v;

        switch (viewType) {
            case Item.SYNC:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_campaign_sync, parent, false);
                return new ViewHolder(v, viewType);
        }

        return null;

    }

    @Override
    public void onBindViewHolder(final AdapterSyncUserData.ViewHolder holder, int position) {

        final ItemSync item = items.get(position);

        switch (item.getType()) {
            case Item.SYNC:

                Picasso.with(context)
                        .load(R.drawable.test_logo_si2)
                        .transform(new CircleTransform())
                        .resize(150, 150)
                        .into(holder.imgUser);

                holder.title.setText(item.getTitle());
                holder.url.setText(item.getUrl());
                holder.date.setText(item.getDate());

                if (item.isSynchronizing()) { // Synchronizing state

                    if (!item.isSync()) {
                        holder.isSync.setVisibility(View.INVISIBLE);
                        holder.syncProgress.setVisibility(View.VISIBLE);
                        item.setIsSync(true);
                    }

                } else { // Actual state
                    holder.isSync.setVisibility(View.VISIBLE);
                    holder.syncProgress.setVisibility(View.INVISIBLE);
                    setSyncState(item.isSync(), holder.isSync);
                }

                if (position == items.size()-1) // Not show last separator
                    holder.separator.setVisibility(View.INVISIBLE);

                break;
        }

    }

    public void setSyncState (boolean isSync, ImageView view) {

        if (isSync) {
            view.setImageResource(R.drawable.ic_done_black_24dp);
            int color = ContextCompat.getColor(context, R.color.primary);
            if (Build.VERSION.SDK_INT >= 21)
                view.getDrawable().setTint(color);
            else
                view.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        } else {
            view.setImageResource(R.drawable.ic_clear_black_24dp);
            int color = ContextCompat.getColor(context, R.color.error);
            if (Build.VERSION.SDK_INT >= 21)
                view.getDrawable().setTint(color);
            else
                view.setColorFilter(color, PorterDuff.Mode.SRC_IN);
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

    /**
     * Se encarga de sincronizar los datos del usuario que no han sido enviados a servidor
     * @param state Boolean que indica si sincronizar o detener la sincronización
     */
    public void setSynchronizing (boolean state) {

        for (int i=0; i<items.size(); i++) {
            ItemSync item = items.get(i);
            if (!item.isSync()) { // Synchronize items not synchronized
                item.setIsSynchronizing(state);
                notifyItemChanged(i);
            }

            if (!state) { // Stop synchronizing items
                if (item.isSynchronizing()) {
                    item.setIsSynchronizing(false);
                    notifyItemChanged(i);
                }

            }

        }

    }

}
