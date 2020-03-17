#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <math.h>
#include "agents.h"

world readWorld (char* fileName) {
    
    //setup data structures and rad world from the file
    world w;
    int size;
    FILE *inFile;
    inFile = fopen (fileName, "r");
    if (inFile == NULL) 
        perror ("Error opening file");
    else {
        int h1_x, h1_y, h2_x, h2_y, p_x,p_y, i, j, o_x, o_y;
        fscanf (inFile, "%d %d %d %d %d %d %d\n", &size, &h1_x, &h1_y, &h2_x, &h2_y, &p_x, &p_y);
        
        char *nextLn;
        i = 0;
        w.obstacles = malloc (sizeof (mapObject));
        while ( ! feof (inFile) ) {
            fscanf (inFile, "%d %d\n", &o_x, &o_y);
            mapObject o;
            o.type = OBSTACLE;
            o.x = o_x;
            o.y = o_y;
            w.obstacles = realloc (w.obstacles, (i+1) * sizeof(mapObject));
            w.obstacles[i]  = o;
            i++;
        }
        w.obsCount = i;
        w.h1.x = h1_x;
        w.h1.y = h1_y;
        w.h1.type = HUNTER;
        w.tableSize = (int) pow ((double) size * 2 , 4.0);
        w.h1_table = malloc (w.tableSize  * sizeof (double));
        memset (w.h1_table, 0, sizeof(double));
        
        w.h2.x = h2_x;
        w.h2.y = h2_y;
        w.h2.type = HUNTER;
        w.h2_table = malloc (w.tableSize * sizeof (double));
        memset (w.h2_table, 0, sizeof(double));
        
        w.prey.x =p_x;
        w.prey.y = p_y;
        w.prey.type = PREY;
        
        w.size = size;
        fclose (inFile);
        return w;
    }
    
}

void randomise(world* w) {
    

    int x = rand() % w->size;
    int y = rand() % w->size;
    
    while (getObject (w,x,y) != NULL) {
        x = rand() % w->size;
        y = rand() % w->size;
    }
    w->prey.x = x;
    w->prey.y = y;
    
    x = rand() % w->size; 
    y = rand() % w->size;
    while (getObject (w,x,y) != NULL) {
        x = rand() % w->size;
        y = rand() % w->size;
    }
    w->h1.x =x;
    w->h1.y =y;
    
    x = rand() % w->size; 
    y = rand() % w->size;
    while (getObject (w,x,y) != NULL) {
        x = rand() % w->size;
        y = rand() % w->size;
    }
    w->h2.x =x;
    w->h2.y =y;
};


Direction randomMove(mapObject* a, world* w) {
    int d = rand() % 5, x = a->x, y = a->y;
    int canMove = 0;
    while (!canMove) {
        switch (d){
            case NORTH:
                if (y < 1 || getObject (w,x,y-1) != NULL)
                    d = (d+1) % 5;
                else
                    canMove = 1; 
                break;
            case EAST:
                if (x>w->size -2 || getObject(w, x+1, y) != NULL)
                    d = (d+1) % 5;
                else 
                    canMove = 1;
                break;
            case SOUTH:
                if (y > w->size-2 || getObject(w, x, y+1) != NULL)
                    d = (d+1) % 5;
                else 
                    canMove = 1;
                break;
            case WEST:
                if (x < 1 || getObject(w, x-1, y) != NULL)
                    d = (d+1) % 5;
                else 
                    canMove = 1;
                break;  
            default:
                canMove = 1;
                break;
        }
    }
    return d;
};

void move (Direction d, mapObject *a) {
    switch (d) {
        case NORTH:
            a->y = a->y-1;
            break;
        case EAST:
            a->x = a->x+1;
            break;
        case SOUTH:
            a->y = a->y+1;
            break;
        case WEST:
            a->x = a->x-1;
            break;
        default:
            break;
    }
}

