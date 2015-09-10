package info.si2.iista.volunteernetworks;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import info.si2.iista.bolunteernetworks.apiclient.Item;
import info.si2.iista.bolunteernetworks.apiclient.ItemIssue;
import info.si2.iista.volunteernetworks.util.CircleTransform;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 3/9/15
 * Project: Virde
 */
public class AdapterHome extends RecyclerView.Adapter<AdapterHome.ViewHolder> {

    // Adapter
    private Context context;
    private ArrayList<ItemIssue> items;

    // Listener
    private ClickListener clickListener;

    AdapterHome(Context context, ArrayList<ItemIssue> items){
        this.context = context;
        this.items = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;
        TextView description;
        LinearLayout topUsers;
        Button suscribe;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);

            switch (viewType) {
                case Item.ISSUE:
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
            case Item.ISSUE:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_issue, parent, false);
                return new ViewHolder(v, viewType);
        }

        return null;

    }

    @Override
    public void onBindViewHolder(final AdapterHome.ViewHolder holder, int position) {

        final ItemIssue item = items.get(position);

        switch (item.getType()) {
            case Item.ISSUE:

                // Color de cabecera
                if (item.getHeaderColor() == null) { // Color por defecto
                    int color = ContextCompat.getColor(context, R.color.primary_dark);
                    holder.title.setBackgroundColor(color);
                } else { // Color de asignado
                    int color = Color.parseColor(item.getHeaderColor());
                    holder.title.setBackgroundColor(color);
                }

                // Imágenes de Top Users
                int topUsers = holder.topUsers.getChildCount();
                for (int i=0; i<topUsers; i++) {

                    ImageView image = (ImageView)holder.topUsers.getChildAt(i);

                    Picasso.with(context)
                            .load(R.drawable.test_logo_si2)
                            .transform(new CircleTransform())
                            .resize(150, 150)
                            .into(image);

                }

                // Text
                holder.title.setText(item.getTitle());
                holder.description.setText(item.getDescription());

                // Suscribe button style
                setStyleButton(item.isSuscribe(), holder.suscribe);

                holder.suscribe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        item.setIsSuscribe(!item.isSuscribe());
                        setStyleButton(item.isSuscribe(), holder.suscribe);
                    }
                });

                break;
        }

    }

    public void setStyleButton (boolean isSuscribe, Button view) {

        if (isSuscribe) {
            view.setBackgroundResource(R.drawable.button_unsuscribe);
            view.setText(context.getString(R.string.unsuscribe));
        } else {
            view.setBackgroundResource(R.drawable.button_suscribe);
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
