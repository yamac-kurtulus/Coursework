#include "Agent.h"
#include <math.h> 

//computes sum of Manhattan distances of b to final 
int score(Board_t *b, Board_t *final) {
    int sc = 0, finalInd, currentInd, i, dx, dy;
    for (i = 0; i<BLOCKS; i++) {
        if (final -> board[i]!=0) {
    currentInd = findIndex(b->board, final->board[i]);
    dx = abs((i % EDGE_LENGTH) - (currentInd % EDGE_LENGTH));
    dy = abs(currentInd - i + dx) / EDGE_LENGTH;
    
    
    sc += (dx + dy);
        }
    }
    return sc;
}

//returns the index of the given element in board
int findIndex (int board[], int element) {
    int i;
    for ( i = 0; i<BLOCKS; i++)
        if (board [i] == element)
            return i;
    return -1;
}

//main function of the agent
int tick (Agent_t *a, Board_t *final, int t, int hashTable []) {
   int best = 1000, best2 = 1000, bestIndex, best2Index, sc, n = 0, i, j, G = 16;
    State_t set[4], temp;
    set [n]= *(a->history.tail->prev);
    Board_t *b = &(set[n].board) ;
    int iso;
        extern Agent_t* agents;
        extern int nrAgents;

    //generate all possible next states
    //up:
    if (b->empty < BLOCKS - EDGE_LENGTH) {
        b->board[b->empty] = b->board[b->empty + EDGE_LENGTH]; 
        b->empty += EDGE_LENGTH;
        b->board[b->empty] = 0;
        set [n].move = UP;
        sc = getFromTable(a->localTable, set[n].board.board);
        if (sc != -1)
            set [n].score = sc;
        else {
            sc = getFromTable(hashTable, set[n].board.board);
            if (sc != -1)
                set [n].score = sc;
            else
                set [n].score=score(b, final) ;
        }
        n++;
        set [n]= *(a->history.tail->prev);
    }

    //down
    b = &(set[n].board); 
    if (b->empty > EDGE_LENGTH-1) {
        b->board[b->empty] = b->board[b->empty - EDGE_LENGTH]; 
        b->empty -= EDGE_LENGTH;
        b->board[b->empty] = 0;
        set[n].move = DOWN;
        sc = getFromTable(a->localTable, set[n].board.board);
        if (sc != -1)
            set [n].score = sc;
        else {
            sc = getFromTable(hashTable, set[n].board.board);
            if (sc != -1)
                set [n].score = sc;
            else
                set [n].score=score(b, final) ;
        }
        n++;
        set [n]= *(a->history.tail->prev);
    }

    //left
    b = &(set[n].board); 
    if (b->empty % EDGE_LENGTH != EDGE_LENGTH-1) {
        b->board[b->empty] = b->board[b->empty + 1]; 
        b->empty +=1;
        b->board[b->empty] = 0;
        set[n].move = LEFT;
        sc = getFromTable(a->localTable, set[n].board.board);
        if (sc != -1)
            set [n].score = sc;
        else {
            sc = getFromTable(hashTable, set[n].board.board);
            if (sc != -1)
                set [n].score = sc;
            else
                set [n].score=score(b, final) ;
        }
        n++;
        set [n]= *(a->history.tail->prev);
    }
    //right

    b = &(set[n].board);
    if (b->empty % EDGE_LENGTH != 0) {
        b->board[b->empty] = b->board[b->empty - 1]; 
        b->empty -= 1;
        b->board[b->empty] = 0;
        set[n].move = RIGHT;
        sc = getFromTable(a->localTable, set[n].board.board);
        if (sc != -1)
            set [n].score = sc;
        else {
            sc = getFromTable(hashTable, set[n].board.board);
            if (sc != -1)
                set [n].score = sc;
            else
                set [n].score=score(b, final) ;
        }
        n++;
    }

    //find best and second best amongst candidates
    for (i = 0; i<n; i++) {
        for (j = i; j<n; j++) {
            if (set[i].score >= set[j].score) {
                temp = set[i];
                set [i] = set [j];
                set[j] = temp;
            }
        }
    }
    //randomize selection if needed
    j = 1;
    for (i = 1; i<n; i++) {
        if (set[0].score == set[i].score)
            j++;
    }
 bestIndex = rand ()%j;
    
//    if (j > 1) {
//        //employ attraction
//        int miniso = 100, flag = 0;
//        for (i = 0; i<j; i++) {
//            set[i].iso = getIsolation(a, set[i], agents, nrAgents);
//        }
//        for (i = 0; i<j; i++) {
//            if (set [i].iso <= G){
//                flag = 1;
//            }
//            else if (miniso > set [i].iso) 
//                miniso = set[i].iso;
//        }
//        if (!flag) {
//            for (i = 0; i<j; i++)
//                if (miniso == set [i].iso) {
//                    bestIndex = i;
//                }
//        }
//        else 
//            bestIndex = rand()%j;
//    }
    
    if (bestIndex != 0)
        best2Index = 0;
    else {
        j=1;  
            for (i = 2; i<n; i++) {
                if (set[1].score == set[i].score)
                    j++;
            }
        best2Index = (rand() % j) + 1;
    }
    best2 = set[best2Index].score;
    best = set[bestIndex].score;

    //continue with the best state
    set[bestIndex].time = t;
    addState(&a->history, &set[bestIndex]);
    addToTable(a->history.tail->prev->board.board, a->localTable, best2);
    addToTable(a->history.tail->prev->board.board, hashTable, best);

    return set[bestIndex].score;
}



//adds heuristic value of the given state to hash table
void addToTable (int boardPos[], int table [], int val ) {
    unsigned int index;
    index = hash (boardPos);
    table [index] = val;
}

//gets the heuristic value of the given state from the hash table
int getFromTable (int table [], int boardPos[]) {
    unsigned int index = hash (boardPos);
    return table[index];
}

//get the distance of agent a to agent b
int getAgentDistance (State_t *a, Agent_t *b) {
    return score (&(a->board), &(b->history.tail->prev->board));
}

//set the isolation of all agents in the agents array
int getIsolation (Agent_t* a, State_t s, Agent_t agents1[], int nrAgents) {
    int i, j, iso = 0, max = 0;
        max  = 0;
        for (j = 0; j<nrAgents; j++) {
            if (a->id != agents1[j].id) {
                iso = getAgentDistance(&s, &agents1[j]);
                if (max < iso)
                    max = iso;
            }
        }
        a->isolation = max;
        return max;
}

//used for printing the state history
void printHistory(Agent_t *a, int nrAgents) {

} 
