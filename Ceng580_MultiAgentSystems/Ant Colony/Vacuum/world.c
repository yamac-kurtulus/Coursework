
#include "world.h"
#include "vacuum.h"
//World Functions

void printWorldState (World_t *world, int t) {
    int i, j, n = world->n;
    Vacuum_t *v1 = (Vacuum_t*) world->v1;
    Vacuum_t *v2 = (Vacuum_t*) world->v2;
    printf ("t = %d, number of dirt cells: %d\n", t, world->l);
    for (i = 0; i<n; i++) {
        for (j = 0; j<n; j++) {
            if ( j==v1->x  && i ==v1->y) {
                
                printSymbol (v1->orientation);
                continue;
            }
            if (j == v2->x && i ==v2->y) {
                printSymbol (v2->orientation);
                continue;
            }
            printSymbol(world->grid[j][i]);
        }
        printf ("\n");
    }
    printf ("\n");
}

void printSymbol (int sym) {
    char c;
    switch (sym) {
        case EMPTY:
            c = ' ';
            break;
        case FEROMONE:
            c = '.';
            break;
        case WALL:
            c = ',';
            break;
        case OBSTACLE:
            c = 'O';
            break;
        case DIRT:
            c = 'D';
            break;
        case NORTH:
            c = 'N';
            break;
        case EAST:
            c = 'E';
            break;
        case SOUTH:
            c = 'S';
            break;
        case WEST:
            c = 'W';
            break;
    }
    printf (" %c", c);
}
