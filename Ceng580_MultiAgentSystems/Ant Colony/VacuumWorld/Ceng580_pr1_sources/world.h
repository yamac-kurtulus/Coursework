/* 
 * File:   world.h
 * Author: Yamak
 *
 * Created on 29 Mart 2014 Cumartesi, 23:55
 */

#ifndef WORLD_H
#define	WORLD_H

typedef struct {
    int ** grid;
    int n, d, k, l; //size of world, visual depth, number of obstacles, number of dirts and current timestep respectively
    void *v1, *v2;
}
World_t;

void printWorldState (World_t* world, int t);
void printSymbol (int sym);


#endif	/* WORLD_H */

