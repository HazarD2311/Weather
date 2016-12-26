package ru.surfproject.app.weather.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.surfproject.app.weather.adapters.FavouritesAdapter;
import ru.surfproject.app.weather.models.Favourite;
import ru.surfproject.app.weather.R;

/**
 * Created by pkorl on 03.12.2016.
 */

public class FavouritesFragment extends Fragment {

    private RecyclerView recyclerViewFavourites;
    private List<Favourite> favouriteList;
    private FavouritesAdapter favouriteRecyclerAdapter;
    private FavouriteRenameDialogFragment renameDialog;
    private View viewRoot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (viewRoot == null) {
            viewRoot = inflater.inflate(R.layout.fragment_favourites, container, false);
            getActivity().setTitle("Места");
            setupRecycler(); //заполнение RecycleView
        }
        return viewRoot;
    }

    private void setupRecycler() {
        recyclerViewFavourites = (RecyclerView) viewRoot.findViewById(R.id.recycler_view_favourite);
        recyclerViewFavourites.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewFavourites.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL)); // Добавляем разделитель между элементами
        setupList();
        favouriteRecyclerAdapter = new FavouritesAdapter(this.favouriteList,
                new FavouritesAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, Favourite favourite) {
                        //открывается новый фрагмент с погодой для этого города
                        Toast.makeText(getContext(), "click", Toast.LENGTH_SHORT).show();
                        Log.d("debug", "click");
                    }
                },
                new FavouritesAdapter.OnItemLongClickListener() {
                    @Override
                    public void onItemLongClick(View v, Favourite favourite, int position) {
                        showPopupMenu(v, position);
                        Log.d("debug", "long click");
                    }
                });
        recyclerViewFavourites.setAdapter(favouriteRecyclerAdapter);
    }

    private void setupList() {
        favouriteList = new ArrayList<>();
        favouriteList.add(new Favourite("Воронеж"));
        favouriteList.add(new Favourite("Нью-йорк"));
        favouriteList.add(new Favourite("Лондон"));
    }

    private void showPopupMenu(View view, final int position) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view, Gravity.RIGHT);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.favourite_rename:
                        //вызвать диалоговое окно с переименованием
                        showRenameDialog(position);
                        return true;
                    case R.id.favourite_delete:
                        //удалить из списка элемент
                        deleteFavourite(position);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.inflate(R.menu.favourite_popupmenu);

        popupMenu.show();
    }

    private void deleteFavourite(int position) {
        favouriteList.remove(position);
        favouriteRecyclerAdapter.notifyItemRemoved(position);
    }

    private void showRenameDialog(int position) {
        FragmentManager fm = getFragmentManager();
        renameDialog = new FavouriteRenameDialogFragment();
        renameDialog.setPosition(position);
        renameDialog.setFavouritesAdapter(favouriteRecyclerAdapter);
        renameDialog.show(fm, "RenameDialog");
    }

}
