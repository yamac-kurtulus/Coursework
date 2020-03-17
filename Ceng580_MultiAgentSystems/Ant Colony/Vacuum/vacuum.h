#ifndef VACUUM_H
#define	VACUUM_H
#include "world.h"
typedef enum {NORTH = 1, EAST = 2, SOUTH = 3, WEST = 4}Orientations;
typedef enum {EMPTY = 0, DIRT = 5, OBSTACLE = 6, WALL = 7, FEROMONE = 8}Objects;
typedef enum {MOVED, TURNED, CLEANED}States;
typedef enum {MOVE, TURN, CLEAN}Actions;
typedef struct Vacuum {
    int x, y, depth;  //coords and visual depth
    int vLength;
    int orientation;  //orientation
    int action;       //what the next action will be
    char** vision;    //which squares can the vacuum see
    struct Vacuum *sibling;
    int *adjacent;
    int id;
} Vacuum_t;

void turn (Vacuum_t *v, World_t *world);
void move (Vacuum_t *v, World_t *world);
void clean (Vacuum_t *v, World_t *world);
void see (Vacuum_t *v, World_t *world);
int tick (Vacuum_t *v, World_t *world);
int decide (Vacuum_t *v, World_t *world);
void printvision (Vacuum_t* v);

#endif	/* VACUUM_H */

