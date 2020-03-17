/* 
 * File:   agents.h
 * Author: yamak
 *
 * Created on 24 Mayıs 2014 Cumartesi, 22:27
 */

#ifndef AGENTS_H
#define	AGENTS_H

#ifdef	__cplusplus
extern "C" {
#endif
    
#define HUNTER 'H'
#define PREY 'P'
#define OBSTACLE 'O'
#define EMPTY '_'
    
    
typedef enum {NORTH, EAST, SOUTH, WEST, STAY} Direction;

typedef struct {
    char type;
    int x, y;
}mapObject;

typedef struct {
    mapObject h1, h2, prey;
    int size;
}State;
    
typedef struct {
    int tableSize;
    mapObject h1, h2, prey;
    mapObject* obstacles;
    double* h1_table;
    double* h2_table;
    int caught;
    Direction source;
    int size;
    int obsCount;
}world;
    
    mapObject* getObject (world* w, int x, int y);
    world readWorld (char*);
    void randomise(world*);
    Direction preyMove(world*);
    Direction hunterMove(mapObject*, world*);
    void printworld (world);
    void runEpoch (world*);
    int isSurrounded (world* w);
    int bump (mapObject h1, mapObject h2, Direction h1d, Direction h2d);
    int reinforce (double* qtable,  State state, Direction action, double signal);
    int hash (State state) ;
    void execute (world* w, Direction h1d, Direction h2d, Direction h3d);
    int hit (mapObject *a, world *w, Direction d); 

#ifdef	__cplusplus
}
#endif

#endif	/* AGENTS_H */

