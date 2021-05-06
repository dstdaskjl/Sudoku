// https://www.geeksforgeeks.org/sudoku-backtracking-7/
package edu.psu.sxa5362.sudoku;

class SudokuSolver
{
    public static boolean isSafe(int[][] board,
                                 int row, int col,
                                 int num)
    {
        for (int d = 0; d < board.length; d++)
        {
            if (board[row][d] == num)
            {
                return false;
            }
        }

        for (int r = 0; r < board.length; r++)
        {
            if (board[r][col] == num)
            {
                return false;
            }
        }

        int sqrt = (int) Math.sqrt(board.length);
        int boxRowStart = row - row % sqrt;
        int boxColStart = col - col % sqrt;

        for (int r = boxRowStart;
             r < boxRowStart + sqrt; r++)
        {
            for (int d = boxColStart;
                 d < boxColStart + sqrt; d++)
            {
                if (board[r][d] == num)
                {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean solveSudoku(int[][] board, int n)
    {
        int row = -1;
        int col = -1;
        boolean isEmpty = true;
        for (int i = 0; i < n; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (board[i][j] == 0)
                {
                    row = i;
                    col = j;

                    isEmpty = false;
                    break;
                }
            }
            if (!isEmpty)
            {
                break;
            }
        }

        if (isEmpty)
        {
            return true;
        }

        for (int num = 1; num <= n; num++)
        {
            if (isSafe(board, row, col, num))
            {
                board[row][col] = num;
                if (solveSudoku(board, n))
                {
                    return true;
                }
                else
                {
                    board[row][col] = 0;
                }
            }
        }
        return false;
    }

    public static void print(int[][] board, int N)
    {
        for (int r = 0; r < N; r++)
        {
            for (int d = 0; d < N; d++)
            {
                System.out.print(board[r][d]);
                System.out.print(" ");
            }
            System.out.print("\n");

            if ((r + 1) % (int) Math.sqrt(N) == 0)
            {
                System.out.print("");
            }
        }
    }
}