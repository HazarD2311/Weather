package ru.surfproject.app.weather.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ru.surfproject.app.weather.R;
import ru.surfproject.app.weather.adapter.FavouritesAdapter;

/**
 * Диалоговое окно для переименования избранного места
 */

public class FavouriteRenameDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {

    private EditText renameEditText;
    private int position;
    private String newName;
    private FavouritesAdapter favouritesAdapter;
    private View form = null;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        form = getActivity().getLayoutInflater().inflate(R.layout.dialog_favourite_rename, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        return builder
                .setTitle("Редактировать место")
                .setView(form)
                .setPositiveButton("Ок", this)
                .setNegativeButton("Отмена", null)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        renameEditText = (EditText) form.findViewById(R.id.edit_dialog_favourite_rename);
        newName = renameEditText.getText().toString();

        if (newName.equals("")) {
            Toast.makeText(getContext(), "Вы не ввели название", Toast.LENGTH_SHORT).show();
        } else {
            favouritesAdapter.renameFavourite(position, newName);
        }
    }

    public void setFavouritesAdapter(FavouritesAdapter favouritesAdapter) {
        this.favouritesAdapter = favouritesAdapter;
    }

    public void setPosition(int position) {
        this.position = position;
    }

}
