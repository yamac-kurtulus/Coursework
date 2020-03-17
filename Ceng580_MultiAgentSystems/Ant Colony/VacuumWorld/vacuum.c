#include "vacuum.h"
#include "world.h"
#include <math.h>
#include <stdlib.h>

void turn(Vacuum_t *v, World_t *world) {
    v->orientation++;
    v->orientation = ((v->orientation - 1) % 4) + 1;

}

//moves the agent 1 square according to orientation. assuming (1,1) at topleft, 
//y increasing towards south, x increasing towards west

void move(Vacuum_t *v, World_t *world) {


    switch (v->orientation) {
        case NORTH:
            if (v->sibling->y != v->y - 1 ||v->sibling->x !=v->x) {
                world->grid[v->x][v->y] = FEROMONE;
                v->y--;
            }
           else turn(v, world);
            break;
        case SOUTH:
            if (v->sibling->y != v->y + 1 || v->sibling->x !=v->x){
                
                world->grid[v->x][v->y] = FEROMONE;
                v->y++;
            }
            else turn(v, world);
            break;
        case EAST:
            if (v->sibling->x != v->x + 1 || v->sibling->y !=v->y){
                
                world->grid[v->x][v->y] = FEROMONE;
                v->x++;
            }
            else 
                turn(v, world);
            break;
        case WEST:
            if (v->sibling->x != v->x - 1 || v->sibling->y !=v->y){
                world->grid[v->x][v->y] = FEROMONE;
                v->x--;
            }
            else 
                turn(v, world);
            break;
    }

}

void clean(Vacuum_t *v, World_t *world) {
    world->grid [v->x][v->y] = EMPTY;
    (world->l)--;
}

int tick(Vacuum_t *v, World_t *world) {

    see(v, world);
    int action;
    action = decide(v, world);

    switch (action) {
        case MOVE:
            move(v, world);
            break;
        case TURN:
            turn(v, world);
            break;
        case CLEAN:
            clean(v, world);
            break;
    }
    see(v, world);
    printf("vision \n");
    printVision(v);
    printf("\n");

}

//percepts the world by copying it into vision array

void see(Vacuum_t *v, World_t *world) {
    int d = v->depth;
    int wSize = world -> n;
    int x = v->x, y = v->y;
    int i, j;
    for (i = x - d; i <= x + d; i++) {
        for (j = y - d; j <= y + d; j++) {
            if (i <= 0 || i >= wSize - 1 || j <= 0 || j >= wSize - 1)
                v->vision[i - x + d][j - y + d] = WALL;
            else
                v->vision[i - x + d][j - y + d] = world->grid[i][j];
        }
    }
    v->adjacent[NORTH - 1] = world->grid[v->x][v->y - 1];
    v->adjacent[SOUTH - 1] = world->grid[v->x][v->y + 1];
    v->adjacent[EAST - 1] = world->grid[v->x + 1][v->y];
    v->adjacent[WEST - 1] = world->grid[v->x - 1][v->y];
}

int decide(Vacuum_t *v, World_t *world) {
    int decision = MOVE, x = 0, y = 0, k, l, destX = 0, destY = 0;
    int feromone = 0;
    int maxDistance = v->vLength * v->vLength;

    //level 1: If there is dirt, then clean it
    if (world->grid[v->x][v->y] == DIRT)
        return CLEAN;

    //level 2: If cannot move, then turn
    switch (v->orientation) {
        case NORTH:
            if (v->vision[v->depth][v->depth - 1] == OBSTACLE || v->vision[v->depth][v->depth - 1] == WALL)
                return TURN;
            else if (v->vision[v->depth][v->depth - 1] == FEROMONE)
                feromone = 1;
            break;
        case WEST:
            if (v->vision[v->depth - 1][v->depth] == OBSTACLE || v->vision[v->depth - 1][v->depth] == WALL)
                return TURN;
            else if (v->vision[v->depth - 1][v->depth] == FEROMONE)
                feromone = 1;
            break;
        case SOUTH:
            if (v->vision[v->depth][v->depth + 1] == OBSTACLE || v->vision[v->depth][v->depth + 1] == WALL)
                return TURN;
            else if (v->vision[v->depth][v->depth + 1] == FEROMONE)
                feromone = 1;
            break;
        case EAST:
            if (v->vision[v->depth + 1][v->depth] == OBSTACLE || v->vision[v->depth + 1][v->depth] == WALL)
                return TURN;
            else if (v->vision[v->depth + 1][v->depth] == FEROMONE)
                feromone = 1;
            break;
    }

    //level 3: find dirt in the visual area
    int distance = findCellType(v, world, &destX, &destY, DIRT);
    if (distance < maxDistance)
        return moveToRef(v, destX, destY);

    //level 4: go to the closest empty cell if encountered a feromone.
    if (feromone == 1) {
        distance = findCellType(v, world, &destX, &destY, EMPTY);
        if (distance < maxDistance)
            return moveToRef(v, destX, destY);
        else {
            return rand() % 100 > 65; //% 34 chance to turn right, %66 chance to move fw
        }
    }
    //if no emptycell in vision, move randomly


    //level 5: Move forward if all else fails.
    return MOVE;
}


