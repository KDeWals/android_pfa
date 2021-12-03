package be.heh.pfa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {

    private ArrayList<Automate> listPlc;
    private LayoutInflater layoutInflater;
    private Context context;

    public ListAdapter(Context context, ArrayList<Automate> listData) {
        this.context = context;
        this.listPlc = listData;
        layoutInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return listPlc.size();
    }

    @Override
    public Object getItem(int i) {
        return listPlc.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.row_plc_layout, null);
            holder = new ViewHolder();

            holder.tv_plc_name = (TextView) convertView.findViewById(R.id.plc_name);
            holder.tv_plc_ip = (TextView) convertView.findViewById(R.id.plc_ip);
            holder.tv_plc_rack = (TextView) convertView.findViewById(R.id.plc_rack);
            holder.tv_plc_slot = (TextView) convertView.findViewById(R.id.plc_slot);
            holder.tv_plc_type = (TextView) convertView.findViewById(R.id.plc_type);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Automate automate = this.listPlc.get(position);
        holder.tv_plc_name.setText(automate.getName());
        holder.tv_plc_ip.setText(automate.getIp());
        holder.tv_plc_rack.setText(String.valueOf(automate.getRack()));
        holder.tv_plc_slot.setText(String.valueOf(automate.getSlot()));
        holder.tv_plc_type.setText(automate.getType());

        return convertView;
    }



    static class ViewHolder {
        TextView tv_plc_ip;
        TextView tv_plc_name;
        TextView tv_plc_rack;
        TextView tv_plc_slot;
        TextView tv_plc_type;
    }
}
