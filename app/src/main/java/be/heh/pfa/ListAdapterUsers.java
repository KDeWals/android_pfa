package be.heh.pfa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapterUsers extends BaseAdapter {

    private ArrayList<User> listUsers;
    private LayoutInflater layoutInflater;
    private Context context;

    public ListAdapterUsers(Context context, ArrayList<User> listData) {
        this.context = context;
        this.listUsers = listData;
        layoutInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return listUsers.size();
    }

    @Override
    public Object getItem(int i) {
        return listUsers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.row_users_layout, null);
            holder = new ViewHolder();

            holder.tv_user_name = (TextView) convertView.findViewById(R.id.user_name);
            holder.tv_user_firstname = (TextView) convertView.findViewById(R.id.user_first_name);
            holder.tv_user_email = (TextView) convertView.findViewById(R.id.user_mail_address);
            holder.tv_user_permission = (TextView) convertView.findViewById(R.id.user_permission);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        User user = this.listUsers.get(position);
        holder.tv_user_name.setText(user.getName());
        holder.tv_user_firstname.setText(user.getFistname());
        holder.tv_user_email.setText(user.getEmail());
        holder.tv_user_permission.setText(user.getPerm());

        return convertView;
    }



    static class ViewHolder {
        TextView tv_user_name;
        TextView tv_user_firstname;
        TextView tv_user_email;
        TextView tv_user_permission;
    }
}
