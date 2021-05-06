package edu.psu.sxa5362.sudoku;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SudokuAdapter extends RecyclerView.Adapter<SudokuAdapter.ViewHolder> {
    private int[][] sudokuBoard;
    private Map<Integer, Set<Integer>> backgroundColorIndexMap;

    public SudokuAdapter(int[][] sudokuBoard){
        this.sudokuBoard = sudokuBoard;
        this.backgroundColorIndexMap = getBackgroundColorIndex();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Sudoku.currentButtonInfo = null;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View buttonView = layoutInflater.inflate(R.layout.sudoku_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(buttonView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final int groupPosition = position;
        final int[] sudokuLine = sudokuBoard[groupPosition];
        final Set<Integer> backgroundColorIndexSet = backgroundColorIndexMap.get(groupPosition);

        for (int i = 0; i < 9; i++){
            // Set background colors (Whilte || Gray)
            if (backgroundColorIndexSet.contains(i)){
                holder.buttonArray[i].setBackgroundColor(Color.WHITE);
            }
            else {
                holder.buttonArray[i].setBackgroundColor(Color.LTGRAY);
            }

            // If the button contains the initial Sudoku board number, put the number and set the button unselectable
            if (sudokuLine[i] != 0){
                holder.buttonArray[i].setText(String.valueOf(sudokuLine[i]));
                holder.buttonArray[i].setClickable(false);
            }
            // The buttons that the user has to fill out
            else{
                final int itemPosition = i;
                final Button button = holder.buttonArray[i];
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Sudoku.currentButtonInfo != null){
                            Sudoku.currentButtonInfo.changeBackgroundColor();
                        }
                        if (backgroundColorIndexSet.contains(itemPosition)){
                            button.setBackgroundResource(R.drawable.button_border_white);
                            Sudoku.currentButtonInfo = new ButtonInfo(button, true, groupPosition, itemPosition);
                        }
                        else{
                            button.setBackgroundResource(R.drawable.button_border_grey);
                            Sudoku.currentButtonInfo = new ButtonInfo(button, false, groupPosition, itemPosition);
                        }
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return 9;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        Button[] buttonArray;
        public ViewHolder(View v){
            super(v);
            buttonArray = new Button[9];
            buttonArray[0] = v.findViewById(R.id.sudoku_button1);
            buttonArray[1] = v.findViewById(R.id.sudoku_button2);
            buttonArray[2] = v.findViewById(R.id.sudoku_button3);
            buttonArray[3] = v.findViewById(R.id.sudoku_button4);
            buttonArray[4] = v.findViewById(R.id.sudoku_button5);
            buttonArray[5] = v.findViewById(R.id.sudoku_button6);
            buttonArray[6] = v.findViewById(R.id.sudoku_button7);
            buttonArray[7] = v.findViewById(R.id.sudoku_button8);
            buttonArray[8] = v.findViewById(R.id.sudoku_button9);
        }
    }

    private Map<Integer, Set<Integer>> getBackgroundColorIndex(){
        return new HashMap<Integer, Set<Integer>>(){
            {
                put(0, new HashSet<Integer>(){
                    {
                        add(3);
                        add(4);
                        add(5);
                    }
                });
                put(1, new HashSet<Integer>(){
                    {
                        add(3);
                        add(4);
                        add(5);
                    }
                });
                put(2, new HashSet<Integer>(){
                    {
                        add(3);
                        add(4);
                        add(5);
                    }
                });
                put(3, new HashSet<Integer>(){
                    {
                        add(0);
                        add(1);
                        add(2);
                        add(6);
                        add(7);
                        add(8);
                    }
                });
                put(4, new HashSet<Integer>(){
                    {
                        add(0);
                        add(1);
                        add(2);
                        add(6);
                        add(7);
                        add(8);
                    }
                });
                put(5, new HashSet<Integer>(){
                    {
                        add(0);
                        add(1);
                        add(2);
                        add(6);
                        add(7);
                        add(8);
                    }
                });
                put(6, new HashSet<Integer>(){
                    {
                        add(3);
                        add(4);
                        add(5);
                    }
                });
                put(7, new HashSet<Integer>(){
                    {
                        add(3);
                        add(4);
                        add(5);
                    }
                });
                put(8, new HashSet<Integer>(){
                    {
                        add(3);
                        add(4);
                        add(5);
                    }
                });
            }
        };
    }
}
