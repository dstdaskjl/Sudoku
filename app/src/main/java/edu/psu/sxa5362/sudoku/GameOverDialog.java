package edu.psu.sxa5362.sudoku;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class GameOverDialog extends DialogFragment {
    public interface GameOverButtonClickInterface {
        void onGameOverButtonClick();
    }

    GameOverButtonClickInterface buttonClickInterface;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setTitle("Game Over!")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        buttonClickInterface.onGameOverButtonClick();
                        dialog.dismiss();
                    }
                });

        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        buttonClickInterface = (GameOverButtonClickInterface)context;
    }
}