// return Manhattan distance between vacuum and given cell given by relative locations x, y

int getDistance(Vacuum_t *v, int x, int y) {
    int i, j, sum = (v->vLength) * (v->vLength);
    sum = abs(x) + abs(y);
    return sum;
}

void printVision(Vacuum_t* v) {
    int i, j;
    for (i = 0; i < v->vLength; i++) {
        printf("\n");
        for (j = 0; j < v->vLength; j++)
            printSymbol(v->vision[j][i]);
    }
}

int moveToRef(Vacuum_t *v, int destX, int destY) {
    if (destX == 0 && destY == 0)
        return MOVE;
    int tor = (v->orientation - 1) + 3 % 4;

    switch (v->orientation) {
        case NORTH:
            if (destY < 0)
                return MOVE;
            else if (v->adjacent[tor] == WALL || v->adjacent[tor] == OBSTACLE || v->adjacent[tor + 2 % 4] == WALL || v->adjacent[(tor + 2) % 4] == OBSTACLE)
                return MOVE;
            else
                return TURN;
            break;
        case EAST:
            if (destX > 0)
                return MOVE;
            else if (v->adjacent[tor] == WALL || v->adjacent[tor] == OBSTACLE || v->adjacent[tor + 2 % 4] == WALL || v->adjacent[(tor + 2) % 4] == OBSTACLE)
                return MOVE;
            else
                return TURN;
            break;
        case SOUTH:
            if (destY > 0)
                return MOVE;
            else if (v->adjacent[tor] == WALL || v->adjacent[tor] == OBSTACLE || v->adjacent[tor + 2 % 4] == WALL || v->adjacent[(tor + 2) % 4] == OBSTACLE)
                return MOVE;
            else
                return TURN;
            break;
        case WEST:
            if (destX < 0)
                return MOVE;
            else if (v->adjacent[tor] == WALL || v->adjacent[tor] == OBSTACLE || v->adjacent[tor + 2 % 4] == WALL || v->adjacent[(tor + 2) % 4] == OBSTACLE)
                return MOVE;
            else
                return TURN;
            break;
    }
    //    if (destX>0){
    //        if (v->orientation == EAST)
    //            return MOVE;
    //        else 
    //            return TURN;
    //    }
    //    else if (destX<0) {
    //        if (v->orientation == WEST)
    //            return MOVE;
    //        else 
    //            return TURN;
    //    }
    //    else if (destY>0){
    //         if (v->orientation == SOUTH)
    //            return MOVE;
    //        else 
    //            return TURN;
    //    }
    //    else if (destY<0){
    //         if (v->orientation == NORTH)
    //            return MOVE;
    //        else 
    //            return TURN;
    //        
    //    }
    //    else 
    //        return MOVE;
}

int findCellType(Vacuum_t *v, World_t *world, int *destX, int *destY, int cellType) {
    int x, y, d = v->vLength * v->vLength, tmp;
    Vacuum_t* sib = v->sibling;
    for (x = -1 * v->depth; x <= v->depth; x++) {
        for (y = -1 * v->depth; y <= v->depth; y++) {
            if (v->x + x > 0 && v->x + x < world->n - 1 && v->y + y > 0 && v->y + y < world->n - 1) {
                if (world->grid[v->x + x][v->y + y] == cellType && d > getDistance(v, x, y) && !(x == 0 && y == 0)) {
                    *destX = x;
                    *destY = y;
                    d = getDistance(v, x, y);
                }
            }
        }
    }
    
    //if the sibling is in sensor range
    if (abs (sib -> x) <= sib->depth && abs (sib->y) <= sib->depth){
        for (x = -1 * sib->depth; x <= sib->depth; x++) {
        for (y = -1 * sib->depth; y <= sib->depth; y++) {
            if (sib->x + x > 0 && sib->x + x < world->n - 1 && sib->y + y > 0 && sib->y + y < world->n - 1) {
                if (world->grid[sib->x + x][sib->y + y] == cellType && d > getDistance(v, sib->x - v->x + x, sib->y - v->y + y) && !(x == 0 && y == 0)) {
                    *destX = sib->x - v->x + x;
                    *destY = sib->y - v->y + y;
                    d = getDistance(v, sib->x - v->x + x, sib->y - v->y + y);
                }
            }
        }
    }
    }
        
    return d;
}

