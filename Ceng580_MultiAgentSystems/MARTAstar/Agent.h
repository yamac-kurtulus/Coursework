/* 
 * File:   Agent.h
 * Author: yamak
 *
 * Created on 16 Nisan 2014 Çarşamba, 21:03
 */

#ifndef AGENT_H
#define	AGENT_H

#include "Board.h"
#include "History.h"

#ifdef	__cplusplus
extern "C" {
#endif
    
    typedef struct Agent {
        int id;
        int lastMove;
        int isolation;
        History_t history;
        int *localTable;
    }Agent_t; 
    
    
    
    enum {UP, DOWN, LEFT, RIGHT};
    void makeMove(Agent_t*, int direction);
    int score(Board_t *b, Board_t *final);
    int tick (Agent_t *a, Board_t *final, int t, int hashTable[]);

    void addToTable(int boardPos[], int table[], int val);
    int getFromTable (int table [], int boardPos[]);
    int getAgentDistance (State_t *a, Agent_t *b);
    int getIsolation (Agent_t *a, State_t s, Agent_t agents[], int nrAgents);

    void printHistory (Agent_t *a, int agentNr);
    
    
#ifdef	__cplusplus
}
#endif

#endif	/* AGENT_H */

