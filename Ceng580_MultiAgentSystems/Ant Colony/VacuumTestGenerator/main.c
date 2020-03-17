/* 
 * File:   main.c
 * Author: yamak
 *
 * Created on 04 Nisan 2014 Cuma, 00:24
 */

#include <stdio.h>
#include <stdlib.h>
#include <time.h>

/*
 * 
 */
int main(int argc, char** argv) {

    FILE* fp = fopen ("input.dat", "w");
    int obs, l, dimension, depth, i;
    printf ("dimension, depth, obstacle, dirt: \n");
    scanf ("%d %d %d %d", &dimension, &depth, &obs, &l);
    if (fp == 0)
        perror("Error");
    rewind (fp);
    srand (time(0));
    fprintf (fp, "%d\n%d\n%d %d\n%d %d\n%d\n%d\n",dimension, depth, (rand()%dimension)+1, (rand()%dimension) +1, (rand()%dimension)+1, (rand()%dimension) +1, obs, l);
    for (i = 0; i<obs+l; i++)
        fprintf(fp, "%d %d\n", (rand()%dimension) +1, (rand()%dimension) +1 );
    fclose (fp);        
    return (EXIT_SUCCESS);
}

