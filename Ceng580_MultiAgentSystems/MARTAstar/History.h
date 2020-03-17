/* 
 * File:   History.h
 * Author: yamak
 *
 * Created on 21 Nisan 2014 Pazartesi, 22:17
 */

#ifndef HISTORY_H
#define	HISTORY_H
#ifdef	__cplusplus
extern "C" {
#endif
    
#include "Board.h"

    typedef struct State {
        struct State *next, *prev; 
        Board_t board;
        int time;
        int visited;
        int move;
        int score;
        int iso;
    }State_t;
    
    typedef struct List {
        State_t *head;
        State_t *tail;
        int numElements;
    }History_t;
    
    void addState(History_t *list, State_t *state);
    void printState (State_t *state);



#ifdef	__cplusplus
}
#endif

#endif	/* HISTORY_H */

