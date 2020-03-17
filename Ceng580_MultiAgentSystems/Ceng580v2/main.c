/* 
 * File:   main.c
 * Author: yamak
 *
 * Created on 24 Mayıs 2014 Cumartesi, 22:10
 */

#include <stdio.h>
#include <stdlib.h>

#include "agents.h"
#define INFILENAME "environment.inp"
#define OUTFILENAME "indqtable.out"
#define EPOCHS 1000000


/*
 * 
 */
int main(int argc, char** argv) {
    
    int size, e;
    world w;
    w = readWorld(INFILENAME);
    srand (time (NULL));
    for (e =0; e < EPOCHS; e++){
        randomise(&w);
        runEpoch(&w);
    }
    
    writeQtable (w, OUTFILENAME);
    
    
    
    return (EXIT_SUCCESS);
}

