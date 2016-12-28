/* 
 * (c) 2016 by Peter Stamfest <peter@stamfest.at>
 * (c) 1999 by the National Institute Of Standards & Technology
 * 
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests.nist.utils;

import net.stamfest.randomtests.bits.Bits;

/**
 *
 * @author Peter Stamfest
 */
public class IntMatrix {
    int rows, cols;
    int matrix[][];
    public IntMatrix(int rows, int cols) {
        matrix = new int[rows][cols];
        this.rows = rows;
        this.cols = cols;
    }
    
    public void assignFromBits(Bits b, int offset) {
        int i, j, k = offset;
        
        for (i = 0 ; i < rows ; i++) {
            for (j = 0 ; j < cols ; j++, k++) {
                matrix[i][j] = b.bit(k);
            }
        }
    }

    void eliminateForward(int i) {
        int j, k;

        for (j = i + 1; j < rows; j++) {
            if (matrix[j][i] == 1) {
                for (k = i; k < cols; k++) {
                    matrix[j][k] = (matrix[j][k] + matrix[i][k]) % 2;
                }
            }
        }
    }
    
    void eliminateBackward(int i) {
        int j, k;
        
        for (j = i - 1; j >= 0; j--) {
            if (matrix[j][i] == 1) {
                for (k = 0; k < cols; k++) {
                    matrix[j][k] = (matrix[j][k] + matrix[i][k]) % 2;
                }
            }
        }
    }

    
    public int computeRank() {
        int i, rank, m = Math.min(rows, cols);

        /* FORWARD APPLICATION OF ELEMENTARY ROW OPERATIONS */
        for (i = 0; i < m - 1; i++) {
            if (matrix[i][i] == 1) {
                eliminateForward(i);
            } else /* matrix[i][i] = 0 */ {
                /*
                    find a row below in the matrix with a 1 in column i...
                */
                int j = i + 1;
                while (j < rows && matrix[j][i] == 0) {
                    j++;
                }
                if (j < rows) {
                    swapRows(i, j);
                    eliminateForward(i);
                }
            }
        }

        /* BACKWARD APPLICATION OF ELEMENTARY ROW OPERATIONS */
        for (i = m - 1; i > 0; i--) {
            if (matrix[i][i] == 1) {
                eliminateBackward(i);
            } else /* matrix[i][i] = 0 */ {
                /*
                    find a row above in the matrix with a 1 in column i...
                */
                int j = i - 1;
                while (j >= 0 && matrix[j][i] == 0) {
                    j--;
                }
                if (j >= 0) {
                    swapRows(i, j);
                    eliminateBackward(i);
                }
            }
        }

        rank = determine_rank(m);

        return rank;
    }
// swap rows i and j...

    void swapRows(int i, int j) {
        // swap rows i and j...
        int tmp;
        for (int k = 0 ; k < cols ; k++) {
            tmp = matrix[i][k];
            matrix[i][k] = matrix[j][k];
            matrix[j][k] = tmp;
        }
    }

    int determine_rank(int m) {
        int i, j, rank;
        boolean allZeroes;

        /* DETERMINE RANK, THAT IS, COUNT THE NUMBER OF NONZERO ROWS */
        rank = m;
        for (i = 0; i < rows; i++) {
            allZeroes = true;
            for (j = 0; j < cols ; j++) {
                if (matrix[i][j] == 1) {
                    allZeroes = false;
                    break;
                }
            }
            if (allZeroes) {
                rank--;
            }
        }

        return rank;
    }
}