void printWorld (world w) {
    int i, j;
    mapObject* o;
    for ( i = 0; i<w.size; i++) {
        for (j = 0; j<w.size; j++) {
            o = getObject (&w, j, i);
            if (o != NULL)
                printf("%c ", o->type);
            else 
                printf (" ");
        }
        printf ("\n");
    }
    printf ("h1: %d %d, h2: %d %d, p: %d %d\n", w.h1.x, w.h1.y, w.h2.x, w.h2.y, w.prey.x, w.prey.y);
}

void runEpoch (world* w) {
    Direction preyd, h1d, h2d;
    w->caught = 0;
    while (!w->caught) {
        preyd = randomMove (&w->prey, w); //choose a direction for the prey
        h1d = (Direction) rand()%5;     //choose a random direction for hunter1
        h2d = (Direction) rand()%5;     //choose a random direction for hunter2
        execute (w, h1d, h2d, preyd);
     
    }
    
//        printWorld(*w);
}

int isSurrounded (world* w) {
    int hunters = 0, i = 0, j = 0;
    for (i = -1; i<2; i++) {
        if (w->prey.x + i < 0)
            i++;
        else if (w->prey.x +i >= w->size)
            break;
        for (j = -1; j<2; j++) {
            if (w->prey.y + j < 0)
                j++;
            else  if (w->prey.y + j >= w->size)
                break;
            if (getObject(w, w->prey.x, w->prey.y) != NULL)
                if (getObject(w, w->prey.x, w->prey.y)->type == HUNTER)
                    hunters++;
        }
    }
    if (hunters >= 2)
        return 1;
    else 
        return 0;
}

int bump (mapObject h1, mapObject h2, Direction h1d, Direction h2d) {
    //check if agents collide

        if (h1.x == h2.x) {
            if (h1d == NORTH && h2d == SOUTH) {
                if (h1.y + 1 == h2.y)
                    return 1;
            }
            else if (h1d == SOUTH && h2d == NORTH) {
                if (h1.y - 1 == h2.y)
                    return 1;
            }        
        }
        else if (h1.y == h2.y){
            if (h1d == EAST && h2d == WEST) {
                if (h1.x + 1 == h2.x)
                    return 1;
            }
            else if (h1d == WEST && h2d == EAST) {
                if (h1.x - 1 == h2.x)
                    return 1;
            }  
        }
    return 0;
} 

//reinforce the given state of the hunter as a world type, with the given signal
int reinforce (double* qtable, State state, Direction action,  double signal) {
    
    double max = -5000, t;
    int i, q;
    mapObject* temp = &state.h1;
    move (action, temp);
    State tmpState = state;
    for  (i = 0; i<5; i++) {
        tmpState.h1 = *temp;
        move (i, &tmpState.h1);
        q = hash (tmpState);
        if (q>=0)
        t = qtable [q];
        if (max < t)
            max = t;
    }
    qtable [hash(state)] = signal + 0.9 * max;
}

//return a hashvalue for the qtable index
int hash (State state) {
    double h1x = (double) state.size + state.h1.x - state.prey.x;
    double h1y = (double) state.size + state.h1.y - state.prey.y;
    double h2x = (double) state.size + state.h2.x - state.prey.x;
    double h2y = (double) state.size + state.h2.y - state.prey.y;
    
    double s = (double) state.size * 2-1;
    int h = 0;
    
    h = (int) ((h1x * pow (s, 3.0) + h1y * pow (s, 2.0) + h2x * s + h2y ));
    return h;
}

