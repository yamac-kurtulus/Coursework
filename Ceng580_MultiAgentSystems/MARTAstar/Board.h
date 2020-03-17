/* 
 * File:   Board.h
 * Author: yamak
 *
 * Created on 16 Nisan 2014 Çarşamba, 21:25
 */

#ifndef BOARD_H
#define	BOARD_H

#define EDGE_LENGTH 3
#define BLOCKS EDGE_LENGTH * EDGE_LENGTH

#ifdef	__cplusplus
extern "C" {
#endif

    typedef struct Board{
        int board[BLOCKS];
        int empty;
    }Board_t;

    int compare (Board_t, Board_t);
    unsigned int hash (int[]);
    unsigned int rank(int pi[], int invPi[], int n);
    unsigned int fact (int n);
    
    
#ifdef	__cplusplus
}
#endif

#endif	/* BOARD_H */

