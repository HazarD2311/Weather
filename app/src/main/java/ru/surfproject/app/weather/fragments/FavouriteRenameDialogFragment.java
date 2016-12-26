package ru.surfproject.app.weather.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;
import android.widget.Toast;

import ru.surfproject.app.weather.R;
import ru.surfproject.app.weather.adapters.FavouritesAdapter;
/**
 * Created by ПК on 12/25/2016.
 * Диалоговое окно для переименования избранного места
 */

public class FavouriteRenameDialogFragment extends DialogFragment {

    private Button cancelBtn;
    private Button okBtn;
    private EditText renameEditText;
    private int position;
    private String newName;
    private FavouritesAdapter favouritesAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setTitle("Редактировать место");
        View view = inflater.inflate(R.layout.dialog_favourite_rename, null);
        cancelBtn = (Button) view.findViewById(R.id.cancel_dialog_favourite_rename);
        okBtn = (Button) view.findViewById(R.id.ok_dialog_favourite_rename);
        renameEditText = (EditText) view.findViewById(R.id.edit_dialog_favourite_rename);

        cancelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        okBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //считываем текст с поля
                if (renameEditText.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Вы не ввели название", Toast.LENGTH_SHORT).show();
                } else {
                    newName = renameEditText.getText().toString();
                    favouritesAdapter.renameFavourite(position, newName);
                    dismiss();
                }
            }
        });

        return view;
    }

    public void setFavouritesAdapter(FavouritesAdapter favouritesAdapter) {
        this.favouritesAdapter = favouritesAdapter;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
