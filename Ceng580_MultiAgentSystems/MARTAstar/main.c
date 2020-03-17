/* 
 * File:   main.c
 * Author: yamak
 *
 * Created on 16 Nisan 2014 Çarşamba, 20:43
 */

#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <math.h>
#include <string.h>
#include "Agent.h"
#include "Board.h"
#include "History.h"

#define FILENAME "input.txt"
/*
 * 
 */
    Agent_t *agents;
    int nrAgents;
int main(int argc, char** argv) {
    srand (time(0));
    int successful = -1;
    struct timespec start, end;
    Board_t *initial = malloc (sizeof (Board_t));
    Board_t *current = malloc (sizeof (Board_t));
    Board_t *final = malloc (sizeof (Board_t));
    int i, j, k, index, time = 0, cond, m;

    
    FILE *inputfile = fopen (FILENAME, "r");
    if (!inputfile)
        return (EXIT_FAILURE);
    
    //initialization
    
    //initialize board
    for (i = 0; i<EDGE_LENGTH; i++) {
        for (j=0; j<EDGE_LENGTH; j++) {
           index = i*EDGE_LENGTH + j;
           fscanf(inputfile, "%d", &initial->board[index]);
           if (initial->board[index] == 0)
               initial->empty = 0;
           fgetc(inputfile);
        }
    }
    
    //initialize goal state
    for (i = 0; i<EDGE_LENGTH; i++) {
        for (j=0; j<EDGE_LENGTH; j++) {
           index = i*EDGE_LENGTH + j;
           fscanf(inputfile, "%d", &final->board[index]);
           if (final->board[index] == 0)
               final->empty = 0;
           fgetc(inputfile);
        }
    }
    printf ("enter number of agents:");
    scanf ("%d", &nrAgents);
    agents = malloc (sizeof(Agent_t) * nrAgents);
    for (k = 0; k<nrAgents; k++) {
        agents[k].history.head = malloc (sizeof (State_t));
        agents[k].history.tail = malloc (sizeof (State_t));
        agents[k].history.numElements = 0;
        for (i = 0; i<EDGE_LENGTH; i++) {
            for (j=0; j<EDGE_LENGTH; j++) {
                index = i*EDGE_LENGTH + j;
                agents[k].history.head->board.board[index] = initial->board[index];
                if (initial->board[index] == 0)
                    agents[k].history.head->board.empty=index;
            }
        }
        agents[k].history.head->next = agents[k].history.tail;
        agents[k].history.head->prev = NULL;
        agents[k].history.tail->prev = agents[k].history.head;
        agents[k].history.tail->next = NULL;
        agents[k].history.head->time = 0;
        agents[k].history.head->visited = 1;
        agents[k].history.head->score = score (&(agents[k].history.head->board), final);
        
        agents [k].id = k;
        agents [k].lastMove = -1;
        agents[k].localTable = malloc (sizeof (int) *(fact (BLOCKS) - 1)); 
        memset (agents[k].localTable, -1, sizeof (int)*(fact (BLOCKS) - 1));
        
    }
    
    //init hashtable
    int *globalTable = malloc (sizeof (int) *(fact (BLOCKS) - 1)); 
    memset (globalTable, -1, sizeof (int)*(fact (BLOCKS) - 1));
    
    printf ("Board Ready\n");
    printf ("Starting MARTA* with agent count = %d\n", nrAgents);
    printf ("time = %d\n", time);
    printf ("initial state: \n");
    for (i = 0;i<EDGE_LENGTH; i++) {
        for (j=0; j<EDGE_LENGTH; j++) 
            printf ("%d ",initial->board[i*EDGE_LENGTH + j]);
        printf ("\n"); 
    }
    
    printf ("final state: \n");
    for (i = 0;i<EDGE_LENGTH; i++) {
        for (j=0; j<EDGE_LENGTH; j++) 
            printf ("%d ",final->board[i*EDGE_LENGTH + j]);
        printf ("\n"); 
    }
    clock_gettime(CLOCK_PROCESS_CPUTIME_ID, &start);
    while (time <100000 && successful == -1) {
        time++;
        for (i = 0; i<nrAgents; i++) {   
        cond = tick (&agents[i], final, time, globalTable); 
            if (cond  <= 0) {  
                successful = i;
                break;
            }
        }
    }
    clock_gettime(CLOCK_PROCESS_CPUTIME_ID, &end);
    printf ("Agent %d found a solution in %d moves.\n(Total number of moves of all agents: %d\n)", i, time, nrAgents*time);
    printf ("CPU time spent: %f secs", (double) (end.tv_nsec - start.tv_nsec) /  1000000000);
    
    printf ("\nPress a key to see the moves: \n");
    getchar();
    getchar();
    State_t** iterators;
    iterators = malloc (sizeof (State_t*) * nrAgents);
    for (j = 0; j<nrAgents; j++)
        iterators [j] = agents[j].history.head;
    for (j = 0; j<=time; j++) {
        printf ("T=%d: ",j);
        //Header for each timestep
        for (i = 0; i<nrAgents; i++) {
            printf ("\tAgent %d: ", i);
            switch (iterators[i]->move) {
                case -1:
                    printf ("NONE\t");
                    break;
                case UP:
                    printf ("UP\t");
                    break;
                case DOWN:
                    printf ("DOWN\t");
                    break;
                case LEFT:
                    printf ("LEFT\t");
                    break;
                case RIGHT:
                    printf ("RIGHT\t");
                    break;
            }
            
        }
        printf ("\n");
    
        for (m = 0; m<EDGE_LENGTH; m++) {
            for (i = 0; i<nrAgents; i++){
                printf ("\t\t");
                for (k = 0; k<EDGE_LENGTH; k++){
                    printf ("%d ",  iterators[i]->board.board[m*EDGE_LENGTH + k]);
                } 
                printf ("\t");
            }
            printf ("\n");
        }
        
        for (k = 0; k< nrAgents; k++) {
            iterators[k] = iterators[k]->next;
        }
    }
    
    
    return (EXIT_SUCCESS);
    
}