void execute (world* w, Direction h1d, Direction h2d, Direction preyd) {
//    
//    mapObject *h1Temp = w->h1;
//    mapObject *h2Temp = w->h2;
//    mapObject *pTemp = w->prey;
    
    State h1CurrentState = {.h1 = w->h1, .h2 = w->h2, .prey = w->prey, .size = w->size}; 
    State h2CurrentState = {.h1 = w->h2, .h2 = w->h1, .prey = w->prey, .size = w->size};
     if (w->h1.x == w->prey.x && w->h1.y == w->prey.y) {
        reinforce (w->h1_table, h1CurrentState, h1d, 10.0);
        w->caught = 1;
        return;
    }
     if (w->h2.x == w->prey.x && w->h2.y == w->prey.y) {
        reinforce (w->h2_table, h2CurrentState, h2d, 10.0);
        w->caught = 1;
        return;
    }
    
    if (isSurrounded(w))  {
        reinforce (w->h1_table, h1CurrentState, h1d, 5.0);
        reinforce (w->h2_table, h2CurrentState, h2d, 5.0);
        w->caught = 1;
        return;
    }
    
    
    // check if prey bumps into hunters
    
    if (bump (w->prey, w->h1, preyd, h1d)){
        w->caught = 1;
        reinforce(w->h1_table, h1CurrentState, h1d,10);        
        return;
    }
    else if (bump (w->prey, w->h2, preyd, h2d)) {
        w->caught = 1;
        reinforce(w->h2_table,h2CurrentState,h2d,10);
        return;
    }
    //if no bump between prey and an hunter, prey moves first
    move (preyd, &w->prey);
    int h = hit(&w->h1, w, h1d);
    if (h == 1) {
        reinforce (w->h1_table, h1CurrentState, h1d, -1.0);
        h1d = STAY;
    }
    h = hit(&w->h2, w, h2d);
    if (h == 1) {
        reinforce (w->h2_table, h2CurrentState, h2d, -1.0);
        h2d = STAY;
    }
    
    if (bump (w->h1, w->h2, h1d, h2d) == 1) {
        reinforce (w->h1_table, h1CurrentState, h1d, -1.0);
        reinforce (w->h2_table, h2CurrentState, h2d, -1.0);
        h1d = STAY;
        h2d = STAY;
    }
    
    move (h1d, &w->h1);
    move (h2d, &w->h2);
   
   
    
        
    
//    move (preyd, w->prey, w->map);
//    if (isSurrounded(w)) {
//        w->prey->caught = 1;
//        reinforce(w->h1,h1d,w,5);
//        reinforce(w->h2,h2d,w,5);
//        return;
//    }
//    
//    
//    
//    //final check
//    if (isSurrounded(w)) {
//        w->prey->caught = 1;
//        reinforce(w->h1,h1d,w,5);
//        reinforce(w->h2,h2d,w,5);
//        return;
//    }
}

int hit (mapObject *a, world *w, Direction d) {
    int r = 0, x = a->x, y = a->y ;
    switch (d){
            case NORTH:
                if (a->y <1)
                    r = 1;
                else if  (getObject (w, x, y-1)!=NULL)
                    r = 1;
                break;
            case EAST:
                if (a->x>w->size -2)
                    r = 1;
                else if (getObject (w, x+1, y)!=NULL)
                    r = 1;
                 break;
            case SOUTH:
                if (a->y > w->size-2)
                  r = 1; 
                else if (getObject (w, x, y+1)!=NULL)
                    r= 1;
                 break;
            case WEST:
                if (a->x< 1)
                    r = 1; 
                else if (getObject (w, x-1, y)!=NULL)
                    r= 1;
                 break;
            default:
                r = 0;
                break;
        }
    return r;
}

void writeQtable (world w, char* fileName) {
    FILE *outFile;
    int i;
    outFile = fopen (fileName, "w");
    if (outFile == NULL) 
        perror ("Error opening file");
    else {
        for ( i = 0; i<w.tableSize; i++ ) {
            fprintf (outFile, "%f ", w.h1_table[i]);
        }
        fprintf (outFile, "\n");
        for ( i = 0; i<w.tableSize; i++ ) {
            fprintf (outFile, "%f ", w.h2_table[i]);
        }
        
    }
    fclose(outFile);
}

mapObject* getObject (world* w, int x, int y) {
    int i,j;
    int found = 0;
    if (w->h1.x == x && w->h1.y == y)
        return &(w->h1);
    if (w->h2.x == x && w->h2.y == y)
        return &(w->h2);
    if (w->prey.x == x && w->prey.y == y)
        return &(w->prey);
    mapObject* o = w->obstacles;
    for (i = 0; i < w->obsCount; i++) {
        if (o[i].x == x && o[i].y == y)
            return &o[i];
    }
    return NULL;
}