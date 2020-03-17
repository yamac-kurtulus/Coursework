#include "History.h"
#include "Agent.h"

void addState(History_t *list, State_t *stateIn) {
    State_t* state = malloc (sizeof(State_t));
    *state = *stateIn;
    state->next = list->tail;
    state->prev = list->tail->prev;
    list->tail->prev->next = state;
    list->tail->prev = state; 
    list->numElements++;
}

void printState (State_t *state) {
    int i;
    for (i = 0; i<BLOCKS;i++ ) {
        printf ("%d, ", state->board.board[i]);
    }
    switch (state->move) {
        case UP:
            printf ("UP");
            break;
        case DOWN:
            printf ("DOWN");
            break;
        case LEFT: 
            printf ("LEFT");
            break;
        case RIGHT:
            printf ("RIGHT");
            break;
    }
   
    printf ("\n\n");
}

