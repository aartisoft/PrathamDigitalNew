package com.pratham.prathamdigital.ui.dashboard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.Modal_NavigationMenu;

import java.util.ArrayList;

public class RV_MenuAdapter extends RecyclerView.Adapter<RV_MenuAdapter.NormalItemViewHolder> {

    private final ArrayList<Modal_NavigationMenu> menus;
    private final ContractMenu contractMenu;

    public RV_MenuAdapter(Context context, ArrayList<Modal_NavigationMenu> menus, ContractMenu contractMenu) {
        Context context1 = context;
        this.menus = menus;
        this.contractMenu = contractMenu;
    }

    @NonNull
    @Override
    public NormalItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView;
        LayoutInflater normal = LayoutInflater.from(viewGroup.getContext());
        itemView = normal.inflate(R.layout.item_nav_menu, viewGroup, false);
        return new NormalItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NormalItemViewHolder holder, int i) {
        holder.txt_menu_name.setText(menus.get(holder.getAdapterPosition()).getMenu_name());
        holder.img_nav_menu.setImageResource(menus.get(holder.getAdapterPosition()).getMenuImage());
//        if (menus.get(holder.getAdapterPosition()).isIsselected())
//            holder.txt_menu_name.setBackground(context.getResources().getDrawable(R.drawable.navigation_menu_round_bkgd));
//        else
//            holder.txt_menu_name.setBackground(context.getResources().getDrawable(R.drawable.navigation_menu_unselected_round_bkgd));
        holder.itemView.setOnClickListener(v -> contractMenu.menuClicked(holder.getAdapterPosition(), menus.get(holder.getAdapterPosition())));
    }

    @Override
    public int getItemCount() {
        return menus.size();
    }

    public ArrayList<Modal_NavigationMenu> getMenus() {
        return menus;
    }

    public class NormalItemViewHolder extends RecyclerView.ViewHolder {
        TextView txt_menu_name;
        ImageView img_nav_menu;

        NormalItemViewHolder(View itemView) {
            super(itemView);
            img_nav_menu = itemView.findViewById(R.id.img_nav_menu);
            txt_menu_name = itemView.findViewById(R.id.txt_nav_menu_name);
        }
    }
}
