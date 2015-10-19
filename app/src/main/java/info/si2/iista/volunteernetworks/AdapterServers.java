package info.si2.iista.volunteernetworks;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import info.si2.iista.volunteernetworks.apiclient.Item;
import info.si2.iista.volunteernetworks.apiclient.ItemServer;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 7/9/15
 * Project: Virde
 */
public class AdapterServers extends RecyclerView.Adapter<AdapterServers.ViewHolder> {

    // Adapter
    private Context context;
    private ArrayList<ItemServer> items;

    AdapterServers(Context context, ArrayList<ItemServer> items){
        this.context = context;
        this.items = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout view;
        TextView server;
        TextView serverDesc;
        ImageView delete;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);

            switch (viewType) {
                case Item.SERVER:
                    view = (RelativeLayout)itemView.findViewById(R.id.view);
                    delete = (ImageView)itemView.findViewById(R.id.delete);
                    server = (TextView)itemView.findViewById(R.id.server);
                    serverDesc = (TextView)itemView.findViewById(R.id.serverDesc);
                    break;
            }

        }

    }

    @Override
    public AdapterServers.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v;

        switch (viewType) {
            case Item.SERVER:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_server, parent, false);
                return new ViewHolder(v, viewType);
        }

        return null;

    }

    @Override
    public void onBindViewHolder(final AdapterServers.ViewHolder holder, final int position) {

        final ItemServer item = items.get(position);

        switch (item.getType()) {
            case Item.SERVER:

                // Info server
                holder.server.setText(item.getServer());
                if (!item.getDescripcion().equals("")) {
                    holder.serverDesc.setText(item.getDescripcion());
                } else {
                    holder.serverDesc.setText(context.getString(R.string.serverNoDesc));
                }

                // Delete server
                holder.delete.setOnClickListener(new View.OnClickListener() { // Delete one server
                    @Override
                    public void onClick(View view) {

                        // Arguments
                        int index = items.indexOf(item);
                        Bundle bundle = new Bundle();
                        bundle.putInt("position", index);

                        // Dialog
                        DialogFragment dialog = new DeleteServerDialogFragment();
                        dialog.setArguments(bundle);
                        dialog.show(((Activity)context).getFragmentManager(), "DeleteServerDialogFragment");
                    }
                });

                // Active server
                if (item.isActive()) {
                    holder.view.setBackgroundColor(ContextCompat.getColor(context, R.color.itemSelected));
                    holder.delete.setVisibility(View.GONE);
                }

                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((Servers)context).changeServer(position);
                    }
                });

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

    /**
     * DialogFragment para eliminar servidores ya añadidos
     */
    public static class DeleteServerDialogFragment extends DialogFragment {

        public interface DeleteServerDialogListener {
            void onDialogPositiveClick(int position);
        }

        DeleteServerDialogListener mListener;

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            // Verify that the host activity implements the callback interface
            try {
                // Instantiate the NoticeDialogListener so we can send events to the host
                mListener = (DeleteServerDialogListener) activity;
            } catch (ClassCastException e) {
                // The activity doesn't implement the interface, throw exception
                throw new ClassCastException(activity.toString()
                        + " must implement DeleteServerDialogListener");
            }
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            Bundle bundle = getArguments();
            final int position = bundle.getInt("position");

            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.delete_text)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mListener.onDialogPositiveClick(position);
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dismiss();
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

}
