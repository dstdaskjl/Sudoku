package edu.psu.sxa5362.sudoku;

import android.graphics.Color;
import android.widget.Button;

public class ButtonInfo {
    private Button button;
    private boolean isBackgroundWhite;
    private int groupPosition;
    private int itemPosition;

    public ButtonInfo(Button button, boolean isBackgroundWhite, int groupPosition, int itemPosition){
        this.button = button;
        this.isBackgroundWhite = isBackgroundWhite;
        this.groupPosition = groupPosition;
        this.itemPosition = itemPosition;
    }

    public Button getButton() {
        return button;
    }

    public boolean isBackgroundWhite() {
        return isBackgroundWhite;
    }

    public int getGroupPosition() {
        return groupPosition;
    }

    public int getItemPosition() {
        return itemPosition;
    }

    public void setButton(Button button) {
        this.button = button;
    }

    public void setBackgroundWhite(boolean backgroundWhite) {
        isBackgroundWhite = backgroundWhite;
    }

    public void setGroupPosition(int groupPosition) {
        this.groupPosition = groupPosition;
    }

    public void setItemPosition(int itemPosition) {
        this.itemPosition = itemPosition;
    }

    public void changeBackgroundColor(){
        if (isBackgroundWhite() == true){
            getButton().setBackgroundColor(Color.WHITE);
        }
        else{
            getButton().setBackgroundColor(Color.LTGRAY);
        }
    }
}
