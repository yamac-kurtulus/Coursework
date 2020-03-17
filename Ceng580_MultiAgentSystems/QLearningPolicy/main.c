/* 
 * File:   main.c
 * Author: yamak
 *
 * Created on 30 Mayıs 2014 Cuma, 06:08
 */

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include "agents.h"

#define OUTFILENAME "indqtable.out"
#define INFILENAME "environment.inp"

/*
 * 
 */
int main(int argc, char** argv) {

    world w;
    int i;
    double c;
    w = readWorld(INFILENAME);
    FILE *outpf;
    outpf = fopen (OUTFILENAME, "r");
    for (i= 0; i<w.tableSize; i++) {
        fscanf (outpf, "%lf ",  &c);
        w.h1_table[i] = c;
    }
    fgetc(outpf); //get rid of new line
    for (i= 0; i<w.tableSize; i++) {
        fscanf (outpf, "%lf ",  &c);
        w.h1_table[i] = c;
    }
    fclose (outpf);
    double t, max;
    Direction h1a, h2a, p;
    mapObject ptemp;
    w.caught = 0;
    while (!w.caught) {
        ptemp = w.prey;
        p = randomMove(&w.prey, &w);
        State h1s, h2s;
        h1s.h1 = w.h1;
        h1s.h2 = w.h2;
        h1s.prey = w.prey;
        h1s.size = w.size;
        h2s.h1 = w.h2;
        h2s.h2 = w.h1;
        h2s.prey = w.prey;
        h2s.size = w.size;
        max = -10000;
        int possibleMoves[] = {0, 0, 0, 0, 0}, p = 0;
        for (i = 0; i< 5; i++) {
 
            t = w.h1_table[hash (h1s)];
            if (max < t && !hit (&w.h1, &w, i)) {
                possibleMoves[0] = i;
                max = t;
                h1a = i;
                p=0;
            }
            if (max == t) {
                p++;
                possibleMoves[p] = i;
            }
        }
        h1a = possibleMoves [rand() % p];
        
                   
        max = -10000;
        p = 0;
       for (i = 0; i< 5; i++) {
 
            t = w.h1_table[hash (h2s)];
            if (max < t && !hit (&w.h2, &w, i)) {
                possibleMoves[0] = i;
                max = t;
                h2a = i;
                p=0;
            }
            if (max == t) {
                p++;
                possibleMoves[p] = i;
            }
        }
        h2a = possibleMoves [rand() % p];
        
        if (isSurrounded(&w))
            w.caught = 1;
        else if (bump (w.h1, ptemp, h1a, p) || bump (w.h2, ptemp, h2a, p))
            w.caught = 1;
        else if ((w.h1.x == w.prey.x && w.h1.y == w.prey.y)||(w.h2.x == w.prey.x && w.h2.y == w.prey.y))
            w.caught = 1;
        move (p, &w.prey, w);
        printWorld(w);
        move (h1a, &w.h1, w);
        move (h2a, &w.h2, w);
        getchar ();
    }
}

