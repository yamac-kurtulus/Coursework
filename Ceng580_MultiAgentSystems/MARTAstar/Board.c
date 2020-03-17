/* 
 * File:   Board.c
 * Author: yamak
 *
 * Created on 21 Nisan 2014 Pazartesi, 02:05
 */

#include "Board.h"
#include <math.h>


//Uses Myrvold-Ruskey hash function.
unsigned int rank(int pi[], int invPi[], int n) {
    if (n == 1)
        return 0;
    unsigned int retVal = 0;
    int i, temp, s;
    //swap pi[n-1] and pi[invPi [n-1]]
    temp = pi [n-1];
    s = pi[n-1];
    pi[n-1] = pi[invPi[n-1]];
    pi[invPi[n-1]] = temp;
    
    //swap invPi [s] and invPi[n-1]
    temp = invPi[s];
    invPi [s] = invPi[n-1];
    invPi [n-1] = temp;
    return s + n * rank(pi, invPi, n-1);
}

unsigned int hash (int boardPos[]) {
    //copy arrays into temporary arrays
    int *pi, *invPi, i;
    pi = malloc (sizeof(int)*BLOCKS);
    invPi = malloc (sizeof(int)*BLOCKS);
    for (i = 0;i<BLOCKS; i++) {
        pi [i] = boardPos[i];
    }
    for (i = 0;i<BLOCKS; i++) {
        invPi [boardPos[i]] = i;
    }
    return rank (pi, invPi, BLOCKS);
}

unsigned int fact (int n) {
    int i;
    int prod = 1;
    for(i = 2; i<=n; i++)
        prod*=i;
    return prod;
}
