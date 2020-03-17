/* 
 * File:   main.c
 * Author: Yamak
 *
 * Created on 28 Mart 2014 Cuma, 22:24
 */

#include <stdio.h>
#include <stdlib.h>
#include "vacuum.h"
#include "world.h"

/*
 * 
 */
int main(int argc, char** argv) {

    int i, j; //used as counter
    int t = 0; //current time
    int tempx, tempy, step;
    Vacuum_t *v1 = malloc (sizeof (Vacuum_t)), *v2 = malloc (sizeof (Vacuum_t));
    World_t *world = malloc (sizeof (World_t));
    world -> v1=v1;
    world -> v2=v2;
    printf ("Choose mode: 0 for step by step increment, or any integer for max timestep: \n");
    scanf ("%d", &step);
    FILE *infile;
    infile = fopen ("input.dat", "r");
    if (infile == 0)
        perror("Error");
    rewind (infile);
    fscanf (infile, "%d\n%d\n%d %d\n%d %d\n%d\n%d\n", &world->n, &world->d, &v1->x, &v1->y, &v2->x, &v2->y, &world->k, &world->l);
    printf ("size: %d x %d visual depth: %d\nv1: %d %d\nv2: %d %d\nobstacles: %d dirt: %d\ninitialising world.\n", world->n, world->n, world->d, v1->x, v1->y, v2->x, v2->y, world->k, world->l);
    v1->depth = world->d;
    v2->depth = world->d;
    v1 ->vLength  = 2*(v1->depth)+1;
    v2 ->vLength  = 2*(v2->depth)+1;
    v1->orientation = NORTH;
    v2->orientation = NORTH;
    v1->sibling = v2;
    v2->sibling = v1;
    v1->adjacent = calloc (4, sizeof (int));
    v2->adjacent = calloc (4, sizeof (int));
    v1->id = 0;
    v2->id = 1;
    
    world->n +=2; //+2 for one square at each side to use as walls
    //setup world
    world->grid = calloc (world->n, sizeof (int*)); 
    for (i = 0; i<world->n; i++) {
        world->grid [i] = calloc(world->n, sizeof (int));
    }
    
    //setup vacuums
    
    v1->vision = calloc (v1->vLength, sizeof (int*)); 
    for (i = 0; i<v1->vLength; i++) {
       v1->vision [i] = calloc(v1->vLength, sizeof (int));
    }
    
    v2->vision = calloc (v2->vLength, sizeof (int*)); 
    for (i = 0; i<v2->vLength; i++) {
        v2->vision[i] = calloc(v2->vLength, sizeof (int));
    }
    for (i = 0; i<world->k; i++) {
        fscanf (infile, "%d %d", &tempx, &tempy);
        world->grid[tempx][tempy] = OBSTACLE;
    }
    for (i = 0; i<world->l; i++) {
        fscanf (infile, "%d %d", &tempx, &tempy);
        world->grid[tempx][tempy] = DIRT;
    }
    
    for (i = 0; i<world->n; i++) {
        world->grid [0][i] = WALL;
        world->grid [world->n-1][i] = WALL;
        world->grid [i][0] = WALL;
        world->grid [i][world->n-1] = WALL;
    }
    
    printf ("world Ready\n");
    printWorldState(world,t);
    for (i = 0; (i<step && step>0) || step == 0; i++) {
        if (rand () %2) {   //simulate random acting order
            tick (v1, world);
            tick (v2, world);
        }
        else {
            tick (v2, world);
            tick (v1, world);
        }
         t++;
        
        printWorldState(world, t);
        if (world->l <= 0) 
            break;
        if (step == 0)
            getchar();
    }
    printf ("Finished in %d steps. ", t);
    if (world -> l <= 0)
        printf ("All the dirt is cleaned.");
    else printf ("MAX timesteps reached before full cleaning.\n");
    return (EXIT_SUCCESS);    
        
}



